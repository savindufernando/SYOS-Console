package stock;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PerishableProduct.
 */
class PerishableProductTest {

    @Test
    void shouldCreatePerishableProductWithExpiry() {
        LocalDate expiry = LocalDate.of(2024, 12, 31);
        PerishableProduct product = new PerishableProduct("P001", "Milk", 120.0, expiry);

        assertEquals("P001", product.getCode());
        assertEquals("Milk", product.getName());
        assertEquals(120.0, product.getUnitPrice());
        assertEquals(expiry, product.getExpiryDate());
    }

    @Test
    void shouldUpdateExpiryDate() {
        PerishableProduct product = new PerishableProduct("P002", "Cheese", 200.0, null);
        LocalDate newExpiry = LocalDate.of(2025, 6, 1);

        product.setExpiryDate(newExpiry);

        assertEquals(newExpiry, product.getExpiryDate());
    }

    @Test
    void clonedPerishableProductShouldKeepExpiry() {
        LocalDate expiry = LocalDate.of(2024, 10, 15);
        PerishableProduct product = new PerishableProduct("P003", "Yogurt", 80.0, expiry);

        Product cloned = product.clone();

        assertTrue(cloned instanceof PerishableProduct);
        assertEquals(((PerishableProduct) cloned).getExpiryDate(), expiry);
    }
}
