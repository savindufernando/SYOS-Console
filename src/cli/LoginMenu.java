package cli;

import auth.AuthService;
import auth.User;
import java.util.Scanner;

public class LoginMenu implements Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void start() {
        System.out.println("=== SYOS Login ===");
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        AuthService auth = new AuthService();
        User loggedUser = auth.login(user, pass);

        if (loggedUser != null) {
            System.out.println("✅ Welcome, " + loggedUser.getName() + " (" + loggedUser.getRole() + ")");
            new MainMenu(loggedUser).start();
        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }
}
