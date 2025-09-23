package stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product abstract class via concrete subclasses.
 */
class ProductTest {

    @Test
    void shouldSetAndGetId() {
        Product product = new NonPerishableProduct("NP001", "Rice", 100.0);
        product.setId(10);

        assertEquals(10, product.getId());
    }

    @Test
    void shouldReturnCorrectFields() {
        Product product = new PerishableProduct("P001", "Apple", 80.0, null);

        assertEquals("P001", product.getCode());
        assertEquals("Apple", product.getName());
        assertEquals(80.0, product.getUnitPrice());
    }

    @Test
    void shouldCloneProductSuccessfully() {
        Product product = new NonPerishableProduct("NP002", "Book", 200.0);
        product.setId(5);

        Product cloned = product.clone();

        assertNotNull(cloned);
        assertNotSame(product, cloned);
        assertEquals(product.getCode(), cloned.getCode());
        assertEquals(product.getName(), cloned.getName());
        assertEquals(product.getUnitPrice(), cloned.getUnitPrice());
    }

    @Test
    void clonedProductShouldBeIndependent() {
        Product product = new PerishableProduct("P002", "Milk", 120.0, null);
        product.setId(1);

        Product cloned = product.clone();
        cloned.setId(2);

        assertEquals(1, product.getId());
        assertEquals(2, cloned.getId());
    }
}
