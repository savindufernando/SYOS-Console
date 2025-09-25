package cli;

import db.repositories.ProductRepositoryImpl;
import stock.ShelfManager;
import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;
import java.util.InputMismatchException;

import java.util.Scanner;

public class ShelfMenu implements Menu {
    private final ShelfManager shelfManager;
    private final ShelfBatchRepository shelfRepo;
    private final Scanner scanner = new Scanner(System.in);

    public ShelfMenu(ShelfBatchRepository shelfRepo) {
        this.shelfRepo = shelfRepo;
        this.shelfManager = new ShelfManager(shelfRepo);
    }

    @Override
    public void start() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          Shelf Management Menu         ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. View Shelf Stock for a Product      ║");
            System.out.println("║ 2. Reduce Shelf Stock (sim. checkout)  ║");
            System.out.println("║ 3. View All Shelf Stock                ║");
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 0. Back to Previous Menu               ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid choice. Please enter a number.", true);
                continue;
            }

            switch (choice) {
                case 1 -> viewShelfStockByProduct();
                case 2 -> reduceShelfStock();
                case 3 -> viewAllShelfStock();
                case 0 -> { return; }
                default -> printMessage("! Invalid choice, try again.", true);
            }
        }
    }

    private void viewShelfStockByProduct() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        View Stock by Product           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            printMessage("! No product found with code " + productCode, false);
            return;
        }

        int productId = product.getId();
        ShelfBatch shelf = shelfRepo.findByProduct(productId);

        if (shelf == null) {
            printMessage("! No shelf stock found for product " + productCode, false);
        } else {
            printMessage("+ Shelf Stock -> " + productCode +
                    " | Qty = " + shelf.getQuantity() +
                    " | Last Restocked = " +
                    (shelf.getLastRestocked() != null ? shelf.getLastRestocked() : "N/A"), false);
        }
    }

    private void reduceShelfStock() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Reduce Shelf Stock             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            printMessage("! No product found with code " + productCode, true);
            return;
        }

        int productId = product.getId();

        System.out.print("» Enter quantity to reduce: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity. Please enter a number.", true);
            return;
        }

        try {
            shelfManager.reduceShelfStock(productId, qty);
            printMessage("+ Shelf stock reduced successfully.", false);
        } catch (RuntimeException e) {
            printMessage("! Error: " + e.getMessage(), true);
        }
    }

    private void viewAllShelfStock() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          All Shelf Stock               ║");
        System.out.println("╠════════════════════════════════════════╣");
        var all = shelfRepo.findAllWithProducts();

        if (all.isEmpty()) {
            printMessage("! No shelf stock available.", false);
            return;
        }

        for (ShelfBatch b : all) {
            System.out.printf("║ [%s] %-15s | Qty = %-3d | Last Restocked = %s%n",
                    b.getProductCode(),
                    b.getProductName(),
                    b.getQuantity(),
                    (b.getLastRestocked() != null ? b.getLastRestocked() : "N/A"));
        }
        System.out.println("╚════════════════════════════════════════╝");
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