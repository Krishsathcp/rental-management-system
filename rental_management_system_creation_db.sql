-- Step 1: Create the database
CREATE DATABASE if not exists rental_management_system;

-- Step 2: Use the database
USE rental_management_system;

-- Step 3: Create the 'customers' table
CREATE TABLE if not exists customers (
    custId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45),
    city VARCHAR(45)
);

-- Step 4: Create the 'cars' table
CREATE TABLE if not exists cars (
    idcars INT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(45),
    model VARCHAR(45),
    year YEAR,
    rentalStatus CHAR(1)
);

-- Step 5: Create the 'booking' table
CREATE TABLE if not exists booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    carid INT,
    startdate DATE,
    duration INT,
    enddate DATE,
    customerid INT,
    FOREIGN KEY (carid) REFERENCES cars(idcars),
    FOREIGN KEY (customerid) REFERENCES customers(custId)
);
