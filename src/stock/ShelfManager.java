package stock;

import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;

public class ShelfManager {
    private final ShelfBatchRepository shelfRepo;

    public ShelfManager(ShelfBatchRepository shelfRepo) {
        this.shelfRepo = shelfRepo;
    }

    // 🔹 Generic stock reduction (used in stock mgmt / reshelving)
    public void reduceShelfStock(int productId, int quantity) {
        ShelfBatch shelf = shelfRepo.findByProduct(productId);
        if (shelf == null || shelf.getQuantity() < quantity) {
            throw new RuntimeException("!! Not enough shelf stock for product " + productId);
        }
        shelf.setQuantity(shelf.getQuantity() - quantity);
        shelfRepo.update(shelf);
    }

    // 🔹 Billing-specific reduction (used at checkout)
    public void reduceFromShelf(String productCode, int quantity) {
        ShelfBatch shelf = shelfRepo.findByProductCode(productCode);
        if (shelf == null || shelf.getQuantity() < quantity) {
            throw new RuntimeException("!! Not enough shelf stock for product " + productCode);
        }
        shelf.setQuantity(shelf.getQuantity() - quantity);
        shelfRepo.update(shelf);
        System.out.println("✔ Reduced " + quantity + " of product [" + productCode + "] from shelf.");
    }
}
