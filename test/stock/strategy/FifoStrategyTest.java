package stock.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.batch.InventoryBatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FifoStrategy.
 */
class FifoStrategyTest {

    private FifoStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new FifoStrategy();
    }

    @Test
    void shouldSortByPurchaseDateOldestFirst() {
        InventoryBatch older = new InventoryBatch();
        older.setProductId(1);
        older.setPurchaseDate(LocalDate.of(2023, 1, 1));

        InventoryBatch newer = new InventoryBatch();
        newer.setProductId(1);
        newer.setPurchaseDate(LocalDate.of(2024, 1, 1));

        List<InventoryBatch> batches = new ArrayList<>(List.of(newer, older));

        strategy.sortInventoryBatches(batches);

        assertEquals(older, batches.get(0));
        assertEquals(newer, batches.get(1));
    }

    @Test
    void shouldHandleSingleBatch() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setPurchaseDate(LocalDate.of(2024, 1, 1));

        List<InventoryBatch> batches = new ArrayList<>(List.of(batch));

        strategy.sortInventoryBatches(batches);

        assertEquals(batch, batches.get(0));
    }

    @Test
    void shouldNotFailWithEmptyList() {
        List<InventoryBatch> batches = new ArrayList<>(); // mutable empty list

        strategy.sortInventoryBatches(batches);

        assertTrue(batches.isEmpty());
    }
}
