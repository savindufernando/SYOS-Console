package auth;

import db.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceIntegrationTest {

    private static AuthService authService;

    @BeforeAll
    static void setup() {
        // Uses real repository connected to DB
        authService = new AuthService();
    }

    @Test
    void shouldLoginSuccessfully_withValidCredentials() {
        // these must exist in your DB
        User user = authService.login("Savindu", "1234");
        assertNotNull(user);
        assertEquals("Cashier", user.getRole());   // ✅ user_levels working
    }

    @Test
    void shouldLoginSuccessfully_asManager() {
        // insert or ensure manager user exists
        User user = authService.login("Hirusha", "5678");
        assertNotNull(user);
        assertEquals("Manager", user.getRole());
    }

    @Test
    void shouldFailLogin_withInvalidCredentials() {
        User user = authService.login("unknown", "wrongpass");
        assertNull(user);  // ✅ invalid creds
    }
}
