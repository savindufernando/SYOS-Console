package cli;

import auth.User;
import db.repositories.InventoryBatchRepositoryImpl;
import db.repositories.ShelfBatchRepositoryImpl;
import db.repositories.OnlineBatchRepositoryImpl;

public class MainMenu implements Menu {
    private final java.util.Scanner scanner = new java.util.Scanner(System.in);
    private final User loggedUser;

    public MainMenu(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Main Menu (" + loggedUser.getRole() + ") ===");
            System.out.println("1. Checkout / Billing");

            if ("MANAGER".equalsIgnoreCase(loggedUser.getRole()) ||
                    "ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
                System.out.println("2. Inventory Management");
                System.out.println("3. Shelf Management");
                System.out.println("4. Receipt History");
                System.out.println("5. Reports Generation");
                System.out.println("6. Discount Management");
                System.out.println("7. Logout");
            } else {
                System.out.println("2. Logout");
            }

            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if ("MANAGER".equalsIgnoreCase(loggedUser.getRole()) ||
                    "ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
                switch (choice) {
                    case 1 -> new CheckoutMenu(loggedUser).start();   // ✅ Managers/Admins can bill
                    case 2 -> new InventoryMenu(
                            new InventoryBatchRepositoryImpl(),
                            new ShelfBatchRepositoryImpl(),
                            new OnlineBatchRepositoryImpl()
                    ).start();
                    case 3 -> new ShelfMenu(
                            new ShelfBatchRepositoryImpl()
                    ).start();
                    case 4 -> new ReceiptHistoryMenu().start();       // ✅ View old receipts
                    case 5 -> new ReportMenu().start();
                    case 6 -> new DiscountMenu().start();
                    case 7 -> {
                        System.out.println("👋 Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice, try again.");
                }
            } else {
                switch (choice) {
                    case 1 -> new CheckoutMenu(loggedUser).start();   // ✅ Cashiers billing
                    case 2 -> {
                        System.out.println("👋 Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice, try again.");
                }
            }
        }
    }
}
