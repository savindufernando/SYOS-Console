package db.repositories;

import billing.strategy.DiscountPolicy;
import billing.strategy.FixedDiscount;
import billing.strategy.PercentageDiscount;
import billing.strategy.NoDiscount;
import billing.repositories.DiscountRepository;
import db.DatabaseConnection;
import stock.Product;

import java.sql.*;

public class DiscountRepositoryImpl implements DiscountRepository {

    @Override
    public DiscountPolicy findActiveDiscountForProduct(Product product) {
        String sql = "SELECT * FROM discounts_products " +
                "WHERE product_id=? AND status='ACTIVE' " +
                "AND discount_start <= CURDATE() AND discount_end >= CURDATE()";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String type = rs.getString("discount_type");
                double value = rs.getDouble("discount_value");

                if ("PERCENTAGE".equalsIgnoreCase(type)) {
                    return new PercentageDiscount(value);
                } else if ("AMOUNT".equalsIgnoreCase(type)) {
                    return new FixedDiscount(value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new NoDiscount(); // default no discount
    }

    //  Add discount to product
    public void addDiscount(int productId, String name, String type, double value, String start, String end) {
        String sql = "INSERT INTO discounts_products " +
                "(product_id, discount_name, discount_type, discount_value, discount_start, discount_end, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setDouble(4, value);
            ps.setString(5, start);
            ps.setString(6, end);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  View discounts by status
    public void viewDiscounts(String status) {
        String sql = "SELECT d.discount_id, p.name AS product_name, d.discount_name, " +
                "d.discount_type, d.discount_value, d.discount_start, d.discount_end " +
                "FROM discounts_products d " +
                "JOIN products p ON d.product_id = p.product_id " +
                "WHERE d.status=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            System.out.printf("\n--- %s Discounts ---\n", status);
            while (rs.next()) {
                System.out.printf("ID: %d | Product: %s | Name: %s | %s %.2f | %s → %s\n",
                        rs.getInt("discount_id"),
                        rs.getString("product_name"),
                        rs.getString("discount_name"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_value"),
                        rs.getDate("discount_start"),
                        rs.getDate("discount_end"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Disable a discount
    public void disableDiscount(int discountId) {
        String sql = "UPDATE discounts_products SET status='DISABLED' WHERE discount_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, discountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // ✅ Update discount details (name, type, value, dates, status)
    public void updateDiscount(int discountId, String name, String type, double value,
                               String start, String end, String status) {
        String sql = "UPDATE discounts_products " +
                "SET discount_name=?, discount_type=?, discount_value=?, " +
                "discount_start=?, discount_end=?, status=? " +
                "WHERE discount_id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, type);
            ps.setDouble(3, value);
            ps.setString(4, start);
            ps.setString(5, end);
            ps.setString(6, status);
            ps.setInt(7, discountId);
            ps.executeUpdate();

            System.out.println("✔ Discount updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
