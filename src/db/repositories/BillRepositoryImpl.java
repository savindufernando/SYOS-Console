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
        try (Connection conn = DatabaseConnection.getInstance()) {
            // 🔹 Step 1: Get next daily serial
            String serialSql = "SELECT COALESCE(MAX(bill_serial), 0) + 1 FROM bills WHERE DATE(bill_date) = CURDATE()";
            try (PreparedStatement psSerial = conn.prepareStatement(serialSql)) {
                ResultSet rs = psSerial.executeQuery();
                if (rs.next()) {
                    bill.setBillSerial(rs.getInt(1));
                }
            }

            // 🔹 Step 2: Build formatted bill_number before insert
            String formattedNumber = bill.generateBillNumber("SYOS");
            bill.setBillNumber(formattedNumber);

            // 🔹 Step 3: Insert bill
            String sql = "INSERT INTO bills (bill_serial, bill_number, user_id, customer_id, bill_date, total_amount, cash_tendered, change_amount, transaction_type, payment_method, card_number, card_holder) " +
                    "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // 1. bill_serial
                ps.setInt(1, bill.getBillSerial());

                // 2. bill_number
                ps.setString(2, bill.getBillNumber());

                // 3 & 4. user_id / customer_id
                if ("COUNTER".equalsIgnoreCase(bill.getTransactionType())) {
                    ps.setInt(3, bill.getCashierId());   // cashier mapped to user_id
                    ps.setNull(4, Types.INTEGER);
                } else if ("ONLINE".equalsIgnoreCase(bill.getTransactionType())) {
                    ps.setNull(3, Types.INTEGER);
                    ps.setInt(4, bill.getCustomerId());
                } else {
                    throw new IllegalArgumentException("Invalid transaction type: " + bill.getTransactionType());
                }

                // 5. total_amount
                ps.setDouble(5, bill.getTotalAmount());

                // 6. cash_tendered
                if ("CASH".equalsIgnoreCase(bill.getPaymentMethod())) {
                    ps.setDouble(6, bill.getCashTendered());
                } else {
                    ps.setDouble(6, bill.getTotalAmount()); // card = fully paid
                }

                // 7. change_amount
                if ("CASH".equalsIgnoreCase(bill.getPaymentMethod())) {
                    ps.setDouble(7, bill.getChangeAmount());
                } else {
                    ps.setDouble(7, 0.0);
                }

                // 8. transaction_type
                ps.setString(8, bill.getTransactionType());

                // 9. payment_method
                ps.setString(9, bill.getPaymentMethod());

                // 10. card_number
                if ("CARD".equalsIgnoreCase(bill.getPaymentMethod())) {
                    ps.setString(10, bill.getCardNumberMasked());
                } else {
                    ps.setNull(10, Types.VARCHAR);
                }

                // 11. card_holder
                if ("CARD".equalsIgnoreCase(bill.getPaymentMethod())) {
                    ps.setString(11, bill.getCardHolder());
                } else {
                    ps.setNull(11, Types.VARCHAR);
                }

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    bill.setBillId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error saving bill: " + e.getMessage());
        }
        return -1;
    }
    @Override
    public Bill findById(int billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        Bill bill = null;

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bill = mapRowToBill(rs);
                }
            }

            if (bill != null) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error finding bill by ID: " + e.getMessage());
        }

        return bill;
    }



    public Bill findByBillNumber(String billNumber) {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, billNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToBill(rs);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error finding bill by bill_number: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bill> findByDateAndType(LocalDate date, String type) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE DATE(bill_date) = ? ";

        if (!"ALL".equalsIgnoreCase(type)) {
            sql += "AND transaction_type = ? ";
        }

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(date));
            if (!"ALL".equalsIgnoreCase(type)) {
                ps.setString(2, type.toUpperCase());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs)); // headers only
                }
            }

            // ✅ load items after closing ResultSet
            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs));
                }
            }

            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs));
                }
            }

            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bills between dates: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public List<Bill> findRecent(int limit) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs));
                }
            }

            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching recent bills: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs));
                }
            }

            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching all bills: " + e.getMessage());
        }

        return bills;
    }

    public List<Bill> findByCustomerId(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE customer_id = ? AND transaction_type = 'ONLINE' ORDER BY bill_date DESC";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRowToBill(rs));
                }
            }

            for (Bill bill : bills) {
                bill.setItems(loadBillItems(bill.getBillId(), conn));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bills for customer: " + e.getMessage());
        }

        return bills;
    }

    // ✅ helper to map DB row → Bill object (default, keeps compatibility)
    private Bill mapRowToBill(ResultSet rs) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance()) {
            return mapRowToBill(rs, conn);
        }
    }

    // ✅ helper with shared connection
    private Bill mapRowToBill(ResultSet rs, Connection conn) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setBillSerial(rs.getInt("bill_serial"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setCashierId(rs.getInt("user_id"));
        bill.setCustomerId(rs.getInt("customer_id"));
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setCashTendered(rs.getDouble("cash_tendered"));
        bill.setChangeAmount(rs.getDouble("change_amount"));
        bill.setTransactionType(rs.getString("transaction_type"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setCardNumberMasked(rs.getString("card_number"));
        bill.setCardHolder(rs.getString("card_holder"));

        // ✅ load bill items with same connection
        bill.setItems(loadBillItems(bill.getBillId(), conn));

        return bill;
    }

    private List<billing.BillItem> loadBillItems(int billId, Connection conn) throws SQLException {
        List<billing.BillItem> items = new ArrayList<>();
        String sql = "SELECT bi.quantity, bi.line_total, " +
                "p.code, p.name, p.unit_price, p.type " +
                "FROM bill_items bi " +
                "JOIN products p ON bi.product_id = p.product_id " +
                "WHERE bi.bill_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                stock.Product product;
                if ("PERISHABLE".equalsIgnoreCase(rs.getString("type"))) {
                    product = new stock.PerishableProduct(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getDouble("unit_price"),
                            null
                    );
                } else {
                    product = new stock.NonPerishableProduct(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getDouble("unit_price")
                    );
                }

                billing.BillItem item = new billing.BillItem(
                        product,
                        rs.getInt("quantity"),
                        rs.getDouble("line_total") // uses your new constructor
                );

                items.add(item);
            }
        }
        return items;
    }


}
