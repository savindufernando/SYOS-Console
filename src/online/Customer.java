package online;

public class Customer {
    private int id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;

    public Customer(int id, String name, String username, String email, String phoneNumber, String address, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    public Customer(String name, String username, String email, String phoneNumber, String address, String password) {
        this(0, name, username, email, phoneNumber, address, password);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }

    // Setters
    public void setId(int id) { this.id = id; }
}
