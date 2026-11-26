package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Expense {

    private int expenseID;
    private Integer customerID;
    private int branchID;
    private Integer bookingID;
    private String transactionType;
    private String expenseType;
    private Date expenseDate;
    private BigDecimal amount;
    private String description;

    public Expense(int expenseID, Integer customerID, int branchID,
                   Integer bookingID, String transactionType,
                   String expenseType, Date expenseDate,
                   BigDecimal amount, String description) {

        this.expenseID = expenseID;
        this.customerID = customerID;
        this.branchID = branchID;
        this.bookingID = bookingID;
        this.transactionType = transactionType;
        this.expenseType = expenseType;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.description = description;
    }

    public int getExpenseID() {
        return expenseID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public int getBranchID() {
        return branchID;
    }

    public Integer getBookingID() {
        return bookingID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + expenseID +
                ", Type: " + transactionType +
                ", Category: " + expenseType +
                ", Amount: ₱" + amount +
                ", Date: " + expenseDate;
    }
}