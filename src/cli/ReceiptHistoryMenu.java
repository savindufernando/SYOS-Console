package cli;

import billing.Bill;
import billing.BillItem;
import billing.BillPrinter;
import db.repositories.BillRepositoryImpl;
import db.repositories.BillItemRepositoryImpl;

import java.util.List;
import java.util.Scanner;

public class ReceiptHistoryMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final BillRepositoryImpl billRepo = new BillRepositoryImpl();
    private final BillItemRepositoryImpl billItemRepo = new BillItemRepositoryImpl();
    private final BillPrinter billPrinter = new BillPrinter();

    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Receipt History Menu ===");
            System.out.println("1. View by Bill Number");
            System.out.println("2. List Recent Receipts");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter 1–3.");
                continue;
            }

            switch (choice) {
                case 1 -> viewByBillNumber();
                case 2 -> listRecentReceipts();
                case 3 -> { return; }
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }

    private void viewByBillNumber() {
        System.out.print("Enter Bill Number (e.g., SYOS-20250924-001-CT): ");
        String input = scanner.nextLine().trim();

        Bill bill = billRepo.findByBillNumber(input);
        if (bill == null) {
            System.out.println("⚠️ Bill not found.");
            return;
        }

        // Load items for this bill
        List<BillItem> items = billItemRepo.findByBillId(bill.getBillId());
        bill.getItems().addAll(items);

        System.out.println("\n--- Receipt ---");
        billPrinter.print(bill);
    }

    private void listRecentReceipts() {
        System.out.print("How many recent receipts to show? ");
        int limit;
        try {
            limit = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number.");
            return;
        }

        List<Bill> bills = billRepo.findRecent(limit);
        if (bills.isEmpty()) {
            System.out.println("⚠️ No receipts found.");
            return;
        }

        for (Bill bill : bills) {
            List<BillItem> items = billItemRepo.findByBillId(bill.getBillId());
            bill.getItems().addAll(items);

            System.out.println("\n--- Receipt ---");
            billPrinter.print(bill);
        }
    }
}
