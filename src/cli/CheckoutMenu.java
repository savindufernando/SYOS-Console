package cli;

import auth.User;
import billing.Bill;
import billing.BillItem;
import billing.BillPrinter;
import billing.CheckoutService;
import db.repositories.ProductRepositoryImpl;
import stock.Product;

import java.util.Scanner;

public class CheckoutMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
    private final CheckoutService checkoutService = new CheckoutService();
    private final BillPrinter billPrinter = new BillPrinter();
    private final User loggedUser;

    public CheckoutMenu(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void start() {
        System.out.println("\n=== Checkout Menu (" + loggedUser.getRole() + ": " + loggedUser.getName() + ") ===");
        checkoutService.startBill(loggedUser.getId(), loggedUser.getName());

        while (true) {
            System.out.println("\n1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Finalize Checkout");
            System.out.println("4. Cancel & Exit");
            System.out.println("5. View Current Bill");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number 1–5.");
                continue;
            }

            switch (choice) {
                case 1 -> handleAddProduct();
                case 2 -> handleRemoveProduct();
                case 3 -> {
                    if (checkoutService.getCurrentBill().getItems().isEmpty()) {
                        System.out.println("⚠️ No items in bill. Cancelling checkout.");
                        return;
                    }
                    finalizeCheckout();
                    return; // exit after finalizing
                }
                case 4 -> {
                    System.out.println("❌ Checkout cancelled.");
                    return;
                }
                case 5 -> viewCurrentBill();
                default -> System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    private void handleAddProduct() {
        System.out.print("Enter product code: ");
        String code = scanner.nextLine().trim();

        Product p = productRepo.findByCode(code);
        if (p == null) {
            System.out.println("❌ Product not found!");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid quantity.");
            return;
        }

        checkoutService.addProduct(p, qty);
        System.out.println("✔ Added " + qty + " x " + p.getName() + " (Unit price: " + p.getUnitPrice() + ")");
    }

    private void handleRemoveProduct() {
        Bill bill = checkoutService.getCurrentBill();
        if (bill.getItems().isEmpty()) {
            System.out.println("⚠️ No items in the bill to remove.");
            return;
        }

        System.out.println("\n--- Current Bill Items ---");
        int index = 1;
        for (BillItem item : bill.getItems()) {
            System.out.printf("%d. %s x%d = %.2f%n",
                    index++, item.getProduct().getName(), item.getQuantity(), item.getLineTotal());
        }

        System.out.print("Enter item number to remove: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > bill.getItems().size()) {
                System.out.println("❌ Invalid item number.");
                return;
            }
            BillItem toRemove = bill.getItems().get(choice - 1);
            checkoutService.removeProduct(toRemove);
            System.out.println("✔ Removed " + toRemove.getProduct().getName() + " from bill.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input.");
        }
    }

    private void finalizeCheckout() {
        Bill bill = checkoutService.getCurrentBill();
        double total = bill.getTotalAmount();

        double cash = 0;
        while (true) {
            System.out.print("Enter cash tendered (Total: " + total + ", or 0 to cancel): ");
            try {
                cash = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid cash amount. Please enter a number.");
                continue;
            }

            if (cash == 0) {
                System.out.println("❌ Checkout cancelled at payment step.");
                return; // exit without finalizing
            }

            if (cash < total) {
                System.out.printf("❌ Insufficient amount! Total is %.2f, but tendered %.2f%n", total, cash);
                System.out.println("👉 Please enter at least the total amount or type 0 to cancel.");
            } else {
                break; // ✅ valid cash entered
            }
        }

        // ✅ Finalize and save bill
        checkoutService.finalizeBill(cash);

        // ✅ Print receipt immediately
        billPrinter.print(bill);

        // ✅ Ask cashier if they want to view receipt again
        System.out.print("Do you want to view the receipt again? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            System.out.println("\n--- Receipt Reprint ---");
            billPrinter.print(bill);
        }

        System.out.println("✅ Checkout complete. Returning to Main Menu...");
    }


    private void viewCurrentBill() {
        Bill bill = checkoutService.getCurrentBill();
        if (bill.getItems().isEmpty()) {
            System.out.println("⚠️ Bill is currently empty.");
            return;
        }

        System.out.println("\n--- Current Bill Preview ---");
        billPrinter.print(bill);  // ✅ use the same printer for consistency
    }

}



