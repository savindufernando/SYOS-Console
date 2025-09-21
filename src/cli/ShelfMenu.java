package cli;

import db.repositories.ProductRepositoryImpl;
import stock.ShelfManager;
import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;

import java.util.Scanner;

public class ShelfMenu {
    private final ShelfManager shelfManager;
    private final ShelfBatchRepository shelfRepo;
    private final Scanner scanner = new Scanner(System.in);

    public ShelfMenu(ShelfBatchRepository shelfRepo) {
        this.shelfRepo = shelfRepo;
        this.shelfManager = new ShelfManager(shelfRepo);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Shelf Management Menu ===");
            System.out.println("1. View Shelf Stock for a Product");
            System.out.println("2. Reduce Shelf Stock (simulate checkout)");
            System.out.println("3. View All Shelf Stock");
            System.out.println("0. Back to Previous Menu");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> viewShelfStockByProduct();
                case 2 -> reduceShelfStock();
                case 3 -> viewAllShelfStock();
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    private void viewShelfStockByProduct() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode);
            return;
        }

        int productId = product.getId();
        ShelfBatch shelf = shelfRepo.findByProduct(productId);

        if (shelf == null) {
            System.out.println("⚠️ No shelf stock found for product " + productCode);
        } else {
            System.out.println("Shelf Stock → " + productCode +
                    " | Qty = " + shelf.getQuantity() +
                    " | Last Restocked = " +
                    (shelf.getLastRestocked() != null ? shelf.getLastRestocked() : "N/A"));
        }
    }

    private void reduceShelfStock() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode);
            return;
        }

        int productId = product.getId();

        System.out.print("Enter quantity to reduce (simulate checkout): ");
        int qty = Integer.parseInt(scanner.nextLine());

        try {
            shelfManager.reduceShelfStock(productId, qty);
            System.out.println("✅ Shelf stock reduced successfully.");
        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void viewAllShelfStock() {
        System.out.println("=== All Shelf Stock ===");
        var all = shelfRepo.findAllWithProducts();

        if (all.isEmpty()) {
            System.out.println("⚠️ No shelf stock available.");
            return;
        }

        for (ShelfBatch b : all) {
            System.out.println("[" + b.getProductCode() + "] " + b.getProductName() +
                    " | Qty = " + b.getQuantity() +
                    " | Last Restocked = " +
                    (b.getLastRestocked() != null ? b.getLastRestocked() : "N/A"));
        }
    }

}
