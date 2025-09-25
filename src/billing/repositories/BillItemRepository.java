package billing.repositories;

import billing.BillItem;
import java.util.List;

public interface BillItemRepository {
    void save(int billId, BillItem item);

    List<BillItem> findByBillId(int billId);
}
