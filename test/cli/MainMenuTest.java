package cli;

import auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainMenuTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }


    @Test
    void testCashierMenu_Checkout() {
        String input = "1\n2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        User cashier = new User(8, "Cashier", "CASHIER", "c@s.com", "pw");
        new MainMenu(cashier).start();

        String out = outputStream.toString().toLowerCase();
        assertTrue(out.contains("fake checkoutmenu started"));
        assertTrue(out.contains("logging out"));
    }

    @Test
    void testCashierMenu_InvalidChoice() {
        String input = "9\n\n2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        User cashier = new User(9, "Cashier", "CASHIER", "c@s.com", "pw");
        new MainMenu(cashier).start();

        assertTrue(outputStream.toString().toLowerCase().contains("invalid choice"));
    }
}

// ==================================================
// ⚡ Fake menus (shadow production ones during tests)
// ==================================================

class CheckoutMenu implements Menu {
    public CheckoutMenu(User u) {}
    @Override public void start() {
        System.out.println(">> Fake CheckoutMenu started");
    }
}

class InventoryMenu implements Menu {
    public InventoryMenu(Object... args) {}
    @Override public void start() {
        System.out.println(">> Fake InventoryMenu started");
    }
}

class ShelfMenu implements Menu {
    public ShelfMenu(Object... args) {}
    @Override public void start() {
        System.out.println(">> Fake ShelfMenu started");
    }
}

class ReceiptHistoryMenu implements Menu {
    @Override public void start() {
        System.out.println(">> Fake ReceiptHistoryMenu started");
    }
}

class ReportMenu implements Menu {
    @Override public void start() {
        System.out.println(">> Fake ReportMenu started");
    }
}

class DiscountMenu implements Menu {
    @Override public void start() {
        System.out.println(">> Fake DiscountMenu started");
    }
}
