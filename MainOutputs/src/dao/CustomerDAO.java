package dao;

import database.DatabaseConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CustomerDAO {

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    // Convert a database row into a Customer object
    private Customer mapCustomer(ResultSet result) throws SQLException {

        return new Customer(
                result.getInt("CustomerID"),
                result.getString("StudentID"),
                result.getString("GovID"),
                result.getString("IDType"),
                result.getString("FirstName"),
                result.getString("LastName"),
                result.getString("Email"),
                result.getString("MobileNumber"),
                result.getString("University"),
                result.getString("StreetAddress"),
                result.getString("City"),
                result.getString("CustomerType")
        );
    }

    // Retrieve all customers
    public ArrayList<Customer> getAllCustomers() {

        ArrayList<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM Customer ORDER BY CustomerID";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                customers.add(mapCustomer(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }

        return customers;
    }

    // Search customer using Customer ID
    public Customer searchCustomerByID(int customerID) {

        String sql = "SELECT * FROM Customer WHERE CustomerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapCustomer(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching customer: " + e.getMessage());
        }

        return null;
    }

    // Search customer using Student ID
    public Customer searchCustomerByStudentID(String studentID) {

        String sql = "SELECT * FROM Customer WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapCustomer(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching customer: " + e.getMessage());
        }

        return null;
    }

    // Hash a password using PBKDF2
    private String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt) + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    // Verify a password against its stored hash
    private boolean verifyPassword(String password, String storedHash)
            throws Exception {

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                expectedHash.length * 8
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] actualHash = factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(expectedHash, actualHash);

        } finally {
            spec.clearPassword();
        }
    }

    // Register a new customer and return the generated Customer ID
    public int registerCustomer(Customer customer, String password) {

        if (customer == null || password == null || password.length() < 8) {
            return 0;
        }

        String customerType = customer.getCustomerType();

        if (!"Student".equals(customerType)
                && !"Regular".equals(customerType)) {
            return 0;
        }

        if ("Student".equals(customerType)
                && (customer.getStudentID() == null
                || customer.getStudentID().isBlank())) {
            return 0;
        }

        if ("Regular".equals(customerType)
                && (customer.getGovID() == null
                || customer.getGovID().isBlank())) {
            return 0;
        }

        String sql = "INSERT INTO Customer "
                + "(StudentID, GovID, IDType, FirstName, LastName, "
                + "Email, MobileNumber, University, StreetAddress, "
                + "City, CustomerType, PasswordHash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            String passwordHash = hashPassword(password);

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         sql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, customer.getStudentID());
                statement.setString(2, customer.getGovID());
                statement.setString(3, customer.getIDType());
                statement.setString(4, customer.getFirstName());
                statement.setString(5, customer.getLastName());
                statement.setString(6, customer.getEmail());
                statement.setString(7, customer.getMobileNumber());
                statement.setString(8, customer.getUniversity());
                statement.setString(9, customer.getStreetAddress());
                statement.setString(10, customer.getCity());
                statement.setString(11, customerType);
                statement.setString(12, passwordHash);

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (keys.next()) {
                            return keys.getInt(1);
                        }
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("Error registering customer: " + e.getMessage());
        }

        return 0;
    }

    // Authenticate using Customer ID
    public boolean authenticateUser(int customerID, String password) {

        String sql = "SELECT PasswordHash FROM Customer WHERE CustomerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    String storedHash = result.getString("PasswordHash");

                    return storedHash != null
                            && verifyPassword(password, storedHash);
                }
            }

        } catch (Exception e) {
            System.out.println("Error authenticating customer: " + e.getMessage());
        }

        return false;
    }

    // Authenticate using Student ID (compatibility with existing code)
    public boolean authenticateUser(String studentID, String password) {

        String sql = "SELECT PasswordHash FROM Customer WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    String storedHash = result.getString("PasswordHash");

                    return storedHash != null
                            && verifyPassword(password, storedHash);
                }
            }

        } catch (Exception e) {
            System.out.println("Error authenticating customer: " + e.getMessage());
        }

        return false;
    }

    // Check whether a customer exists
    public boolean customerExists(int customerID) {

        String sql = "SELECT 1 FROM Customer WHERE CustomerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking customer: " + e.getMessage());
        }

        return false;
    }

    // Update customer contact details
    public boolean updateCustomerDetails(String studentID,
                                         String mobileNumber,
                                         String university) {

        String sql = "UPDATE Customer "
                + "SET MobileNumber = ?, University = ? "
                + "WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, mobileNumber);
            statement.setString(2, university);
            statement.setString(3, studentID);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }

        return false;
    }

    // Delete customer using Student ID
    public boolean deleteCustomer(String studentID) {

        String sql = "DELETE FROM Customer WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentID);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }

        return false;
    }
}