package db.repositories;

import auth.User;
import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {

    public User findByUsernameAndPassword(String username, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            String sql = """
                SELECT u.user_id, u.username, u.password, u.name, l.level_name 
                FROM users u 
                JOIN user_levels l ON u.level_id = l.level_id
                WHERE u.username = ? AND u.password = ?
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("level_name")
                );
            }
        } catch (Exception e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }
        return null;
    }
    public User findById(int id) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            String sql = """
            SELECT u.user_id, u.username, u.password, u.name, l.level_name 
            FROM users u 
            JOIN user_levels l ON u.level_id = l.level_id
            WHERE u.user_id = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("level_name")
                );
            }
        } catch (Exception e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }
        return null;
    }

}
