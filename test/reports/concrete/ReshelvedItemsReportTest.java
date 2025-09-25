package reports.concrete;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reports.models.ReshelvedRecord;
import stock.Product;
import stock.batch.ShelfBatch;
import stock.repositories.ProductRepository;
import stock.repositories.ShelfBatchRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReshelvedItemsReportTest {

    private ReshelvedItemsReport report;
    private ShelfBatchRepository mockShelfRepo;
    private ProductRepository mockProductRepo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        mockShelfRepo = Mockito.mock(ShelfBatchRepository.class);
        mockProductRepo = Mockito.mock(ProductRepository.class);

        // Inject mocks into report using reflection
        report = new ReshelvedItemsReport() {
            {
                try {
                    var shelfField = ReshelvedItemsReport.class.getDeclaredField("shelfRepo");
                    shelfField.setAccessible(true);
                    shelfField.set(this, mockShelfRepo);

                    var prodField = ReshelvedItemsReport.class.getDeclaredField("productRepo");
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
    void fetchData_ShouldIncludeSingleReshelvedItem() {
        ShelfBatch batch = new ShelfBatch();
        batch.setProductId(1);
        batch.setQuantity(15);

        when(mockShelfRepo.findByDate(any())).thenReturn(List.of(batch));
        when(mockProductRepo.findById(1)).thenReturn(new FakeProduct("P001", "Rice"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("P001"));
        assertTrue(output.contains("Rice"));
        assertTrue(output.contains("15"));
    }

    @Test
    void fetchData_ShouldIncludeMultipleItems() {
        ShelfBatch batch1 = new ShelfBatch();
        batch1.setProductId(1);
        batch1.setQuantity(10);

        ShelfBatch batch2 = new ShelfBatch();
        batch2.setProductId(2);
        batch2.setQuantity(20);

        when(mockShelfRepo.findByDate(any())).thenReturn(List.of(batch1, batch2));
        when(mockProductRepo.findById(1)).thenReturn(new FakeProduct("P001", "Bread"));
        when(mockProductRepo.findById(2)).thenReturn(new FakeProduct("P002", "Milk"));

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString();
        assertTrue(output.contains("Bread"));
        assertTrue(output.contains("Milk"));
        assertTrue(output.contains("10"));
        assertTrue(output.contains("20"));
    }

    @Test
    void fetchData_ShouldHandleNoReshelvedItems() {
        when(mockShelfRepo.findByDate(any())).thenReturn(List.of());

        report.fetchData(LocalDate.now(), LocalDate.now());
        report.processData();

        String output = outContent.toString().trim();

        // Only header row expected
        assertTrue(output.contains("Code"));
        assertTrue(output.contains("Item Name"));
        assertTrue(output.contains("Reshelved Qty"));
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
