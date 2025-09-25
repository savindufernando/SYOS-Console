package cli;

import billing.Bill;
import billing.BillItem;
import billing.BillPrinter;
import db.repositories.BillRepositoryImpl;
import db.repositories.BillItemRepositoryImpl;
import db.repositories.UserRepository;

import java.util.List;
import java.util.Scanner;

public class ReceiptHistoryMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final BillRepositoryImpl billRepo = new BillRepositoryImpl();
    private final BillItemRepositoryImpl billItemRepo = new BillItemRepositoryImpl();
    private final BillPrinter billPrinter = new BillPrinter(new UserRepository());

    @Override
    public void start() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          Receipt History Menu          ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. View by Bill Number                 ║");
            System.out.println("║ 2. List Recent Receipts                ║");
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 3. Back to Main Menu                   ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid choice. Please enter 1-3.", true);
                continue;
            }

            switch (choice) {
                case 1 -> viewByBillNumber();
                case 2 -> listRecentReceipts();
                case 3 -> { return; }
                default -> printMessage("! Invalid option. Try again.", true);
            }
        }
    }

    private void viewByBillNumber() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         View Bill by Number            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter Bill Number (e.g., SYOS-20250924-001-CT): ");
        String input = scanner.nextLine().trim();

        Bill bill = billRepo.findByBillNumber(input);
        if (bill == null) {
            printMessage("! Bill not found.", false);
            return;
        }

        // Load items for this bill
        List<BillItem> items = billItemRepo.findByBillId(bill.getBillId());
        bill.getItems().addAll(items);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║                 Receipt                ║");
        System.out.println("╚════════════════════════════════════════╝");
        billPrinter.print(bill);
    }

    private void listRecentReceipts() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       List Recent Receipts             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» How many recent receipts to show? ");
        int limit;
        try {
            limit = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid number.", true);
            return;
        }
        System.out.println("╚════════════════════════════════════════╝");

        List<Bill> bills = billRepo.findRecent(limit);
        if (bills.isEmpty()) {
            printMessage("! No receipts found.", false);
            return;
        }

        for (Bill bill : bills) {
            List<BillItem> items = billItemRepo.findByBillId(bill.getBillId());
            bill.getItems().addAll(items);

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.printf("║ Receipt # %s%n", bill.getBillNumber());
            System.out.println("╚════════════════════════════════════════╝");
            billPrinter.print(bill);
        }
    }

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