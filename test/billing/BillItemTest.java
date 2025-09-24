package billing;

import billing.strategy.FixedDiscount;
import billing.strategy.NoDiscount;
import billing.strategy.PercentageDiscount;
import org.junit.jupiter.api.Test;
import stock.Product;

import static org.junit.jupiter.api.Assertions.*;

class BillItemTest {

    // ✅ Dummy subclass only for testing
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    @Test
    void testLineTotalWithNoDiscount() {
        Product product = new DummyProduct("P001", "Milk", 50.0);
        BillItem item = new BillItem(product, 2);

        assertEquals(100.0, item.getLineTotal(), 0.01);
        assertEquals("NONE", item.getDiscountType());
        assertEquals(0.0, item.getDiscountValue(), 0.01);
    }

    @Test
    void testLineTotalWithPercentageDiscount() {
        Product product = new DummyProduct("P002", "Bread", 40.0);
        BillItem item = new BillItem(product, 2, new PercentageDiscount(25));

        assertEquals(60.0, item.getLineTotal(), 0.01); // 40 → 30 after discount → 30*2
        assertEquals("PERCENTAGE", item.getDiscountType());
        assertEquals(25.0, item.getDiscountValue(), 0.01);
    }

    @Test
    void testLineTotalWithFixedDiscount() {
        Product product = new DummyProduct("P003", "Rice", 100.0);
        BillItem item = new BillItem(product, 3, new FixedDiscount(20));

        assertEquals(240.0, item.getLineTotal(), 0.01); // 100 → 80 → 80*3
        assertEquals("AMOUNT", item.getDiscountType());
        assertEquals(20.0, item.getDiscountValue(), 0.01);
    }

    @Test
    void testChangeQuantityRecalculatesTotal() {
        Product product = new DummyProduct("P004", "Sugar", 30.0);
        BillItem item = new BillItem(product, 1);

        assertEquals(30.0, item.getLineTotal(), 0.01);

        item.setQuantity(4);
        assertEquals(120.0, item.getLineTotal(), 0.01);
    }

    @Test
    void testChangeDiscountRecalculatesTotalAndMetadata() {
        Product product = new DummyProduct("P005", "Tea", 50.0);
        BillItem item = new BillItem(product, 2);

        assertEquals(100.0, item.getLineTotal(), 0.01);
        assertEquals("NONE", item.getDiscountType());

        item.setDiscount(new PercentageDiscount(10));
        assertEquals(90.0, item.getLineTotal(), 0.01);
        assertEquals("PERCENTAGE", item.getDiscountType());
        assertEquals(10.0, item.getDiscountValue(), 0.01);
    }

    @Test
    void testZeroQuantityMakesLineTotalZero() {
        Product product = new DummyProduct("P006", "Eggs", 15.0);
        BillItem item = new BillItem(product, 0);

        assertEquals(0.0, item.getLineTotal(), 0.01);
    }
}
