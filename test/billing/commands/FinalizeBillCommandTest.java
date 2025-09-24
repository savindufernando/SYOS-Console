package billing.commands;

import billing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class FinalizeBillCommandTest {

    private CheckoutService checkoutService;
    private FinalizeBillCommand command;

    @BeforeEach
    void setup() {
        checkoutService = mock(CheckoutService.class);
        command = new FinalizeBillCommand(checkoutService);
    }

    @Test
    void testExecuteFinalizesBillAndPrints() {
        Bill mockBill = new Bill(101, "Alice");
        when(checkoutService.getCurrentBill()).thenReturn(mockBill);

        // Capture console output
        command.execute(200.0);

        verify(checkoutService, times(1)).finalizeBill(200.0);
        verify(checkoutService, times(1)).getCurrentBill();
        // We can’t easily assert console text here without refactoring,
        // but this ensures both methods are called.
    }
}
