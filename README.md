# ATM_Management_System
The ATM Management System is a console-based Java application that simulates real-world ATM functionalities using a MySQL database. It allows users to securely log in using their account number and PIN, and then perform typical banking operations like checking balance, depositing money, withdrawing cash, transferring funds to another account, and viewing a mini statement.

To develop a secure and user-friendly banking system that:

  -Provides essential banking services from an ATM interface

  -Ensures data security using PIN verification

  -Handles transactions with consistency using SQL and JDBC


🧾 Functional Requirements:
    
    Login: Users must provide a valid account number and PIN.

    Check Balance: Shows current account balance.

    Deposit: Allows users to add money to their account.

    Withdraw: Allows users to withdraw money after balance check.

    Transfer: Enables fund transfer between two accounts with rollback support.

    Mini Statement: Displays user's account number, name, and balance.


| Module                | Description                              |
| --------------------- | ---------------------------------------- |
| `ATMSystem.java`      | Main class with input loop and menu      |
| `AccountService.java` | All banking operations as static methods |
| `DBConnection.java`   | Reusable database connection method      |


✅ Database Setup

CREATE DATABASE IF NOT EXISTS atm;
USE atm;

CREATE TABLE IF NOT EXISTS accounts (
    account_no INT PRIMARY KEY,
    pin INT NOT NULL,
    name VARCHAR(100),
    balance DOUBLE DEFAULT 0.0
);

-- Insert demo accounts
INSERT INTO accounts VALUES
(1001, 1234, 'Shubham', 10000),
(1002, 5678, 'Ravi', 5000),
(1003, 1111, 'Neha', 8000);
