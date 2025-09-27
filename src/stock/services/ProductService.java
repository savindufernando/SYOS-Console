package stock.services;

import stock.Product;
import stock.repositories.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    //  Add Product
    public void addProduct(Product product) {
        repo.save(product);
    }

    //  Edit Product
    public void editProduct(Product product) {
        repo.update(product);
    }

    //  Delete Product by ID
    public void removeProduct(int id) {
        repo.delete(id);
    }

    //  Delete Product by Code
    public void removeProductByCode(String code) {
        repo.deleteByCode(code);
    }

    //  Find
    public Product getProductById(int id) {
        return repo.findById(id);
    }

    public Product getProductByCode(String code) {
        return repo.findByCode(code);
    }

    // Search
    public List<Product> searchProducts(String keyword) {
        return repo.search(keyword);
    }

    // List
    public List<Product> listAllProducts() {
        return repo.findAll();
    }
}
