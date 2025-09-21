package stock.batch;

import java.time.LocalDate;

public class ShelfBatch {
    private int id;
    private int productId;
    private int quantity;
    private LocalDate lastRestocked;

    // Extra fields for reporting
    private String productCode;
    private String productName;

    // --- Getters ---
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public LocalDate getLastRestocked() { return lastRestocked; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLastRestocked(LocalDate lastRestocked) { this.lastRestocked = lastRestocked; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public void setProductName(String productName) { this.productName = productName; }
}
