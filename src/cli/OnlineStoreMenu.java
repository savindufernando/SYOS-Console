package cli;

import online.Customer;
import online.repositories.CustomerRepository;
import db.repositories.CustomerRepositoryImpl;

import java.util.Scanner;

public class OnlineStoreMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final CustomerRepository customerRepo = new CustomerRepositoryImpl();

    @Override
    public void start() {
        while (true) {
            System.out.println("\n=== Online Store ===");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> login();
                case 2 -> signup();
                case 3 -> {
                    return; // back to Main
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Customer customer = customerRepo.login(username, password);
        if (customer != null) {
            System.out.println("✅ Welcome back, " + customer.getName());
            new CustomerMainMenu(customer).start(); // ⬅️ go to online store dashboard
        } else {
            System.out.println("❌ Invalid credentials. Please try again.");
        }
    }


    private void signup() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Customer customer = new Customer(name, username, email, phone, address, password);
        boolean success = customerRepo.save(customer);

        if (success) {
            System.out.println("✅ Account created successfully! You can now login.");
        } else {
            System.out.println("❌ Signup failed. Username or Email may already exist.");
        }
    }
}
