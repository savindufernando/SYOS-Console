package online;

import online.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRepositoryTest {

    private CustomerRepository repo;
    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(CustomerRepository.class);
        mockCustomer = new Customer(1, "Savindu", "savindu123", "savindu@mail.com",
                "0771234567", "Colombo", "securePass");
    }

    @Test
    void testSaveCustomer() {
        when(repo.save(mockCustomer)).thenReturn(true);

        boolean result = repo.save(mockCustomer);
        assertTrue(result);

        verify(repo, times(1)).save(mockCustomer);
    }

    @Test
    void testLoginSuccess() {
        when(repo.login("savindu123", "securePass")).thenReturn(mockCustomer);

        Customer loggedIn = repo.login("savindu123", "securePass");
        assertNotNull(loggedIn);
        assertEquals("Savindu", loggedIn.getName());
    }

    @Test
    void testLoginFailure() {
        when(repo.login("wrongUser", "wrongPass")).thenReturn(null);

        Customer loggedIn = repo.login("wrongUser", "wrongPass");
        assertNull(loggedIn);
    }

    @Test
    void testFindById() {
        when(repo.findById(1)).thenReturn(mockCustomer);

        Customer found = repo.findById(1);
        assertEquals("Savindu", found.getName());
        assertEquals("Colombo", found.getAddress());
    }
}
