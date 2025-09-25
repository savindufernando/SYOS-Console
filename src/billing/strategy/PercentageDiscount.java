package billing.strategy;

public class PercentageDiscount implements DiscountPolicy {
    private final double percent;

    public PercentageDiscount(double percent) {
        this.percent = percent;
    }

    @Override
    public double apply(double basePrice) {
        double discounted = basePrice * (1 - percent / 100.0);
        return Math.max(0, discounted);
    }

    public double getPercent() {
        return percent;
    }

    @Override
    public String toString() {
        return "Percentage Discount: " + percent + "% off";
    }

    @Override
    public String getName() {
        return percent + "% OFF";
    }
}
