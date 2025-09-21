package cli;

import reports.base.Report;
import reports.base.ReportFactory;
import reports.base.ReportFormatter;
import reports.formatters.CLIReportFormatter;
import reports.service.ReportService;

import java.time.LocalDate;
import java.util.Scanner;

public class ReportMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final ReportService reportService = new ReportService();

    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Report Menu ===");
            System.out.println("1. Daily Sales Report");
            System.out.println("2. Reshelved Items Report");
            System.out.println("3. Reorder Level Report");
            System.out.println("4. Stock Report (Batch-wise)");
            System.out.println("5. Bill Report");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 6) break;

            try {
                Report report = ReportFactory.createReport(choice);
                ReportFormatter formatter = new CLIReportFormatter(); // default to CLI

                switch (choice) {
                    case 1 -> { // Daily Sales Report
                        System.out.print("Enter date (YYYY-MM-DD) [default=today]: ");
                        String input = scanner.nextLine().trim();
                        LocalDate date = input.isEmpty() ? LocalDate.now() : LocalDate.parse(input);
                        reportService.generateReport(report, formatter, date, null);
                    }
                    case 2 -> { // Reshelved Items Report
                        System.out.print("Enter date (YYYY-MM-DD) [default=today]: ");
                        String input = scanner.nextLine().trim();
                        LocalDate date = input.isEmpty() ? LocalDate.now() : LocalDate.parse(input);
                        reportService.generateReport(report, formatter, date, null);
                    }
                    case 5 -> { // Bill Report (date range)
                        System.out.print("Enter start date (YYYY-MM-DD) [default=today]: ");
                        String startInput = scanner.nextLine().trim();
                        LocalDate start = startInput.isEmpty() ? LocalDate.now() : LocalDate.parse(startInput);

                        System.out.print("Enter end date (YYYY-MM-DD) [default=same as start]: ");
                        String endInput = scanner.nextLine().trim();
                        LocalDate end = endInput.isEmpty() ? start : LocalDate.parse(endInput);

                        reportService.generateReport(report, formatter, start, end);
                    }
                    default -> { // Reports that don’t require a date
                        reportService.generateReport(report, formatter, null, null);
                    }
                }


            } catch (Exception e) {
                System.out.println("⚠️ Error generating report: " + e.getMessage());
            }
        }
    }
}
