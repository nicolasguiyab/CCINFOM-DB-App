package model;

public class HubBranch {

    private int branchID;
    private String branchName;
    private String address;
    private String city;
    private String contactNumber;

    public HubBranch(int branchID, String branchName, String address,
                     String city, String contactNumber) {
        this.branchID = branchID;
        this.branchName = branchName;
        this.address = address;
        this.city = city;
        this.contactNumber = contactNumber;
    }

    public int getBranchID() {
        return branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getCity() {
        return city;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    @Override
    public String toString() {
        return "Branch ID: " + branchID
                + " | Name: " + branchName
                + " | Address: " + address
                + " | City: " + city
                + " | Contact: " + contactNumber;
    }
}
