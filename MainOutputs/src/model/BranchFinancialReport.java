package model;

import java.math.BigDecimal;

public class BranchFinancialReport {

    private int branchID;
    private String branchName;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;

    public BranchFinancialReport(int branchID, String branchName,
                                 BigDecimal totalRevenue,
                                 BigDecimal totalExpenses) {

        this.branchID = branchID;
        this.branchName = branchName;
        this.totalRevenue = totalRevenue;
        this.totalExpenses = totalExpenses;
    }

    public int getBranchID() {
        return branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetIncome() {
        return totalRevenue.subtract(totalExpenses);
    }

    @Override
    public String toString() {
        return "Branch ID: " + branchID +
                ", Branch Name: " + branchName +
                ", Revenue: ₱" + totalRevenue +
                ", Expenses: ₱" + totalExpenses +
                ", Net Income: ₱" + getNetIncome();
    }
}