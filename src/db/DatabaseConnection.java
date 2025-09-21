package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection instance;
    private static final String URL = "jdbc:mysql://localhost:3306/syos";
    private static final String USERNAME = "java-user";   // adjust if needed
    private static final String PASSWORD = "java1234";    // adjust if needed

    private DatabaseConnection() { }

    public static Connection getInstance() throws SQLException {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found: " + e.getMessage());
        }
        return instance;
    }
}
