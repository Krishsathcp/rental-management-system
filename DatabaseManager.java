import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
class DatabaseManager {
    private Connection conn;
    private Statement stmt;
    private static final double DAILY_RATE = 50.0; // Base rate per day

    public void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rental_management_system", "root", "Manjari03*");
            stmt = conn.createStatement();
            System.out.println("Connected to MySQL!");
        } catch (SQLException e) {
            System.out.println("MySQL Connection Error: " + e.getMessage());
        }
    }

    public void insertCar(String brand, String model, int year) {
        try {
            String sql = "INSERT INTO cars (brand, model, year, rentalStatus) VALUES ('" + brand + "','" + model + "'," + year + ", 'Y')";
            stmt.execute(sql);
            System.out.println("Car added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding car: " + e.getMessage());
        }
    }

    public void deleteCar(int carId) {
        try {
            // Check if car is currently booked
            if (isCarCurrentlyBooked(carId)) {
                System.out.println("Cannot delete car - it's currently booked!");
                return;
            }
            
            String sql = "DELETE FROM cars WHERE idcars = " + carId;
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected > 0) {
                System.out.println("Car deleted successfully!");
            } else {
                System.out.println("Car not found with ID: " + carId);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting car: " + e.getMessage());
        }
    }

    public void addCustomer(String name, String city) {
        try {
            String sql = "INSERT INTO customers(name, city) VALUES('" + name + "','" + city + "')";
            stmt.execute(sql);
            System.out.println("Customer added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    public void showAllCars() {
    try (ResultSet rs = stmt.executeQuery("SELECT * FROM cars")) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        ALL CARS                             ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-4s │ %-12s │ %-12s │ %-6s │ %-12s ║%n", "ID", "Brand", "Model", "Year", "Status");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");

        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
            String rawStatus = rs.getString("rentalStatus");
            String status = "Y".equals(rawStatus) ? "Available" : "Rented";

            System.out.printf("║ %-4d │ %-12s │ %-12s │ %-6d │ %-12s ║%n",
                rs.getInt("idcars"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                status);
        }

        if (!hasData) {
            System.out.println("║                     No cars available                       ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    } catch (SQLException e) {
        System.out.println("Error showing cars: " + e.getMessage());
    }
}


    public void showAvailableCars() {
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM cars WHERE rentalStatus = 'Y'")) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║                  AVAILABLE CARS                       ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.printf("║ %-4s │ %-12s │ %-12s │ %-6s │ %-8s ║%n", "ID", "Brand", "Model", "Year", "Rate/Day");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("║ %-4d │ %-12s │ %-12s │ %-6d │ $%-7.0f ║%n",
                    rs.getInt("idcars"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    DAILY_RATE);
            }
            
            if (!hasData) {
                System.out.println("║              No cars available for rent               ║");
            }
            System.out.println("╚════════════════════════════════════════════════════════╝");
        } catch (SQLException e) {
            System.out.println("Error showing available cars: " + e.getMessage());
        }
    }

    public void showAllCarsBooked(int customerId) {
    String query = "SELECT carid FROM rms.customers " +
                   "JOIN rms.booking ON rms.customers.custId = rms.booking.customerid " +
                   "WHERE customerid = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, customerId);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        CARS BOOKED BY CUSTOMER         ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.printf("║ %-8s │ %-24s ║%n", "Car ID", "Booking Info");
        System.out.println("╠════════════════════════════════════════╣");

        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
            int carId = rs.getInt("carid");
            System.out.printf("║ %-8d │ %-24s ║%n", carId, "Booked by Customer #" + customerId);
        }

        if (!hasData) {
            System.out.println("║    No cars booked by this customer     ║");
        }

        System.out.println("╚════════════════════════════════════════╝");

    } catch (SQLException e) {
        System.out.println("Error retrieving booked cars: " + e.getMessage());
    }
}

    



    public void viewAllCustomers() {
    try (ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║                  CUSTOMER LIST                     ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.printf("║ %-6s │ %-20s │ %-15s ║%n", "ID", "Name", "City");
        System.out.println("╠════════════════════════════════════════════════════╣");

        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
            System.out.printf("║ %-6d │ %-20s │ %-15s ║%n",
                rs.getInt("custId"),
                rs.getString("name"),
                rs.getString("city"));
        }

        if (!hasData) {
            System.out.println("║            No customers found in the system        ║");
        }

        System.out.println("╚════════════════════════════════════════════════════╝");
    } catch (SQLException e) {
        System.out.println("Error retrieving customers: " + e.getMessage());
    }
}




    public boolean isValidCustomer(int custId) {
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE custId = " + custId)) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking customer: " + e.getMessage());
            return false;
        }
    }

    public boolean isCarAvailable(int carId) {
        try (ResultSet rs = stmt.executeQuery("SELECT rentalStatus FROM cars WHERE idcars = " + carId)) {
            if (rs.next()) {
                return rs.getString("rentalStatus").equals("Y");
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error checking car availability: " + e.getMessage());
            return false;
        }
    }

    public boolean isCarCurrentlyBooked(int carId) {
        try (ResultSet rs = stmt.executeQuery(
            "SELECT * FROM booking WHERE carid = " + carId + " AND enddate >= CURDATE()")) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking current bookings: " + e.getMessage());
            return false;
        }
    }

    public boolean hasDateConflict(int carId, LocalDate startDate, LocalDate endDate) {
        try {
            String sql = "SELECT * FROM booking WHERE carid = ? AND " +
                        "((startdate <= ? AND enddate >= ?) OR " +
                        "(startdate <= ? AND enddate >= ?) OR " +
                        "(startdate >= ? AND enddate <= ?))";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, carId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, startDate.toString());
            pstmt.setString(4, endDate.toString());
            pstmt.setString(5, endDate.toString());
            pstmt.setString(6, startDate.toString());
            pstmt.setString(7, endDate.toString());
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking date conflicts: " + e.getMessage());
            return true; // Assume conflict on error for safety
        }
    }

    public boolean bookCar(int carId, LocalDate startDate, LocalDate endDate, int custId) {
        try {
            // Check if car exists and is available
            if (!isCarAvailable(carId)) {
                System.out.println("Car is not available for booking!");
                return false;
            }

            // Check for date conflicts
            if (hasDateConflict(carId, startDate, endDate)) {
                System.out.println("Car already booked for selected dates. Please choose another car or different dates.");
                return false;
            }

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            
            String insert = "INSERT INTO booking (carid, startdate, duration, enddate, customerid) " +
                    "VALUES (" + carId + ", '" + startDate + "', " + days + ", '" + endDate + "', " + custId + ")";
            stmt.execute(insert);

            String update = "UPDATE cars SET rentalStatus = 'N' WHERE idcars = " + carId;
            stmt.execute(update);

            // Display booking confirmation
            displayBookingConfirmation(carId, startDate, endDate, days);
            return true;
        } catch (SQLException e) {
            System.out.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    private void displayBookingConfirmation(int carId, LocalDate startDate, LocalDate endDate, long days) {
        try (ResultSet rs = stmt.executeQuery("SELECT brand, model FROM cars WHERE idcars = " + carId)) {
            if (rs.next()) {
                double totalCost = days * DAILY_RATE;
                System.out.println("\n🎉 BOOKING CONFIRMED! 🎉");
                System.out.println("╔═══════════════════════════════════════╗");
                System.out.println("║           BOOKING SUMMARY             ║");
                System.out.println("╠═══════════════════════════════════════╣");
                System.out.printf("║ Car: %-12s %-12s    ║%n", rs.getString("brand"), rs.getString("model"));
                System.out.printf("║ Rental Period: %s to %s ║%n", startDate, endDate);
                System.out.printf("║ Duration: %-3d days                  ║%n", days);
                System.out.printf("║ Total Cost: $%-8.2f              ║%n", totalCost);
                System.out.println("╚═══════════════════════════════════════╝");
                System.out.println("Thank you for choosing our rental service!");
            }
        } catch (SQLException e) {
            System.out.println("Car booked successfully!");
        }
    }

    public void returnCar(int carId) {
        try {
            // Check if car is actually rented
            if (isCarAvailable(carId)) {
                System.out.println("This car is not currently rented!");
                return;
            }

            String sql = "UPDATE cars SET rentalStatus = 'Y' WHERE idcars = " + carId;
            stmt.execute(sql);
            System.out.println("Car returned successfully! Thank you for using our service.");
        } catch (SQLException e) {
            System.out.println("Error returning car: " + e.getMessage());
        }
    }

    public void viewAllBookings() {
        try (ResultSet rs = stmt.executeQuery(
            "SELECT b.carid, c.brand, c.model, b.startdate, b.enddate, cu.name " +
            "FROM booking b " +
            "JOIN cars c ON b.carid = c.idcars " +
            "JOIN customers cu ON b.customerid = cu.custId " +
            "WHERE b.enddate >= CURDATE() " +
            "ORDER BY b.startdate")) {
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                              CURRENT BOOKINGS                             ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
            System.out.printf("║ %-6s │ %-10s │ %-10s │ %-12s │ %-12s │ %-15s ║%n", 
                "Car ID", "Brand", "Model", "Start Date", "End Date", "Customer");
            System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("║ %-6d │ %-10s │ %-10s │ %-12s │ %-12s │ %-15s ║%n",
                    rs.getInt("carid"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("startdate"),
                    rs.getString("enddate"),
                    rs.getString("name"));
            }
            
            if (!hasData) {
                System.out.println("║                              No active bookings                           ║");
            }
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
        } catch (SQLException e) {
            System.out.println("Error viewing bookings: " + e.getMessage());
        }
    }

    public String getCarDetails(int carId) {
        try (ResultSet rs = stmt.executeQuery("SELECT brand, model FROM cars WHERE idcars = " + carId)) {
            if (rs.next()) {
                return rs.getString("brand") + " " + rs.getString("model");
            }
        } catch (SQLException e) {
            System.out.println("Error getting car details: " + e.getMessage());
        }
        return "Unknown Car";
    }

    public void close() {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}