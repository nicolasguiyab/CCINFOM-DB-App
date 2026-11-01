package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

public class Reservation {

    private int bookingID;
    private int customerID;
    private int spaceID;
    private Date bookingDate;
    private Time startTime;
    private Time endTime;
    private String bookingStatus;
    private BigDecimal hourlyRateAtBooking;
    private BigDecimal totalPrice;

    public Reservation(int bookingID, int customerID, int spaceID,
                       Date bookingDate, Time startTime, Time endTime,
                       String bookingStatus, BigDecimal hourlyRateAtBooking,
                       BigDecimal totalPrice) {

        this.bookingID = bookingID;
        this.customerID = customerID;
        this.spaceID = spaceID;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
        this.hourlyRateAtBooking = hourlyRateAtBooking;
        this.totalPrice = totalPrice;
    }

    public int getBookingID() {
        return bookingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getSpaceID() {
        return spaceID;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public BigDecimal getHourlyRateAtBooking() {
        return hourlyRateAtBooking;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingID +
                "\nCustomer ID: " + customerID +
                "\nStudy Space ID: " + spaceID +
                "\nBooking Date: " + bookingDate +
                "\nStart Time: " + startTime +
                "\nEnd Time: " + endTime +
                "\nStatus: " + bookingStatus +
                "\nHourly Rate: " + hourlyRateAtBooking +
                "\nTotal Price: " + totalPrice;
    }
}