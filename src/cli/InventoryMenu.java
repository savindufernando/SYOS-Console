package cli;

import db.repositories.ProductRepositoryImpl;
import stock.InventoryManager;
import stock.ShelfManager;
import stock.batch.InventoryBatch;
import stock.batch.ShelfBatch;
import stock.strategy.FifoStrategy;
import stock.repositories.InventoryBatchRepository;
import stock.repositories.ShelfBatchRepository;
import stock.repositories.OnlineBatchRepository;
import stock.Product;
import stock.PerishableProduct;
import stock.NonPerishableProduct;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InventoryMenu {
    private final InventoryManager inventoryManager;
    private final ShelfManager shelfManager;
    private final Scanner scanner = new Scanner(System.in);

    public InventoryMenu(InventoryBatchRepository inventoryRepo,
                         ShelfBatchRepository shelfRepo,
                         OnlineBatchRepository onlineRepo) {
        this.inventoryManager = new InventoryManager(inventoryRepo, shelfRepo, onlineRepo, new FifoStrategy());
        this.shelfManager = new ShelfManager(shelfRepo);
    }

    public void start() {
        while (true) {
            // Main menu UI
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          INVENTORY MANAGEMENT          ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║         Product Management             ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Add Product                         ║");
            System.out.println("║ 2. Update Product                      ║");
            System.out.println("║ 3. Delete Product                      ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║         Batch Management               ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 4. Add Inventory Batch                 ║");
            System.out.println("║ 5. Restock Shelf (from inventory)      ║");
            System.out.println("║ 6. Restock Online (from inventory)     ║");
            System.out.println("║ 7. View Shelf Stock                    ║");
            System.out.println("║ 8. Reduce Shelf Stock (checkout)       ║");
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 0. Exit                                ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid choice. Please enter a number.", true);
                continue;
            }

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> updateProduct();
                case 3 -> deleteProduct();
                case 4 -> addInventoryBatch();
                case 5 -> restockShelf();
                case 6 -> restockOnline();
                case 7 -> viewShelfStock();
                case 8 -> reduceShelfStock();
                case 0 -> { return; }
                default -> printMessage("! Invalid choice.", true);
            }
        }
    }

    private void addProduct() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Add New Product           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String code = scanner.nextLine();

        System.out.print("» Enter product name: ");
        String name = scanner.nextLine();

        System.out.println("║ Select Product Type:");
        System.out.println("║  1. Perishable");
        System.out.println("║  2. Non-Perishable");
        System.out.print("» Enter choice (1/2): ");
        int typeChoice;
        try {
            typeChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid choice. Product not added.", true);
            return;
        }

        System.out.print("» Enter unit price: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid price. Product not added.", true);
            return;
        }

        Product product;
        if (typeChoice == 1) {
            product = new PerishableProduct(code, name, price, null);
        } else if (typeChoice == 2) {
            product = new NonPerishableProduct(code, name, price);
        } else {
            printMessage("! Invalid choice. Product not added.", true);
            return;
        }

        productRepo.save(product);
        printMessage("+ Product added successfully.", false);
    }

    private void updateProduct() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Update Product            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code to update: ");
        String code = scanner.nextLine();

        Product product = productRepo.findByCode(code);
        if (product == null) {
            printMessage("! No product found with code " + code, false);
            return;
        }

        System.out.print("» Enter new name (leave blank to keep '" + product.getName() + "'): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            product.setName(name);
        }

        System.out.print("» Enter new unit price (current " + product.getUnitPrice() + "): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isBlank()) {
            try {
                product.setUnitPrice(Double.parseDouble(priceInput));
            } catch (NumberFormatException e) {
                printMessage("! Invalid price format. Update failed.", true);
                return;
            }
        }

        productRepo.update(product);
        printMessage("+ Product updated successfully.", false);
    }
    private void deleteProduct() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Delete Product            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code to delete: ");
        String code = scanner.nextLine();

        Product product = productRepo.findByCode(code);
        if (product == null) {
            printMessage("! No product found with code " + code, false);
            return;
        }

        System.out.print("» Are you sure you want to delete '" + product.getName() + "'? (y/n): ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            printMessage("! Deletion cancelled.", false);
            return;
        }

        productRepo.delete(product.getId());
        printMessage("+ Product deleted successfully.", false);
    }



    private void addInventoryBatch() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Add New Inventory Batch       ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        var product = productRepo.findByCode(productCode);
        if (product == null) {
            printMessage("! No product found with code " + productCode + ". Please add the product first.", true);
            return;
        }

        System.out.print("» Enter quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity.", true);
            return;
        }

        System.out.print("» Enter purchase date (yyyy-mm-dd): ");
        LocalDate purchaseDate = LocalDate.parse(scanner.nextLine());

        System.out.print("» Enter expiry date (yyyy-mm-dd or leave blank): ");
        String expiryInput = scanner.nextLine();
        LocalDate expiry = expiryInput.isBlank() ? null : LocalDate.parse(expiryInput);

        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(product.getId());
        batch.setQuantity(qty);
        batch.setPurchaseDate(purchaseDate);
        batch.setExpiryDate(expiry);

        inventoryManager.addInventoryBatch(batch);
        printMessage("+ Inventory batch added for product " + productCode, false);
    }

    private void restockShelf() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Restock Shelf             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        Product product = productRepo.findByCode(productCode);
        if (product == null) {
            printMessage("! No product found with code " + productCode, true);
            return;
        }

        System.out.print("» Enter quantity to move to SHELF: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity.", true);
            return;
        }

        inventoryManager.restockShelf(product, qty);
        printMessage("+ Shelf restocked with " + qty + " of " + product.getName() + ".", false);
    }


    private void restockOnline() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║            Restock Online              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        Product product = productRepo.findByCode(productCode);
        if (product == null) {
            printMessage("! No product found with code " + productCode, true);
            return;
        }

        System.out.print("» Enter quantity to move to ONLINE stock: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity.", true);
            return;
        }

        inventoryManager.restockOnline(product, qty);
        printMessage("+ Online stock restocked with " + qty + " of " + product.getName() + ".", false);
    }

    private void viewShelfStock() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║             View Shelf Stock           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        var product = productRepo.findByCode(productCode);
        if (product == null) {
            printMessage("! No product found with code " + productCode, true);
            return;
        }

        ShelfBatch shelf = inventoryManager.getShelfStock(product.getId());
        if (shelf == null) {
            printMessage("! No stock on shelf for " + productCode, false);
        } else {
            printMessage("+ Shelf Stock: " + productCode + " | Qty = " + shelf.getQuantity() + " | Last Restocked = " +
                    (shelf.getLastRestocked() != null ? shelf.getLastRestocked() : "N/A"), false);
        }
    }

    private void reduceShelfStock() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║            Reduce Shelf Stock          ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String productCode = scanner.nextLine();

        var product = productRepo.findByCode(productCode);
        if (product == null) {
            printMessage("! No product found with code " + productCode, true);
            return;
        }

        System.out.print("» Enter quantity to reduce: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity.", true);
            return;
        }

        try {
            shelfManager.reduceShelfStock(product.getId(), qty);
            printMessage("+ Shelf stock reduced successfully.", false);
        } catch (RuntimeException e) {
            printMessage("! Error: " + e.getMessage(), true);
        }
    }

    // A helper method for consistent framed messages with symbols
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