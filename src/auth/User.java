package auth;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role; // comes from user_levels

    public User(int id, String username, String password, String name, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
