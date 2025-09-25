package cli;

import auth.User;
import billing.Bill;
import billing.BillItem;
import billing.BillPrinter;
import billing.CheckoutService;
import db.repositories.ProductRepositoryImpl;
import stock.Product;
import db.repositories.UserRepository;


import java.util.Scanner;

public class CheckoutMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final ProductRepositoryImpl productRepo = new ProductRepositoryImpl();
    private final CheckoutService checkoutService = new CheckoutService();
    private final BillPrinter billPrinter = new BillPrinter(new UserRepository());

    private final User loggedUser;


    public CheckoutMenu(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void start() {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.printf("║  Checkout Menu (%s: %s)  ║%n", loggedUser.getRole(), loggedUser.getName());
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║ Started new bill. Add products to begin.  ║");
        System.out.println("╚═════════════════════════════════════════╝");
        checkoutService.startBill(loggedUser.getId(), loggedUser.getName());

        while (true) {
            System.out.println("\n╔═════════════════════════════════════════╗");
            System.out.println("║ 1. Add Product                          ║");
            System.out.println("║ 2. Remove Product                       ║");
            System.out.println("║ 3. Finalize Checkout                    ║");
            System.out.println("║ 4. Cancel & Exit                        ║");
            System.out.println("║ 5. View Current Bill                    ║");
            System.out.println("╚═════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid choice. Please enter a number 1-5.", true);
                continue;
            }

            switch (choice) {
                case 1 -> handleAddProduct();
                case 2 -> handleRemoveProduct();
                case 3 -> {
                    if (checkoutService.getCurrentBill().getItems().isEmpty()) {
                        printMessage("! No items in bill. Cancelling checkout.", true);
                        return;
                    }
                    finalizeCheckout();
                    return; // exit after finalizing
                }
                case 4 -> {
                    printMessage("! Checkout cancelled.", false);
                    return;
                }
                case 5 -> viewCurrentBill();
                default -> printMessage("! Invalid choice, try again.", true);
            }
        }
    }

    private void handleAddProduct() {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║             Add Product                 ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String code = scanner.nextLine().trim();

        Product p = productRepo.findByCode(code);
        if (p == null) {
            printMessage("! Product not found!", true);
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

        checkoutService.addProduct(p, qty);
        printMessage("+ Added " + qty + " x " + p.getName() + " (Unit price: " + p.getUnitPrice() + ")", false);
    }

    private void handleRemoveProduct() {
        Bill bill = checkoutService.getCurrentBill();
        if (bill.getItems().isEmpty()) {
            printMessage("! No items in the bill to remove.", true);
            return;
        }

        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║          Current Bill Items             ║");
        System.out.println("╠═════════════════════════════════════════╣");
        int index = 1;
        for (BillItem item : bill.getItems()) {
            System.out.printf("║ %d. %s x%d = %.2f%n",
                    index++, item.getProduct().getName(), item.getQuantity(), item.getLineTotal());
        }
        System.out.println("╚═════════════════════════════════════════╝");

        System.out.print("» Enter item number to remove: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > bill.getItems().size()) {
                printMessage("! Invalid item number.", true);
                return;
            }
            BillItem toRemove = bill.getItems().get(choice - 1);
            checkoutService.removeProduct(toRemove);
            printMessage("+ Removed " + toRemove.getProduct().getName() + " from bill.", false);
        } catch (NumberFormatException e) {
            printMessage("! Invalid input.", true);
        }
    }

    private void finalizeCheckout() {
        Bill bill = checkoutService.getCurrentBill();
        double total = bill.getTotalAmount();

        double cash = 0;
        while (true) {
            System.out.printf("» Enter cash tendered (Total: %.2f): ", total);
            try {
                cash = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid cash amount. Please enter a number.", true);
                continue;
            }

            if (cash == 0) {
                printMessage("! Checkout cancelled at payment step.", false);
                return;
            }

            if (cash < total) {
                printMessage("! Insufficient amount! Please enter at least the total.", true);
            } else {
                break;
            }
        }

        checkoutService.finalizeBill(cash);
        billPrinter.print(bill);
        printMessage("+ Checkout complete. Printing receipt...", false);

        System.out.print("\n» View receipt again? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            System.out.println("\n╔═════════════════════════════════════════╗");
            System.out.println("║           Receipt Reprint               ║");
            System.out.println("╚═════════════════════════════════════════╝");
            billPrinter.print(bill);
        }

        printMessage("+ Returning to Main Menu...", false);
    }

    private void viewCurrentBill() {
        Bill bill = checkoutService.getCurrentBill();
        if (bill.getItems().isEmpty()) {
            printMessage("! Bill is currently empty.", false);
            return;
        }

        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║          Current Bill Preview           ║");
        System.out.println("╚═════════════════════════════════════════╝");
        billPrinter.print(bill);
    }

    // A helper method for consistent framed messages with symbols
    private void printMessage(String message, boolean isError) {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.printf("║ %-39s ║%n", message);
        System.out.println("╚═════════════════════════════════════════╝");
        if (isError) {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }
}