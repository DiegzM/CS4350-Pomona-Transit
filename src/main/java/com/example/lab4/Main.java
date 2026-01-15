package com.example.lab4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;


public class Main {
    public static void main(String[] args) {

        // Initialize Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Connect to SQLite Database
        String url = "jdbc:sqlite:pomona_transit.db";
        Connection conn = null;

        // Try to connect, if fail, end the program
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        }
        catch (Exception e) {
            System.out.println("Connection to SQLite has failed.");
            System.out.println(e.getMessage());
            return;
        }

        // Begin the looping menu
        while (true) {
            // Display menu options
            System.out.println("\n==============================");
            System.out.println("Pomona Transit Database Menu");
            System.out.println("==============================");
            System.out.println("1. Display the schedule of all trips for a given start and end destination and date");
            System.out.println("2. Edit the schedule of a trip");
            System.out.println("3. Display the stops of a given trip");
            System.out.println("4. Display the weekly schedule for a given driver and date");
            System.out.println("5. Add a driver");
            System.out.println("6. Add a bus");
            System.out.println("7. Delete a bus");
            System.out.println("8. Record (insert) data of a given trip");
            System.out.println("9. Exit");

            // Get user input for menu option
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            // Handle user choice
            switch (choice) {
                case "1"  :
                    displayTrips(conn, scanner);
                    break;
                case "2":
                    editSchedule(conn, scanner);
                    break;
                case "3":
                    displayStops(conn, scanner);
                    break;
                case "4":
                    displayWeeklySchedule(conn, scanner);
                    break;
                case "5":
                    addDriver(conn, scanner);
                    break;
                case "6":
                    addBus(conn, scanner);
                    break;
                case "7":
                    deleteBus(conn, scanner);
                    break;
                case "8":
                    recordTripData(conn, scanner);
                    break;
                case "9":
                    System.out.println("\nExiting program.\n");
                    try {
                        if (conn != null && !conn.isClosed()) {
                            conn.close();
                        }
                    } catch (Exception e) {
                        System.out.println("\nError closing the database connection.\n");
                    }
                    scanner.close();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }

    }

    // Method to display trips based on user input
    static void displayTrips(Connection conn, Scanner scanner) {

        // Get user input for start destination, end destination, and date
        System.out.println("Display trips for a given start and end destination and date");
        String startDest, endDest, date;
        System.out.print("Enter start destination: ");
        startDest = scanner.nextLine();
        System.out.print("Enter end destination: ");
        endDest = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        date = scanner.nextLine();

        // Query to get StartLocationName and DestinationName, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, and BusID.
        String query = """
            SELECT t.StartLocationName, t.DestinationName, o.Date, o.ScheduledStartTime, o.ScheduledArrivalTime, o.DriverName, o.BusID
            FROM Trip t
            JOIN TripOffering o on t.TripNumber = o.TripNumber
            WHERE t.StartLocationName = ? AND t.DestinationName = ? AND o.Date = ?;
        """;

        // Execute the query and display results
        try {
            displayTable(conn, query, new String[] {startDest, endDest, date});
        }
        catch (Exception e) {
            System.out.println("Error displaying trips: " + e.getMessage());
        }
            


    }

