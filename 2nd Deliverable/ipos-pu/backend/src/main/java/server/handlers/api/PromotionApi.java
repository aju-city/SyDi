package server.handlers.api;

import java.util.List;

/**
 * Promotions API contract (Functionalities 15–17).
 *
 * This file centralizes:
 * - endpoint paths
 * - request/response DTO shapes for JSON
 * - stable response/error codes
 */
public final class PromotionApi {

    private PromotionApi() {}

    /**
     * API routes used by the promotions handlers.
     */
    public static final class Routes {
        private Routes() {}

        public static final String ADMIN_CAMPAIGNS = "/api/admin/promotions/campaigns";
        public static final String ADMIN_CAMPAIGN = "/api/admin/promotions/campaign";
        public static final String ADMIN_CAMPAIGN_TERMINATE = "/api/admin/promotions/campaign/terminate";

        public static final String ADMIN_CAMPAIGN_ITEMS = "/api/admin/promotions/campaign/items";
        public static final String ADMIN_CAMPAIGN_ITEM = "/api/admin/promotions/campaign/item";

        public static final String PROMOTIONS_ACTIVE = "/api/promotions/active";
        public static final String PROMOTION_PRODUCTS = "/api/promotions/products";
    }

    /**
     * Stable response codes returned in ApiResponse.code.
     */
    public static final class Codes {
        private Codes() {}

        public static final String OK = "OK";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String DB_CONNECTION_FAILED = "DB_CONNECTION_FAILED";
        public static final String DB_ERROR = "DB_ERROR";
        public static final String SERVER_ERROR = "SERVER_ERROR";

        public static final String CAMPAIGN_CREATED = "CAMPAIGN_CREATED";
        public static final String CAMPAIGN_UPDATED = "CAMPAIGN_UPDATED";
        public static final String CAMPAIGN_DELETED = "CAMPAIGN_DELETED";
        public static final String CAMPAIGN_TERMINATED = "CAMPAIGN_TERMINATED";
        public static final String CAMPAIGN_NOT_FOUND = "CAMPAIGN_NOT_FOUND";

        public static final String CAMPAIGN_ITEM_ADDED = "CAMPAIGN_ITEM_ADDED";
        public static final String CAMPAIGN_ITEM_UPDATED = "CAMPAIGN_ITEM_UPDATED";
        public static final String CAMPAIGN_ITEM_DELETED = "CAMPAIGN_ITEM_DELETED";
        public static final String CAMPAIGN_ITEM_NOT_FOUND = "CAMPAIGN_ITEM_NOT_FOUND";
        public static final String DUPLICATE_CAMPAIGN_PRODUCT = "DUPLICATE_CAMPAIGN_PRODUCT";

        public static final String INVALID_PRODUCT_ID = "INVALID_PRODUCT_ID";
        public static final String INVALID_DISCOUNT_RATE = "INVALID_DISCOUNT_RATE";
        public static final String INVALID_CAMPAIGN_DATES = "INVALID_CAMPAIGN_DATES";
        public static final String PROMOTION_CONFLICT = "PROMOTION_CONFLICT";
    }

    public static final class CreateCampaignRequest {
        public String campaignName;
        public String startDatetime;
        public String endDatetime;
        public String createdBy;
        public String status;
    }

    public static final class CampaignData {
        public Integer campaignId;
        public String campaignName;
        public String startDatetime;
        public String endDatetime;
        public String status;
        public String createdBy;
        public String createdAt;
        public String updatedAt;
    }

    public static final class CreateCampaignResponse {
        public Integer campaignId;
    }

    public static final class ListCampaignsResponse {
        public List<CampaignData> campaigns;
    }

    public static final class GetCampaignRequest {
        public Integer campaignId;
    }

    public static final class UpdateCampaignRequest {
        public Integer campaignId;
        public String campaignName;
        public String startDatetime;
        public String endDatetime;
        public String status;
    }

    public static final class DeleteCampaignRequest {
        public Integer campaignId;
    }

    public static final class TerminateCampaignRequest {
        public Integer campaignId;
    }

    public static final class AddCampaignItemRequest {
        public Integer campaignId;
        public String productId;
        public Double discountRate;
    }

    public static final class CampaignItemData {
        public Integer campaignItemId;
        public Integer campaignId;
        public String productId;
        public Double discountRate;
    }

    public static final class AddCampaignItemResponse {
        public Integer campaignItemId;
    }

    public static final class ListCampaignItemsRequest {
        public Integer campaignId;
    }

    public static final class ListCampaignItemsResponse {
        public List<CampaignItemData> items;
    }

    public static final class UpdateCampaignItemRequest {
        public Integer campaignItemId;
        public Double discountRate;
    }

    public static final class DeleteCampaignItemRequest {
        public Integer campaignItemId;
    }

    public static final class ActiveCampaignsResponse {
        public List<CampaignData> campaigns;
    }

    public static final class PromotionProductsRequest {
        public Integer campaignId;
    }

    public static final class PromotionProductPriceData {
        public String productId;
        public String name;
        public String description;
        public Double basePrice;
        public Double discountRate;
        public Double discountedPrice;
    }

    public static final class PromotionProductsResponse {
        public Integer campaignId;
        public List<PromotionProductPriceData> products;
    }
}