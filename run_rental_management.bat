@echo off
cd /d "C:\Users\cpkri\java\mysql_rental_java - Copy"

REM -----------------------------------
REM STEP 1: Run SQL script in MySQL
REM -----------------------------------
echo Running SQL script to set up database...

mysql -u root -pYour_Password < rental_management_system_creation_db.sql

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to execute SQL script.
    pause
    exit /b
) ELSE (
    echo Database created or updated successfully.
)

REM -----------------------------------
REM STEP 2: Compile Java files
REM -----------------------------------
echo Compiling Java files...

javac -cp ".;mysql-connector-j-9.1.0.jar" -d . DatabaseManager.java CarRentalApp.java Owner.java model\Car.java model\Customer.java

IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b
) ELSE (
    echo Compilation successful.
)

REM -----------------------------------
REM STEP 3: Run Java application
REM -----------------------------------
echo Launching Rental Management System...

java -cp ".;mysql-connector-j-9.1.0.jar" CarRentalApp

pause
