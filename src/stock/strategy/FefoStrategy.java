package stock.strategy;

import stock.batch.InventoryBatch;
import java.util.Comparator;
import java.util.List;

public class FefoStrategy implements StockReductionStrategy {
    @Override
    public void sortInventoryBatches(List<InventoryBatch> batches) {
        batches.sort(
                Comparator
                        // null expiry dates go last
                        .comparing(InventoryBatch::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        // tie-breaker: purchase date (earliest first)
                        .thenComparing(InventoryBatch::getPurchaseDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );
    }
}
