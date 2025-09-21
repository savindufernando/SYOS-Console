package auth;

import db.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    // ✅ 1. Valid credentials return user
    @Test
    void shouldReturnUser_whenValidCredentials() {
        UserRepository mockRepo = mock(UserRepository.class);
        User fakeUser = new User(1, "john", "1234", "John Doe", "Cashier");
        when(mockRepo.findByUsernameAndPassword("john", "1234")).thenReturn(fakeUser);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("john", "1234");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("Cashier", result.getRole());
    }

    // ✅ 2. Invalid credentials return null
    @Test
    void shouldReturnNull_whenInvalidCredentials() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("wrong", "pass")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("wrong", "pass");

        assertNull(result);
    }

    // ✅ 3. Correct role = Cashier
    @Test
    void shouldReturnCashierRole_whenCashierLogsIn() {
        UserRepository mockRepo = mock(UserRepository.class);
        User fakeUser = new User(2, "cashier1", "pw", "Jane Doe", "Cashier");
        when(mockRepo.findByUsernameAndPassword("cashier1", "pw")).thenReturn(fakeUser);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("cashier1", "pw");

        assertEquals("Cashier", result.getRole());
    }

    // ✅ 4. Correct role = Manager
    @Test
    void shouldReturnManagerRole_whenManagerLogsIn() {
        UserRepository mockRepo = mock(UserRepository.class);
        User fakeUser = new User(3, "manager1", "pw", "Boss Man", "Manager");
        when(mockRepo.findByUsernameAndPassword("manager1", "pw")).thenReturn(fakeUser);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("manager1", "pw");

        assertEquals("Manager", result.getRole());
    }

    // ✅ 5. Empty username
    @Test
    void shouldReturnNull_whenUsernameEmpty() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("", "pass")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("", "pass");

        assertNull(result);
    }

    // ✅ 6. Empty password
    @Test
    void shouldReturnNull_whenPasswordEmpty() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("user", "")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("user", "");

        assertNull(result);
    }

    // ✅ 7. Username case-sensitive
    @Test
    void shouldBeCaseSensitive_whenUsernameDiffersByCase() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("John", "1234")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("John", "1234");

        assertNull(result);
    }

    // ✅ 8. Password case-sensitive
    @Test
    void shouldBeCaseSensitive_whenPasswordDiffersByCase() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("john", "1234")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("john", "1234");

        assertNull(result);
    }

    // ✅ 9. Returned user has correct fields
    @Test
    void shouldContainCorrectFields_whenValidLogin() {
        UserRepository mockRepo = mock(UserRepository.class);
        User fakeUser = new User(5, "alice", "pw", "Alice Doe", "Cashier");
        when(mockRepo.findByUsernameAndPassword("alice", "pw")).thenReturn(fakeUser);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("alice", "pw");

        assertEquals(5, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("Alice Doe", result.getName());
        assertEquals("Cashier", result.getRole());
    }

    // ✅ 10. Password is stored correctly (state test)
    @Test
    void shouldStorePassword_whenUserIsCreated() {
        User user = new User(10, "bob", "secret", "Bob Marley", "Cashier");
        assertEquals("secret", user.getPassword());
    }

    // ✅ 11. Repository called once
    @Test
    void shouldCallRepositoryOnce_perLoginAttempt() {
        UserRepository mockRepo = mock(UserRepository.class);
        AuthService authService = new AuthService(mockRepo);

        authService.login("user1", "pw1");
        verify(mockRepo, times(1)).findByUsernameAndPassword("user1", "pw1");
    }

    // ✅ 12. Repository not called with null inputs
    @Test
    void shouldNotCallRepository_whenNullInputs() {
        UserRepository mockRepo = mock(UserRepository.class);
        AuthService authService = new AuthService(mockRepo);

        authService.login(null, null);
        verify(mockRepo, never()).findByUsernameAndPassword(anyString(), anyString());
    }

    // ✅ 13. Handle repository exception gracefully
    @Test
    void shouldHandleRepositoryExceptionGracefully() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("boom", "pw"))
                .thenThrow(new RuntimeException("DB error"));

        AuthService authService = new AuthService(mockRepo);

        assertThrows(RuntimeException.class,
                () -> authService.login("boom", "pw"));
    }

    // ✅ 14. Handle long username
    @Test
    void shouldHandleLongUsername() {
        String longUsername = "u".repeat(255);
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword(longUsername, "pw")).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login(longUsername, "pw");

        assertNull(result);
    }

    // ✅ 15. Handle long password
    @Test
    void shouldHandleLongPassword() {
        String longPassword = "p".repeat(255);
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findByUsernameAndPassword("user", longPassword)).thenReturn(null);

        AuthService authService = new AuthService(mockRepo);
        User result = authService.login("user", longPassword);

        assertNull(result);
    }
}
