package dao;

import database.DatabaseConnection;
import model.HubBranch;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class HubBranchDAO {

    public ArrayList<HubBranch> getAllBranches() {

        ArrayList<HubBranch> branches = new ArrayList<>();

        String sql = "SELECT BranchID, BranchName, Address, City, ContactNumber "
                + "FROM HubBranch";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                HubBranch branch = new HubBranch(
                        result.getInt("BranchID"),
                        result.getString("BranchName"),
                        result.getString("Address"),
                        result.getString("City"),
                        result.getString("ContactNumber")
                );

                branches.add(branch);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving branches: " + e.getMessage());
        }

        return branches;
    }

    public boolean isWithinOperatingHours(int spaceID, Time startTime, Time endTime) {

        String sql = "SELECT h.OperatingHoursOpen, h.OperatingHoursClose " +
                "FROM HubBranch h " +
                "JOIN StudySpace s ON h.BranchID = s.BranchID " +
                "WHERE s.SpaceID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, spaceID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Time openingTime = result.getTime("OperatingHoursOpen");
                    Time closingTime = result.getTime("OperatingHoursClose");

                    return !startTime.before(openingTime) && !endTime.after(closingTime);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking operating hours: " + e.getMessage());
        }

        return false;
    }
}
