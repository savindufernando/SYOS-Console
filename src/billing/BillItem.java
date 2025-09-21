package billing;

import stock.Product;
import billing.strategy.DiscountPolicy;
import billing.strategy.NoDiscount;
import billing.strategy.PercentageDiscount;
import billing.strategy.FixedDiscount;

public class BillItem {
    private Product product;
    private int quantity;
    private double lineTotal;
    private DiscountPolicy discount;
    private String discountName;
    private String discountType;   // "AMOUNT", "PERCENTAGE", or "NONE"
    private double discountValue;

    // === Constructor with discount (used in cashier system) ===
    public BillItem(Product product, int quantity, DiscountPolicy discount) {
        this.product = product;
        this.quantity = quantity;
        this.discount = discount;

        recalcLineTotal();
        setDiscountMetadata();
    }

    // === Constructor without discount (used in online store) ===
    public BillItem(Product product, int quantity) {
        this(product, quantity, new NoDiscount()); // defaults to no discount
    }

    // === Business logic ===
    private void recalcLineTotal() {
        double discountedPrice = discount.apply(product.getUnitPrice());
        this.lineTotal = discountedPrice * quantity;
    }

    private void setDiscountMetadata() {
        this.discountName = discount.toString();

        if (discount instanceof PercentageDiscount) {
            this.discountType = "PERCENTAGE";
            this.discountValue = ((PercentageDiscount) discount).getPercent();
        } else if (discount instanceof FixedDiscount) {
            this.discountType = "AMOUNT";
            this.discountValue = ((FixedDiscount) discount).getAmount();
        } else {
            this.discountType = "NONE";
            this.discountValue = 0;
        }
    }

    // === Setters ===
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalcLineTotal();
    }

    public void setDiscount(DiscountPolicy discount) {
        this.discount = discount;
        recalcLineTotal();
        setDiscountMetadata();
    }

    // === Getters ===
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return lineTotal; }
    public DiscountPolicy getDiscount() { return discount; }
    public String getDiscountName() { return discountName; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
}
