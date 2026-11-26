package app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import dao.CustomerDAO;
import dao.StudySpaceDAO;
import dao.HubBranchDAO;
import dao.ReservationDAO;
import model.Customer;
import model.HubBranch;
import model.StudySpace;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class RegistrationServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0
        );

        server.createContext("/api/register",
                RegistrationServer::handleRegistration);

        server.createContext("/api/login", RegistrationServer::handleLogin);

        server.createContext("/api/customer",
                RegistrationServer::handleGetCustomer);

        server.createContext("/api/branches",
                RegistrationServer::handleGetBranches);

        server.createContext("/api/study-spaces",
                RegistrationServer::handleGetStudySpaces);

        server.createContext("/api/reservations",
                RegistrationServer::handleCreateReservation);

        server.createContext("/api/operating-hours",
                RegistrationServer::handleGetOperatingHours);

        server.createContext("/api/reservation-slots",
                RegistrationServer::handleGetReservationSlots);

        server.createContext("/api/monthly-reservations",
                RegistrationServer::handleGetMonthlyReservations);

        server.start();

        System.out.println("Study Hub server started!");
        System.out.println("Registration API: http://localhost:" + PORT
                + "/api/register");
    }

    private static void handleRegistration(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String requestBody = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> data = parseFormData(requestBody);

            String customerType = data.get("customerType");

            if (customerType == null ||
                    (!customerType.equals("Student")
                            && !customerType.equals("Regular"))) {

                sendResponse(exchange, 400,
                        "Invalid customer type.");
                return;
            }

            Customer customer = new Customer(
                    0,
                    data.get("studentID"),
                    data.get("govID"),
                    data.get("idType"),
                    data.get("firstName"),
                    data.get("lastName"),
                    data.get("email"),
                    data.get("mobileNumber"),
                    data.get("university"),
                    data.get("streetAddress"),
                    data.get("city"),
                    customerType
            );

            CustomerDAO customerDAO = new CustomerDAO();

            int customerID = customerDAO.registerCustomer(
                    customer,
                    data.get("password")
            );

            if (customerID > 0) {
                sendResponse(exchange, 200,
                        "Registration successful! Your Customer ID is: "
                                + customerID);
            } else {
                sendResponse(exchange, 400,
                        "Registration failed. Please check your information.");
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(exchange, 500,
                    "An error occurred during registration.");
        }
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String requestBody = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> data = parseFormData(requestBody);

            int customerID = Integer.parseInt(data.get("customerID"));
            String password = data.get("password");

            CustomerDAO customerDAO = new CustomerDAO();

            boolean authenticated =
                    customerDAO.authenticateUser(customerID, password);

            if (authenticated) {
                sendResponse(exchange, 200, "Login successful!");
            } else {
                sendResponse(exchange, 401, "Invalid Customer ID or password.");
            }

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid Customer ID.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "An error occurred during login.");
        }
    }

    private static Map<String, String> parseFormData(String body) {

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8);

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1],
                    StandardCharsets.UTF_8)
                    : "";

            data.put(key, value);
        }

        return data;
    }

    private static void sendResponse(
            HttpExchange exchange, int status, String message)
            throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
        );
    }


    private static void handleGetCustomer(HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();
            Map<String, String> data = parseFormData(query);

            int customerID = Integer.parseInt(data.get("customerID"));

            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.searchCustomerByID(customerID);

            if (customer == null) {
                sendResponse(exchange, 404, "Customer not found.");
                return;
            }

            String fullName = customer.getFirstName() + " "
                    + customer.getLastName();

            String json = "{\"customerID\":" + customer.getCustomerID()
                    + ",\"customerName\":\""
                    + fullName.replace("\\", "\\\\").replace("\"", "\\\"")
                    + "\"}";

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json);

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid Customer ID.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Unable to retrieve customer.");
        }
    }


    private static void handleGetBranches(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            HubBranchDAO dao = new HubBranchDAO();
            ArrayList<HubBranch> branches = dao.getAllBranches();

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < branches.size(); i++) {

                HubBranch branch = branches.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"branchID\":").append(branch.getBranchID()).append(",")
                        .append("\"branchName\":\"").append(jsonEscape(branch.getBranchName())).append("\",")
                        .append("\"city\":\"").append(jsonEscape(branch.getCity())).append("\"")
                        .append("}");
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Unable to retrieve branches.");
        }
    }


    private static void handleGetStudySpaces(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();
            Map<String, String> data = parseFormData(query);

            int branchID = Integer.parseInt(data.get("branchID"));

            StudySpaceDAO dao = new StudySpaceDAO();

            ArrayList<StudySpace> spaces =
                    dao.getStudySpacesByBranch(branchID);

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < spaces.size(); i++) {

                StudySpace space = spaces.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"spaceID\":").append(space.getSpaceID()).append(",")
                        .append("\"spaceType\":\"").append(jsonEscape(space.getSpaceType())).append("\",")
                        .append("\"capacity\":").append(space.getCapacity()).append(",")
                        .append("\"hourlyRate\":").append(space.getHourlyRate()).append(",")
                        .append("\"spaceStatus\":\"").append(jsonEscape(space.getSpaceStatus())).append("\"")
                        .append("}");
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json.toString());

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid branch ID.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Unable to retrieve study spaces.");
        }
    }


    private static String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void handleCreateReservation(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String requestBody = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> data = parseFormData(requestBody);

            int customerID = Integer.parseInt(data.get("customerID"));
            int spaceID = Integer.parseInt(data.get("spaceID"));

            java.sql.Date bookingDate =
                    java.sql.Date.valueOf(data.get("reservationDate"));

            java.sql.Time startTime =
                    java.sql.Time.valueOf(data.get("startTime") + ":00");

            java.sql.Time endTime =
                    java.sql.Time.valueOf(data.get("endTime") + ":00");

            ReservationDAO reservationDAO = new ReservationDAO();

            boolean success = reservationDAO.createReservation(
                    customerID,
                    spaceID,
                    bookingDate,
                    startTime,
                    endTime
            );

            if (success) {
                sendResponse(
                        exchange,
                        200,
                        "Reservation created successfully!"
                );
            } else {
                sendResponse(
                        exchange,
                        400,
                        "Reservation failed. The selected space may be unavailable, "
                                + "the time may be invalid, or the branch may be closed."
                );
            }

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid reservation information.");
        } catch (Exception e) {
            e.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    "An error occurred while creating your reservation."
            );
        }
    }


    private static void handleGetOperatingHours(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();
            Map<String, String> data = parseFormData(query);

            int branchID = Integer.parseInt(data.get("branchID"));

            HubBranchDAO dao = new HubBranchDAO();

            String[] hours = dao.getOperatingHours(branchID);

            if (hours == null) {
                sendResponse(exchange, 404,
                        "Operating hours not found.");
                return;
            }

            String json = "{"
                    + "\"openingTime\":\"" + jsonEscape(hours[0]) + "\","
                    + "\"closingTime\":\"" + jsonEscape(hours[1]) + "\""
                    + "}";

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json);

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid branch ID.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500,
                    "Unable to retrieve operating hours.");
        }
    }


    private static void handleGetReservationSlots(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();
            Map<String, String> data = parseFormData(query);

            int spaceID = Integer.parseInt(data.get("spaceID"));

            java.sql.Date bookingDate =
                    java.sql.Date.valueOf(data.get("bookingDate"));

            ReservationDAO dao = new ReservationDAO();

            ArrayList<Map<String, String>> slots =
                    dao.getReservationSlots(spaceID, bookingDate);

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < slots.size(); i++) {

                Map<String, String> slot = slots.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"startTime\":\"")
                        .append(jsonEscape(slot.get("startTime")))
                        .append("\",")

                        .append("\"endTime\":\"")
                        .append(jsonEscape(slot.get("endTime")))
                        .append("\",")

                        .append("\"bookingStatus\":\"")
                        .append(jsonEscape(slot.get("bookingStatus")))
                        .append("\",")

                        .append("\"paymentStatus\":\"")
                        .append(jsonEscape(slot.get("paymentStatus")))
                        .append("\"")

                        .append("}");
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json.toString());

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid reservation information.");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500,
                    "Unable to retrieve reservation slots.");
        }
    }


    private static void handleGetMonthlyReservations(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed.");
            return;
        }

        try {
            String query = exchange.getRequestURI().getRawQuery();
            Map<String, String> data = parseFormData(query);

            int spaceID = Integer.parseInt(data.get("spaceID"));
            int month = Integer.parseInt(data.get("month"));
            int year = Integer.parseInt(data.get("year"));

            if (month < 1 || month > 12 || year < 1) {
                sendResponse(exchange, 400, "Invalid month or year.");
                return;
            }

            ReservationDAO dao = new ReservationDAO();

            ArrayList<Map<String, String>> reservations =
                    dao.getReservationsByMonth(spaceID, month, year);

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < reservations.size(); i++) {

                Map<String, String> reservation = reservations.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"bookingDate\":\"")
                        .append(jsonEscape(reservation.get("bookingDate")))
                        .append("\",")

                        .append("\"startTime\":\"")
                        .append(jsonEscape(reservation.get("startTime")))
                        .append("\",")

                        .append("\"endTime\":\"")
                        .append(jsonEscape(reservation.get("endTime")))
                        .append("\",")

                        .append("\"bookingStatus\":\"")
                        .append(jsonEscape(reservation.get("bookingStatus")))
                        .append("\",")

                        .append("\"paymentStatus\":\"")
                        .append(jsonEscape(reservation.get("paymentStatus")))
                        .append("\"")

                        .append("}");
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=UTF-8"
            );

            sendResponse(exchange, 200, json.toString());

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid reservation parameters.");

        } catch (Exception e) {
            e.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    "Unable to retrieve monthly reservations."
            );
        }
    }


}
