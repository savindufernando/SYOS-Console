package cli;

import db.repositories.DiscountRepositoryImpl;
import stock.repositories.ProductRepository;
import db.repositories.ProductRepositoryImpl;

import java.util.Scanner;

public class DiscountMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final DiscountRepositoryImpl discountRepo = new DiscountRepositoryImpl();
    private final ProductRepository productRepo = new ProductRepositoryImpl();

    @Override
    public void start() {
        while (true) {
            // Main menu UI
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║         DISCOUNT MANAGEMENT MENU           ║");
            System.out.println("╠════════════════════════════════════════════╣");
            System.out.println("║ 1. Add Discount to Product                 ║");
            System.out.println("║ 2. View Active Discounts                   ║");
            System.out.println("║ 3. View Disabled Discounts                 ║");
            System.out.println("║ 4. Disable Discount                        ║");
            System.out.println("║ 5. Edit Discount                           ║");
            System.out.println("╟────────────────────────────────────────────╢");
            System.out.println("║ 6. Back to Main Menu                       ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.print("» Select option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid input, enter 1–6.", true);
                continue;
            }

            switch (choice) {
                case 1 -> addDiscount();
                case 2 -> discountRepo.viewDiscounts("ACTIVE");
                case 3 -> discountRepo.viewDiscounts("DISABLED");
                case 4 -> disableDiscount();
                case 5 -> editDiscount();
                case 6 -> { return; }
                default -> printMessage("! Invalid choice, try again.", true);
            }
        }
    }

    private void showProducts() {
        var products = productRepo.findAll();
        if (products.isEmpty()) {
            printMessage("! No products found. Please add products first.", false);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║              Available Products            ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.printf("║ %-5s %-10s %-18s %-10s ║%n", "ID", "Code", "Name", "Price");
        System.out.println("╠════════════════════════════════════════════╣");
        for (var p : products) {
            System.out.printf("║ %-5d %-10s %-18s %.2f ║%n",
                    p.getId(), p.getCode(), p.getName(), p.getUnitPrice());
        }
        System.out.println("╚════════════════════════════════════════════╝");
    }

    private void addDiscount() {
        showProducts();

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║              Add New Discount              ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String code = scanner.nextLine().trim();
        var product = productRepo.findByCode(code);

        if (product == null) {
            printMessage("! Product not found.", true);
            return;
        }

        System.out.print("» Enter discount name: ");
        String name = scanner.nextLine();

        System.out.println("║ Select Discount Type:");
        System.out.println("║   1. Amount");
        System.out.println("║   2. Percentage");
        System.out.print("» Enter choice (1/2): ");
        int typeChoice = Integer.parseInt(scanner.nextLine());
        String type = (typeChoice == 1) ? "AMOUNT" : "PERCENTAGE";

        System.out.print("» Enter discount value: ");
        double value = Double.parseDouble(scanner.nextLine());

        System.out.print("» Enter start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();

        System.out.print("» Enter end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();
        System.out.println("╚════════════════════════════════════════════╝");

        discountRepo.addDiscount(product.getId(), name, type, value, start, end);
        printMessage("+ Discount added successfully for product " + product.getName(), false);
    }

    private void disableDiscount() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║              Disable Discount              ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.print("» Enter discount ID to disable: ");
        int id = Integer.parseInt(scanner.nextLine());
        discountRepo.disableDiscount(id);
        printMessage("+ Discount disabled.", false);
    }

    private void editDiscount() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║               Edit Discount                ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.print("» Enter discount ID to edit: ");
        int id = Integer.parseInt(scanner.nextLine());

        showProducts(); // To help user select the product for the discount
        System.out.println("║                                            ║");
        System.out.print("» Enter new discount name: ");
        String name = scanner.nextLine();

        System.out.println("║ Select Discount Type:");
        System.out.println("║   1. Amount");
        System.out.println("║   2. Percentage");
        System.out.print("» Enter choice (1/2): ");
        int typeChoice = Integer.parseInt(scanner.nextLine());
        String type = (typeChoice == 1) ? "AMOUNT" : "PERCENTAGE";

        System.out.print("» Enter new discount value: ");
        double value = Double.parseDouble(scanner.nextLine());

        System.out.print("» Enter new start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();

        System.out.print("» Enter new end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();

        System.out.println("║ Select Status:");
        System.out.println("║   1. Active");
        System.out.println("║   2. Disabled");
        System.out.print("» Enter choice (1/2): ");
        int statusChoice = Integer.parseInt(scanner.nextLine());
        String status = (statusChoice == 1) ? "ACTIVE" : "DISABLED";
        System.out.println("╚════════════════════════════════════════════╝");

        discountRepo.updateDiscount(id, name, type, value, start, end, status);
        printMessage("+ Discount updated successfully.", false);
    }

    // A helper method for consistent framed messages
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