package stock.strategy;

import stock.batch.InventoryBatch;
import java.util.List;

public interface StockReductionStrategy {
    void sortInventoryBatches(List<InventoryBatch> batches);
}
