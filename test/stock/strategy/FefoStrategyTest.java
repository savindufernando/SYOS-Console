package stock.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.batch.InventoryBatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FefoStrategy.
 */
class FefoStrategyTest {

    private FefoStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new FefoStrategy();
    }

    @Test
    void shouldSortByExpiryDateEarliestFirst() {
        InventoryBatch expiringSoon = new InventoryBatch();
        expiringSoon.setProductId(1);
        expiringSoon.setExpiryDate(LocalDate.of(2024, 1, 1));

        InventoryBatch expiringLater = new InventoryBatch();
        expiringLater.setProductId(1);
        expiringLater.setExpiryDate(LocalDate.of(2025, 1, 1));

        List<InventoryBatch> batches = new ArrayList<>(List.of(expiringLater, expiringSoon));

        strategy.sortInventoryBatches(batches);

        assertEquals(expiringSoon, batches.get(0));
        assertEquals(expiringLater, batches.get(1));
    }

    @Test
    void shouldHandleBatchesWithSameExpiry() {
        InventoryBatch batch1 = new InventoryBatch();
        batch1.setProductId(1);
        batch1.setExpiryDate(LocalDate.of(2025, 1, 1));

        InventoryBatch batch2 = new InventoryBatch();
        batch2.setProductId(2);
        batch2.setExpiryDate(LocalDate.of(2025, 1, 1));

        List<InventoryBatch> batches = new ArrayList<>(List.of(batch2, batch1));

        strategy.sortInventoryBatches(batches);

        assertTrue(batches.contains(batch1));
        assertTrue(batches.contains(batch2));
    }

    @Test
    void shouldNotFailWithEmptyList() {
        List<InventoryBatch> batches = new ArrayList<>(); // mutable empty list

        strategy.sortInventoryBatches(batches);

        assertTrue(batches.isEmpty());
    }
}
