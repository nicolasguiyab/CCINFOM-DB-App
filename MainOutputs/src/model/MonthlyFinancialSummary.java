package model;

import java.math.BigDecimal;

public class MonthlyFinancialSummary {

    private int month;
    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;

    public MonthlyFinancialSummary(int month, int year,
                                   BigDecimal totalIncome,
                                   BigDecimal totalExpenses) {

        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetIncome() {
        return totalIncome.subtract(totalExpenses);
    }

    @Override
    public String toString() {
        return "Monthly Financial Summary: " +
                month + "/" + year +
                ", Income: ₱" + totalIncome +
                ", Expenses: ₱" + totalExpenses +
                ", Net Income: ₱" + getNetIncome();
    }
}
