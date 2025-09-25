package billing;

import auth.User;
import billing.strategy.FixedDiscount;
import billing.strategy.NoDiscount;
import db.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.Product;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BillPrinter.
 */
class BillPrinterTest {

    // ✅ Dummy product for testing
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    private UserRepository userRepo;
    private BillPrinter printer;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        printer = new BillPrinter(userRepo);
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
        Bill bill = new Bill(101, "COUNTER");
        bill.setUserId(1);

        // ✅ Mock a User instead of constructing directly
        User fakeUser = mock(User.class);
        when(fakeUser.getName()).thenReturn("Alice");
        when(userRepo.findById(1)).thenReturn(fakeUser);

        Product milk = new DummyProduct("P001", "Milk", 50.0);
        bill.addItem(new BillItem(milk, 2, new NoDiscount()));
        bill.finalizePayment(200.0);

        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("SYOS BILL"));
        assertTrue(output.contains("User: Alice"));
        assertTrue(output.contains("Transaction: COUNTER"));
        assertTrue(output.contains("Milk"));
        assertTrue(output.contains("Subtotal (before discounts)"));
        assertTrue(output.contains("FINAL TOTAL"));
        assertTrue(output.contains("Change:"));
    }

    @Test
    void testPrintsBillWithDiscountDetails() {
        Bill bill = new Bill(102, "COUNTER");
        bill.setUserId(2);

        User fakeUser = mock(User.class);
        when(fakeUser.getName()).thenReturn("Bob");
        when(userRepo.findById(2)).thenReturn(fakeUser);

        Product rice = new DummyProduct("P002", "Rice", 100.0);

        // Apply fixed discount 20 per unit → 2 * (100-20) = 160
        bill.addItem(new BillItem(rice, 2, new FixedDiscount(20)));
        bill.finalizePayment(200.0);

        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("Rice"));
        assertTrue(output.contains("Discount")); // ensure discount line present
        assertTrue(output.contains("FINAL TOTAL: LKR"));
        assertTrue(output.contains("Change:"));
    }

    @Test
    void testPrintHandlesEmptyBill() {
        Bill bill = new Bill(103, "COUNTER");
        bill.setUserId(3);

        User fakeUser = mock(User.class);
        when(fakeUser.getName()).thenReturn("Charlie");
        when(userRepo.findById(3)).thenReturn(fakeUser);

        bill.finalizePayment(100.0);

        String output = capturePrintedOutput(() -> printer.print(bill));

        assertTrue(output.contains("Subtotal (before discounts): LKR 0.00"));
        assertTrue(output.contains("FINAL TOTAL: LKR"));
        assertTrue(output.contains("Cash Tendered:"));
        assertTrue(output.contains("Change:"));
    }

    @Test
    void testBillNumberFormatCounter() {
        Bill bill = new Bill(104, "COUNTER");
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
        Bill onlineBill = new Bill(201); // ✅ Use the ONLINE constructor
        onlineBill.setBillSerial(7);

        String billNumber = onlineBill.generateBillNumber("SYOS");
        String today = LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        assertTrue(
                billNumber.startsWith("SYOS-" + today + "-007-ON"),
                "Bill number format should be SYOS-YYYYMMDD-XXX-ON but got: " + billNumber
        );
    }

}
