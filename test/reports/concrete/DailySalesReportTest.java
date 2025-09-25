package reports.concrete;

import billing.Bill;
import billing.BillItem;
import billing.repositories.BillRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reports.models.DailySalesRecord;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DailySalesReportTest {

    private DailySalesReport report;
    private BillRepository mockRepo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Replace System.out to capture output
        System.setOut(new PrintStream(outContent));

        // Create mock repo
        mockRepo = Mockito.mock(BillRepository.class);

        // Inject mock repo via subclass (because original uses new BillRepositoryImpl())
        report = new DailySalesReport() {
            {
                // Override billRepo with mock
                try {
                    var field = DailySalesReport.class.getDeclaredField("billRepo");
                    field.setAccessible(true);
                    field.set(this, mockRepo);
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
    void fetchData_ShouldAggregateSalesCorrectly() {
        // Arrange: Create fake bill with 2 items
        BillItem item1 = mock(BillItem.class);
        when(item1.getProduct()).thenReturn(new FakeProduct("P001", "Apples"));
        when(item1.getQuantity()).thenReturn(3);
        when(item1.getLineTotal()).thenReturn(30.0);

        BillItem item2 = mock(BillItem.class);
        when(item2.getProduct()).thenReturn(new FakeProduct("P001", "Apples"));
        when(item2.getQuantity()).thenReturn(2);
        when(item2.getLineTotal()).thenReturn(20.0);

        Bill bill = mock(Bill.class);
        when(bill.getItems()).thenReturn(List.of(item1, item2));

        when(mockRepo.findByDate(any())).thenReturn(List.of(bill));

        // Act
        report.fetchData(LocalDate.now(), LocalDate.now());

        // Capture console output via processData
        report.processData();
        String output = outContent.toString();

        // Assert
        assertTrue(output.contains("P001"));
        assertTrue(output.contains("Apples"));
        assertTrue(output.contains("5"));      // 3+2 qty
        assertTrue(output.contains("50.00"));  // 30+20 revenue
    }

    @Test
    void fetchData_ShouldHandleNoBills() {
        when(mockRepo.findByDate(any())).thenReturn(List.of());

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();
        String output = outContent.toString().trim();

        // ✅ Header row should always be present
        assertTrue(output.contains("Code"));
        assertTrue(output.contains("Revenue"));

        // ✅ But no product codes or quantities should appear
        // (since no records were added)
        assertFalse(output.matches("(?s).*\\n.*\\d+.*"),
                "There should be no data rows after the header");
    }


    @Test
    void setType_ShouldDefaultToAll_WhenNullOrBlank() {
        report.setType(null);
        assertEquals("ALL", getSelectedType());

        report.setType("  ");
        assertEquals("ALL", getSelectedType());
    }

    @Test
    void setType_ShouldConvertToUpperCase() {
        report.setType("online");
        assertEquals("ONLINE", getSelectedType());
    }

    // Helper to peek at private field
    private String getSelectedType() {
        try {
            var field = DailySalesReport.class.getDeclaredField("selectedType");
            field.setAccessible(true);
            return (String) field.get(report);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- Fake Product for mocking ---
    static class FakeProduct extends stock.Product {
        private final String code;
        private final String name;

        public FakeProduct(String code, String name) {
            super(code, name, 10.0); // assuming constructor exists
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() { return code; }
        @Override
        public String getName() { return name; }
    }
}
