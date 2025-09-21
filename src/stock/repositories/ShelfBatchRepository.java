package stock.repositories;

import stock.batch.ShelfBatch;
import java.time.LocalDate;
import java.util.List;

public interface ShelfBatchRepository {
    void save(ShelfBatch batch);
    void update(ShelfBatch batch);
    ShelfBatch findByProduct(int productId);
    List<ShelfBatch> findAll();

    // 🔹 With product details
    List<ShelfBatch> findAllWithProducts();

    // 🔹 Lookup by product code
    ShelfBatch findByProductCode(String productCode);

    // 🔹 Needed for ReshelvedItemsReport
    List<ShelfBatch> findByDate(LocalDate date);
}
