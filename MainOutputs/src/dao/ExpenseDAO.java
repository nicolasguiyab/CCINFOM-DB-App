package dao;

import database.DatabaseConnection;
import model.Expense;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseDAO {

    // Convert a database row into an Expense object
    private Expense mapExpense(ResultSet result) throws SQLException {

        return new Expense(
                result.getInt("ExpenseID"),
                result.getObject("CustomerID", Integer.class),
                result.getInt("BranchID"),
                result.getObject("BookingID", Integer.class),
                result.getString("TransactionType"),
                result.getString("ExpenseType"),
                result.getDate("ExpenseDate"),
                result.getBigDecimal("Amount"),
                result.getString("Description")
        );
    }

    // Set an Integer parameter that may be NULL
    private void setNullableInteger(
            PreparedStatement statement, int index, Integer value)
            throws SQLException {

        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    // Retrieve all financial transactions
    public ArrayList<Expense> getAllExpenses() {

        ArrayList<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM Expense";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                expenses.add(mapExpense(result));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }

    // Add a new financial transaction
    public boolean addExpense(Expense expense) {

        String sql = "INSERT INTO Expense " +
                "(CustomerID, BranchID, BookingID, TransactionType, " +
                "ExpenseType, ExpenseDate, Amount, Description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableInteger(statement, 1, expense.getCustomerID());
            statement.setInt(2, expense.getBranchID());
            setNullableInteger(statement, 3, expense.getBookingID());

            statement.setString(4, expense.getTransactionType());
            statement.setString(5, expense.getExpenseType());
            statement.setDate(6, expense.getExpenseDate());
            statement.setBigDecimal(7, expense.getAmount());
            statement.setString(8, expense.getDescription());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Retrieve transactions for a specific branch
    public ArrayList<Expense> getExpensesByBranch(int branchID) {

        ArrayList<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM Expense WHERE BranchID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branchID);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    expenses.add(mapExpense(result));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }

    // Calculate operating expenses for a specific branch
    public BigDecimal getTotalExpensesByBranch(int branchID) {

        String sql = "SELECT COALESCE(SUM(Amount), 0) AS TotalExpenses " +
                "FROM Expense " +
                "WHERE BranchID = ? AND TransactionType = 'Expense'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branchID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getBigDecimal("TotalExpenses");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    // Search for a transaction using its ID
    public Expense searchExpenseByID(int expenseID) {

        String sql = "SELECT * FROM Expense WHERE ExpenseID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, expenseID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapExpense(result);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Calculate operating expenses across all branches
    public BigDecimal getTotalExpenses() {

        String sql = "SELECT COALESCE(SUM(Amount), 0) AS TotalExpenses " +
                "FROM Expense WHERE TransactionType = 'Expense'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getBigDecimal("TotalExpenses");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    // Calculate operating expenses for a specific month and year
    public BigDecimal getMonthlyExpenses(int month, int year) {

        String sql = "SELECT COALESCE(SUM(Amount), 0) AS TotalExpenses " +
                "FROM Expense " +
                "WHERE TransactionType = 'Expense' " +
                "AND MONTH(ExpenseDate) = ? " +
                "AND YEAR(ExpenseDate) = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, month);
            statement.setInt(2, year);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getBigDecimal("TotalExpenses");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    // Update an existing financial transaction
    public boolean updateExpense(Expense expense) {

        String sql = "UPDATE Expense SET " +
                "CustomerID = ?, BranchID = ?, BookingID = ?, " +
                "TransactionType = ?, ExpenseType = ?, ExpenseDate = ?, " +
                "Amount = ?, Description = ? " +
                "WHERE ExpenseID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableInteger(statement, 1, expense.getCustomerID());
            statement.setInt(2, expense.getBranchID());
            setNullableInteger(statement, 3, expense.getBookingID());

            statement.setString(4, expense.getTransactionType());
            statement.setString(5, expense.getExpenseType());
            statement.setDate(6, expense.getExpenseDate());
            statement.setBigDecimal(7, expense.getAmount());
            statement.setString(8, expense.getDescription());
            statement.setInt(9, expense.getExpenseID());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete a financial transaction
    public boolean deleteExpense(int expenseID) {

        String sql = "DELETE FROM Expense WHERE ExpenseID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, expenseID);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Expense> getTransactionByType(String transactionType) {

        ArrayList<Expense> transactions = new ArrayList<>();

        if (!transactionType.equalsIgnoreCase("Income") &&
        !transactionType.equalsIgnoreCase("Expense")) {
            return transactions;
        }

        String sql = "SELECT * FROM Expense WHERE TransactionType = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transactionType);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    transactions.add(mapExpense(result));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public BigDecimal getTotalIncomeByBranch(int branchID) {

        String sql = "SELECT COALESCE(SUM(Amount), 0) AS TotalIncome " +
                "FROM Expense WHERE BranchID = ? AND TransactionType = 'Income'";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branchID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getBigDecimal("TotalIncome");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getMonthlyIncome(int month, int year) {

        String sql = "SELECT COALESCE(SUM(Amound), 0) AS TotalIncome " +
                "FROM Expense " +
                "WHERE TransactionType = 'Income' " +
                "AND MONTH(ExpenseDate) = ? " +
                "AND YEAR(ExpenseDate) = ?";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, month);
            statement.setInt(2, year);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getBigDecimal("TotalIncome");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalIncome() {
        
        String sql = "SELECT COALESCE(SUM(Amount), 0) AS TotalIncome " +
                "FROM Expense WHERE TransactionType = 'Income'";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getBigDecimal("TotalIncome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public boolean hasIncomeForBooking(int bookingID) {

        String sql = "SELECT COUNT(*) FROM Expense " +
                "WHERE BookingID = ? " +
                "AND TransactionType = 'Income'";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Expense> getTransactionByDateRange(Date startDate, Date endDate) {

        ArrayList<Expense> transaction = new ArrayList<>();

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return transaction;
        }

        String sql = "SELECT * FROM Expense " +
                "WHERE ExpenseDate BETWEEN ? AND ? " +
                "ORDER BY ExpenseDate ASC, ExpenseID ASC";

        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, startDate);
            statement.setDate(2, endDate);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    transaction.add(mapExpense(result));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaction;
    }
}