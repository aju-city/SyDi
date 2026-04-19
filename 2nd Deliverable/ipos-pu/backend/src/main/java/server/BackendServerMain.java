package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.HealthHandler;
import server.handlers.api.AdminPromotionCampaignHandler;
import server.handlers.api.AdminPromotionCampaignItemHandler;
import server.handlers.api.AdminPromotionCampaignItemsHandler;
import server.handlers.api.AdminPromotionCampaignsHandler;
import server.handlers.api.AdminPromotionTerminateHandler;
import server.handlers.api.ChangePasswordHandler;
import server.handlers.api.CommercialApplicationHandler;
import server.handlers.api.LoginHandler;
import server.handlers.api.NonCommercialRegisterHandler;
import server.handlers.api.PromotionApi;
import server.handlers.api.PromotionProductsHandler;
import server.handlers.api.PromotionsActiveHandler;

import java.net.InetSocketAddress;

/**
 * Starts the API server and registers all available routes.
 */
public class BackendServerMain {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/health", new HealthHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/change-password", new ChangePasswordHandler());
        server.createContext("/api/non-commercial/register", new NonCommercialRegisterHandler());
        server.createContext("/api/commercial-application", new CommercialApplicationHandler());

        server.createContext(PromotionApi.Routes.ADMIN_CAMPAIGNS, new AdminPromotionCampaignsHandler());
        server.createContext(PromotionApi.Routes.ADMIN_CAMPAIGN, new AdminPromotionCampaignHandler());
        server.createContext(PromotionApi.Routes.ADMIN_CAMPAIGN_TERMINATE, new AdminPromotionTerminateHandler());
        server.createContext(PromotionApi.Routes.ADMIN_CAMPAIGN_ITEMS, new AdminPromotionCampaignItemsHandler());
        server.createContext(PromotionApi.Routes.ADMIN_CAMPAIGN_ITEM, new AdminPromotionCampaignItemHandler());
        server.createContext(PromotionApi.Routes.PROMOTIONS_ACTIVE, new PromotionsActiveHandler());
        server.createContext(PromotionApi.Routes.PROMOTION_PRODUCTS, new PromotionProductsHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server running on http://localhost:" + port + "/");
    }
}