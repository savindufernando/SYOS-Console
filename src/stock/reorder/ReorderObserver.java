package stock.reorder;

public interface ReorderObserver {
    void notifyLowStock(String productCode, int currentQty);
}
