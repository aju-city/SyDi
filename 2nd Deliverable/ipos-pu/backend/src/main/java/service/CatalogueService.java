package service;

import dao.ProductDAO;
import model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for catalogue operations.
 */
public class CatalogueService {

    private final ProductDAO productDAO;

    public CatalogueService(Connection connection) {
        this.productDAO = new ProductDAO(connection);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProducts(String query) throws SQLException {
        return productDAO.searchProducts(query);
    }

    public Product getProductById(String id) throws SQLException {
        return productDAO.getProductById(id);
    }
}