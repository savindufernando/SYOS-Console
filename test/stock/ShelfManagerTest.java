package stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShelfManager using JUnit 5 + Mockito.
 */
class ShelfManagerTest {

    private ShelfBatchRepository shelfRepo;
    private ShelfManager shelfManager;

    @BeforeEach
    void setUp() {
        shelfRepo = Mockito.mock(ShelfBatchRepository.class);
        shelfManager = new ShelfManager(shelfRepo);
    }

    // ---------- reduceShelfStock (by productId) ----------

    @Test
    void shouldReduceShelfStockWhenEnoughAvailable() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(10);

        when(shelfRepo.findByProduct(1)).thenReturn(shelf);

        shelfManager.reduceShelfStock(1, 5);

        assertEquals(5, shelf.getQuantity());
        verify(shelfRepo).update(shelf);
    }

    @Test
    void shouldThrowWhenShelfStockNotFoundById() {
        when(shelfRepo.findByProduct(1)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> shelfManager.reduceShelfStock(1, 5));
    }

    @Test
    void shouldThrowWhenNotEnoughShelfStockById() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(3);

        when(shelfRepo.findByProduct(1)).thenReturn(shelf);

        assertThrows(RuntimeException.class,
                () -> shelfManager.reduceShelfStock(1, 5));
    }

    @Test
    void shouldAllowExactReductionById() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(5);

        when(shelfRepo.findByProduct(1)).thenReturn(shelf);

        shelfManager.reduceShelfStock(1, 5);

        assertEquals(0, shelf.getQuantity());
        verify(shelfRepo).update(shelf);
    }

    // ---------- reduceFromShelf (by productCode) ----------

    @Test
    void shouldReduceFromShelfWhenEnoughAvailable() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(8);

        when(shelfRepo.findByProductCode("P001")).thenReturn(shelf);

        shelfManager.reduceFromShelf("P001", 3);

        assertEquals(5, shelf.getQuantity());
        verify(shelfRepo).update(shelf);
    }

    @Test
    void shouldThrowWhenShelfStockNotFoundByCode() {
        when(shelfRepo.findByProductCode("P001")).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> shelfManager.reduceFromShelf("P001", 2));
    }

    @Test
    void shouldThrowWhenNotEnoughShelfStockByCode() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(1);

        when(shelfRepo.findByProductCode("P001")).thenReturn(shelf);

        assertThrows(RuntimeException.class,
                () -> shelfManager.reduceFromShelf("P001", 5));
    }

    @Test
    void shouldAllowExactReductionByCode() {
        ShelfBatch shelf = new ShelfBatch();
        shelf.setProductId(1);
        shelf.setQuantity(4);

        when(shelfRepo.findByProductCode("P001")).thenReturn(shelf);

        shelfManager.reduceFromShelf("P001", 4);

        assertEquals(0, shelf.getQuantity());
        verify(shelfRepo).update(shelf);
    }
}
