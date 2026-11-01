package app;

//import dao.HubBranchDAO;
//import model.HubBranch;

//import dao.CustomerDAO;
//import model.Customer;

import dao.ReservationDAO;

import java.sql.Time;
import java.sql.Date;

//needs SQL server to be running
public class Main {

    public static void main(String[] args) {

        ReservationDAO reservationDAO = new ReservationDAO();

        boolean created = reservationDAO.createReservation(
                1,
                1,
                Date.valueOf("2026-10-01"),
                Time.valueOf("09:00:00"),
                Time.valueOf("11:00:00")
        );

        System.out.println("Reservation created: " + created);
    }
}
