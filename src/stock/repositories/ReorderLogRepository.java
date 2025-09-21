package stock.repositories;

public interface ReorderLogRepository {
    void logReorder(int productId, int qty);
}
