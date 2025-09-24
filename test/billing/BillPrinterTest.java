package billing;

import billing.strategy.FixedDiscount;
import billing.strategy.NoDiscount;
import org.junit.jupiter.api.Test;
import stock.Product;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BillPrinterTest {

    // ✅ Dummy product for testing only
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    private String capturePrintedOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return out.toString();
    }

    @Test
    void testPrintsBillWithBasicDetails() {
        Bill bill = new Bill(101, "Alice");
        Product milk = new DummyProduct("P001", "Milk", 50.0);

        bill.addItem(new BillItem(milk, 2, new NoDiscount()));
        bill.finalizePayment(200.0);

        BillPrinter printer = new BillPrinter();
        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("SYOS BILL"));
        assertTrue(output.contains("Cashier: Alice"));
        assertTrue(output.contains("Transaction: COUNTER"));
        assertTrue(output.contains("Milk"));
        assertTrue(output.contains("Subtotal"));
        assertTrue(output.contains("Final Total"));
        assertTrue(output.contains("Change: 100.00")); // 200 - 100
    }

    @Test
    void testPrintsBillWithDiscountDetails() {
        Bill bill = new Bill(102, "Bob");
        Product rice = new DummyProduct("P002", "Rice", 100.0);

        // Apply fixed discount 20 per unit → 2 * 80 = 160
        bill.addItem(new BillItem(rice, 2, new FixedDiscount(20)));
        bill.finalizePayment(200.0);

        BillPrinter printer = new BillPrinter();
        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("Rice"));
        assertTrue(output.contains("After Discount")); // ensures discount section printed
        assertTrue(output.contains("Subtotal (before discounts): 200.00"));
        assertTrue(output.contains("Total Discounts Applied: -40.00"));
        assertTrue(output.contains("Final Total (after discounts): 160.00"));
        assertTrue(output.contains("Change: 40.00"));
    }

    @Test
    void testPrintHandlesEmptyBill() {
        Bill bill = new Bill(103, "Charlie");
        bill.finalizePayment(100.0);

        BillPrinter printer = new BillPrinter();
        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("Subtotal (before discounts): 0.00"));
        assertTrue(output.contains("Final Total (after discounts): 0.00"));
        assertTrue(output.contains("Cash Tendered: 100.00"));
        assertTrue(output.contains("Change: 100.00"));
    }

    @Test
    void testBillNumberFormatCounter() {
        Bill bill = new Bill(104, "Diana");
        bill.setBillSerial(5); // force serial to 5

        String billNumber = bill.generateBillNumber("SYOS");
        String today = LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        assertTrue(
                billNumber.startsWith("SYOS-" + today + "-005-CT"),
                "Bill number format should be SYOS-YYYYMMDD-XXX-CT but got: " + billNumber
        );
    }

    @Test
    void testBillNumberFormatOnline() {
        Bill onlineBill = new Bill(201); // customer-based constructor → ONLINE
        onlineBill.setBillSerial(7); // force serial to 7

        String billNumber = onlineBill.generateBillNumber("SYOS");
        String today = LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        assertTrue(
                billNumber.startsWith("SYOS-" + today + "-007-ON"),
                "Bill number format should be SYOS-YYYYMMDD-XXX-ON but got: " + billNumber
        );
    }
}
