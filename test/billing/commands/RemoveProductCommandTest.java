package billing.commands;

import billing.*;
import billing.strategy.NoDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.Product;

import static org.mockito.Mockito.*;

class RemoveProductCommandTest {

    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    private CheckoutService checkoutService;
    private RemoveProductCommand command;

    @BeforeEach
    void setup() {
        checkoutService = mock(CheckoutService.class);
        command = new RemoveProductCommand(checkoutService);
    }

    @Test
    void testExecuteCallsCheckoutService() {
        Product bread = new DummyProduct("P002", "Bread", 40.0);
        BillItem item = new BillItem(bread, 2, new NoDiscount());

        command.execute(item);

        verify(checkoutService, times(1)).removeProduct(item);
    }
}
