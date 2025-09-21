package stock.repositories;

import stock.batch.OnlineBatch;
import java.util.List;

public interface OnlineBatchRepository {
    void save(OnlineBatch batch);
    void update(OnlineBatch batch);
    OnlineBatch findByProduct(int productId);
    List<OnlineBatch> findAll();
}
