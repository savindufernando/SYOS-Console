package billing;

public class BillPrinter {
    public void print(Bill bill) {
        // use new structured bill number
        String formattedBillNo = bill.generateBillNumber("SYOS");

        System.out.println("\n=== SYOS BILL " + formattedBillNo + " ===");
        System.out.println("Date: " + bill.getBillDate());
        System.out.println("Cashier: " + bill.getCashierName());
        System.out.println("Transaction: " + bill.getTransactionType());
        System.out.println("---------------------------------");

        double subtotal = 0.0;
        double discountTotal = 0.0;

        for (BillItem item : bill.getItems()) {
            double unitPrice = item.getProduct().getUnitPrice();
            double discountedUnitPrice = item.getLineTotal() / item.getQuantity();

            double itemSubtotal = unitPrice * item.getQuantity();
            subtotal += itemSubtotal;

            double itemDiscount = itemSubtotal - item.getLineTotal();
            discountTotal += itemDiscount;

            if (itemDiscount > 0) {
                // show discount details
                System.out.printf("%s x%d @ %.2f = %.2f\n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        unitPrice,
                        itemSubtotal);
                System.out.printf("   ➝ After Discount: %.2f  (%s)\n",
                        item.getLineTotal(),
                        item.getDiscount().toString());
            } else {
                System.out.printf("%s x%d = %.2f\n",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        itemSubtotal);
            }
        }

        System.out.println("---------------------------------");
        System.out.printf("Subtotal (before discounts): %.2f\n", subtotal);
        System.out.printf("Total Discounts Applied: -%.2f\n", discountTotal);
        System.out.printf("Final Total (after discounts): %.2f\n", bill.getTotalAmount());
        System.out.println("---------------------------------");
        System.out.printf("Cash Tendered: %.2f\n", bill.getCashTendered());
        System.out.printf("Change: %.2f\n", bill.getChangeAmount());
        System.out.println("=================================\n");
    }
}
