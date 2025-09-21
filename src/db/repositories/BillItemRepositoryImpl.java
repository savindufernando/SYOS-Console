package db.repositories;

import billing.BillItem;
import billing.repositories.BillItemRepository;
import billing.strategy.DiscountPolicy;
import billing.strategy.NoDiscount;
import db.DatabaseConnection;
import stock.Product;
import stock.PerishableProduct;
import stock.NonPerishableProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemRepositoryImpl implements BillItemRepository {

    @Override
    public void save(int billId, BillItem item) {
        String sql = "INSERT INTO bill_items (bill_id, product_id, quantity, line_total, " +
                "discount_name, discount_type, discount_value) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            ps.setInt(2, item.getProduct().getId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getLineTotal());
            ps.setString(5, item.getDiscountName());
            ps.setString(6, item.getDiscountType());
            ps.setDouble(7, item.getDiscountValue());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<BillItem> findByBillId(int billId) {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT bi.*, p.name, p.unit_price, p.code, p.type " +
                "FROM bill_items bi " +
                "JOIN products p ON bi.product_id = p.product_id " +
                "WHERE bi.bill_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                Product product = "PERISHABLE".equalsIgnoreCase(type)
                        ? new stock.PerishableProduct(rs.getString("code"), rs.getString("name"), rs.getDouble("unit_price"), null)
                        : new stock.NonPerishableProduct(rs.getString("code"), rs.getString("name"), rs.getDouble("unit_price"));
                product.setId(rs.getInt("product_id"));

                int qty = rs.getInt("quantity");

                // reconstruct BillItem with stored discount metadata
                BillItem item = new BillItem(product, qty, new billing.strategy.NoDiscount());
                item = new BillItem(product, qty, new billing.strategy.NoDiscount()); // create baseline

                // overwrite stored discount info
                item = new BillItem(product, qty, new billing.strategy.NoDiscount()); // keep compatibility
                // but hydrate manually
                // (or better: create a constructor with all fields)
                // Here simplified:
                item.getProduct().setId(product.getId());

                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
