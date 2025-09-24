package billing;

import billing.repositories.BillItemRepository;
import billing.repositories.BillRepository;
import billing.strategy.FixedDiscount;
import billing.strategy.NoDiscount;
import billing.strategy.PercentageDiscount;
import db.repositories.DiscountRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.Product;
import stock.ShelfManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {

    // ✅ Dummy product for testing only
    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    // Dependencies (mocked)
    private BillRepository billRepo;
    private BillItemRepository itemRepo;
    private DiscountRepositoryImpl discountRepo;
    private ShelfManager shelfManager;

    // System under test
    private CheckoutService checkoutService;

    @BeforeEach
    void setup() {
        billRepo = mock(BillRepository.class);
        itemRepo = mock(BillItemRepository.class);
        discountRepo = mock(DiscountRepositoryImpl.class);
        shelfManager = mock(ShelfManager.class);

        // Always return billId = 1
        when(billRepo.save(any(Bill.class))).thenReturn(1);

        // Default: no discount
        when(discountRepo.findActiveDiscountForProduct(any())).thenReturn(new NoDiscount());

        // Create service but inject mocks using reflection
        checkoutService = new CheckoutService() {
            {
                try {
                    java.lang.reflect.Field billRepoField = CheckoutService.class.getDeclaredField("billRepo");
                    billRepoField.setAccessible(true);
                    billRepoField.set(this, billRepo);

                    java.lang.reflect.Field itemRepoField = CheckoutService.class.getDeclaredField("itemRepo");
                    itemRepoField.setAccessible(true);
                    itemRepoField.set(this, itemRepo);

                    java.lang.reflect.Field discountRepoField = CheckoutService.class.getDeclaredField("discountRepo");
                    discountRepoField.setAccessible(true);
                    discountRepoField.set(this, discountRepo);

                    java.lang.reflect.Field shelfManagerField = CheckoutService.class.getDeclaredField("shelfManager");
                    shelfManagerField.setAccessible(true);
                    shelfManagerField.set(this, shelfManager);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // ───────────── BASIC FLOW TESTS ─────────────

    @Test
    void testStartBillSetsCounterTransaction() {
        checkoutService.startBill(101, "Alice");
        Bill bill = checkoutService.getCurrentBill();

        assertEquals("COUNTER", bill.getTransactionType());
        assertEquals("Alice", bill.getCashierName());
        assertEquals(101, bill.getCashierId());
    }

    @Test
    void testStartBillOnlineTransaction() {
        Bill onlineBill = new Bill(202); // customer
        assertEquals("ONLINE", onlineBill.getTransactionType());
        assertEquals(202, onlineBill.getCustomerId());
    }

    @Test
    void testAddProductUpdatesTotal() {
        checkoutService.startBill(101, "Alice");
        checkoutService.addProduct(new DummyProduct("P001", "Milk", 50.0), 2);

        assertEquals(100.0, checkoutService.getCurrentBill().getTotalAmount(), 0.01);
    }

    // ───────────── DISCOUNT HANDLING ─────────────

    @Test
    void testPercentageDiscountApplied() {
        Product juice = new DummyProduct("PX", "Juice", 100.0);
        when(discountRepo.findActiveDiscountForProduct(juice))
                .thenReturn(new PercentageDiscount(10));

        checkoutService.startBill(101, "Alice");
        checkoutService.addProduct(juice, 2);

        assertEquals(180.0, checkoutService.getCurrentBill().getTotalAmount(), 0.01);
    }

    @Test
    void testFixedDiscountApplied() {
        Product rice = new DummyProduct("PY", "Rice", 50.0);
        when(discountRepo.findActiveDiscountForProduct(rice))
                .thenReturn(new FixedDiscount(5));

        checkoutService.startBill(102, "Bob");
        checkoutService.addProduct(rice, 3); // unit price → 45, total = 135

        assertEquals(135.0, checkoutService.getCurrentBill().getTotalAmount(), 0.01);
    }

    // ───────────── REMOVE PRODUCT ─────────────

    @Test
    void testRemoveProductRemovesFromBill() {
        checkoutService.startBill(101, "Alice");
        Product bread = new DummyProduct("P002", "Bread", 40.0);

        checkoutService.addProduct(bread, 2);

        // get the actual item from the bill
        BillItem itemInBill = checkoutService.getCurrentBill().getItems().get(0);
        checkoutService.removeProduct(itemInBill);

        assertEquals(0.0, checkoutService.getCurrentBill().getTotalAmount(), 0.01);
    }


    @Test
    void testRemoveProductWithoutActiveBillThrows() {
        assertThrows(IllegalStateException.class, () -> {
            checkoutService.removeProduct(
                    new BillItem(new DummyProduct("X", "Fake", 10.0), 1, new NoDiscount())
            );
        });
    }

    // ───────────── FINALIZE BILL ─────────────

    @Test
    void testFinalizeBillSetsBillIdAndChange() {
        checkoutService.startBill(101, "Bob");
        checkoutService.addProduct(new DummyProduct("P003", "Rice", 30.0), 3);

        checkoutService.finalizeBill(200.0);

        Bill bill = checkoutService.getCurrentBill();
        assertEquals(90.0, bill.getTotalAmount(), 0.01);
        assertEquals(110.0, bill.getChangeAmount(), 0.01);
        assertEquals(1, bill.getBillId()); // mocked repo returned 1
    }

    @Test
    void testFinalizeBillSavesBillAndItemsAndReducesStock() {
        checkoutService.startBill(101, "Bob");
        Product oil = new DummyProduct("P004", "Oil", 60.0);
        checkoutService.addProduct(oil, 1);

        checkoutService.finalizeBill(100.0);

        verify(billRepo, times(1)).save(any(Bill.class));
        verify(itemRepo, times(1)).save(anyInt(), any(BillItem.class));
        verify(shelfManager, times(1)).reduceFromShelf("P004", 1);
    }

    // ───────────── EDGE CASES ─────────────

    @Test
    void testAddProductWithoutStartingBillThrows() {
        CheckoutService freshService = new CheckoutService();
        Product milk = new DummyProduct("P001", "Milk", 50.0);

        assertThrows(NullPointerException.class, () -> freshService.addProduct(milk, 2));
    }

    @Test
    void testFinalizeBillWithoutItems() {
        checkoutService.startBill(101, "Alice");
        checkoutService.finalizeBill(100.0);

        assertEquals(0.0, checkoutService.getCurrentBill().getTotalAmount(), 0.01);
        assertEquals(100.0, checkoutService.getCurrentBill().getChangeAmount(), 0.01);
    }

    @Test
    void testCashTenderedExactAmountResultsInZeroChange() {
        checkoutService.startBill(101, "Alice");
        checkoutService.addProduct(new DummyProduct("P006", "Tea", 25.0), 4); // total = 100

        checkoutService.finalizeBill(100.0);

        assertEquals(0.0, checkoutService.getCurrentBill().getChangeAmount(), 0.01);
    }

    @Test
    void testCashTenderedLessThanTotalResultsInNegativeChange() {
        checkoutService.startBill(101, "Alice");
        checkoutService.addProduct(new DummyProduct("P007", "Coffee", 80.0), 2); // total = 160

        checkoutService.finalizeBill(100.0);

        assertTrue(checkoutService.getCurrentBill().getChangeAmount() < 0);
    }
}
