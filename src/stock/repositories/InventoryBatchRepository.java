package stock.repositories;

import stock.batch.InventoryBatch;
import java.util.List;

public interface InventoryBatchRepository {
    void save(InventoryBatch batch);
    void update(InventoryBatch batch);
    List<InventoryBatch> findByProduct(int productId);

    List<InventoryBatch> findAll();
}
