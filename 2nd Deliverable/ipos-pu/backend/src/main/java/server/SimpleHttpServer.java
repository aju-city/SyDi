package server;

import com.sun.net.httpserver.HttpServer;
import controller.*;

import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Register endpoints
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

        server.createContext("/api/order/validateStock", new ValidateCartStockHandler());

        // Promotions (Functionality 15)
        server.createContext("/api/promotions/campaign/create", new AdminPromotionCreateHandler());
        server.createContext("/api/promotions/campaign/add-item", new AdminPromotionAddItemHandler());
        server.createContext("/api/promotions/campaign/update", new AdminPromotionUpdateCampaignHandler());
        server.createContext("/api/promotions/campaign/update-item", new AdminPromotionUpdateItemHandler());
        server.createContext("/api/promotions/campaign/delete", new AdminPromotionDeleteHandler());
        server.createContext("/api/promotions/campaign/terminate", new AdminPromotionTerminateHandler());
        server.createContext("/api/promotions/campaign/all", new AdminPromotionListAllHandler());
        server.createContext("/api/promotions/campaign/items", new AdminPromotionListItemsHandler());

        server.createContext("/api/promotions/active", new PromotionsActiveHandler());
        server.createContext("/api/promotions/products", new PromotionProductsHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("Server running on http://localhost:8080/");
    }
}