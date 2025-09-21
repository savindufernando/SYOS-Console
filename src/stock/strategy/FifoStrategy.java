package stock.strategy;

import stock.batch.InventoryBatch;
import java.util.Comparator;
import java.util.List;

public class FifoStrategy implements StockReductionStrategy {
    @Override
    public void sortInventoryBatches(List<InventoryBatch> batches) {
        batches.sort(Comparator.comparing(InventoryBatch::getPurchaseDate));
    }
}
