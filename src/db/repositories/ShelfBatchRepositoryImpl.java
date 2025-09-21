package db.repositories;

import db.DatabaseConnection;
import stock.batch.ShelfBatch;
import stock.repositories.ShelfBatchRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShelfBatchRepositoryImpl implements ShelfBatchRepository {

    @Override
    public void save(ShelfBatch batch) {
        String sql = "INSERT INTO shelf_batches (product_id, quantity, last_restocked) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batch.getProductId());
            ps.setInt(2, batch.getQuantity());
            if (batch.getLastRestocked() != null) {
                ps.setDate(3, Date.valueOf(batch.getLastRestocked()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ShelfBatch batch) {
        String sql = "UPDATE shelf_batches SET quantity=?, last_restocked=? WHERE shelf_batch_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batch.getQuantity());
            if (batch.getLastRestocked() != null) {
                ps.setDate(2, Date.valueOf(batch.getLastRestocked()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setInt(3, batch.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShelfBatch findByProduct(int productId) {
        String sql = "SELECT * FROM shelf_batches WHERE product_id=? LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapToShelfBatch(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ShelfBatch> findAll() {
        List<ShelfBatch> list = new ArrayList<>();
        String sql = "SELECT * FROM shelf_batches";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapToShelfBatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ShelfBatch> findAllWithProducts() {
        List<ShelfBatch> list = new ArrayList<>();
        String sql = """
            SELECT sb.shelf_batch_id, sb.product_id, sb.quantity, sb.last_restocked,
                   p.code AS product_code, p.name AS product_name
            FROM shelf_batches sb
            JOIN products p ON sb.product_id = p.product_id
        """;
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ShelfBatch batch = mapToShelfBatch(rs);
                batch.setProductCode(rs.getString("product_code"));
                batch.setProductName(rs.getString("product_name"));
                list.add(batch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ShelfBatch findByProductCode(String productCode) {
        String sql = """
            SELECT sb.*, p.code AS product_code, p.name AS product_name
            FROM shelf_batches sb
            JOIN products p ON sb.product_id = p.product_id
            WHERE p.code=? LIMIT 1
        """;
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ShelfBatch batch = mapToShelfBatch(rs);
                batch.setProductCode(rs.getString("product_code"));
                batch.setProductName(rs.getString("product_name"));
                return batch;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 🔹 New for ReshelvedItemsReport
    public List<ShelfBatch> findByDate(LocalDate date) {
        List<ShelfBatch> list = new ArrayList<>();
        String sql = "SELECT * FROM shelf_batches WHERE last_restocked = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapToShelfBatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ShelfBatch mapToShelfBatch(ResultSet rs) throws SQLException {
        ShelfBatch b = new ShelfBatch();
        b.setId(rs.getInt("shelf_batch_id"));
        b.setProductId(rs.getInt("product_id"));
        b.setQuantity(rs.getInt("quantity"));

        Date d = rs.getDate("last_restocked");
        if (d != null) b.setLastRestocked(d.toLocalDate());

        return b;
    }
}
