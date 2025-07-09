Rental Management System 🚗

A **Java-based Rental Management System** with a MySQL backend and JDBC integration.

---

📋 Requirements

- ✅ **Java 8 or later** (Recommended: JDK 17)
- ✅ **MySQL Server** running locally (default host: `localhost`, port: `3306`)
- ✅ `mysql-connector-j-9.1.0.jar` (included in project)
- ✅ MySQL root user with privileges to create tables

---

📁 Project Structure

- `CarRentalApp.java` – Main application entry point.
- `DatabaseManager.java` – Handles all database operations.
- `Owner.java` – Admin logic and menu handling.
- `model/` – Contains model classes:
  - `Car.java`
  - `Customer.java`
- `rental_management_system_creation_db.sql` – SQL script to create required tables and schema.
- `run_rental_management.bat` – Batch file to initialize and run the app.
- `mysql-connector-j-9.1.0.jar` – MySQL JDBC driver used for database connectivity.

---
🛠️ Tech Stack

- **Java** – Core application logic.
- **MySQL** – Database for persistent storage.
- **JDBC** – Java Database Connectivity for MySQL interaction.
- **Batch Script** – Automates database setup, compilation, and execution.

---

⚙️ How to Run

⚠️ **Replace `Your_Password` in the `.bat` file** with your actual MySQL root password.And then Double Click on `.bat` file.

### Step-by-step (via `run_rental_management.bat`):

```bat
@echo off
cd /d "C:\Users\cpkri\java\mysql_rental_java - Copy"

REM STEP 1: Run SQL script in MySQL
echo Running SQL script to set up database...
mysql -u root -pYour_Password < rental_management_system_creation_db.sql

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to execute SQL script.
    pause
    exit /b
) ELSE (
    echo Database created or updated successfully.
)

REM STEP 2: Compile Java files
echo Compiling Java files...
javac -cp ".;mysql-connector-j-9.1.0.jar" -d . DatabaseManager.java CarRentalApp.java Owner.java model\Car.java model\Customer.java

IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b
) ELSE (
    echo Compilation successful.
)

REM STEP 3: Run Java application
echo Launching Rental Management System...
java -cp ".;mysql-connector-j-9.1.0.jar" CarRentalApp

pause
```

👨‍💻 Author
Krishsath CP

📬 Contact Me

- 🔗 [LinkedIn](https://www.linkedin.com/in/krishsath-cp-59754532a/)
- 💻 [GitHub](https://github.com/Krishsathcp)
- 📧 Email: cpkrishsath@gmail.com
