package dao;

import database.DatabaseConnection;
import model.HubBranch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
