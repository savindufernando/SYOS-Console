package billing;

import billing.repositories.BillItemRepository;
import billing.repositories.BillRepository;
import billing.strategy.DiscountPolicy;

import db.repositories.BillItemRepositoryImpl;
import db.repositories.BillRepositoryImpl;
import db.repositories.ShelfBatchRepositoryImpl;
import db.repositories.DiscountRepositoryImpl;

import stock.ShelfManager;
import stock.Product;

public class CheckoutService {
    private final BillRepository billRepo = new BillRepositoryImpl();
    private final BillItemRepository itemRepo = new BillItemRepositoryImpl();
    private final ShelfManager shelfManager = new ShelfManager(new ShelfBatchRepositoryImpl());
    private final DiscountRepositoryImpl discountRepo = new DiscountRepositoryImpl();


    private Bill currentBill;

    public void startBill(int userId, String cashierName) {
        currentBill = new Bill(userId, cashierName);
    }

    public void addProduct(Product product, int quantity) {
        DiscountPolicy discount = discountRepo.findActiveDiscountForProduct(product);
        BillItem item = new BillItem(product, quantity, discount);
        currentBill.addItem(item);
    }

    // 🔹 NEW: remove item from the current bill
    public void removeProduct(BillItem item) {
        if (currentBill != null) {
            currentBill.removeItem(item);
        } else {
            throw new IllegalStateException("❌ No active bill to remove item from.");
        }
    }

    public void finalizeBill(double cashTendered) {
        currentBill.finalizePayment(cashTendered);

        int billId = billRepo.save(currentBill);
        currentBill.setBillId(billId);

        for (BillItem item : currentBill.getItems()) {
            itemRepo.save(billId, item);
            shelfManager.reduceFromShelf(item.getProduct().getCode(), item.getQuantity());
        }
    }

    public Bill getCurrentBill() {
        return currentBill;
    }
}

