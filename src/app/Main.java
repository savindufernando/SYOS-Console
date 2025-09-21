package app;

import cli.LoginMenu;
import cli.OnlineStoreMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Welcome to SYOS ===");
            System.out.println("1. SYOS Employee System");
            System.out.println("2. Online Store");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    // Redirect to SYOS System (Employee login)
                    new LoginMenu().start();
                    break;
                case 2:
                    // Redirect to Online Store (Customer login/signup)
                    new OnlineStoreMenu().start();
                    break;
                case 3:
                    System.out.println("👋 Thank you for visiting SYOS. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
}
