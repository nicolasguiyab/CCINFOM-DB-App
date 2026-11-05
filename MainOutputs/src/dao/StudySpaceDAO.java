package dao;

import database.DatabaseConnection;
import model.StudySpace;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudySpaceDAO {

    public ArrayList<StudySpace> getAllStudySpaces() {

        ArrayList<StudySpace> spaces = new ArrayList<>();

        String sql =
                "SELECT s.SpaceID, b.BranchName, s.SpaceType, " +
                        "s.Capacity, s.HourlyRate, s.SpaceStatus " +
                        "FROM StudySpace s " +
                        "JOIN HubBranch b ON s.BranchID = b.BranchID " +
                        "ORDER BY s.SpaceID";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                StudySpace space = new StudySpace(
                        result.getInt("SpaceID"),
                        result.getString("BranchName"),
                        result.getString("SpaceType"),
                        result.getInt("Capacity"),
                        result.getDouble("HourlyRate"),
                        result.getString("SpaceStatus")
                );

                spaces.add(space);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving study spaces: "
                    + e.getMessage());
        }

        return spaces;
    }

    public BigDecimal getHourlyRate(int spaceID) {

        String sql = "SELECT HourlyRate, DiscountPercent " +
                "FROM StudySpace WHERE SpaceID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    BigDecimal hourlyRate = result.getBigDecimal("HourlyRate");

                    BigDecimal discountPercent = result.getBigDecimal("DiscountPercent");

                    return hourlyRate.multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"))));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving hourly rate: " + e.getMessage());
        }

        return null;
    }

    public boolean studySpaceExists(int spaceID) {

        String sql = "SELECT 1 FROM StudySpace WHERE SpaceID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking study space: " + e.getMessage());
            return false;
        }
    }

    public boolean isSpaceBookable(int spaceID) {

        String sql = "SELECT SpaceStatus FROM StudySpace WHERE SpaceID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String status = result.getString("SpaceStatus");
                    return "Available".equalsIgnoreCase(status);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking space status: " + e.getMessage());
        }

        return false;
    }

    public boolean updateSpaceStatus(int spaceID, String status) {

        String[] validStatuses = {"Available", "Occupied", "Under Maintenance"};

        boolean valid = false;

        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                status = validStatus;
                valid = true;
                break;
            }
        }

        String sql = "UPDATE StudySpace SET SpaceStatus = ? WHERE SpaceID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, spaceID);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Study space status updated");
                return true;
            }

            System.out.println("Study space not found");

        } catch (SQLException e) {
            System.out.println("Error updating study space status: " + e.getMessage());
        }

        return false;
    }

    public boolean releaseSpace(int spaceID) {

        String sql = "UPDATE StudySpace " +
                "SET SpaceStatus = 'Available' " +
                "WHERE SpaceID = ? " +
                "AND SpaceStatus = 'Occupied' " +
                "AND NOT EXISTS (" +
                "SELECT 1 FROM Reservation " +
                "WHERE SpaceID = ? " +
                "AND BookingStatus = 'CheckedIn')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);
            statement.setInt(2, spaceID);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Study space is now available.");
                return true;
            }

            System.out.println("Study space remains occupied or is not currently occupied.");
            return false;

        } catch (SQLException e) {
            System.out.println("Error releasing study space: " + e.getMessage());
        }

        return false;
    }
}
