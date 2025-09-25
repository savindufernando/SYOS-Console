package cli;

import auth.AuthService;
import auth.User;
import java.util.Scanner;

public class LoginMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void start() {
        // Top of the frame
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║          SYOS Login          ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║ Please enter your credentials. ║");
        System.out.println("╚══════════════════════════════╝");

        // User Input Prompts (outside the frame for cleanliness)
        System.out.println(" ");
        System.out.print("» Username: ");
        String user = scanner.nextLine();
        System.out.print("» Password: ");
        String pass = scanner.nextLine();
        System.out.println(" ");

        AuthService auth = new AuthService();
        User loggedUser = auth.login(user, pass);

        if (loggedUser != null) {
            // Framed success message with a symbol
            System.out.println("╔══════════════════════════════╗");
            System.out.println("║ + Welcome, " + loggedUser.getName());
            System.out.printf("║   (%s)%-16s ║%n", loggedUser.getRole(), "");
            System.out.println("╚══════════════════════════════╝");
            new MainMenu(loggedUser).start();
        } else {
            // Framed failure message with a symbol
            System.out.println("╔══════════════════════════════╗");
            System.out.println("║ ! Invalid credentials.       ║");
            System.out.println("╚══════════════════════════════╝");
        }
    }
}