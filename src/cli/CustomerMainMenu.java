package cli;

import billing.Bill;
import billing.BillItem;
import db.repositories.BillRepositoryImpl;
import online.Customer;
import stock.Product;
import stock.repositories.ProductRepository;
import db.repositories.ProductRepositoryImpl;

import java.util.*;

public class CustomerMainMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final Customer customer;
    private final db.repositories.OnlineBatchRepositoryImpl onlineBatchRepo = new db.repositories.OnlineBatchRepositoryImpl();
    private final ProductRepository productRepo = new ProductRepositoryImpl();
    private final BillRepositoryImpl billRepo = new BillRepositoryImpl();
    private final List<BillItem> cart = new ArrayList<>();

    public CustomerMainMenu(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void start() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         Online Store Dashboard         ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.printf("║ Welcome, %-27s║%n", customer.getName());
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 1. View Products                       ║");
            System.out.println("║ 2. Search Product                      ║");
            System.out.println("║ 3. View Cart                           ║");
            System.out.println("║ 4. Checkout                            ║");
            System.out.println("║ 5. Logout                              ║");
            System.out.println("║ 6. View Purchase History               ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid input.", true);
                continue;
            }

            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> searchProduct();
                case 3 -> viewCart();
                case 4 -> checkout();
                case 5 -> {
                    printMessage("-> Logging out...", false);
                    return;
                }
                case 6 -> viewPurchaseHistory();
                default -> printMessage("! Invalid choice.", true);
            }
        }
    }


    private void viewProducts() {
        List<stock.batch.OnlineBatch> batches = onlineBatchRepo.findAvailable();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     Available Products (Online)        ║");
        System.out.println("╠════════════════════════════════════════╣");
        for (stock.batch.OnlineBatch b : batches) {
            Product p = productRepo.findById(b.getProductId());
            if (p != null) {
                System.out.printf("║ %-10s | %-12s | LKR %-6.2f | Qty: %-3d ║%n",
                        p.getCode(), p.getName(), p.getUnitPrice(), b.getQuantity());
            }
        }
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("» Add to cart? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            addToCart();
        }
    }


    private void searchProduct() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           Search Products              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product name or code: ");
        String keyword = scanner.nextLine();

        List<Product> results = productRepo.search(keyword);
        if (results.isEmpty()) {
            printMessage("! No products found.", false);
            return;
        }

        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n--- Search Results ---");
        for (Product p : results) {
            System.out.printf("%s | %s | LKR %.2f%n", p.getCode(), p.getName(), p.getUnitPrice());
        }

        System.out.print("» Add one to cart? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            addToCart();
        }
    }

    private void addToCart() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║             Add to Cart                ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Enter product code: ");
        String code = scanner.nextLine();
        Product product = productRepo.findByCode(code);
        if (product == null) {
            printMessage("! Invalid product code.", true);
            return;
        }

        System.out.print("» Enter quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid quantity. Please enter a number.", true);
            return;
        }

        if (onlineBatchRepo.reduceStock(product.getId(), qty)) {
            BillItem item = new BillItem(product, qty);
            cart.add(item);
            printMessage("+ Added " + qty + "x " + product.getName() + " to cart.", false);
        } else {
            printMessage("! Not enough stock in online store.", false);
        }
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            printMessage("! Cart is empty.", false);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Your Cart                 ║");
        System.out.println("╠════════════════════════════════════════╣");
        double total = 0;
        for (int i = 0; i < cart.size(); i++) {
            BillItem item = cart.get(i);
            System.out.printf("║ %-2d. %s | Qty: %-3d | Total: LKR %.2f ║%n",
                    (i + 1), item.getProduct().getName(), item.getQuantity(), item.getLineTotal());
            total += item.getLineTotal();
        }
        System.out.println("╟────────────────────────────────────────╢");
        System.out.printf("║ Total: LKR %-27.2f║%n", total);
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("» 1. Remove item | 2. Edit quantity | 3. Back: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printMessage("! Invalid choice.", true);
            return;
        }

        if (choice == 1) {
            System.out.print("» Enter item number to remove: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < cart.size()) {
                cart.remove(idx);
                printMessage("+ Item removed.", false);
            }
        } else if (choice == 2) {
            System.out.print("» Enter item number to edit: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < cart.size()) {
                System.out.print("» Enter new quantity: ");
                int newQty = Integer.parseInt(scanner.nextLine());
                cart.get(idx).setQuantity(newQty);
                printMessage("+ Quantity updated.", false);
            }
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            printMessage("! Cart is empty.", false);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║                Checkout                ║");
        System.out.println("╠════════════════════════════════════════╣");
        double total = cart.stream().mapToDouble(BillItem::getLineTotal).sum();

        System.out.printf("║ Total to be charged: LKR %-11.2f║%n", total);
        System.out.println("╟────────────────────────────────────────╢");
        System.out.print("» Enter Card Holder Name: ");
        String holder = scanner.nextLine();
        System.out.print("» Enter Card Number (16 digits): ");
        String cardNum = scanner.nextLine();
        String masked = "**** **** **** " + cardNum.substring(cardNum.length() - 4);
        System.out.println("╚════════════════════════════════════════╝");

        Bill bill = new Bill(customer.getId());
        bill.setTransactionType("ONLINE");
        bill.setCustomerId(customer.getId());
        bill.setPaymentMethod("CARD");
        bill.setTotalAmount(total);
        bill.setCardHolder(holder);
        bill.setCardNumberMasked(masked);
        bill.setCashTendered(total);
        bill.setChangeAmount(0);

        for (BillItem item : cart) {
            bill.addItem(item);
        }

        int billId = billRepo.save(bill);

        db.repositories.BillItemRepositoryImpl billItemRepo = new db.repositories.BillItemRepositoryImpl();
        for (BillItem item : cart) {
            billItemRepo.save(billId, item);
        }

        printMessage("+ Checkout complete. Bill No: " + bill.getBillNumber(), false);
        cart.clear();
    }

    private void viewPurchaseHistory() {
        List<Bill> bills = billRepo.findByCustomerId(customer.getId());

        if (bills.isEmpty()) {
            printMessage("! You have no past purchases.", false);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Your Purchase History         ║");
        System.out.println("╚════════════════════════════════════════╝");
        for (Bill bill : bills) {
            System.out.println("\nBill No: " + bill.getBillNumber());
            System.out.println("Date: " + bill.getBillDate());
            System.out.println("Total: LKR " + bill.getTotalAmount());
            System.out.println("Payment Method: " + bill.getPaymentMethod());

            List<BillItem> items = new db.repositories.BillItemRepositoryImpl().findByBillId(bill.getBillId());
            if (items.isEmpty()) {
                System.out.println("   (No items found for this bill)");
            } else {
                System.out.println("   Purchased Items:");
                for (BillItem item : items) {
                    System.out.printf("     - %s x%d = LKR %.2f%n",
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getLineTotal());
                }
            }
            System.out.println("----------------------------------------");
        }
    }

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