package billing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int billId;
    private Integer cashierId;       // null if ONLINE
    private Integer customerId;      // null if COUNTER
    private String cashierName;      // optional, for receipts
    private LocalDateTime billDate;
    private double totalAmount;
    private double cashTendered;
    private double changeAmount;
    private String transactionType = "COUNTER";
    private String paymentMethod = "CASH"; // default
    private String cardNumberMasked;       // last 4 digits only
    private String cardHolder;
    private final List<BillItem> items = new ArrayList<>();

    // === Constructors ===
    public Bill() {
        this.billDate = LocalDateTime.now();
    }

    // For counter sales
    public Bill(int cashierId, String cashierName) {
        this.cashierId = cashierId;
        this.cashierName = cashierName;
        this.transactionType = "COUNTER";
        this.billDate = LocalDateTime.now();
    }

    // For online sales
    public Bill(int customerId) {
        this.customerId = customerId;
        this.transactionType = "ONLINE";
        this.billDate = LocalDateTime.now();
    }

    // === Business Methods ===
    public void addItem(BillItem item) {
        items.add(item);
        totalAmount += item.getLineTotal();
    }

    public void removeItem(BillItem item) {
        if (items.remove(item)) {
            totalAmount -= item.getLineTotal();
        }
    }

    public void finalizePayment(double cashTendered) {
        this.cashTendered = cashTendered;
        this.changeAmount = cashTendered - totalAmount;
    }

    // === Getters & Setters ===
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public Integer getCashierId() { return cashierId; }
    public void setCashierId(Integer cashierId) { this.cashierId = cashierId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getCashTendered() { return cashTendered; }
    public void setCashTendered(double cashTendered) { this.cashTendered = cashTendered; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCardNumberMasked() { return cardNumberMasked; }
    public void setCardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public List<BillItem> getItems() { return items; }
}
