package stock.reorder;

public class ReorderService implements ReorderObserver {
    @Override
    public void notifyLowStock(String productCode, int currentQty) {
        System.out.println("📦 ALERT: Product " + productCode +
                " is low in inventory! Total left = " + currentQty);
    }
}
