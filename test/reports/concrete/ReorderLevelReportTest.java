package reports.concrete;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reports.models.ReorderRecord;
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

class ReorderLevelReportTest {

    private ReorderLevelReport report;
    private InventoryBatchRepository mockInvRepo;
    private ProductRepository mockProductRepo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        mockInvRepo = Mockito.mock(InventoryBatchRepository.class);
        mockProductRepo = Mockito.mock(ProductRepository.class);

        // Inject mocks into report instance (override with reflection)
        report = new ReorderLevelReport() {
            {
                try {
                    var invField = ReorderLevelReport.class.getDeclaredField("invRepo");
                    invField.setAccessible(true);
                    invField.set(this, mockInvRepo);

                    var prodField = ReorderLevelReport.class.getDeclaredField("productRepo");
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
    void fetchData_ShouldIncludeProduct_WhenStockBelow50() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(30);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch));
        when(mockProductRepo.findById(1)).thenReturn(new FakeProduct("P001", "Bananas"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P001"));
        assertTrue(output.contains("Bananas"));
        assertTrue(output.contains("30"));
    }

    @Test
    void fetchData_ShouldExcludeProduct_WhenStockAtLeast50() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(2);
        batch.setQuantity(60);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch));
        when(mockProductRepo.findById(2)).thenReturn(new FakeProduct("P002", "Oranges"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        // Should only contain header, no Oranges
        assertTrue(output.contains("Code"));
        assertFalse(output.contains("P002"));
        assertFalse(output.contains("Oranges"));
    }

    @Test
    void fetchData_ShouldAggregateStockAcrossBatches() {
        InventoryBatch batch1 = new InventoryBatch();
        batch1.setProductId(3);
        batch1.setQuantity(20);

        InventoryBatch batch2 = new InventoryBatch();
        batch2.setProductId(3);
        batch2.setQuantity(25);

        when(mockInvRepo.findAll()).thenReturn(List.of(batch1, batch2));
        when(mockProductRepo.findById(3)).thenReturn(new FakeProduct("P003", "Apples"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P003"));
        assertTrue(output.contains("Apples"));
        assertTrue(output.contains("45")); // 20+25
    }

    @Test
    void fetchData_ShouldHandleNoBatches() {
        when(mockInvRepo.findAll()).thenReturn(List.of());

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString().trim();

        // Only header expected
        assertTrue(output.contains("Code"));
        assertTrue(output.contains("Item Name"));
        assertTrue(output.contains("Stock"));
        assertFalse(output.matches("(?s).*\\n.*\\d+.*"),
                "No data rows should be printed after header");
    }

    // --- Fake Product for testing ---
    static class FakeProduct extends Product {
        private final String code;
        private final String name;

        public FakeProduct(String code, String name) {
            super(code, name, 10.0); // assuming Product has (code,name,price) constructor
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() { return code; }
        @Override
        public String getName() { return name; }
    }
}
