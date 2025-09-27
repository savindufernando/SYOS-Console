package auth;

import db.repositories.UserRepository;

public class AuthService {
    private final UserRepository userRepo;

    // Constructor injection for testing
    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Default constructor for production use
    public AuthService() {
        this.userRepo = new UserRepository();
    }

    public User login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password);
    }
}
