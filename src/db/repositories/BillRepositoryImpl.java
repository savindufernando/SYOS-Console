package db.repositories;

import billing.Bill;
import billing.repositories.BillRepository;
import db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillRepositoryImpl implements BillRepository {

    @Override
    public int save(Bill bill) {
        String sql = "INSERT INTO bills (user_id, customer_id, bill_date, total_amount, cash_tendered, change_amount, transaction_type, payment_method, card_number, card_holder) " +
                "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1 & 2: user_id / customer_id
            if ("COUNTER".equalsIgnoreCase(bill.getTransactionType())) {
                ps.setInt(1, bill.getCashierId());   // cashier mapped to user_id
                ps.setNull(2, Types.INTEGER);
            } else if ("ONLINE".equalsIgnoreCase(bill.getTransactionType())) {
                ps.setNull(1, Types.INTEGER);
                ps.setInt(2, bill.getCustomerId());
            } else {
                throw new IllegalArgumentException("Invalid transaction type: " + bill.getTransactionType());
            }

            // 3. total_amount
            ps.setDouble(3, bill.getTotalAmount());

            // 4. cash_tendered
            if ("CASH".equalsIgnoreCase(bill.getPaymentMethod())) {
                ps.setDouble(4, bill.getCashTendered());
            } else {
                ps.setDouble(4, bill.getTotalAmount()); // card = fully paid
            }

            // 5. change_amount
            if ("CASH".equalsIgnoreCase(bill.getPaymentMethod())) {
                ps.setDouble(5, bill.getChangeAmount());
            } else {
                ps.setDouble(5, 0.0);
            }

            // 6. transaction_type
            ps.setString(6, bill.getTransactionType());

            // 7. payment_method
            ps.setString(7, bill.getPaymentMethod());

            // 8. card_number
            if ("CARD".equalsIgnoreCase(bill.getPaymentMethod())) {
                ps.setString(8, bill.getCardNumberMasked());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            // 9. card_holder
            if ("CARD".equalsIgnoreCase(bill.getPaymentMethod())) {
                ps.setString(9, bill.getCardHolder());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                bill.setBillId(id);
                return id;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error saving bill: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public Bill findById(int billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToBill(rs);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error finding bill by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bill> findRecent(int limit) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bills.add(mapRowToBill(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching recent bills: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public List<Bill> findByDate(LocalDate date) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE DATE(bill_date) = ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bills.add(mapRowToBill(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bills by date: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public List<Bill> findBetweenDates(LocalDate start, LocalDate end) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE DATE(bill_date) BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bills.add(mapRowToBill(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bills between dates: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bills.add(mapRowToBill(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching all bills: " + e.getMessage());
        }

        return bills;
    }

    // ✅ helper to map DB row → Bill object
    private Bill mapRowToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setCashierId(rs.getInt("user_id"));   // ✅ map DB user_id → cashierId
        bill.setCustomerId(rs.getInt("customer_id"));
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setCashTendered(rs.getDouble("cash_tendered"));
        bill.setChangeAmount(rs.getDouble("change_amount"));
        bill.setTransactionType(rs.getString("transaction_type"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setCardNumberMasked(rs.getString("card_number"));
        bill.setCardHolder(rs.getString("card_holder"));
        return bill;
    }
}
