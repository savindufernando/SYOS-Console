package stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductPrototype.
 */
class ProductPrototypeTest {

    @Test
    void shouldCloneProductUsingPrototype() {
        Product prototype = new NonPerishableProduct("NP100", "Soap", 50.0);
        ProductPrototype proto = new ProductPrototype(prototype);

        Product cloned = proto.cloneProduct();

        assertNotNull(cloned);
        assertNotSame(prototype, cloned);
        assertEquals(prototype.getCode(), cloned.getCode());
        assertEquals(prototype.getName(), cloned.getName());
    }

    @Test
    void clonedProductShouldBeIndependentOfPrototype() {
        Product prototype = new PerishableProduct("P200", "Yogurt", 75.0, null);
        prototype.setId(10);

        ProductPrototype proto = new ProductPrototype(prototype);
        Product cloned = proto.cloneProduct();
        cloned.setId(20);

        assertEquals(10, prototype.getId());
        assertEquals(20, cloned.getId());
    }
}
