package server;

import com.sun.net.httpserver.HttpServer;
import controller.*;
import controller.ChangePasswordHandler;
import controller.LoginHandler;
import server.handlers.api.AdminPromotionCampaignHandler;
import server.handlers.api.AdminPromotionCampaignItemHandler;
import server.handlers.api.AdminPromotionCampaignItemsHandler;
import server.handlers.api.AdminPromotionCampaignsHandler;
import server.handlers.api.AdminPromotionTerminateHandler;
import server.handlers.api.PromotionProductsHandler;
import server.handlers.api.PromotionsActiveHandler;

import java.net.InetSocketAddress;

/**
 * Starts the main HTTP server and registers the application endpoints.
 */
public class SimpleHttpServer {

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/commercial-application",
                new CommercialApplicationHandler());

        server.createContext("/api/non-commercial/register",
                new RegisterNonCommercialMemberHandler());

        server.createContext("/api/login", new LoginHandler());

        server.createContext("/api/change-password", new ChangePasswordHandler());

        server.createContext("/api/cart/create-guest", new CreateGuestCartHandler());

        server.createContext("/api/cart/add", new AddToCartHandler());

        server.createContext("/api/cart/get", new GetCartHandler());

        server.createContext("/api/cart/remove", new RemoveCartHandler());

        server.createContext("/api/catalogue", new CatalogueController());

        server.createContext("/api/cart/clear", new ClearCartHandler());

        server.createContext("/api/activity/log", new LogActivityHandler());

        server.createContext("/api/checkout", new CheckoutHandler());

        server.createContext("/api/admin/promotions/campaigns", new AdminPromotionCampaignsHandler());
        server.createContext("/api/admin/promotions/campaign", new AdminPromotionCampaignHandler());
        server.createContext("/api/admin/promotions/campaign/items", new AdminPromotionCampaignItemsHandler());
        server.createContext("/api/admin/promotions/campaign/item", new AdminPromotionCampaignItemHandler());
        server.createContext("/api/admin/promotions/campaign/terminate", new AdminPromotionTerminateHandler());
        server.createContext("/api/promotions/active", new PromotionsActiveHandler());
        server.createContext("/api/promotions/products", new PromotionProductsHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server running on http://localhost:8080/");
    }
}