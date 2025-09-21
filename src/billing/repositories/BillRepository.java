package billing.repositories;

import billing.Bill;
import java.time.LocalDate;
import java.util.List;

public interface BillRepository {
    int save(Bill bill);  // returns generated bill_id

    Bill findById(int billId);
    List<Bill> findRecent(int limit);
    List<Bill> findByDate(LocalDate date);
    List<Bill> findBetweenDates(LocalDate start, LocalDate end);
    List<Bill> findAll();
}
