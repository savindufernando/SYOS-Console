package db.repositories;

import db.DatabaseConnection;
import stock.Product;
import stock.PerishableProduct;
import stock.NonPerishableProduct;
import stock.repositories.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public void save(Product product) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO products (code, name, type, unit_price) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            ps.setString(3, product instanceof PerishableProduct ? "PERISHABLE" : "NONPERISHABLE");
            ps.setDouble(4, product.getUnitPrice());
            ps.executeUpdate();

            // fetch auto-generated product_id
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                product.setId(keys.getInt(1)); // store DB id in the object
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findByCode(String code) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM products WHERE code=?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Product product) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE products SET name=?, unit_price=? WHERE code=?")) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getUnitPrice());
            ps.setString(3, product.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findById(int id) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM products WHERE product_id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE code LIKE ? OR name LIKE ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching products: " + e.getMessage());
        }
        return products;
    }


    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DatabaseConnection.getInstance();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching products: " + e.getMessage());
        }
        return products;
    }

    // ✅ Utility mapper (avoid duplicate code)
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("product_id");
        String code = rs.getString("code");
        String name = rs.getString("name");
        String type = rs.getString("type");
        double price = rs.getDouble("unit_price");

        Product p;
        if ("PERISHABLE".equalsIgnoreCase(type)) {
            p = new PerishableProduct(code, name, price, null);
        } else {
            p = new NonPerishableProduct(code, name, price);
        }
        p.setId(id);
        return p;
    }
    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE product_id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("⚠️ No product found with ID " + id);
            } else {
                System.out.println("✅ Product deleted (ID: " + id + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public void deleteByCode(String code) {
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE code=?")) {
            ps.setString(1, code);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("⚠️ No product found with code " + code);
            } else {
                System.out.println("✅ Product deleted (Code: " + code + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting product: " + e.getMessage());
        }
    }

}
