package online.repositories;

import online.Customer;

public interface CustomerRepository {
    boolean save(Customer customer);
    Customer login(String username, String password);
    Customer findById(int id);
}
