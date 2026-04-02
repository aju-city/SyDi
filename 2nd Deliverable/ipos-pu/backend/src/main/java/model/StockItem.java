package model;

/**
 * Model class representing a stock item from IPOS_CA.
 */
public class StockItem {

    private String itemId;
    private String name;
    private String description;
    private String packageType;
    private String unit;
    private Integer unitsPerPack;
    private double price;
    private int quantity;
    private int stockLimit;

    public StockItem() {
    }

    public StockItem(String itemId, String name, String description, String packageType,
                     String unit, Integer unitsPerPack, double price, int quantity, int stockLimit) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.packageType = packageType;
        this.unit = unit;
        this.unitsPerPack = unitsPerPack;
        this.price = price;
        this.quantity = quantity;
        this.stockLimit = stockLimit;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getUnitsPerPack() {
        return unitsPerPack;
    }

    public void setUnitsPerPack(Integer unitsPerPack) {
        this.unitsPerPack = unitsPerPack;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getStockLimit() {
        return stockLimit;
    }

    public void setStockLimit(int stockLimit) {
        this.stockLimit = stockLimit;
    }
}