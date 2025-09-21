package db.repositories;

import db.DatabaseConnection;
import stock.repositories.ReorderLogRepository;

import java.sql.*;

public class ReorderLogRepositoryImpl implements ReorderLogRepository {
    @Override
    public void logReorder(int productId, int qty) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO reorder_log (product_id, current_qty) VALUES (?, ?)")) {
            ps.setInt(1, productId);
            ps.setInt(2, qty);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
