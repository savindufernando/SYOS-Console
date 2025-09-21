package billing.commands;

import billing.CheckoutService;

public class CheckoutCommand {
    private final CheckoutService checkoutService;

    public CheckoutCommand(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public void execute(double cashTendered) {
        checkoutService.finalizeBill(cashTendered);
    }
}
