package billing.repositories;

import stock.Product;
import java.util.ArrayList;
import billing.strategy.DiscountPolicy;

public interface DiscountRepository {
    DiscountPolicy findActiveDiscountForProduct(Product product);
}
