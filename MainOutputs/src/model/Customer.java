package model;

public class Customer {

    private int customerID;
    private String studentID;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String university;

    public Customer(int customerID, String studentID,
                    String firstName, String lastName,
                    String email, String mobileNumber,
                    String university) {

        this.customerID = customerID;
        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.university = university;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getUniversity() {
        return university;
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerID
                + "\nStudent ID: " + studentID
                + "\nName: " + firstName + " " + lastName
                + "\nEmail: " + email
                + "\nMobile: " + mobileNumber
                + "\nUniversity: " + university;
    }

}
