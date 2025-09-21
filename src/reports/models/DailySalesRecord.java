package reports.models;

public class DailySalesRecord {
    public String code;
    public String name;
    public int quantitySold;
    public double revenue;

    public DailySalesRecord(String code, String name, int qty, double revenue) {
        this.code = code;
        this.name = name;
        this.quantitySold = qty;
        this.revenue = revenue;
    }
}
