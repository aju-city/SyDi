package model;

/**
 * Model class representing a product retrieved from ipos_ca.
 */
public class Product {

    private String itemId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private int stockLimit;

    public Product() {}

    public Product(String itemId, String name, String description,
                   double price, int quantity, int stockLimit) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.stockLimit = stockLimit;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockLimit() { return stockLimit; }
    public void setStockLimit(int stockLimit) { this.stockLimit = stockLimit; }
}
