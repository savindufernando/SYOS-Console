package billing.repositories;

import billing.BillItem;
import java.util.List;

public interface BillItemRepository {
    void save(int billId, BillItem item);

    // ✅ add this
    List<BillItem> findByBillId(int billId);
}
