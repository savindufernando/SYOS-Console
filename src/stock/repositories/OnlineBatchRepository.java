package stock.repositories;

import stock.batch.OnlineBatch;
import java.util.List;

public interface OnlineBatchRepository {
    void save(OnlineBatch batch);
    void update(OnlineBatch batch);
    OnlineBatch findByProduct(int productId);
    List<OnlineBatch> findAll();

    // new helpers
    List<OnlineBatch> findAvailable(); // only >0 quantity
    boolean reduceStock(int productId, int qty);
}
