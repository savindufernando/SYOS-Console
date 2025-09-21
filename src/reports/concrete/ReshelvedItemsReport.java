package reports.concrete;

import reports.base.Report;
import reports.models.ReshelvedRecord;
import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;
import db.repositories.ShelfBatchRepositoryImpl;
import stock.repositories.ProductRepository;
import db.repositories.ProductRepositoryImpl;

import java.time.LocalDate;
import java.util.*;

public class ReshelvedItemsReport extends Report {
    private final ShelfBatchRepository shelfRepo = new ShelfBatchRepositoryImpl();
    private final ProductRepository productRepo = new ProductRepositoryImpl();
    private final List<ReshelvedRecord> records = new ArrayList<>();

    public ReshelvedItemsReport() {
        this.title = "Reshelved Items Report";
    }

    @Override
    protected void fetchData(LocalDate start, LocalDate end) {
        records.clear();
        LocalDate date = (start != null) ? start : LocalDate.now(); // ✅ fallback to today
        List<ShelfBatch> batches = shelfRepo.findByDate(date);

        for (ShelfBatch sb : batches) {
            var product = productRepo.findById(sb.getProductId());
            records.add(new ReshelvedRecord(
                    product.getCode(),
                    product.getName(),
                    sb.getQuantity()
            ));
        }
    }


    @Override
    protected void processData() {
        System.out.printf("%-10s %-20s %-15s%n", "Code", "Item Name", "Reshelved Qty");
        for (ReshelvedRecord rec : records) {
            System.out.printf("%-10s %-20s %-15d%n",
                    rec.code, rec.name, rec.reshelvedQty);
        }
    }
}
