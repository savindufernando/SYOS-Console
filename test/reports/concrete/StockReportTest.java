package reports.concrete;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reports.models.StockBatchRecord;
import stock.Product;
import stock.batch.InventoryBatch;
import stock.repositories.InventoryBatchRepository;
import stock.repositories.ProductRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockReportTest {

    private StockReport report;
    private InventoryBatchRepository mockInvRepo;
    private ProductRepository mockProductRepo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        mockInvRepo = Mockito.mock(InventoryBatchRepository.class);
        mockProductRepo = Mockito.mock(ProductRepository.class);

        // Inject mocks via reflection
        report = new StockReport() {
            {
                try {
                    var invField = StockReport.class.getDeclaredField("invRepo");
                    invField.setAccessible(true);
                    invField.set(this, mockInvRepo);

                    var prodField = StockReport.class.getDeclaredField("productRepo");
                    prodField.setAccessible(true);
                    prodField.set(this, mockProductRepo);
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
    void fetchData_ShouldIncludeSingleBatch() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setPurchaseDate(LocalDate.of(2025, 1, 10));
        batch.setExpiryDate(LocalDate.of(2025, 2, 10));
        batch.setQuantity(40);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch));
        when(mockProductRepo.findById(1)).thenReturn(new FakeProduct("P001", "Sugar"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P001"));
        assertTrue(output.contains("2025-01-10"));
        assertTrue(output.contains("2025-02-10"));
        assertTrue(output.contains("40"));
    }

    @Test
    void fetchData_ShouldHandleMultipleBatches() {
        InventoryBatch batch1 = new InventoryBatch();
        batch1.setProductId(1);
        batch1.setPurchaseDate(LocalDate.of(2025, 1, 1));
        batch1.setExpiryDate(LocalDate.of(2025, 2, 1));
        batch1.setQuantity(20);

        InventoryBatch batch2 = new InventoryBatch();
        batch2.setProductId(2);
        batch2.setPurchaseDate(LocalDate.of(2025, 1, 5));
        batch2.setExpiryDate(LocalDate.of(2025, 2, 5));
        batch2.setQuantity(30);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch1, batch2));
        when(mockProductRepo.findById(1)).thenReturn(new FakeProduct("P001", "Rice"));
        when(mockProductRepo.findById(2)).thenReturn(new FakeProduct("P002", "Flour"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P001"));
        assertTrue(output.contains("P002"));
        assertTrue(output.contains("20"));
        assertTrue(output.contains("30"));
    }

    @Test
    void fetchData_ShouldHandleBatchWithoutExpiryDate() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(3);
        batch.setPurchaseDate(LocalDate.of(2025, 1, 15));
        batch.setExpiryDate(null);
        batch.setQuantity(10);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch));
        when(mockProductRepo.findById(3)).thenReturn(new FakeProduct("P003", "Oil"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P003"));
        assertTrue(output.contains("2025-01-15"));
        assertTrue(output.contains("-")); // expiry missing should be printed as "-"
    }

    @Test
    void fetchData_ShouldHandleNoBatches() {
        when(mockInvRepo.findAll()).thenReturn(List.of());

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString().trim();

        // Only header expected
        assertTrue(output.contains("Code"));
        assertTrue(output.contains("Purchase Date"));
        assertTrue(output.contains("Remaining Qty"));
        assertFalse(output.matches("(?s).*\\n.*\\d+.*"),
                "No data rows should be printed after header");
    }

    // --- Fake Product for mocking ---
    static class FakeProduct extends Product {
        private final String code;
        private final String name;

        public FakeProduct(String code, String name) {
            super(code, name, 10.0);
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() { return code; }
        @Override
        public String getName() { return name; }
    }
}
