package db.repositories;

import online.Customer;
import online.repositories.CustomerRepository;
import db.DatabaseConnection;

import java.sql.*;

public class CustomerRepositoryImpl implements CustomerRepository {
    @Override
    public boolean save(Customer customer) {
        String sql = "INSERT INTO customers (cus_name, username, cus_email, phone_number, cus_address, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getUsername());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhoneNumber());
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getPassword());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error saving customer: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Customer login(String username, String password) {
        String sql = "SELECT * FROM customers WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("cus_name"),
                        rs.getString("username"),
                        rs.getString("cus_email"),
                        rs.getString("phone_number"),
                        rs.getString("cus_address"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error during login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("cus_name"),
                        rs.getString("username"),
                        rs.getString("cus_email"),
                        rs.getString("phone_number"),
                        rs.getString("cus_address"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding customer: " + e.getMessage());
        }
        return null;
    }
}
