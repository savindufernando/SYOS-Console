package stock.repositories;

import stock.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product product);
    Product findByCode(String code);
    void update(Product product);
    Product findById(int id);
    List<Product> findAll();
    List<Product> search(String keyword);
}