    // Method to edit the schedule of a trip
    static void editSchedule(Connection conn, Scanner scanner) {
        /*
        Delete a trip offering specified by Trip#, Date, and ScheduledStartTime;
        -Add a set of trip offerings assuming the values of all attributes are given (the software
        asks if you have more trips to enter) ;
        - Change the driver for a given Trip offering (i.e given TripNumber, Date,
        ScheduledStartTime);
        - Change the bus for a given Trip offering.
         */

        
        // Edit schedule menu loop
        while (true) {
            System.out.println("\n==============================");
            System.out.println("EDIT SCHEDULE MENU");
            System.out.println("==============================");
            System.out.println("1. Delete a trip offering");
            System.out.println("2. Add a trip offering");
            System.out.println("3. Change the driver for a trip offering");
            System.out.println("4. Change the bus for a trip offering");
            System.out.println("5. Return to main menu");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            // handle user choice
            switch (choice) {
                case "1":
                    System.out.println("Delete a trip offering");
                    System.out.print("Enter Trip Number: ");
                    String tripNumber = scanner.nextLine();
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter Scheduled Start Time (HH:MM): ");
                    String startTime = scanner.nextLine();
                    String deleteQuery = """
                        DELETE FROM TripOffering
                        WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?;
                    """;
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                        pstmt.setString(1, tripNumber);
                        pstmt.setString(2, date);
                        pstmt.setString(3, startTime);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\nTrip offering deleted successfully.\n");
                        } else {
                            System.out.println("\nNo matching trip offering found.\n");
                        }
                    } catch (Exception e) {
                        System.out.println("\nError deleting trip offering: " + e.getMessage() + "\n");
                    }

                    break;
                case "2":
                    while (true) {
                        System.out.println("Add a trip offering");
                        System.out.print("Enter Trip Number: ");
                        String tripNumberAdd = scanner.nextLine();
                        System.out.print("Enter Date (YYYY-MM-DD): ");
                        String dateAdd = scanner.nextLine();
                        System.out.print("Enter Scheduled Start Time (HH:MM): ");
                        String startTimeAdd = scanner.nextLine();
                        System.out.print("Enter Scheduled Arrival Time (HH:MM): ");
                        String arrivalTimeAdd = scanner.nextLine();
                        System.out.print("Enter Driver Name: ");
                        String driverNameAdd = scanner.nextLine();
                        System.out.print("Enter Bus ID: ");
                        String busIDAdd = scanner.nextLine();

                        String insertQuery = """
                            INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID)
                            VALUES (?, ?, ?, ?, ?, ?);
                        """;
                        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                            pstmt.setString(1, tripNumberAdd);
                            pstmt.setString(2, dateAdd);
                            pstmt.setString(3, startTimeAdd);
                            pstmt.setString(4, arrivalTimeAdd);
                            pstmt.setString(5, driverNameAdd);
                            pstmt.setString(6, busIDAdd);
                            pstmt.executeUpdate();
                            System.out.println("\nTrip offering added successfully.\n");
                        } catch (Exception e) {
                            System.out.println("\nError adding trip offering: " + e.getMessage() + "\n");
                        }

                        System.out.print("Do you want to add another trip offering? (yes/no): ");
                        String another = scanner.nextLine();
                        if (!another.equalsIgnoreCase("yes")) {
                            break;
                        }
                    }
                    break;
                case "3":
                    System.out.println("Update Driver for a trip offering");
                    System.out.print("Enter Trip Number: ");
                    String tripNumberUpdateD = scanner.nextLine();
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    String dateUpdateD = scanner.nextLine();
                    System.out.print("Enter Scheduled Start Time (HH:MM): ");
                    String startTimeUpdateD = scanner.nextLine();
                    System.out.print("Enter new Driver Name: ");
                    String newDriverName = scanner.nextLine();
                    String updateDriverQuery = """
                        UPDATE TripOffering
                        SET DriverName = ?
                        WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?;
                    """;
                    try (PreparedStatement pstmt = conn.prepareStatement(updateDriverQuery)) {
                        pstmt.setString(1, newDriverName);
                        pstmt.setString(2, tripNumberUpdateD);
                        pstmt.setString(3, dateUpdateD);
                        pstmt.setString(4, startTimeUpdateD);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\nDriver updated successfully.\n");
                        } else {
                            System.out.println("\nNo matching trip offering found.\n");
                        }
                    } catch (Exception e) {
                        System.out.println("\nError updating driver: " + e.getMessage() + "\n");
                    }
                    break;
                case "4":
                    System.out.println("Update Bus for a trip offering");
                    System.out.print("Enter Trip Number: ");
                    String tripNumberUpdateB = scanner.nextLine();
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    String dateUpdateB = scanner.nextLine();
                    System.out.print("Enter Scheduled Start Time (HH:MM): ");
                    String startTimeUpdateB = scanner.nextLine();
                    System.out.print("Enter new Bus ID: ");
                    String newBusID = scanner.nextLine();
                    String updateBusQuery = """
                        UPDATE TripOffering
                        SET BusID = ?
                        WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?;
                    """;
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBusQuery)) {
                        pstmt.setString(1, newBusID);
                        pstmt.setString(2, tripNumberUpdateB);
                        pstmt.setString(3, dateUpdateB);
                        pstmt.setString(4, startTimeUpdateB);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("\nBus updated successfully.\n");
                        } else {
                            System.out.println("\nNo matching trip offering found.\n");
                        }
                    } catch (Exception e) {
                        System.out.println("\nError updating bus: " + e.getMessage() + "\n");
                    }
                    break;
                case "5":
                    // Return to main menu
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    
    }

    // Display stops of a given trip
    static void displayStops(Connection conn, Scanner scanner) {
        System.out.println("Display stops of a given trip");
        System.out.print("Enter Trip Number: ");
        String tripNumber = scanner.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Scheduled Start Time (HH:MM): ");
        String startTime = scanner.nextLine();
        String query = """
            SELECT ts.StopNumber, ts.SequenceNumber, ts.DrivingTime
            FROM TripStopInfo ts
            JOIN Trip t ON t.TripNumber = ts.TripNumber
            WHERE t.TripNumber = ?;
        """;
        try {
            displayTable(conn, query, new String[] {tripNumber});
        }
        catch (Exception e) {
            System.out.println("Error displaying stops: " + e.getMessage());
        }
    }

    // Display weekly schedule for a given driver and date
    static void displayWeeklySchedule(Connection conn, Scanner scanner) {
        System.out.println("Display weekly schedule for a given driver and date");
        System.out.print("Enter Driver Name: ");
        String driverName = scanner.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        String query = """
            SELECT o.TripNumber, o.Date, o.ScheduledStartTime, o.ScheduledArrivalTime, o.BusID
            FROM TripOffering o
            WHERE o.DriverName = ? AND strftime('%W', o.Date) = strftime('%W', ?);
        """;
        try {
            displayTable(conn, query, new String[] {driverName, date});
        }
        catch (Exception e) {
            System.out.println("Error displaying weekly schedule: " + e.getMessage());
        }
    }

    // Add a driver
    static void addDriver(Connection conn, Scanner scanner) {
        System.out.println("Add a driver");
        System.out.print("Enter Driver Name: ");
        String driverName = scanner.nextLine();
        System.out.print("Enter Driver's phone number: ");
        String phoneNumber = scanner.nextLine();
        String insertQuery = """
            INSERT INTO Driver (DriverName, DriverTelephoneNumber)
            VALUES (?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, driverName);
            pstmt.setString(2, phoneNumber);
            pstmt.executeUpdate();
            System.out.println("\nDriver added successfully.\n");
        } catch (Exception e) {
            System.out.println("\nError adding driver: " + e.getMessage() + "\n");
        }
    }

    // Add a bus
    static void addBus(Connection conn, Scanner scanner) {
        System.out.println("Add a bus");
        System.out.print("Enter Bus ID: ");
        String busID = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Year: ");
        String year = scanner.nextLine();
        String insertQuery = """
            INSERT INTO Bus (BusID, Model, Year)
            VALUES (?, ?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, busID);
            pstmt.setString(2, model);
            pstmt.setString(3, year);
            pstmt.executeUpdate();
            System.out.println("\nBus added successfully.\n");
        } catch (Exception e) {
            System.out.println("\nError adding bus: " + e.getMessage() + "\n");
        }
    }

    // Delete a bus (by ID)
    static void deleteBus(Connection conn, Scanner scanner) {
        System.out.println("Delete a bus");
        System.out.print("Enter Bus ID: ");
        String busID = scanner.nextLine();
        String deleteQuery = """
            DELETE FROM Bus
            WHERE BusID = ?;
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, busID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nBus deleted successfully.\n");
            } else {
                System.out.println("\nNo matching bus found.\n");
            }
        } catch (Exception e) {
            System.out.println("\nError deleting bus: " + e.getMessage() + "\n");
        }
    }

    // Record (insert) data of a given trip
    static void recordTripData(Connection conn, Scanner scanner) {
        System.out.println("Record (insert) data of a given trip");
        System.out.print("Enter Trip Number: ");
        String tripNumber = scanner.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Scheduled Start Time (HH:MM): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter Stop Number: ");
        String stopNumber = scanner.nextLine();
        System.out.print("Enter Scheduled Arrival Time (HH:MM): ");
        String scheduledArrivalTime = scanner.nextLine();
        System.out.print("Enter Actual Start Time (HH:MM): ");
        String actualStartTime = scanner.nextLine();
        System.out.print("Enter Actual Arrival Time (HH:MM): ");
        String actualArrivalTime = scanner.nextLine();
        System.out.print("Enter Number of Passengers entered: ");
        String numPassengersEntered = scanner.nextLine();
        System.out.print("Enter Number of Passengers exited: ");
        String numPassengersExited = scanner.nextLine();
        String insertQuery = """
            INSERT INTO ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, tripNumber);
            pstmt.setString(2, date);
            pstmt.setString(3, startTime);
            pstmt.setString(4, stopNumber);
            pstmt.setString(5, scheduledArrivalTime);
            pstmt.setString(6, actualStartTime);
            pstmt.setString(7, actualArrivalTime);
            pstmt.setString(8, numPassengersEntered);
            pstmt.setString(9, numPassengersExited);
            pstmt.executeUpdate();
            System.out.println("\nTrip data recorded successfully.\n");
        } catch (Exception e) {
            System.out.println("\nError recording trip data: " + e.getMessage() + "\n");
        }
    }

    // Helper method to display a table by columns based on a connection, query, and values (default empty)
    static void displayTable(Connection conn, String query, String[] values) {
        ResultSet rs = null;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }

            rs = pstmt.executeQuery();
            var meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Check if there are results
            if (!rs.isBeforeFirst()) {
                System.out.println("\nNo results found.");
                return;
            }

            System.out.println("\n=============== RESULTS ===============\n");
            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            System.out.println("--------------------------------------------------");
            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
            System.out.println("\n========================================\n");
        }
        catch (Exception e) {
            System.out.println("\nError executing query: " + e.getMessage());
        }
        finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
                System.out.println("\nError closing ResultSet: " + e.getMessage());   
            }
        }
    }
}

