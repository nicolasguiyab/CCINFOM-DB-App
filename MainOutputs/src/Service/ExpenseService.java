package service;

import model.Expense;
import dao.ExpenseDAO;
import dao.CustomerDAO;
import dao.ReservationDAO;
import model.Reservation;
import dao.StudySpaceDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.Date;


public class ExpenseService {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final StudySpaceDAO studySpaceDAO = new StudySpaceDAO();

    //Validate a financial transaction
    private boolean isValidTransaction(Expense expense) {

        if (expense == null) {
            return false;
        }

        if (expense.getBranchID() <= 0 ||
                expense.getExpenseDate() == null ||
                expense.getExpenseType() == null ||
                expense.getExpenseType().trim().isEmpty() ||
                expense.getAmount() == null ||
                expense.getAmount().compareTo(BigDecimal.ZERO) < 0) {

            return false;
        }

        String type = expense.getTransactionType();

        // Validate income transactions
        if ("Income".equals(type)) {

            if (expense.getCustomerID() == null ||
                    expense.getCustomerID() <= 0) {
                return false;
            }

            if (!customerDAO.customerExists(expense.getCustomerID())) {
                return false;
            }

            // If linked to a reservation, verify its customer
            if (expense.getBookingID() != null) {

                Reservation reservation =
                        reservationDAO.searchReservationByID(
                                expense.getBookingID()
                        );

                // Check whether the reservation exists
                if (reservation == null) {
                    return false;
                }

                // Check whether the reservation belongs to the customer
                if (reservation.getCustomerID() != expense.getCustomerID()) {
                    return false;
                }

                // Retrieve the actual branch of the reservation's study space
                int actualBranchID =
                        studySpaceDAO.getBranchIDBySpaceID(reservation.getSpaceID());

                // Check whether the branch matches the transaction
                if (actualBranchID != expense.getBranchID()) {
                    return false;
                }
            }

            return true;
        }

        // Validate operating expenses
        if ("Expense".equals(type)) {
            return expense.getCustomerID() == null &&
                    expense.getBookingID() == null;
        }

        return false;
    }

    public ArrayList<Expense> getTransactionByType(String type) {

        if (!"Income".equals(type) && !"Expense".equals(type)) {
            return new ArrayList<>();
        }

        return expenseDAO.getTransactionByType(type);
    }

    public ArrayList<Expense> getTransactionByBranch(int branchID) {

        if (branchID <= 0) {
            return new ArrayList<>();
        }

        return expenseDAO.getExpensesByBranch(branchID);
    }

    public boolean addTransaction(Expense expense) {

        if (!isValidTransaction(expense)) {
            return false;
        }

        // Prevent duplicate reservation payments
        if ("Income".equals(expense.getTransactionType()) &&
                expense.getBookingID() != null) {

            if (expenseDAO.hasIncomeForBooking(expense.getBookingID())) {
                return false;
            }
        }

        return expenseDAO.addExpense(expense);
    }

    public boolean updateTransaction(Expense expense) {

        if (!isValidTransaction(expense) || expense.getExpenseID() <= 0) {
            return false;
        }

        return expenseDAO.updateExpense(expense);
    }

    public boolean deleteTransaction(int expenseID) {

        if (expenseID <= 0) {
            return false;
        }

        return expenseDAO.deleteExpense(expenseID);
    }

    public ArrayList<Expense> getTransactionByDateRange(Date startDate, Date endDate) {

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return new ArrayList<>();
        }

        return expenseDAO.getTransactionByDateRange(startDate, endDate);
    }
}
