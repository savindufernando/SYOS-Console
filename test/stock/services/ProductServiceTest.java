package stock.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import stock.Product;
import stock.repositories.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository mockRepo;
    private ProductService service;
    private Product dummyProduct;

    // Dummy concrete implementation for testing
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    @BeforeEach
    void setUp() {
        mockRepo = Mockito.mock(ProductRepository.class);
        service = new ProductService(mockRepo);

        dummyProduct = new DummyProduct("P001", "Milk", 50.0);
    }

    @Test
    void addProduct_ShouldCallRepoSave() {
        service.addProduct(dummyProduct);
        verify(mockRepo, times(1)).save(dummyProduct);
    }

    @Test
    void editProduct_ShouldCallRepoUpdate() {
        service.editProduct(dummyProduct);
        verify(mockRepo, times(1)).update(dummyProduct);
    }

    @Test
    void removeProduct_ShouldCallRepoDeleteById() {
        service.removeProduct(101);
        verify(mockRepo, times(1)).delete(101);
    }

    @Test
    void removeProductByCode_ShouldCallRepoDeleteByCode() {
        service.removeProductByCode("P001");
        verify(mockRepo, times(1)).deleteByCode("P001");
    }

    @Test
    void getProductById_ShouldReturnProductFromRepo() {
        when(mockRepo.findById(101)).thenReturn(dummyProduct);

        Product result = service.getProductById(101);

        assertEquals(dummyProduct, result);
        verify(mockRepo, times(1)).findById(101);
    }

    @Test
    void getProductByCode_ShouldReturnProductFromRepo() {
        when(mockRepo.findByCode("P001")).thenReturn(dummyProduct);

        Product result = service.getProductByCode("P001");

        assertEquals(dummyProduct, result);
        verify(mockRepo, times(1)).findByCode("P001");
    }

    @Test
    void searchProducts_ShouldReturnMatchingList() {
        List<Product> expected = List.of(dummyProduct);
        when(mockRepo.search("Milk")).thenReturn(expected);

        List<Product> result = service.searchProducts("Milk");

        assertEquals(expected, result);
        verify(mockRepo, times(1)).search("Milk");
    }

    @Test
    void listAllProducts_ShouldReturnAllFromRepo() {
        List<Product> expected = List.of(dummyProduct);
        when(mockRepo.findAll()).thenReturn(expected);

        List<Product> result = service.listAllProducts();

        assertEquals(expected, result);
        verify(mockRepo, times(1)).findAll();
    }
}
