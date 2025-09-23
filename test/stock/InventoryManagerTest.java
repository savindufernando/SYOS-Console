package stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import stock.batch.InventoryBatch;
import stock.batch.ShelfBatch;
import stock.batch.OnlineBatch;
import stock.repositories.InventoryBatchRepository;
import stock.repositories.ShelfBatchRepository;
import stock.repositories.OnlineBatchRepository;
import stock.strategy.StockReductionStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Extended unit tests for InventoryManager.
 */
class InventoryManagerTest {

    private InventoryBatchRepository inventoryRepo;
    private ShelfBatchRepository shelfRepo;
    private OnlineBatchRepository onlineRepo;
    private StockReductionStrategy strategy;
    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        inventoryRepo = Mockito.mock(InventoryBatchRepository.class);
        shelfRepo = Mockito.mock(ShelfBatchRepository.class);
        onlineRepo = Mockito.mock(OnlineBatchRepository.class);
        strategy = Mockito.mock(StockReductionStrategy.class);

        manager = new InventoryManager(inventoryRepo, shelfRepo, onlineRepo, strategy);
    }

    // ---------- ADD INVENTORY ----------

    @Test
    void shouldAddInventoryBatch() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(10);

        manager.addInventoryBatch(batch);

        verify(inventoryRepo).save(batch);
    }

    @Test
    void shouldAllowZeroQuantityBatch() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(0);

        manager.addInventoryBatch(batch);

        verify(inventoryRepo).save(batch);
    }

    // ---------- GETTERS ----------

    @Test
    void shouldReturnInventoryBatches() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(5);

        when(inventoryRepo.findByProduct(1)).thenReturn(List.of(batch));

        List<InventoryBatch> result = manager.getInventoryBatches(1);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getQuantity());
    }

    @Test
    void shouldReturnEmptyListWhenNoInventoryBatches() {
        when(inventoryRepo.findByProduct(99)).thenReturn(List.of());

        List<InventoryBatch> result = manager.getInventoryBatches(99);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnShelfStock() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(20);

        when(shelfRepo.findByProduct(1)).thenReturn(shelf);

        ShelfBatch result = manager.getShelfStock(1);

        assertEquals(20, result.getQuantity());
    }

    @Test
    void shouldReturnNullIfShelfStockNotFound() {
        when(shelfRepo.findByProduct(1)).thenReturn(null);

        ShelfBatch result = manager.getShelfStock(1);

        assertNull(result);
    }

    // ---------- RESTOCK SHELF ----------

    @Test
    void shouldRestockShelfWithAvailableInventory() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(10);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(shelfRepo.findByProduct(1)).thenReturn(null);

        manager.restockShelf(1, 5);

        assertEquals(5, batch.getQuantity()); // reduced
        verify(inventoryRepo).update(batch);
        verify(shelfRepo).save(any(ShelfBatch.class));
    }

    @Test
    void shouldUpdateExistingShelfWhenRestocking() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(8);

        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(2);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(shelfRepo.findByProduct(1)).thenReturn(shelf);

        manager.restockShelf(1, 5);

        assertEquals(3, batch.getQuantity());
        assertEquals(7, shelf.getQuantity());
        verify(shelfRepo).update(shelf);
    }

    @Test
    void shouldNotRestockShelfWhenNoInventory() {
        when(inventoryRepo.findByProduct(1)).thenReturn(List.of());

        manager.restockShelf(1, 5);

        verifyNoInteractions(shelfRepo);
    }

    @Test
    void shouldPartiallyRestockWhenNotEnoughInventory() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(3);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(shelfRepo.findByProduct(1)).thenReturn(null);

        manager.restockShelf(1, 5);

        assertEquals(0, batch.getQuantity()); // used up
        verify(inventoryRepo).update(batch);
        verify(shelfRepo).save(any(ShelfBatch.class));
    }

    // ---------- RESTOCK ONLINE ----------

    @Test
    void shouldRestockOnlineWithAvailableInventory() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(12);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(onlineRepo.findByProduct(1)).thenReturn(null);

        manager.restockOnline(1, 10);

        assertEquals(2, batch.getQuantity());
        verify(inventoryRepo).update(batch);
        verify(onlineRepo).save(any(OnlineBatch.class));
    }

    @Test
    void shouldUpdateExistingOnlineBatchWhenRestocking() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(10);

        OnlineBatch online = new OnlineBatch();
        online.setProductId(1);
        online.setQuantity(3);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(onlineRepo.findByProduct(1)).thenReturn(online);

        manager.restockOnline(1, 5);

        assertEquals(5, batch.getQuantity());
        assertEquals(8, online.getQuantity());
        verify(onlineRepo).update(online);
    }

    @Test
    void shouldNotRestockOnlineWhenNoInventory() {
        when(inventoryRepo.findByProduct(1)).thenReturn(List.of());

        manager.restockOnline(1, 5);

        verifyNoInteractions(onlineRepo);
    }

    @Test
    void shouldPartiallyRestockOnlineWhenNotEnoughInventory() {
        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(1);
        batch.setQuantity(2);

        when(inventoryRepo.findByProduct(1)).thenReturn(new ArrayList<>(List.of(batch)));
        when(onlineRepo.findByProduct(1)).thenReturn(null);

        manager.restockOnline(1, 5);

        assertEquals(0, batch.getQuantity());
        verify(inventoryRepo).update(batch);
        verify(onlineRepo).save(any(OnlineBatch.class));
    }
}
