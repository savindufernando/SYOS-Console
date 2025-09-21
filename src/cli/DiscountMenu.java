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
            System.out.println("\n=== Discount Management Menu ===");
            System.out.println("1. Add Discount to Product");
            System.out.println("2. View Active Discounts");
            System.out.println("3. View Disabled Discounts");
            System.out.println("4. Disable Discount");
            System.out.println("5. Edit Discount");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input, enter 1–6.");
                continue;
            }

            switch (choice) {
                case 1 -> addDiscount();
                case 2 -> discountRepo.viewDiscounts("ACTIVE");
                case 3 -> discountRepo.viewDiscounts("DISABLED");
                case 4 -> disableDiscount();
                case 5 -> editDiscount();
                case 6 -> { return; }
                default -> System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    private void addDiscount() {
        System.out.print("Enter product code: ");
        String code = scanner.nextLine().trim();
        var product = productRepo.findByCode(code);

        if (product == null) {
            System.out.println("❌ Product not found.");
            return;
        }

        System.out.print("Enter discount name: ");
        String name = scanner.nextLine();

        System.out.print("Discount type (AMOUNT/PERCENTAGE): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter discount value: ");
        double value = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();

        discountRepo.addDiscount(product.getId(), name, type, value, start, end);
    }

    private void disableDiscount() {
        System.out.print("Enter discount ID to disable: ");
        int id = Integer.parseInt(scanner.nextLine());
        discountRepo.disableDiscount(id);
    }

    private void editDiscount() {
        System.out.print("Enter discount ID to edit: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new discount name: ");
        String name = scanner.nextLine();

        System.out.print("New discount type (AMOUNT/PERCENTAGE): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter new discount value: ");
        double value = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter new start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();

        System.out.print("Enter new end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();

        System.out.print("Enter status (ACTIVE/DISABLED): ");
        String status = scanner.nextLine().trim().toUpperCase();

        discountRepo.updateDiscount(id, name, type, value, start, end, status);
    }
}
