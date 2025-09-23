package online;

import online.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1, "Savindu", "savindu123", "savindu@mail.com",
                "0771234567", "Colombo", "securePass");
    }

    @Test
    void testCustomerFieldsAreSetCorrectly() {
        assertEquals(1, customer.getId());
        assertEquals("Savindu", customer.getName());
        assertEquals("savindu123", customer.getUsername());
        assertEquals("savindu@mail.com", customer.getEmail());
        assertEquals("0771234567", customer.getPhoneNumber());
        assertEquals("Colombo", customer.getAddress());
        assertEquals("securePass", customer.getPassword());
    }

    @Test
    void testSetId() {
        customer.setId(10);
        assertEquals(10, customer.getId());
    }

    @Test
    void testCreateCustomerWithoutIdDefaultsToZero() {
        Customer newCustomer = new Customer("John", "john123", "john@mail.com",
                "0777654321", "Kandy", "johnPass");
        assertEquals(0, newCustomer.getId());
    }
}
