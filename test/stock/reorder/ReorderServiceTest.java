package stock.reorder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReorderService.
 */
class ReorderServiceTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private ReorderService reorderService;

    @BeforeEach
    void setUp() {
        reorderService = new ReorderService();

        // Redirect System.out to capture console output
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void shouldPrintAlertMessageWhenLowStock() {
        reorderService.notifyLowStock("P001", 10);

        String output = outContent.toString().trim();
        assertTrue(output.contains("📦 ALERT: Product P001 is low in inventory! Total left = 10"));
    }

    @Test
    void shouldHandleZeroQuantity() {
        reorderService.notifyLowStock("P002", 0);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Total left = 0"));
    }

    @Test
    void shouldHandleNegativeQuantity() {
        reorderService.notifyLowStock("P003", -5);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Total left = -5"));
    }
}
