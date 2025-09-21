package reports.concrete;

import reports.base.Report;
import reports.models.ReorderRecord;
import stock.batch.InventoryBatch;
import stock.repositories.InventoryBatchRepository;
import db.repositories.InventoryBatchRepositoryImpl;
import stock.repositories.ProductRepository;
import db.repositories.ProductRepositoryImpl;

import java.time.LocalDate;
import java.util.*;

public class ReorderLevelReport extends Report {
    private final InventoryBatchRepository invRepo = new InventoryBatchRepositoryImpl();
    private final ProductRepository productRepo = new ProductRepositoryImpl();
    private final List<ReorderRecord> records = new ArrayList<>();

    public ReorderLevelReport() {
        this.title = "Reorder Level Report";
    }

    @Override
    protected void fetchData(LocalDate start, LocalDate end) {
        records.clear();
        // Dates not used here, but guard anyway
        LocalDate safeStart = (start != null) ? start : LocalDate.now();

        Map<String, ReorderRecord> stockMap = new HashMap<>();
        for (InventoryBatch batch : invRepo.findAll()) {
            var product = productRepo.findById(batch.getProductId());
            stockMap.putIfAbsent(product.getCode(),
                    new ReorderRecord(product.getCode(), product.getName(), 0));
            stockMap.get(product.getCode()).currentStock += batch.getQuantity();
        }

        for (ReorderRecord rec : stockMap.values()) {
            if (rec.currentStock < 50) {
                records.add(rec);
            }
        }
    }


    @Override
    protected void processData() {
        System.out.printf("%-10s %-20s %-15s%n", "Code", "Item Name", "Stock");
        for (ReorderRecord rec : records) {
            System.out.printf("%-10s %-20s %-15d%n",
                    rec.code, rec.name, rec.currentStock);
        }
    }
}
