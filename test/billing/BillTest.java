package billing;

import billing.strategy.PercentageDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.Product;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BillTest {

    // ✅ Dummy subclass for testing only
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    private Bill bill;

    @BeforeEach
    void setup() {
        bill = new Bill(); // use default constructor
    }

    @Test
    void testEmptyBillHasZeroTotal() {
        assertEquals(0.0, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testAddItemsAndTotalCalculation() {
        Product milk = new DummyProduct("P001", "Milk", 50.0);
        Product bread = new DummyProduct("P002", "Bread", 30.0);

        bill.addItem(new BillItem(milk, 2));
        bill.addItem(new BillItem(bread, 1));

        assertEquals(130.0, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testAddDuplicateItemsAccumulatesTotal() {
        Product milk = new DummyProduct("P001", "Milk", 50.0);

        bill.addItem(new BillItem(milk, 1));
        bill.addItem(new BillItem(milk, 2));

        assertEquals(150.0, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testItemDiscountReducesTotal() {
        Product bread = new DummyProduct("P002", "Bread", 40.0);

        bill.addItem(new BillItem(bread, 2, new PercentageDiscount(20))); // 20% off → 32*2=64
        assertEquals(64.0, bill.getTotalAmount(), 0.01);
    }

    @Test
    void testCashTenderedAndChange() {
        Product milk = new DummyProduct("P001", "Milk", 50.0);
        bill.addItem(new BillItem(milk, 2));

        bill.finalizePayment(120.0); // ✅ use finalizePayment()

        assertEquals(100.0, bill.getTotalAmount(), 0.01);
        assertEquals(20.0, bill.getChangeAmount(), 0.01);
    }

    @Test
    void testInsufficientCashHandledGracefully() {
        Product bread = new DummyProduct("P002", "Bread", 40.0);
        bill.addItem(new BillItem(bread, 2));

        bill.finalizePayment(30.0);

        assertTrue(bill.getChangeAmount() < 0,
                "Expected negative change when cash tendered is insufficient");
    }

    @Test
    void testBillDateIsSet() {
        assertNotNull(bill.getBillDate());
        assertTrue(bill.getBillDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testBillIdSetAndGet() {
        bill.setBillId(101);
        assertEquals(101, bill.getBillId());
    }

    @Test
    void testBillSerialAndNumberGeneration() {
        bill.setBillSerial(5);
        String billNumber = bill.generateBillNumber("SYOS");
        assertTrue(billNumber.startsWith("SYOS-"));
        assertTrue(billNumber.contains("-005-"));
    }
}
