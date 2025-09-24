package billing.commands;

import billing.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CheckoutCommandTest {

    private CheckoutService checkoutService;
    private CheckoutCommand command;

    @BeforeEach
    void setup() {
        checkoutService = mock(CheckoutService.class);
        command = new CheckoutCommand(checkoutService);
    }

    @Test
    void testExecuteCallsFinalizeBill() {
        command.execute(150.0);
        verify(checkoutService, times(1)).finalizeBill(150.0);
    }
}
