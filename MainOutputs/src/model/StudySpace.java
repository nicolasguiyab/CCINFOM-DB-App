package model;

public class StudySpace {

    private int spaceID;
    private String branchName;
    private String spaceType;
    private int capacity;
    private double hourlyRate;
    private String spaceStatus;

    public StudySpace(int spaceID, String branchName,
                      String spaceType, int capacity,
                      double hourlyRate, String spaceStatus) {

        this.spaceID = spaceID;
        this.branchName = branchName;
        this.spaceType = spaceType;
        this.capacity = capacity;
        this.hourlyRate = hourlyRate;
        this.spaceStatus = spaceStatus;
    }

    public int getSpaceID() {
        return spaceID;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String getSpaceStatus() {
        return spaceStatus;
    }

    @Override
    public String toString() {
        return "Space ID: " + spaceID
                + "\nBranch: " + branchName
                + "\nType: " + spaceType
                + "\nCapacity: " + capacity
                + "\nHourly Rate: ₱" + String.format("%.2f", hourlyRate)
                + "\nStatus: " + spaceStatus;
    }
}
