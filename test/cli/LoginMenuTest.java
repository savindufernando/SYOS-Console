package cli;

import auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class LoginMenuTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // ⚡ Fake AuthService (shadows the real one ONLY in tests)
    public static class AuthService extends auth.AuthService {
        @Override
        public User login(String username, String password) {
            if ("alice".equalsIgnoreCase(username) && "pw".equals(password)) {
                return new User(1, "Alice", "CASHIER", "alice@s.com", "pw");
            }
            return null;
        }
    }

    // ⚡ Fake MainMenu (so we don’t launch the real one)
    public static class MainMenu extends cli.MainMenu {
        private final User user;
        public MainMenu(User user) {
            super(user);
            this.user = user;
        }
        @Override
        public void start() {
            System.out.println(">> Fake MainMenu started for " + user.getName());
        }
    }


    @Test
    void testLogin_Failure() {
        String input = "bob\nwrongpw\n"; // invalid credentials
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        new LoginMenu().start();

        String out = outputStream.toString().toLowerCase();
        assertTrue(out.contains("invalid credentials"), "Expected invalid login message but got:\n" + out);
    }
}
