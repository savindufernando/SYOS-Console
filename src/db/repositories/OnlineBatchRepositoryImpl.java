package db.repositories;

import db.DatabaseConnection;
import stock.batch.OnlineBatch;
import stock.repositories.OnlineBatchRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineBatchRepositoryImpl implements OnlineBatchRepository {
    @Override
    public void save(OnlineBatch batch) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO online_batches (product_id, quantity, last_restocked) VALUES (?, ?, ?)")) {
            ps.setInt(1, batch.getProductId());
            ps.setInt(2, batch.getQuantity());
            if (batch.getLastRestocked() != null) {
                ps.setDate(3, Date.valueOf(batch.getLastRestocked()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(OnlineBatch batch) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE online_batches SET quantity=?, last_restocked=? WHERE online_batch_id=?")) {
            ps.setInt(1, batch.getQuantity());
            if (batch.getLastRestocked() != null) {
                ps.setDate(2, Date.valueOf(batch.getLastRestocked()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setInt(3, batch.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public OnlineBatch findByProduct(int productId) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM online_batches WHERE product_id=?")) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                OnlineBatch b = new OnlineBatch();
                b.setId(rs.getInt("online_batch_id"));
                b.setProductId(rs.getInt("product_id"));
                b.setQuantity(rs.getInt("quantity"));
                Date d = rs.getDate("last_restocked");
                if (d != null) b.setLastRestocked(d.toLocalDate());
                return b;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<OnlineBatch> findAll() {
        List<OnlineBatch> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM online_batches")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OnlineBatch b = new OnlineBatch();
                b.setId(rs.getInt("online_batch_id"));
                b.setProductId(rs.getInt("product_id"));
                b.setQuantity(rs.getInt("quantity"));
                Date d = rs.getDate("last_restocked");
                if (d != null) b.setLastRestocked(d.toLocalDate());
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    @Override
    public List<OnlineBatch> findAvailable() {
        List<OnlineBatch> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM online_batches WHERE quantity > 0")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OnlineBatch b = new OnlineBatch();
                b.setId(rs.getInt("online_batch_id"));
                b.setProductId(rs.getInt("product_id"));
                b.setQuantity(rs.getInt("quantity"));
                Date d = rs.getDate("last_restocked");
                if (d != null) b.setLastRestocked(d.toLocalDate());
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean reduceStock(int productId, int qty) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE online_batches SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

}
