package stock;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductFactory.
 */
class ProductFactoryTest {

    @Test
    void shouldCreatePerishableProduct() {
        Product product = ProductFactory.createProduct(
                "PERISHABLE", "P001", "Milk", 120.0, LocalDate.of(2024, 12, 31));

        assertNotNull(product);
        assertTrue(product instanceof PerishableProduct);
        assertEquals("P001", product.getCode());
        assertEquals("Milk", product.getName());
        assertEquals(120.0, product.getUnitPrice());
    }

    @Test
    void shouldCreateNonPerishableProduct() {
        Product product = ProductFactory.createProduct(
                "NONPERISHABLE", "NP001", "Soap", 50.0, null);

        assertNotNull(product);
        assertTrue(product instanceof NonPerishableProduct);
        assertEquals("NP001", product.getCode());
        assertEquals("Soap", product.getName());
        assertEquals(50.0, product.getUnitPrice());
    }

    @Test
    void shouldDefaultToNonPerishableIfTypeIsUnknown() {
        Product product = ProductFactory.createProduct(
                "UNKNOWN", "X001", "Test", 10.0, null);

        assertNotNull(product);
        assertTrue(product instanceof NonPerishableProduct);
    }
}
