package reports.models;

public class StockBatchRecord {
    public String code;
    public String purchaseDate;
    public int qtyReceived;
    public String expiryDate;
    public int remainingQty;

    public StockBatchRecord(String code, String purchaseDate, int qtyReceived, String expiryDate, int remainingQty) {
        this.code = code;
        this.purchaseDate = purchaseDate;
        this.qtyReceived = qtyReceived;
        this.expiryDate = expiryDate;
        this.remainingQty = remainingQty;
    }
}
