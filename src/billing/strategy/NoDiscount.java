package billing.strategy;

public class NoDiscount implements DiscountPolicy {
    @Override
    public double apply(double basePrice) {
        return basePrice; // no change
    }

    @Override
    public String toString() {
        return "No Discount";
    }

    @Override
    public String getName() {
        return "No Discount";
    }
}
