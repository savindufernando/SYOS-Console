package cli;

import reports.base.Report;
import reports.base.ReportFactory;
import reports.base.ReportFormatter;
import reports.formatters.CLIReportFormatter;
import reports.service.ReportService;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ReportMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final ReportService reportService = new ReportService();

    @Override
    public void start() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║              Report Menu               ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Daily Sales Report                  ║");
            System.out.println("║ 2. Reshelved Items Report              ║");
            System.out.println("║ 3. Reorder Level Report                ║");
            System.out.println("║ 4. Stock Report (Batch-wise)           ║");
            System.out.println("║ 5. Bill Report                         ║");
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 6. Back to Main Menu                   ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid input. Please enter a number.", true);
                continue;
            }

            if (choice == 6) break;

            try {
                Report report = ReportFactory.createReport(choice);
                ReportFormatter formatter = new CLIReportFormatter();

                switch (choice) {
                    case 1 -> {
                        System.out.print("» Enter date (YYYY-MM-DD) [default=today]: ");
                        String input = scanner.nextLine().trim();
                        LocalDate date = input.isEmpty() ? LocalDate.now() : LocalDate.parse(input);

                        System.out.print("» Filter by type [ALL/COUNTER/ONLINE] (default=ALL): ");
                        String typeInput = scanner.nextLine().trim().toUpperCase();
                        String type = typeInput.isEmpty() ? "ALL" : typeInput;

                        if (report instanceof reports.concrete.DailySalesReport dsr) {
                            dsr.setType(type);
                        }

                        reportService.generateReport(report, formatter, date, null);
                    }
                    case 2 -> {
                        System.out.print("» Enter date (YYYY-MM-DD) [default=today]: ");
                        String input = scanner.nextLine().trim();
                        LocalDate date = input.isEmpty() ? LocalDate.now() : LocalDate.parse(input);
                        reportService.generateReport(report, formatter, date, null);
                    }
                    case 5 -> {
                        System.out.print("» Enter start date (YYYY-MM-DD) [default=today]: ");
                        String startInput = scanner.nextLine().trim();
                        LocalDate start = startInput.isEmpty() ? LocalDate.now() : LocalDate.parse(startInput);

                        System.out.print("» Enter end date (YYYY-MM-DD) [default=same as start]: ");
                        String endInput = scanner.nextLine().trim();
                        LocalDate end = endInput.isEmpty() ? start : LocalDate.parse(endInput);

                        reportService.generateReport(report, formatter, start, end);
                    }
                    default -> reportService.generateReport(report, formatter, null, null);
                }

                printMessage("+ Report generated successfully.", false);

            } catch (Exception e) {
                printMessage("! Error generating report: " + e.getMessage(), true);
            }
        }
    }

    // A helper method for consistent framed messages
    private void printMessage(String message, boolean isError) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.printf("║ %-38s ║%n", message);
        System.out.println("╚════════════════════════════════════════╝");
        if (isError) {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }
}