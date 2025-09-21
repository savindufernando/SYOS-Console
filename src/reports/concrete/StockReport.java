package reports.concrete;

import reports.base.Report;
import reports.models.StockBatchRecord;
import stock.batch.InventoryBatch;
import stock.repositories.InventoryBatchRepository;
import db.repositories.InventoryBatchRepositoryImpl;
import stock.repositories.ProductRepository;
import db.repositories.ProductRepositoryImpl;

import java.time.LocalDate;
import java.util.*;

public class StockReport extends Report {
    private final InventoryBatchRepository invRepo = new InventoryBatchRepositoryImpl();
    private final ProductRepository productRepo = new ProductRepositoryImpl();
    private final List<StockBatchRecord> records = new ArrayList<>();

    public StockReport() {
        this.title = "Stock Report (Batch-wise)";
    }

    @Override
    protected void fetchData(LocalDate start, LocalDate end) {
        records.clear();
        // Dates not used, but guard anyway
        LocalDate safeStart = (start != null) ? start : LocalDate.now();

        for (InventoryBatch batch : invRepo.findAll()) {
            var product = productRepo.findById(batch.getProductId());
            records.add(new StockBatchRecord(
                    product.getCode(),
                    batch.getPurchaseDate().toString(),
                    batch.getQuantity(),
                    batch.getExpiryDate() == null ? "-" : batch.getExpiryDate().toString(),
                    batch.getQuantity()
            ));
        }
    }

    @Override
    protected void processData() {
        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n",
                "Code", "Purchase Date", "Qty Received", "Expiry Date", "Remaining Qty");
        for (StockBatchRecord rec : records) {
            System.out.printf("%-10s %-15s %-15d %-15s %-15d%n",
                    rec.code, rec.purchaseDate, rec.qtyReceived, rec.expiryDate, rec.remainingQty);
        }
    }
}
