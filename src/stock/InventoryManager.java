package stock;

import stock.batch.InventoryBatch;
import stock.batch.ShelfBatch;
import stock.batch.OnlineBatch;
import stock.repositories.InventoryBatchRepository;
import stock.repositories.ShelfBatchRepository;
import stock.repositories.OnlineBatchRepository;
import stock.strategy.StockReductionStrategy;

import java.time.LocalDate;
import java.util.List;

public class InventoryManager {
    private final InventoryBatchRepository inventoryRepo;
    private final ShelfBatchRepository shelfRepo;
    private final OnlineBatchRepository onlineRepo;
    private static final int REORDER_THRESHOLD = 50;
    private StockReductionStrategy strategy;

    public InventoryManager(InventoryBatchRepository inventoryRepo,
                            ShelfBatchRepository shelfRepo,
                            OnlineBatchRepository onlineRepo,   // ✅ added
                            StockReductionStrategy strategy) {
        this.inventoryRepo = inventoryRepo;
        this.shelfRepo = shelfRepo;
        this.onlineRepo = onlineRepo;   // ✅ set it
        this.strategy = strategy;
    }

    // 🔹 Add a new inventory batch
    public void addInventoryBatch(InventoryBatch batch) {
        inventoryRepo.save(batch);
    }

    // 🔹 View all inventory batches for a product
    public List<InventoryBatch> getInventoryBatches(int productId) {
        return inventoryRepo.findByProduct(productId);
    }

    // 🔹 Get shelf stock for a product
    public ShelfBatch getShelfStock(int productId) {
        return shelfRepo.findByProduct(productId);
    }

    // 🔹 Move stock from inventory → shelf (FIFO/FEFO)
    public void restockShelf(int productId, int quantity) {
        List<InventoryBatch> invBatches = inventoryRepo.findByProduct(productId);
        if (invBatches == null || invBatches.isEmpty()) {
            System.out.println("⚠️ No inventory available for product " + productId);
            return;
        }

        strategy.sortInventoryBatches(invBatches);

        int remaining = quantity;

        for (InventoryBatch inv : invBatches) {
            if (remaining <= 0) break;
            int available = inv.getQuantity();
            if (available <= 0) continue;

            int move = Math.min(available, remaining);

            // Deduct from inventory batch
            inv.setQuantity(available - move);
            inventoryRepo.update(inv);

            // Update or create shelf stock
            ShelfBatch shelf = shelfRepo.findByProduct(productId);
            if (shelf == null) {
                shelf = new ShelfBatch();
                shelf.setProductId(productId);
                shelf.setQuantity(move);
                shelf.setLastRestocked(LocalDate.now());
                shelfRepo.save(shelf);
            } else {
                shelf.setQuantity(shelf.getQuantity() + move);
                shelf.setLastRestocked(LocalDate.now());
                shelfRepo.update(shelf);
            }

            remaining -= move;
        }

        if (remaining > 0) {
            System.out.println("⚠️ Could not move full quantity. Missing " + remaining + " units from inventory.");
        } else {
            System.out.println("✅ Successfully restocked SHELF with " + quantity + " units.");
        }
    }

    // 🔹 Move stock from inventory → online batches
    public void restockOnline(int productId, int quantity) {
        List<InventoryBatch> invBatches = inventoryRepo.findByProduct(productId);
        if (invBatches == null || invBatches.isEmpty()) {
            System.out.println("⚠️ No inventory available for product " + productId);
            return;
        }

        strategy.sortInventoryBatches(invBatches);

        int remaining = quantity;

        for (InventoryBatch inv : invBatches) {
            if (remaining <= 0) break;

            int available = inv.getQuantity();
            if (available <= 0) continue;

            int move = Math.min(available, remaining);

            // Deduct from inventory batch
            inv.setQuantity(available - move);
            inventoryRepo.update(inv);

            // Update or create online stock
            OnlineBatch online = onlineRepo.findByProduct(productId);
            if (online == null) {
                online = new OnlineBatch();
                online.setProductId(productId);
                online.setQuantity(move);
                online.setLastRestocked(LocalDate.now());
                onlineRepo.save(online);
            } else {
                online.setQuantity(online.getQuantity() + move);
                online.setLastRestocked(LocalDate.now());
                onlineRepo.update(online);
            }

            remaining -= move;
        }

        if (remaining > 0) {
            System.out.println("⚠️ Could not move full quantity. Missing " + remaining + " units from inventory.");
        } else {
            System.out.println("✅ Successfully restocked ONLINE stock with " + quantity + " units.");
        }
    }

}
