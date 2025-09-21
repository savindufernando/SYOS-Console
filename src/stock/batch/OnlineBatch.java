package stock.batch;

import java.time.LocalDate;

public class OnlineBatch {
    private int id;
    private int productId;
    private int quantity;
    private LocalDate lastRestocked;

    // --- Getters ---
    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public LocalDate getLastRestocked() { return lastRestocked; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLastRestocked(LocalDate lastRestocked) { this.lastRestocked = lastRestocked; }
}
