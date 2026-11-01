package dao;

import database.DatabaseConnection;
import model.Reservation;
import dao.StudySpaceDAO;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.sql.Date;
import java.sql.Time;

public class ReservationDAO {

    public ArrayList<Reservation> getAllReservations() {

        ArrayList<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT BookingID, CustomerID, SpaceID, " +
                "BookingDate, StartTime, EndTime, BookingStatus, " +
                "HourlyRateAtBooking, TotalPrice " +
                "FROM Reservation ORDER BY BookingID";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Reservation reservation = new Reservation(
                        result.getInt("BookingID"),
                        result.getInt("CustomerID"),
                        result.getInt("SpaceID"),
                        result.getDate("BookingDate"),
                        result.getTime("StartTime"),
                        result.getTime("EndTime"),
                        result.getString("BookingStatus"),
                        result.getBigDecimal("HourlyRateAtBooking"),
                        result.getBigDecimal("TotalPrice")
                );

                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving reservations: "
                    + e.getMessage());
        }

        return reservations;
    }

    public Reservation searchReservationByID(int bookingID) {

        String sql = "SELECT BookingID, CustomerID, SpaceID, " +
                "BookingDate, StartTime, EndTime, BookingStatus, " +
                "HourlyRateAtBooking, TotalPrice " +
                "FROM Reservation WHERE BookingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new Reservation(
                            result.getInt("BookingID"),
                            result.getInt("CustomerID"),
                            result.getInt("SpaceID"),
                            result.getDate("BookingDate"),
                            result.getTime("StartTime"),
                            result.getTime("EndTime"),
                            result.getString("BookingStatus"),
                            result.getBigDecimal("HourlyRateAtBooking"),
                            result.getBigDecimal("TotalPrice")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching for reservation: " + e.getMessage());
        }

        return null;
    }

    public boolean updateReservationStatus(int bookingID, String status) {

        String currentStatus;
        int spaceID;

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Start transaction.
            connection.setAutoCommit(false);

            try {
                // Step 1: Retrieve and lock the reservation.
                String selectSQL = "SELECT BookingStatus, SpaceID " +
                        "FROM Reservation WHERE BookingID = ? FOR UPDATE";

                try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {

                    statement.setInt(1, bookingID);

                    try (ResultSet resultSet = statement.executeQuery()) {

                        if (!resultSet.next()) {
                            System.out.println("Reservation not found.");
                            connection.rollback();
                            return false;
                        }

                        currentStatus = resultSet.getString("BookingStatus");
                        spaceID = resultSet.getInt("SpaceID");
                    }
                }

                // Step 2: Validate the status transition.
                boolean allowed = false;

                switch (currentStatus) {
                    case "Pending":
                        allowed = status.equalsIgnoreCase("Confirmed")
                                || status.equalsIgnoreCase("Cancelled");
                        break;

                    case "Confirmed":
                        allowed = status.equalsIgnoreCase("CheckedIn")
                                || status.equalsIgnoreCase("Cancelled");
                        break;

                    case "CheckedIn":
                        allowed = status.equalsIgnoreCase("Completed");
                        break;

                    case "Completed":
                    case "Cancelled":
                        allowed = false;
                        break;
                }

                if (!allowed) {
                    System.out.println("Invalid reservation status transition.");
                    connection.rollback();
                    return false;
                }

                // Step 3: Check whether the space can be occupied.
                if (status.equalsIgnoreCase("CheckedIn")) {

                    String spaceSQL = "UPDATE StudySpace " +
                            "SET SpaceStatus = 'Occupied' " +
                            "WHERE SpaceID = ? AND SpaceStatus = 'Available'";

                    try (PreparedStatement statement = connection.prepareStatement(spaceSQL)) {

                        statement.setInt(1, spaceID);

                        if (statement.executeUpdate() == 0) {
                            System.out.println("Study space is unavailable.");
                            connection.rollback();
                            return false;
                        }
                    }
                }

                // Step 4: Update reservation status.
                String updateSQL = "UPDATE Reservation " +
                        "SET BookingStatus = ? WHERE BookingID = ?";

                try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {

                    statement.setString(1, status);
                    statement.setInt(2, bookingID);

                    statement.executeUpdate();
                }

                // Step 5: Release the space if the reservation is completed.
                if (status.equalsIgnoreCase("Completed")) {

                    String releaseSQL = "UPDATE StudySpace " +
                            "SET SpaceStatus = 'Available' " +
                            "WHERE SpaceID = ? " +
                            "AND SpaceStatus = 'Occupied' " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM Reservation " +
                            "WHERE SpaceID = ? " +
                            "AND BookingStatus = 'CheckedIn')";

                    try (PreparedStatement statement = connection.prepareStatement(releaseSQL)) {

                        statement.setInt(1, spaceID);
                        statement.setInt(2, spaceID);

                        statement.executeUpdate();
                    }
                }

                // Step 6: Commit all changes.
                connection.commit();

                System.out.println("Reservation status updated successfully.");
                return true;

            } catch (SQLException e) {

                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error updating reservation status: " + e.getMessage());
        }

        return false;
    }

    public boolean cancelReservation(int bookingID) {

        String sql = "UPDATE Reservation " +
                "SET BookingStatus = 'Cancelled' " +
                "WHERE BookingID = ? " +
                "AND BookingStatus IN ('PENDING', 'CONFIRMED')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingID);

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.println("Reservation cancelled successfully");
                return true;
            } else {
                System.out.println("Reservation cannot be cancelled or does not exist");
            }

        } catch (SQLException e) {
            System.out.println("Error cancelling reservation: " + e.getMessage());
        }

        return false;
    }

    public boolean addReservation(Reservation reservation) {

        String sql = "INSERT INTO Reservation " +
                "(CustomerID, SpaceID, BookingDate, StartTime, Endtime, " +
                "BookingStatus, HourlyRateAtBooking, TotalPrice) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, reservation.getCustomerID());
            statement.setInt(2, reservation.getSpaceID());
            statement.setDate(3, reservation.getBookingDate());
            statement.setTime(4, reservation.getStartTime());
            statement.setTime(5, reservation.getEndTime());
            statement.setString(6, reservation.getBookingStatus());
            statement.setBigDecimal(7, reservation.getHourlyRateAtBooking());
            statement.setBigDecimal(8, reservation.getTotalPrice());

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error in updating reservation: " + e.getMessage());
            return false;
        }
    }

    public boolean createReservation(int customerID, int spaceID, Date bookingDate,
                                     Time startTime, Time endTime) {

        // Validate the booking date.
        if (bookingDate.toLocalDate().isBefore(java.time.LocalDate.now())) {
            System.out.println("Booking date cannot be in the past.");
            return false;
        }

        // Validate the reservation time.
        if (endTime.toLocalTime().compareTo(startTime.toLocalTime()) <= 0) {
            System.out.println("End time must be later than start time.");
            return false;
        }

        // Step 1: Validate the reservation time.
        if (endTime.toLocalTime().compareTo(startTime.toLocalTime()) <= 0) {
            System.out.println("End time must be later than start time.");
            return false;
        }

        // Step 2: Check whether the customer exists.
        CustomerDAO customerDAO = new CustomerDAO();

        if (!customerDAO.customerExists(customerID)) {
            System.out.println("Customer ID does not exist.");
            return false;
        }

        // Step 3: Check whether the study space exists.
        StudySpaceDAO studySpaceDAO = new StudySpaceDAO();

        if (!studySpaceDAO.studySpaceExists(spaceID)) {
            System.out.println("Study Space ID does not exist.");
            return false;
        }

        HubBranchDAO hubBranchDAO = new HubBranchDAO();

        if (!hubBranchDAO.isWithinOperatingHours(spaceID, startTime, endTime)) {
            System.out.println("Reservation time is outside branch operating hours.");
            return false;
        }

        if (!studySpaceDAO.isSpaceBookable(spaceID)) {
            System.out.println("This study space is currently unavailable.");
            return false;
        }

        // Step 4: Check availability.
        if (!isSpaceAvailable(spaceID, bookingDate, startTime, endTime)) {
            System.out.println("Study space is unavailable at that time.");
            return false;
        }

        // Step 5: Retrieve the discounted hourly rate.
        BigDecimal hourlyRate = studySpaceDAO.getHourlyRate(spaceID);

        if (hourlyRate == null) {
            System.out.println("Unable to retrieve the hourly rate.");
            return false;
        }

        // Step 6: Calculate the reservation duration.
        long minutes = Duration.between(
                startTime.toLocalTime(),
                endTime.toLocalTime()
        ).toMinutes();

        BigDecimal durationHours = BigDecimal.valueOf(minutes)
                .divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);

        // Step 7: Calculate the total price.
        BigDecimal totalPrice = hourlyRate.multiply(durationHours)
                .setScale(2, RoundingMode.HALF_UP);

        // Step 8: Create the reservation object.
        Reservation reservation = new Reservation(
                0,
                customerID,
                spaceID,
                bookingDate,
                startTime,
                endTime,
                "Pending",
                hourlyRate,
                totalPrice
        );

        // Step 9: Insert the reservation.
        boolean added = addReservation(reservation);

        if (added) {
            System.out.println("Reservation created successfully!");
            System.out.println("Hourly rate after discount: ₱" + hourlyRate);
            System.out.println("Duration: " + durationHours + " hours");
            System.out.println("Total price: ₱" + totalPrice);
        }

        return added;
    }

    public boolean isSpaceAvailable(int spaceID, Date bookingDate, Time startTime, Time endTime) {

        String sql = "SELECT COUNT(*) FROM Reservation " +
                "WHERE SpaceID = ? " +
                "AND BookingDate = ? " +
                "AND BookingStatus IN ('Pending', 'Confirmed', 'CheckedIn') " +
                "AND StartTime < ? " +
                "AND EndTime > ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);
            statement.setDate(2, bookingDate);
            statement.setTime(3, endTime);
            statement.setTime(4, startTime);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) == 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking availability: " + e.getMessage());
        }

        return false;
    }

    public ArrayList<Reservation> getReservationByCustomer(int customerID) {

        ArrayList<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT * FROM Reservation " +
                "WHERE CustomerID = ? " +
                "ORDER BY BookingDate DESC, StartTime DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    Reservation reservation = new Reservation(
                            result.getInt("BookingID"),
                            result.getInt("CustomerID"),
                            result.getInt("SpaceID"),
                            result.getDate("BookingDate"),
                            result.getTime("StartTime"),
                            result.getTime("EndTime"),
                            result.getString("BookingStatus"),
                            result.getBigDecimal("HourlyRateAtBooking"),
                            result.getBigDecimal("TotalPrice")
                    );

                    reservations.add(reservation);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customer reservation: " +e.getMessage());
        }

        return reservations;
    }


}
