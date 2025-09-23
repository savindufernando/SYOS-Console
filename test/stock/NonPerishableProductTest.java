package stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NonPerishableProduct.
 */
class NonPerishableProductTest {

    @Test
    void shouldCreateNonPerishableProduct() {
        NonPerishableProduct product = new NonPerishableProduct("NP001", "Soap", 50.0);

        assertEquals("NP001", product.getCode());
        assertEquals("Soap", product.getName());
        assertEquals(50.0, product.getUnitPrice());
    }

    @Test
    void clonedNonPerishableProductShouldRemainIndependent() {
        NonPerishableProduct product = new NonPerishableProduct("NP002", "Book", 200.0);
        product.setId(1);

        Product cloned = product.clone();
        cloned.setId(2);

        assertEquals(1, product.getId());
        assertEquals(2, cloned.getId());
        assertTrue(cloned instanceof NonPerishableProduct);
    }
}
