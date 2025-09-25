package reports.concrete;

import billing.Bill;
import billing.BillItem;
import billing.repositories.BillRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reports.models.BillRecord;
import stock.Product;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillReportTest {

    private BillReport report;
    private BillRepository mockBillRepo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        mockBillRepo = Mockito.mock(BillRepository.class);

        // Inject mock repo using reflection
        report = new BillReport() {
            {
                try {
                    var field = BillReport.class.getDeclaredField("billRepo");
                    field.setAccessible(true);
                    field.set(this, mockBillRepo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outContent.reset();
    }

    @Test
    void fetchData_ShouldIncludeSingleBill() {
        Bill bill = new Bill();
        bill.setBillId(1);
        bill.setBillDate(LocalDateTime.of(2025, 1, 10, 12, 0));
        bill.setTotalAmount(100.0);
        bill.setCashTendered(120.0);
        bill.setChangeAmount(20.0);
        bill.setTransactionType("INSTORE");

        BillItem item = new BillItem(new FakeProduct("P001", "Sugar", 50.0), 2, 100.0);
        bill.setItems(List.of(item));

        when(mockBillRepo.findBetweenDates(any(), any())).thenReturn(List.of(bill));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2025-01-10"));
        assertTrue(output.contains("Sugar(2)"));
        assertTrue(output.contains("100.00"));
        assertTrue(output.contains("120.00"));
        assertTrue(output.contains("20.00"));
        assertTrue(output.contains("INSTORE"));
    }

    @Test
    void fetchData_ShouldIncludeMultipleBills() {
        Bill bill1 = new Bill();
        bill1.setBillId(1);
        bill1.setBillDate(LocalDateTime.of(2025, 1, 5, 10, 0));
        bill1.setTotalAmount(50.0);
        bill1.setCashTendered(60.0);
        bill1.setChangeAmount(10.0);
        bill1.setTransactionType("ONLINE");

        Bill bill2 = new Bill();
        bill2.setBillId(2);
        bill2.setBillDate(LocalDateTime.of(2025, 1, 6, 15, 0));
        bill2.setTotalAmount(75.0);
        bill2.setCashTendered(100.0);
        bill2.setChangeAmount(25.0);
        bill2.setTransactionType("INSTORE");

        when(mockBillRepo.findBetweenDates(any(), any())).thenReturn(List.of(bill1, bill2));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("ONLINE"));
        assertTrue(output.contains("INSTORE"));
        assertTrue(output.contains("50.00"));
        assertTrue(output.contains("75.00"));
    }

    @Test
    void fetchData_ShouldHandleNoBills() {
        when(mockBillRepo.findBetweenDates(any(), any())).thenReturn(List.of());

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString().trim();

        // Only header expected
        assertTrue(output.contains("No"));
        assertTrue(output.contains("Date"));
        assertTrue(output.contains("Items"));
        assertTrue(output.contains("Total"));
        assertFalse(output.matches("(?s).*\\n.*\\d+.*"),
                "No data rows should be printed after header");
    }

    @Test
    void fetchData_ShouldConcatenateMultipleItems() {
        Bill bill = new Bill();
        bill.setBillId(10);
        bill.setBillDate(LocalDateTime.of(2025, 1, 15, 18, 0));
        bill.setTotalAmount(200.0);
        bill.setCashTendered(250.0);
        bill.setChangeAmount(50.0);
        bill.setTransactionType("INSTORE");

        BillItem item1 = new BillItem(new FakeProduct("P001", "Rice", 100.0), 1, 100.0);
        BillItem item2 = new BillItem(new FakeProduct("P002", "Milk", 50.0), 2, 100.0);
        bill.setItems(List.of(item1, item2));

        when(mockBillRepo.findBetweenDates(any(), any())).thenReturn(List.of(bill));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("Rice(1)"));
        assertTrue(output.contains("Milk(2)"));
    }

    // --- Fake Product for BillItem ---
    static class FakeProduct extends Product {
        private final String code;
        private final String name;

        public FakeProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() { return code; }
        @Override
        public String getName() { return name; }
    }
}
