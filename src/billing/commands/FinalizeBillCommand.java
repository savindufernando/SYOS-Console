package billing.commands;

import billing.Bill;
import billing.BillPrinter;
import billing.CheckoutService;

public class FinalizeBillCommand {
    private final CheckoutService checkoutService;
    private final BillPrinter billPrinter = new BillPrinter();

    public FinalizeBillCommand(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public void execute(double cashTendered) {
        checkoutService.finalizeBill(cashTendered);
        Bill bill = checkoutService.getCurrentBill();
        billPrinter.print(bill);
    }
}
