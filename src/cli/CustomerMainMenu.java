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
    private final ProductRepository productRepo = new ProductRepositoryImpl();
    private final BillRepositoryImpl billRepo = new BillRepositoryImpl();
    private final List<BillItem> cart = new ArrayList<>();

    public CustomerMainMenu(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Online Store Dashboard ===");
            System.out.println("Welcome, " + customer.getName());
            System.out.println("1. View Products");
            System.out.println("2. Search Product");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input.");
                continue;
            }

            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> searchProduct();
                case 3 -> viewCart();
                case 4 -> checkout();
                case 5 -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void viewProducts() {
        List<Product> products = productRepo.findAll();
        System.out.println("\n=== Available Products ===");
        for (Product p : products) {
            System.out.println(p.getCode() + " | " + p.getName() + " | LKR " + p.getUnitPrice());
        }

        System.out.print("Do you want to add to cart? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            addToCart();
        }
    }

    private void searchProduct() {
        System.out.print("Enter product name or code: ");
        String keyword = scanner.nextLine();

        List<Product> results = productRepo.search(keyword);
        if (results.isEmpty()) {
            System.out.println("❌ No products found.");
            return;
        }

        for (Product p : results) {
            System.out.println(p.getCode() + " | " + p.getName() + " | LKR " + p.getUnitPrice());
        }

        System.out.print("Do you want to add one of these to cart? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            addToCart();
        }
    }

    private void addToCart() {
        System.out.print("Enter product code: ");
        String code = scanner.nextLine();
        Product product = productRepo.findByCode(code);
        if (product == null) {
            System.out.println("❌ Invalid product code.");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine());

        BillItem item = new BillItem(product, qty);
        cart.add(item);
        System.out.println("✅ Added " + qty + "x " + product.getName() + " to cart.");
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("🛒 Cart is empty.");
            return;
        }

        System.out.println("\n=== Your Cart ===");
        double total = 0;
        for (int i = 0; i < cart.size(); i++) {
            BillItem item = cart.get(i);
            System.out.println((i + 1) + ". " + item.getProduct().getName() + " | Qty: " + item.getQuantity() + " | Total: " + item.getLineTotal());
            total += item.getLineTotal();
        }
        System.out.println("Total: LKR " + total);

        System.out.println("1. Remove item");
        System.out.println("2. Edit quantity");
        System.out.println("3. Back");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            System.out.print("Enter item number to remove: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < cart.size()) {
                cart.remove(idx);
                System.out.println("✅ Item removed.");
            }
        } else if (choice == 2) {
            System.out.print("Enter item number to edit: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < cart.size()) {
                System.out.print("Enter new quantity: ");
                int newQty = Integer.parseInt(scanner.nextLine());
                cart.get(idx).setQuantity(newQty);
                System.out.println("✅ Quantity updated.");
            }
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("🛒 Cart is empty.");
            return;
        }

        double total = cart.stream().mapToDouble(BillItem::getLineTotal).sum();
        Bill bill = new Bill(customer.getId());
        bill.setTransactionType("ONLINE");
        bill.setCustomerId(customer.getId());
        bill.setPaymentMethod("CARD");
        bill.setTotalAmount(total);


        System.out.print("Enter Card Holder Name: ");
        String holder = scanner.nextLine();
        System.out.print("Enter Card Number (16 digits): ");
        String cardNum = scanner.nextLine();
        String masked = "**** **** **** " + cardNum.substring(cardNum.length() - 4);

        bill.setCardHolder(holder);
        bill.setCardNumberMasked(masked);
        bill.setCashTendered(total); // fully paid
        bill.setChangeAmount(0);

        for (BillItem item : cart) {
            bill.addItem(item);
        }

        billRepo.save(bill);

        System.out.println("✅ Checkout complete. Bill ID: " + bill.getBillId());
        cart.clear();
    }
}
