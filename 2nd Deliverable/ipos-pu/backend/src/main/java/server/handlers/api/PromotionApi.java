package server.handlers.api;

import java.util.List;

/**
 * Promotions API contract (Functionalities 15–17).
 *
 * This file centralizes:
 * - Endpoint paths (so handlers + frontend stay consistent)
 * - Request/response DTO shapes for JSON (Gson-friendly public fields)
 * - Stable response/error codes returned via ApiResponse
 */
public final class PromotionApi {

    private PromotionApi() {}

    /**
     * API routes for Step 2 (Promotions 15/16/17).
     *
     * Notes:
     * - This backend uses com.sun.net.httpserver.HttpServer, which doesn't provide path params.
     *   For now we encode IDs as query params (e.g. ?campaignId=1) to keep routing simple.
     * - If you later add a router that supports path params, these constants can be migrated.
     */
    public static final class Routes {
        private Routes() {}

        // Admin: campaign CRUD
        public static final String ADMIN_CAMPAIGNS = "/api/admin/promotions/campaigns"; // POST/GET
        public static final String ADMIN_CAMPAIGN = "/api/admin/promotions/campaign"; // GET/PATCH/DELETE (campaignId query param)
        public static final String ADMIN_CAMPAIGN_TERMINATE = "/api/admin/promotions/campaign/terminate"; // POST (campaignId query param)

        // Admin: campaign item management
        public static final String ADMIN_CAMPAIGN_ITEMS = "/api/admin/promotions/campaign/items"; // POST/GET (campaignId query param)
        public static final String ADMIN_CAMPAIGN_ITEM = "/api/admin/promotions/campaign/item"; // PATCH/DELETE (campaignItemId query param)

        // Public: promotions discovery + pricing
        public static final String PROMOTIONS_ACTIVE = "/api/promotions/active"; // GET
        public static final String PROMOTION_PRODUCTS = "/api/promotions/products"; // GET (campaignId query param)
    }

    /**
     * Stable response codes for ApiResponse.code.
     * Keep these consistent so the frontend can map to popups/notifications.
     */
    public static final class Codes {
        private Codes() {}

        // Generic
        public static final String OK = "OK";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String DB_CONNECTION_FAILED = "DB_CONNECTION_FAILED";
        public static final String DB_ERROR = "DB_ERROR";
        public static final String SERVER_ERROR = "SERVER_ERROR";

        // Campaign lifecycle
        public static final String CAMPAIGN_CREATED = "CAMPAIGN_CREATED";
        public static final String CAMPAIGN_UPDATED = "CAMPAIGN_UPDATED";
        public static final String CAMPAIGN_DELETED = "CAMPAIGN_DELETED";
        public static final String CAMPAIGN_TERMINATED = "CAMPAIGN_TERMINATED";
        public static final String CAMPAIGN_NOT_FOUND = "CAMPAIGN_NOT_FOUND";

        // Items
        public static final String CAMPAIGN_ITEM_ADDED = "CAMPAIGN_ITEM_ADDED";
        public static final String CAMPAIGN_ITEM_UPDATED = "CAMPAIGN_ITEM_UPDATED";
        public static final String CAMPAIGN_ITEM_DELETED = "CAMPAIGN_ITEM_DELETED";
        public static final String CAMPAIGN_ITEM_NOT_FOUND = "CAMPAIGN_ITEM_NOT_FOUND";
        public static final String DUPLICATE_CAMPAIGN_PRODUCT = "DUPLICATE_CAMPAIGN_PRODUCT";

        // Promotions pricing / constraints
        public static final String INVALID_PRODUCT_ID = "INVALID_PRODUCT_ID";
        public static final String INVALID_DISCOUNT_RATE = "INVALID_DISCOUNT_RATE";
        public static final String INVALID_CAMPAIGN_DATES = "INVALID_CAMPAIGN_DATES";
        public static final String PROMOTION_CONFLICT = "PROMOTION_CONFLICT";
    }

    // ----------------------------
    // DTOs (request/response JSON)
    // ----------------------------

    // Campaign CRUD
    public static final class CreateCampaignRequest {
        public String campaignName;
        public String startDatetime; // ISO-8601 string, parsed server-side
        public String endDatetime;   // ISO-8601 string, parsed server-side
        public String createdBy;     // AdminUsers.Username (FK)
        public String status;        // optional: SCHEDULED | ACTIVE | ENDED | TERMINATED
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
        public String campaignName;  // optional
        public String startDatetime; // optional
        public String endDatetime;   // optional
        public String status;        // optional
    }

    public static final class DeleteCampaignRequest {
        public Integer campaignId;
    }

    public static final class TerminateCampaignRequest {
        public Integer campaignId;
    }

    // Campaign items
    public static final class AddCampaignItemRequest {
        public Integer campaignId;
        public String productId;     // ipos_ca.stock_items.item_id
        public Double discountRate;  // 0..100
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
        public Double discountRate; // 0..100
    }

    public static final class DeleteCampaignItemRequest {
        public Integer campaignItemId;
    }

    // Public listing + pricing view
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

