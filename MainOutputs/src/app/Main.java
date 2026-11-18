package app;

import dao.CustomerDAO;
import dao.ReservationDAO; 
import model.Customer;

import java.sql.Time;      
import java.sql.Date;      
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== WELCOME TO STUDY HUB ===");
        System.out.print("Enter your ID to Log In: ");
        
        
        String inputtedID = scanner.nextLine(); 
        
       
        if (inputtedID.equals("admin")) {
            System.out.println("\nSUCCESS: Admin logged in.");
            System.out.println("Redirecting to Admin Dashboard...");
        } 
        else {
           
            CustomerDAO dao = new CustomerDAO();
            Customer existingCustomer = dao.searchCustomerByStudentID(inputtedID);
            
            if (existingCustomer != null) {
                System.out.println("\nSUCCESS: Welcome back, " + existingCustomer.getFirstName() + "!");
                System.out.println("Redirecting to Reservation Page...");
            } 
            
            else {
                System.out.println("\nNOTICE: User not found in database.");
                System.out.println("Redirecting to Registration Page...");
            }
        }
        
        scanner.close();
    }
}