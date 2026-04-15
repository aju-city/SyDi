package dao;

import model.PromotionCampaignItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for PromotionCampaignItems table.
 */
public class PromotionCampaignItemsDAO {

    private Connection connection;

    public PromotionCampaignItemsDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Row used for enforcing promotion conflict rule across overlapping campaigns.
     */
    public static class ProductDiscountRow {
        private final int campaignId;
        private final double discountRate;

        public ProductDiscountRow(int campaignId, double discountRate) {
            this.campaignId = campaignId;
            this.discountRate = discountRate;
        }

        public int getCampaignId() {
            return campaignId;
        }

        public double getDiscountRate() {
            return discountRate;
        }
    }

    /**
     * Row used for promotion pricing endpoints (joins item discount with CA base price).
     */
    public static class PromotionProductRow {
        private final int campaignItemId;
        private final int campaignId;
        private final String productId;
        private final double discountRate;
        private final String name;
        private final String description;
        private final double basePrice;

        public PromotionProductRow(int campaignItemId,
                                   int campaignId,
                                   String productId,
                                   double discountRate,
                                   String name,
                                   String description,
                                   double basePrice) {
            this.campaignItemId = campaignItemId;
            this.campaignId = campaignId;
            this.productId = productId;
            this.discountRate = discountRate;
            this.name = name;
            this.description = description;
            this.basePrice = basePrice;
        }

        public int getCampaignItemId() {
            return campaignItemId;
        }

        public int getCampaignId() {
            return campaignId;
        }

        public String getProductId() {
            return productId;
        }

        public double getDiscountRate() {
            return discountRate;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public double getBasePrice() {
            return basePrice;
        }
    }

    /**
     * Creates a new promotion campaign item.
     */
    public void createCampaignItem(PromotionCampaignItems item) throws SQLException {
        String sql = "INSERT INTO PromotionCampaignItems (campaign_id, product_id, discount_rate) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getCampaignId());
            stmt.setString(2, item.getProductId());
            stmt.setDouble(3, item.getDiscountRate());
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new promotion campaign item and returns its generated id.
     */
    public int createCampaignItemAndReturnId(PromotionCampaignItems item) throws SQLException {
        String sql = "INSERT INTO PromotionCampaignItems (campaign_id, product_id, discount_rate) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getCampaignId());
            stmt.setString(2, item.getProductId());
            stmt.setDouble(3, item.getDiscountRate());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to retrieve generated campaign_item_id.");
    }

    /**
     * Retrieves a campaign item by id.
     */
    public PromotionCampaignItems getCampaignItemById(int campaignItemId) throws SQLException {
        String sql = "SELECT * FROM PromotionCampaignItems WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCampaignItem(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all items for a campaign.
     */
    public List<PromotionCampaignItems> getItemsByCampaignId(int campaignId) throws SQLException {
        List<PromotionCampaignItems> items = new ArrayList<>();
        String sql = "SELECT * FROM PromotionCampaignItems WHERE campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToCampaignItem(rs));
                }
            }
        }

        return items;
    }

    /**
     * Returns discount rates for a given product in campaigns that overlap a time window.
     *
     * Used to enforce the conflict rule (Functionalities 16): same product in overlapping
     * campaigns cannot have different discount rates.
     *
     * Excludes campaigns with status ENDED/TERMINATED.
     */
    public List<ProductDiscountRow> findOverlappingDiscountsForProduct(String productId,
                                                                       Timestamp windowStart,
                                                                       Timestamp windowEnd,
                                                                       Integer excludeCampaignId) throws SQLException {
        List<ProductDiscountRow> rows = new ArrayList<>();

        String sql = "SELECT pci.campaign_id, pci.discount_rate " +
                "FROM PromotionCampaignItems pci " +
                "JOIN PromotionCampaign pc ON pc.campaign_id = pci.campaign_id " +
                "WHERE pci.product_id = ? " +
                "AND pc.status NOT IN ('ENDED','TERMINATED') " +
                "AND pc.start_datetime < ? AND pc.end_datetime > ? " +
                (excludeCampaignId == null ? "" : "AND pc.campaign_id <> ? ");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int idx = 1;
            stmt.setString(idx++, productId);
            stmt.setTimestamp(idx++, windowEnd);
            stmt.setTimestamp(idx++, windowStart);
            if (excludeCampaignId != null) {
                stmt.setInt(idx, excludeCampaignId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new ProductDiscountRow(rs.getInt("campaign_id"), rs.getDouble("discount_rate")));
                }
            }
        }

        return rows;
    }

    /**
     * Returns promotion products for a campaign along with their base prices from CA.
     */
    public List<PromotionProductRow> getPromotionProductsWithBasePrice(int campaignId) throws SQLException {
        List<PromotionProductRow> rows = new ArrayList<>();

        String sql = "SELECT pci.campaign_item_id, pci.campaign_id, pci.product_id, pci.discount_rate, " +
                "si.name, si.description, si.price " +
                "FROM PromotionCampaignItems pci " +
                "JOIN ipos_ca.stock_items si ON si.item_id = pci.product_id " +
                "WHERE pci.campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new PromotionProductRow(
                            rs.getInt("campaign_item_id"),
                            rs.getInt("campaign_id"),
                            rs.getString("product_id"),
                            rs.getDouble("discount_rate"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price")
                    ));
                }
            }
        }

        return rows;
    }

    /**
     * Updates the discount rate of a campaign item.
     */
    public void updateDiscountRate(int campaignItemId, double discountRate) throws SQLException {
        String sql = "UPDATE PromotionCampaignItems SET discount_rate = ? WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, discountRate);
            stmt.setInt(2, campaignItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a campaign item.
     */
    public void deleteCampaignItem(int campaignItemId) throws SQLException {
        String sql = "DELETE FROM PromotionCampaignItems WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to PromotionCampaignItems object.
     */
    private PromotionCampaignItems mapResultSetToCampaignItem(ResultSet rs) throws SQLException {
        PromotionCampaignItems item = new PromotionCampaignItems();
        item.setCampaignItemId(rs.getInt("campaign_item_id"));
        item.setCampaignId(rs.getInt("campaign_id"));
        item.setProductId(rs.getString("product_id"));
        item.setDiscountRate(rs.getDouble("discount_rate"));
        return item;
    }
}