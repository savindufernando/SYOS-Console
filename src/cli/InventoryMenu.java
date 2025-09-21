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
            System.out.println("\n=== Inventory Management Menu ===");
            System.out.println("1. Add Product");
            System.out.println("2. Add Inventory Batch");
            System.out.println("3. Restock Shelf (from inventory)");
            System.out.println("4. Restock Online (from inventory)");
            System.out.println("5. View Shelf Stock");
            System.out.println("6. Reduce Shelf Stock (simulate checkout)");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> addInventoryBatch();
                case 3 -> restockShelf();
                case 4 -> restockOnline();
                case 5 -> viewShelfStock();
                case 6 -> reduceShelfStock();
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    private void addProduct() {
        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();

        System.out.print("Enter product code: ");
        String code = scanner.nextLine();

        System.out.print("Enter product name: ");
        String name = scanner.nextLine();

        System.out.print("Enter product type (PERISHABLE/NONPERISHABLE): ");
        String type = scanner.nextLine().toUpperCase();

        System.out.print("Enter unit price: ");
        double price = Double.parseDouble(scanner.nextLine());

        Product product;
        if ("PERISHABLE".equals(type)) {
            product = new PerishableProduct(code, name, price, null);
        } else {
            product = new NonPerishableProduct(code, name, price);
        }

        productRepo.save(product);
        System.out.println("✅ Product added successfully.");
    }

    private void addInventoryBatch() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode + ". Please add the product first.");
            return;
        }

        int productId = product.getId();

        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter purchase date (yyyy-mm-dd): ");
        LocalDate purchaseDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter expiry date (yyyy-mm-dd or leave blank): ");
        String expiryInput = scanner.nextLine();
        LocalDate expiry = expiryInput.isBlank() ? null : LocalDate.parse(expiryInput);

        InventoryBatch batch = new InventoryBatch();
        batch.setProductId(productId);
        batch.setQuantity(qty);
        batch.setPurchaseDate(purchaseDate);
        batch.setExpiryDate(expiry);

        inventoryManager.addInventoryBatch(batch);
        System.out.println("✅ Inventory batch added for product " + productCode);
    }

    private void restockShelf() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode);
            return;
        }

        int productId = product.getId();

        System.out.print("Enter quantity to move to SHELF: ");
        int qty = Integer.parseInt(scanner.nextLine());

        inventoryManager.restockShelf(productId, qty);
    }

    private void restockOnline() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode);
            return;
        }

        int productId = product.getId();

        System.out.print("Enter quantity to move to ONLINE stock: ");
        int qty = Integer.parseInt(scanner.nextLine());

        inventoryManager.restockOnline(productId, qty);
    }

    private void viewShelfStock() {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();

        ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
        var product = productRepo.findByCode(productCode);

        if (product == null) {
            System.out.println("❌ No product found with code " + productCode);
            return;
        }

        ShelfBatch shelf = inventoryManager.getShelfStock(product.getId());

        if (shelf == null) {
            System.out.println("⚠️ No stock on shelf for " + productCode);
        } else {
            System.out.println("Shelf Stock: " + productCode +
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
            System.out.println("✅ Shelf stock reduced.");
        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
