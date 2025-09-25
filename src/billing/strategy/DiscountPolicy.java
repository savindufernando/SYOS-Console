package billing.strategy;

public interface DiscountPolicy {
    /**
     * Apply discount on the given base price
     * @param basePrice original price before discount
     * @return discounted price
     */
    double apply(double basePrice);
    String getName();
}
