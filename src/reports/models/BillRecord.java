package reports.models;

public class BillRecord {
    public int billNo;
    public String date;
    public String itemsBought;
    public double total;
    public double cash;
    public double change;
    public String type;

    public BillRecord(int billNo, String date, String itemsBought, double total, double cash, double change, String type) {
        this.billNo = billNo;
        this.date = date;
        this.itemsBought = itemsBought;
        this.total = total;
        this.cash = cash;
        this.change = change;
        this.type = type;
    }
}
