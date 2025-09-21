package reports.models;

public class ReshelvedRecord {
    public String code;
    public String name;
    public int reshelvedQty;

    public ReshelvedRecord(String code, String name, int qty) {
        this.code = code;
        this.name = name;
        this.reshelvedQty = qty;
    }
}
