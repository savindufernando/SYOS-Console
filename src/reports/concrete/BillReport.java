package reports.concrete;

import reports.base.Report;
import reports.models.BillRecord;
import billing.Bill;
import billing.BillItem;
import billing.repositories.BillRepository;
import db.repositories.BillRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillReport extends Report {
    private final BillRepository billRepo = new BillRepositoryImpl();
    private final List<BillRecord> records = new ArrayList<>();

    public BillReport() {
        this.title = "Bill Report";
    }

    @Override
    protected void fetchData(LocalDate start, LocalDate end) {
        records.clear();
        LocalDate from = (start != null) ? start : LocalDate.now();
        LocalDate to   = (end != null) ? end : from;

        List<Bill> bills = billRepo.findBetweenDates(from, to);

        for (Bill bill : bills) {
            StringBuilder items = new StringBuilder();
            for (BillItem item : bill.getItems()) {
                items.append(item.getProduct().getName())
                        .append("(").append(item.getQuantity()).append("), ");
            }
            records.add(new BillRecord(
                    bill.getBillId(),
                    bill.getBillDate().toString(),
                    items.toString(),
                    bill.getTotalAmount(),
                    bill.getCashTendered(),
                    bill.getChangeAmount(),
                    bill.getTransactionType()
            ));
        }
    }


    @Override
    protected void processData() {
        System.out.printf("%-5s %-15s %-35s %-10s %-10s %-10s %-10s%n",
                "No", "Date", "Items", "Total", "Cash", "Change", "Type");
        for (BillRecord rec : records) {
            System.out.printf("%-5d %-15s %-35s %-10.2f %-10.2f %-10.2f %-10s%n",
                    rec.billNo, rec.date, rec.itemsBought, rec.total, rec.cash, rec.change, rec.type);
        }
    }
}
