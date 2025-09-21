package reports.base;

import reports.concrete.*;

public class ReportFactory {
    public static Report createReport(int choice) {
        return switch (choice) {
            case 1 -> new DailySalesReport();
            case 2 -> new ReshelvedItemsReport();
            case 3 -> new ReorderLevelReport();
            case 4 -> new StockReport();
            case 5 -> new BillReport();
            default -> throw new IllegalArgumentException("Invalid report type");
        };
    }
}
