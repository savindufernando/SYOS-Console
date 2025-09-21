package db.repositories;

import db.DatabaseConnection;
import stock.batch.InventoryBatch;
import stock.repositories.InventoryBatchRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryBatchRepositoryImpl implements InventoryBatchRepository {

    @Override
    public void save(InventoryBatch batch) {
        String sql = "INSERT INTO inventory_batches (product_id, purchase_date, expiry_date, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batch.getProductId());
            ps.setDate(2, Date.valueOf(batch.getPurchaseDate()));
            if (batch.getExpiryDate() != null) {
                ps.setDate(3, Date.valueOf(batch.getExpiryDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, batch.getQuantity());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(InventoryBatch batch) {
        String sql = "UPDATE inventory_batches SET quantity=? WHERE batch_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, batch.getQuantity());
            ps.setInt(2, batch.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<InventoryBatch> findByProduct(int productId) {
        List<InventoryBatch> batches = new ArrayList<>();
        String sql = "SELECT * FROM inventory_batches WHERE product_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                batches.add(mapToInventoryBatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batches;
    }

    // 🔹 Needed for reports (StockReport, ReorderLevelReport)
    public List<InventoryBatch> findAll() {
        List<InventoryBatch> batches = new ArrayList<>();
        String sql = "SELECT * FROM inventory_batches";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                batches.add(mapToInventoryBatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batches;
    }

    private InventoryBatch mapToInventoryBatch(ResultSet rs) throws SQLException {
        InventoryBatch b = new InventoryBatch();
        b.setId(rs.getInt("batch_id"));
        b.setProductId(rs.getInt("product_id"));
        b.setQuantity(rs.getInt("quantity"));
        b.setPurchaseDate(rs.getDate("purchase_date").toLocalDate());

        Date expiry = rs.getDate("expiry_date");
        if (expiry != null) b.setExpiryDate(expiry.toLocalDate());

        return b;
    }
}
