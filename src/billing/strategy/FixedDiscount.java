package billing.strategy;

public class FixedDiscount implements DiscountPolicy {
    private final double amount;

    public FixedDiscount(double amount) {
        this.amount = amount;
    }

    @Override
    public double apply(double basePrice) {
        double discounted = basePrice - amount;
        return Math.max(0, discounted);
    }

    public double getAmount() {   // ✅ Added
        return amount;
    }

    @Override
    public String toString() {
        return "Fixed Discount: -" + amount;
    }
}
