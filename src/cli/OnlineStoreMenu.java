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
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║            Online Store                ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Login                               ║");
            System.out.println("║ 2. Signup                              ║");
            System.out.println("╟────────────────────────────────────────╢");
            System.out.println("║ 3. Back to Main Menu                   ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("» Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid input. Please enter a number.", true);
                continue;
            }

            switch (choice) {
                case 1 -> login();
                case 2 -> signup();
                case 3 -> {
                    return; // back to Main
                }
                default -> printMessage("! Invalid choice. Try again.", true);
            }
        }
    }

    private void login() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║               Customer Login           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.print("» Username: ");
        String username = scanner.nextLine();
        System.out.print("» Password: ");
        String password = scanner.nextLine();
        System.out.println("╚════════════════════════════════════════╝");

        Customer customer = customerRepo.login(username, password);
        if (customer != null) {
            printMessage("+ Welcome back, " + customer.getName(), false);
            new CustomerMainMenu(customer).start();
        } else {
            printMessage("! Invalid credentials. Please try again.", true);
        }
    }


    private void signup() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              New Account Signup        ║");
        System.out.println("║   (Enter 0 at any field to cancel)     ║");
        System.out.println("╠════════════════════════════════════════╣");

        String name = promptRequired("» Name: ");
        if (name == null) return;

        String username = promptRequired("» Username: ");
        if (username == null) return;

        String email = promptRequired("» Email: ");
        if (email == null) return;

        String phone = promptRequired("» Phone Number: ");
        if (phone == null) return;

        String address = promptRequired("» Address: ");
        if (address == null) return;

        String password = promptRequired("» Password: ");
        if (password == null) return;

        System.out.println("╚════════════════════════════════════════╝");

        Customer customer = new Customer(name, username, email, phone, address, password);
        boolean success = customerRepo.save(customer);

        if (success) {
            printMessage("+ Account created successfully! You can now login.", false);
        } else {
            printMessage("! Signup failed. Username or Email may already exist.", true);
        }
    }

    /**
     * Utility method to enforce required input with cancel option.
     * Returns null if user enters "0".
     */
    private String promptRequired(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("! Signup cancelled. Returning to main menu...");
                return null;
            }

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("! This field is required. Please try again or enter 0 to cancel.");
        }
    }

    private void printMessage(String message, boolean isError) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.printf("║ %-38s ║%n", message);
        System.out.println("╚════════════════════════════════════════╝");
        if (isError) {
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }
}