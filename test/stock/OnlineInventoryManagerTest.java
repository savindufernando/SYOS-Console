package stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import stock.batch.OnlineBatch;
import stock.repositories.OnlineBatchRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnlineInventoryManagerTest {

    private OnlineBatchRepository onlineRepo;
    private OnlineInventoryManager manager;

    @BeforeEach
    void setUp() {
        onlineRepo = Mockito.mock(OnlineBatchRepository.class);
        manager = new OnlineInventoryManager(onlineRepo);
    }

    private OnlineBatch createBatch(int productId, int quantity, LocalDate lastRestocked) {
        OnlineBatch batch = new OnlineBatch();
        batch.setProductId(productId);
        batch.setQuantity(quantity);
        batch.setLastRestocked(lastRestocked);
        return batch;
    }

    @Test
    void testReduceOnlineStock_Success() {
        OnlineBatch batch = createBatch(1, 20, LocalDate.of(2025, 9, 20));
        when(onlineRepo.findByProduct(1)).thenReturn(batch);

        manager.reduceOnlineStock(1, 5);

        assertEquals(15, batch.getQuantity());
        assertEquals(LocalDate.of(2025, 9, 20), batch.getLastRestocked());
        verify(onlineRepo).update(batch);
    }

    @Test
    void testReduceOnlineStock_NoStockFound() {
        when(onlineRepo.findByProduct(99)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                manager.reduceOnlineStock(99, 5));

        assertTrue(ex.getMessage().contains("No online stock for product"));
        verify(onlineRepo, never()).update(any());
    }

    @Test
    void testReduceOnlineStock_NotEnoughStock() {
        OnlineBatch batch = createBatch(2, 3, LocalDate.of(2025, 9, 21));
        when(onlineRepo.findByProduct(2)).thenReturn(batch);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                manager.reduceOnlineStock(2, 5));

        assertTrue(ex.getMessage().contains("Not enough online stock"));
        assertEquals(3, batch.getQuantity());
        verify(onlineRepo, never()).update(any());
    }

    @Test
    void testReduceOnlineStock_ExactStock() {
        OnlineBatch batch = createBatch(3, 10, LocalDate.now());
        when(onlineRepo.findByProduct(3)).thenReturn(batch);

        manager.reduceOnlineStock(3, 10);

        assertEquals(0, batch.getQuantity());
        assertEquals(LocalDate.now(), batch.getLastRestocked());
        verify(onlineRepo).update(batch);
    }

    // 🔹 NEW TESTS 🔹

    @Test
    void testMultipleSequentialReductions() {
        OnlineBatch batch = createBatch(4, 30, LocalDate.now());
        when(onlineRepo.findByProduct(4)).thenReturn(batch);

        manager.reduceOnlineStock(4, 10); // first reduce
        manager.reduceOnlineStock(4, 5);  // second reduce

        assertEquals(15, batch.getQuantity()); // 30 - 10 - 5
        verify(onlineRepo, times(2)).update(batch);
    }

    @Test
    void testReduceOnlineStock_NegativeQuantity() {
        OnlineBatch batch = createBatch(5, 20, LocalDate.now());
        when(onlineRepo.findByProduct(5)).thenReturn(batch);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                manager.reduceOnlineStock(5, -5));

        assertTrue(ex.getMessage().contains("Quantity must be greater than 0"));
        assertEquals(20, batch.getQuantity()); // unchanged
        verify(onlineRepo, never()).update(any());
    }


    @Test
    void testReduceOnlineStock_ZeroQuantity() {
        OnlineBatch batch = createBatch(6, 25, LocalDate.now());
        when(onlineRepo.findByProduct(6)).thenReturn(batch);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                manager.reduceOnlineStock(6, 0));

        assertTrue(ex.getMessage().contains("Quantity must be greater than 0"));
        assertEquals(25, batch.getQuantity()); // unchanged
        verify(onlineRepo, never()).update(any());
    }


    @Test
    void testReduceOnlineStock_VeryLargeQuantity() {
        OnlineBatch batch = createBatch(7, 40, LocalDate.now());
        when(onlineRepo.findByProduct(7)).thenReturn(batch);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                manager.reduceOnlineStock(7, Integer.MAX_VALUE));

        assertTrue(ex.getMessage().contains("Not enough online stock"));
        assertEquals(40, batch.getQuantity()); // unchanged
        verify(onlineRepo, never()).update(any());
    }
}
