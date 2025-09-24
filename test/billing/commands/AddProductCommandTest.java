package billing.commands;

import billing.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stock.Product;

import static org.mockito.Mockito.*;

class AddProductCommandTest {

    static class DummyProduct extends Product {
        public DummyProduct(String code, String name, double unitPrice) {
            super(code, name, unitPrice);
        }
    }

    private CheckoutService checkoutService;
    private AddProductCommand command;

    @BeforeEach
    void setup() {
        checkoutService = mock(CheckoutService.class);
        command = new AddProductCommand(checkoutService);
    }

    @Test
    void testExecuteCallsCheckoutService() {
        Product milk = new DummyProduct("P001", "Milk", 50.0);
        command.execute(milk, 2);

        verify(checkoutService, times(1)).addProduct(milk, 2);
    }
}
