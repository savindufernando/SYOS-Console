package app;

import cli.LoginMenu;
import cli.OnlineStoreMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Main menu UI
            System.out.println("\n╔═════════════════════════════════════════╗");
            System.out.println("║         Welcome to SYOS                 ║");
            System.out.println("╠═════════════════════════════════════════╣");
            System.out.println("║ 1. SYOS Employee System                 ║");
            System.out.println("║ 2. Online Store                         ║");
            System.out.println("╟─────────────────────────────────────────╢");
            System.out.println("║ 3. Exit                                 ║");
            System.out.println("╚═════════════════════════════════════════╝");
            System.out.print("» Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printMessage("! Invalid input. Please enter a number.", true);
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
                    printMessage("-> Thank you for visiting SYOS. Goodbye!", false);
                    return;
                default:
                    printMessage("! Invalid choice. Try again.", true);
            }
        }
    }

    // A helper method for consistent framed messages with symbols
    private static void printMessage(String message, boolean isError) {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.printf("║ %-39s ║%n", message);
        System.out.println("╚═════════════════════════════════════════╝");
        if (isError) {
            System.out.println("Press Enter to continue...");
            new Scanner(System.in).nextLine();
        }
    }
}