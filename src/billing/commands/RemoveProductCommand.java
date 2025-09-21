package billing.commands;

import billing.BillItem;
import billing.CheckoutService;

public class RemoveProductCommand {
    private final CheckoutService checkoutService;

    public RemoveProductCommand(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public void execute(BillItem item) {
        checkoutService.removeProduct(item);
    }
}
