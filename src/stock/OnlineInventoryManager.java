package stock;

import stock.batch.OnlineBatch;
import stock.repositories.OnlineBatchRepository;
import java.util.List;

public class OnlineInventoryManager {
    private final OnlineBatchRepository onlineRepo;

    public OnlineInventoryManager(OnlineBatchRepository onlineRepo) {
        this.onlineRepo = onlineRepo;
    }

    public void reduceOnlineStock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("❌ Quantity must be greater than 0");
        }

        OnlineBatch online = onlineRepo.findByProduct(productId);
        if (online == null) {
            throw new RuntimeException("❌ No online stock for product " + productId);
        }

        if (online.getQuantity() < quantity) {
            throw new RuntimeException("⚠️ Not enough online stock for product " + productId);
        }

        online.setQuantity(online.getQuantity() - quantity);
        onlineRepo.update(online);

        System.out.println("✅ Reduced online stock by " + quantity +
                " → Remaining: " + online.getQuantity());
    }

}
