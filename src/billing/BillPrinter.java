package billing;

import auth.User;
import db.repositories.UserRepository;

public class BillPrinter {
    private final UserRepository userRepo;

    // ✅ Pass in a UserRepository when creating BillPrinter
    public BillPrinter(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void print(Bill bill) {
        String formattedBillNo = bill.generateBillNumber("SYOS");

        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║                SYOS BILL                ║");
        System.out.printf("║ Bill No: %-29s ║%n", formattedBillNo);
        System.out.println("╟─────────────────────────────────────────╢");
        System.out.printf("║ Date: %-32s ║%n", bill.getBillDate());

        // ✅ Fetch user name from repo
        String cashierName = "N/A";
        if (bill.getUserId() != null) {
            User user = userRepo.findById(bill.getUserId());
            if (user != null) {
                cashierName = user.getName();  // show real/display name
            }
        }
        System.out.printf("║ User: %-32s ║%n", cashierName);

        System.out.printf("║ Transaction: %-25s ║%n", bill.getTransactionType());
        System.out.println("╠═════════════════════════════════════════╣");

        double subtotal = 0.0;
        double discountTotal = 0.0;

        for (BillItem item : bill.getItems()) {
            double unitPrice = item.getProduct().getUnitPrice();
            double itemSubtotal = unitPrice * item.getQuantity();
            subtotal += itemSubtotal;
            double itemDiscount = itemSubtotal - item.getLineTotal();
            discountTotal += itemDiscount;

            System.out.printf("║ %-39s ║%n", item.getProduct().getName());
            if (itemDiscount > 0) {
                System.out.printf("║   %-5d x LKR %-7.2f = LKR %-7.2f║%n",
                        item.getQuantity(), unitPrice, itemSubtotal);
                System.out.printf("║   -> Discount: %.2f (%s)        ║%n",
                        itemDiscount, item.getDiscount().getName());
                System.out.printf("║   Final Price: LKR %.2f         ║%n", item.getLineTotal());
            } else {
                System.out.printf("║   %-5d x LKR %-7.2f = LKR %-7.2f║%n",
                        item.getQuantity(), unitPrice, itemSubtotal);
            }
        }

        System.out.println("╠═════════════════════════════════════════╣");
        System.out.printf("║ Subtotal (before discounts): LKR %-7.2f ║%n", subtotal);
        System.out.printf("║ Total Discounts Applied:    -LKR %-7.2f ║%n", discountTotal);
        System.out.printf("║ FINAL TOTAL: LKR %-19.2f ║%n", bill.getTotalAmount());
        System.out.println("╟─────────────────────────────────────────╢");
        System.out.printf("║ Cash Tendered: LKR %-18.2f ║%n", bill.getCashTendered());
        System.out.printf("║ Change: LKR %-25.2f ║%n", bill.getChangeAmount());
        System.out.println("╚═════════════════════════════════════════╝");
    }
}
