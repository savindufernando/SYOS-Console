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
        String userRole = loggedUser.getRole();
        boolean isAdminOrManager = "MANAGER".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole);

        while (true) {
            // Build the menu UI in a single block
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.printf("║             Main Menu (%-7s)              ║%n", userRole);
            System.out.println("╠════════════════════════════════════════════╣");
            System.out.println("║ 1. Checkout / Billing                      ║");

            if (isAdminOrManager) {
                System.out.println("║ 2. Inventory Management                    ║");
                System.out.println("║ 3. Shelf Management                        ║");
                System.out.println("║ 4. Receipt History                         ║");
                System.out.println("║ 5. Reports Generation                      ║");
                System.out.println("║ 6. Discount Management                     ║");
                System.out.println("╟────────────────────────────────────────────╢");
                System.out.println("║ 7. Logout                                  ║");
            } else {
                // For Cashier/other roles, the options are different
                System.out.println("╟────────────────────────────────────────────╢");
                System.out.println("║ 2. Logout                                  ║");
            }

            System.out.println("╚════════════════════════════════════════════╝");
            System.out.print("» Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (isAdminOrManager) {
                switch (choice) {
                    case 1 -> new CheckoutMenu(loggedUser).start();
                    case 2 -> new InventoryMenu(new InventoryBatchRepositoryImpl(), new ShelfBatchRepositoryImpl(), new OnlineBatchRepositoryImpl()).start();
                    case 3 -> new ShelfMenu(new ShelfBatchRepositoryImpl()).start();
                    case 4 -> new ReceiptHistoryMenu().start();
                    case 5 -> new ReportMenu().start();
                    case 6 -> new DiscountMenu().start();
                    case 7 -> {
                        printMessage("-> Logging out...", false);
                        return;
                    }
                    default -> printMessage("! Invalid choice, please try again.", true);
                }
            } else {
                switch (choice) {
                    case 1 -> new CheckoutMenu(loggedUser).start();
                    case 2 -> {
                        printMessage("-> Logging out...", false);
                        return;
                    }
                    default -> printMessage("! Invalid choice, please try again.", true);
                }
            }
        }
    }

    // A helper method for consistent framed messages with symbols
    private void printMessage(String message, boolean isError) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.printf("║ %-42s ║%n", message);
        System.out.println("╚════════════════════════════════════════════╝");
        if (isError) {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }
}
