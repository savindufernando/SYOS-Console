package billing.commands;

import billing.CheckoutService;
import stock.Product;

public class AddProductCommand {
    private final CheckoutService checkoutService;

    public AddProductCommand(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public void execute(Product product, int qty) {
        checkoutService.addProduct(product, qty);
    }
}
