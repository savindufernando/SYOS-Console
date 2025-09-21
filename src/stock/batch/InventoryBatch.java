package stock.batch;

import java.time.LocalDate;

public class InventoryBatch {
    private int id;
    private int productId;
    private int quantity;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    // Getters
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
