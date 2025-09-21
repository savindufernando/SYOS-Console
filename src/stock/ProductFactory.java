package stock;

import java.time.LocalDate;

public class ProductFactory {
    public static Product createProduct(String type, String code, String name, double price, LocalDate expiry) {
        if ("PERISHABLE".equalsIgnoreCase(type)) {
            return new PerishableProduct(code, name, price, expiry);
        } else {
            return new NonPerishableProduct(code, name, price);
        }
    }
}
