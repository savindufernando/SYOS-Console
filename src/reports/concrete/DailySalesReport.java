package reports.concrete;

import reports.base.Report;
import reports.models.DailySalesRecord;
import billing.Bill;
import billing.repositories.BillRepository;
import billing.repositories.BillRepository;
import db.repositories.BillRepositoryImpl;

import java.time.LocalDate;
import java.util.*;

public class DailySalesReport extends Report {
    private final BillRepository billRepo = new BillRepositoryImpl();
    private final List<DailySalesRecord> records = new ArrayList<>();
    private String selectedType = "ALL"; // default


    public DailySalesReport() {
        this.title = "Daily Sales Report";
    }

    @Override
    protected void fetchData(LocalDate start, LocalDate end) {
        records.clear();
        LocalDate date = (start != null) ? start : LocalDate.now(); // ✅ fallback to today
        List<Bill> bills = billRepo.findByDate(date);

        Map<String, DailySalesRecord> map = new HashMap<>();
        for (Bill bill : bills) {
            bill.getItems().forEach(item -> {
                String code = item.getProduct().getCode();
                map.putIfAbsent(code, new DailySalesRecord(code, item.getProduct().getName(), 0, 0.0));
                DailySalesRecord rec = map.get(code);
                rec.quantitySold += item.getQuantity();
                rec.revenue += item.getLineTotal();
            });
        }
        records.addAll(map.values());
    }


    @Override
    protected void processData() {
        System.out.printf("%-10s %-20s %-15s %-10s%n", "Code", "Item Name", "Qty Sold", "Revenue");
        for (DailySalesRecord rec : records) {
            System.out.printf("%-10s %-20s %-15d %.2f%n",
                    rec.code, rec.name, rec.quantitySold, rec.revenue);
        }
    }

    public void setType(String type) {
        if (type == null || type.isBlank()) {
            this.selectedType = "ALL";
        } else {
            this.selectedType = type.toUpperCase();
        }
    }

}
