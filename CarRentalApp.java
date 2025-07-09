import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;


public class CarRentalApp {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static boolean checkPasscode(Scanner sc) {
        System.out.print("Enter Admin passcode: ");
        String pass = sc.nextLine();
        return pass.equals("qaz");
    }

    public static LocalDate getValidDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String dateStr = sc.nextLine().trim();
            
            if (dateStr.isEmpty()) {
                System.out.println("Date cannot be empty. Please try again.");
                continue;
            }
            
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Date cannot be in the past. Please enter today's date or a future date.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format (e.g., 2024-12-25).");
            }
        }
    }

    public static boolean validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            System.out.println("End date must be after start date. Please try again.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        System.out.println("Welcome to Elite Car Rental System! 🚗");
        System.out.println("═══════════════════════════════════════════");

        while (true) {
            System.out.println("\nMAIN MENU");
            System.out.println("┌─────────────────────────────┐");
            System.out.println("│ 0 - Admin Login             │");
            System.out.println("│ 1 - Customer Login          │");
            System.out.println("│ 2 - Exit System             │");
            System.out.println("└─────────────────────────────┘");
            System.out.print("Please enter your choice (0-2): ");
            
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (0-2).");
                continue;
            }

            switch (choice) {
                case 0:
                    if (checkPasscode(sc)) {
                        System.out.println("Admin login successful!");
                        adminMenu(sc, dbManager);
                    } else {
                        System.out.println("Wrong admin passcode. Access denied!");
                    }
                    break;
                case 1:
                    System.out.print("Enter your Customer ID: ");
                    try {
                        int custId = Integer.parseInt(sc.nextLine().trim());
                        if (dbManager.isValidCustomer(custId)) {
                            System.out.println("Customer login successful!");
                            userMenu(sc, dbManager, custId);
                        } else {
                            System.out.println("Invalid Customer ID. Please check and try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Customer ID format. Please enter a valid number.");
                    }
                    break;
                case 2:
                    System.out.println("Thank you for using Elite Car Rental System!");
                    System.out.println("Drive safely and see you again soon! 🚗");
                    dbManager.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please select 0, 1, or 2.");
            }
        }
    }

    public static void adminMenu(Scanner sc, DatabaseManager db) {
        while (true) {
            System.out.println("\nADMIN DASHBOARD");
            System.out.println("┌─────────────────────────────┐");
            System.out.println("│ 1 - Add New Car            │");
            System.out.println("│ 2 - Delete Car             │");
            System.out.println("│ 3 - Add New Customer       │");
            System.out.println("│ 4 - View All Cars          │");
            System.out.println("│ 5 - View All Bookings      │");
            System.out.println("│ 6 - View All Customers     │");
            System.out.println("│ 7 - Logout                 │");
            System.out.println("└─────────────────────────────┘");
            System.out.print("Select an option (1-6): ");
            
            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1-6).");
                continue;
            }

            switch (ch) {
                case 1:
                    System.out.println("\nAdding New Car");
                    System.out.print("Enter car brand: ");
                    String brand = sc.nextLine().trim();
                    if (brand.isEmpty()) {
                        System.out.println("Brand cannot be empty!");
                        break;
                    }
                    
                    System.out.print("Enter car model: ");
                    String model = sc.nextLine().trim();
                    if (model.isEmpty()) {
                        System.out.println("Model cannot be empty!");
                        break;
                    }
                    
                    System.out.print("Enter manufacturing year: ");
                    try {
                        int year = Integer.parseInt(sc.nextLine().trim());
                        if (year < 1900 || year > LocalDate.now().getYear() + 1) {
                            System.out.println("Invalid year! Please enter a valid year.");
                            break;
                        }
                        db.insertCar(brand, model, year);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid year format!");
                    }
                    break;
                    
                case 2:
                    System.out.println("\nDeleting Car");
                    db.showAllCars();
                    System.out.print("Enter Car ID to delete: ");
                    try {
                        int carId = Integer.parseInt(sc.nextLine().trim());
                        db.deleteCar(carId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Car ID format!");
                    }
                    break;
                    
                case 3:
                    System.out.println("\n👤 Adding New Customer");
                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Name cannot be empty!");
                        break;
                    }
                    
                    System.out.print("Enter customer city: ");
                    String city = sc.nextLine().trim();
                    if (city.isEmpty()) {
                        System.out.println("City cannot be empty!");
                        break;
                    }
                    
                    db.addCustomer(name, city);
                    break;
                    
                case 4:
                    db.showAllCars();
                    break;
                    
                case 5:
                    db.viewAllBookings();
                    break;

                case 6:
                    db.viewAllCustomers();
                    break;   
                case 7:
                    System.out.println("Admin logging out...");
                    return;
                    
                default:
                    System.out.println("Invalid choice. Please select a number from 1-6.");
            }
        }
    }

    public static void userMenu(Scanner sc, DatabaseManager db, int custId) {
        while (true) {
            System.out.println("\n👤 CUSTOMER DASHBOARD");
            System.out.println("┌─────────────────────────────┐");
            System.out.println("│ 1 - Book a Car             │");
            System.out.println("│ 2 - Return a Car           │");
            System.out.println("│ 3 - View Available Cars    │");
            System.out.println("│ 4 - View Rented Cars       │");
            System.out.println("│ 5 - Logout                 │");
            System.out.println("└─────────────────────────────┘");
            System.out.print("Select an option (1-4): ");
            
            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1-4).");
                continue;
            }

            switch (ch) {
                case 1:
                    System.out.println("\nCar Booking Service");
                    db.showAvailableCars();
                    
                    System.out.print("Enter Car ID you want to rent: ");
                    try {
                        int carId = Integer.parseInt(sc.nextLine().trim());
                        
                        if (!db.isCarAvailable(carId)) {
                            System.out.println("Selected car is not available. Please choose another car.");
                            break;
                        }
                        
                        System.out.println("Please enter your rental dates:");
                        LocalDate startDate = getValidDate(sc, "Enter start date");
                        LocalDate endDate = getValidDate(sc, "Enter end date");
                        
                        if (!validateDateRange(startDate, endDate)) {
                            break;
                        }
                        
                        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                        double totalCost = days * 50.0; // Assuming $50 per day
                        
                        System.out.printf("\nRental Summary:\n");
                        System.out.printf("Car: %s\n", db.getCarDetails(carId));
                        System.out.printf("Duration: %d days\n", days);
                        System.out.printf("Total Cost: $%.2f\n", totalCost);
                        
                        System.out.print("Confirm booking? (y/n): ");
                        String confirm = sc.nextLine().trim().toLowerCase();
                        
                        if (confirm.equals("y") || confirm.equals("yes")) {
                            if (db.bookCar(carId, startDate, endDate, custId)) {
                                System.out.println("Booking process completed successfully!");
                            } else {
                                System.out.println("Booking failed. Please try again later.");
                            }
                        } else {
                            System.out.println("Booking cancelled.");
                        }
                        
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Car ID format!");
                    }
                    break;
                    
                case 2:
                    System.out.println("\nCar Return Service");
                    System.out.print("Enter Car ID to return: ");
                    try {
                        int returnId = Integer.parseInt(sc.nextLine().trim());
                        db.returnCar(returnId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Car ID format!");
                    }
                    break;
                    
                case 3:
                    db.showAvailableCars();
                    break;
                   
                case 4:
                    db.showAllCarsBooked(custId);
                    break;

                case 5:
                    System.out.println("Customer logging out...");
                    return;
                    
                default:
                    System.out.println("Invalid choice. Please select a number from 1-4.");
            }
        }
    }
}