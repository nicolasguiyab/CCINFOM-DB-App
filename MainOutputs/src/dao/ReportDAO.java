package dao;

import database.DatabaseConnection;
import model.BranchFinancialReport;
import model.MonthlyFinancialSummary;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class ReportDAO {

    // Retrieve financial reports for all branches
    public ArrayList<BranchFinancialReport> getBranchFinancialReports() {

        ArrayList<BranchFinancialReport> reports = new ArrayList<>();

        String sql = "SELECT hb.BranchID, hb.BranchName, " +
                "COALESCE((SELECT SUM(e.Amount) FROM Expense e " +
                "WHERE e.BranchID = hb.BranchID AND e.TransactionType = 'Income'" +
                "), 0) AS TotalRevenue, " +
                "COALESCE((SELECT SUM(e.Amount) FROM Expense e " +
                "WHERE e.BranchID = hb.BranchID AND e.TransactionType = 'Expense'" +
                "), 0) AS TotalExpenses " +
                "FROM hubBranch hb " +
                "ORDER BY hb.BranchID";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                BranchFinancialReport report =
                        new BranchFinancialReport(
                                result.getInt("BranchID"),
                                result.getString("BranchName"),
                                result.getBigDecimal("TotalRevenue"),
                                result.getBigDecimal("TotalExpenses")
                        );

                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    public MonthlyFinancialSummary getMonthlyFinancialSummary(int month, int year) {

        String sql = "SELECT COALESCE(SUM(CASE WHEN TransactionType = 'Income' " +
                "THEN Amount ELSE 0 END), 0) AS TotalIncome, " +
                "COALESCE(SUM(CASE WHEN TransactionType = 'Expense' " +
                "THEN Amount ELSE 0 END), 0) AS TotalExpenses " +
                "FROM Expense WHERE MONTH(ExpenseDate) = ? " +
                "AND YEAR(ExpenseDate) = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, month);
            statement.setInt(2, year);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new MonthlyFinancialSummary(
                            month,
                            year,
                            result.getBigDecimal("TotalIncome"),
                            result.getBigDecimal("TotalExpenses")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new MonthlyFinancialSummary(month, year, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public MonthlyFinancialSummary getBranchMonthlyFinancialSummary(int branchID, int month, int year) {

        String sql = "SELECT COALESCE(SUM(CASE WHEN TransactionType = 'Income' " +
                "THEN Amount ELSE 0 END), 0) AS TotalIncome, " +
                "COALESCE(SUM(CASE WHEN TransactionType = 'Expense' " +
                "THEN Amount ELSE 0 END), 0) AS TotalExpenses " +
                "FROM Expense " +
                "WHERE BranchID = ? AND MONTH(ExpenseDate) = ? AND YEAR(ExpenseDate) = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branchID);
            statement.setInt(2, month);
            statement.setInt(3, year);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new MonthlyFinancialSummary(
                            month, year,
                            result.getBigDecimal("TotalIncome"),
                            result.getBigDecimal("TotalExpenses")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new MonthlyFinancialSummary(month, year, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}