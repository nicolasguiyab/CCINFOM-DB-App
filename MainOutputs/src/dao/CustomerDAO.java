package dao;

import database.DatabaseConnection;
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDAO {

    public ArrayList<Customer> getAllCustomers() {

        ArrayList<Customer> customers = new ArrayList<>();

        String sql =
                "SELECT CustomerID, StudentID, FirstName, LastName, " +
                        "Email, MobileNumber, University " +
                        "FROM Customer ORDER BY CustomerID";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Customer customer = new Customer(
                        result.getInt("CustomerID"),
                        result.getString("StudentID"),
                        result.getString("FirstName"),
                        result.getString("LastName"),
                        result.getString("Email"),
                        result.getString("MobileNumber"),
                        result.getString("University")
                );

                customers.add(customer);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customers: "
                    + e.getMessage());
        }

        return customers;
    }

    public boolean addCustomer(Customer customer) {

        String sql = "INSERT INTO Customer " +
                "(StudentID, FirstName, LastName, Email, MobileNumber, University) " +
                "VALUES (?, ?, ?, ?, ?, ?)"; //"?" is a placeholder for the prepared statement

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getStudentID()); //setString inputs
            statement.setString(2, customer.getFirstName()); //them into the
            statement.setString(3, customer.getLastName()); //correct spots
            statement.setString(4, customer.getEmail());
            statement.setString(5, customer.getMobileNumber());
            statement.setString(6, customer.getUniversity());

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
            return false;
        }

    }

    public Customer searchCustomerByStudentID(String studentID) {

        String sql = "SELECT CustomerID, StudentID, FirstName, LastName, " +
                "Email, MobileNumber, University " +
                "FROM Customer WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Customer(
                            result.getInt("CustomerID"),
                            result.getString("StudentID"),
                            result.getString("FirstName"),
                            result.getString("LastName"),
                            result.getString("Email"),
                            result.getString("MobileNumber"),
                            result.getString("University")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching for costumer: " + e.getMessage());
        }

        return null;
    }

    public boolean updateCustomerDetails(String studentID, String mobileNumber,
                                         String university) {

        String sql = "UPDATE Customer " +
                "SET MobileNumber = ?, University = ? " +
                "WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, mobileNumber);
            statement.setString(2, university);
            statement.setString(3, studentID);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(String studentID) {

        String sql = "DELETE FROM Customer WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentID);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public boolean customerExists(int customerID) {

        String sql = "SELECT 1 FROM Customer WHERE CustomerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking customer: " +e.getMessage());
            return false;
        }
    }
}
