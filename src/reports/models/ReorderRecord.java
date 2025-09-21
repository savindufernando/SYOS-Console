package reports.models;

public class ReorderRecord {
    public String code;
    public String name;
    public int currentStock;

    public ReorderRecord(String code, String name, int stock) {
        this.code = code;
        this.name = name;
        this.currentStock = stock;
    }
}
