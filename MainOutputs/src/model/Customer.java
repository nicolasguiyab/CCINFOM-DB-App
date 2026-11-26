package model;

public class Customer {

    private int customerID;
    private String studentID;
    private String govID;
    private String idType;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String university;
    private String streetAddress;
    private String city;
    private String customerType;

    // Original constructor - retained for compatibility
    public Customer(int customerID, String studentID,
                    String firstName, String lastName,
                    String email, String mobileNumber,
                    String university) {

        this(customerID, studentID, null, null,
                firstName, lastName, email, mobileNumber,
                university, null, null,
                studentID != null && !studentID.isBlank()
                        ? "Student" : "Regular");
    }

    // Updated constructor
    public Customer(int customerID, String studentID,
                    String govID, String idType,
                    String firstName, String lastName,
                    String email, String mobileNumber,
                    String university, String streetAddress,
                    String city, String customerType) {

        this.customerID = customerID;
        this.studentID = studentID;
        this.govID = govID;
        this.idType = idType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.university = university;
        this.streetAddress = streetAddress;
        this.city = city;
        this.customerType = customerType;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getGovID() {
        return govID;
    }

    public String getIDType() {
        return idType;
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

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getCustomerType() {
        return customerType;
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerID
                + "\nCustomer Type: " + customerType
                + "\nStudent ID: " + studentID
                + "\nGovernment ID: " + govID
                + "\nID Type: " + idType
                + "\nName: " + firstName + " " + lastName
                + "\nEmail: " + email
                + "\nMobile: " + mobileNumber
                + "\nUniversity: " + university
                + "\nAddress: " + streetAddress
                + "\nCity: " + city;
    }
}