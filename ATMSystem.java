import java.sql.*;
import java.util.Scanner;

public class ATMSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/atm";
    private static final String USER = "root";
    private static final String PASSWORD = "shubham@9356";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("Welcome to ATM System!");

                System.out.print("Enter Account Number: ");
                int accNo = sc.nextInt();
                System.out.print("Enter PIN: ");
                int pin = sc.nextInt();

                if (!validateLogin(conn, accNo, pin)) {
                    System.out.println("Invalid credentials. Exiting...");
                    return;
                }

                while (true) {
                    System.out.println("\n1. Check Balance\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Mini Statement\n6. Exit");
                    System.out.print("Choose an option: ");
                    int choice = sc.nextInt();

                    switch (choice) {
                        case 1 -> checkBalance(conn, accNo);
                        case 2 -> {
                            System.out.print("Enter deposit amount: ");
                            double amount = sc.nextDouble();
                            deposit(conn, accNo, amount);
                        }
                        case 3 -> {
                            System.out.print("Enter withdrawal amount: ");
                            double amount = sc.nextDouble();
                            withdraw(conn, accNo, amount);
                        }
                        case 4 -> {
                            System.out.print("Enter receiver's account number: ");
                            int toAcc = sc.nextInt();
                            System.out.print("Enter transfer amount: ");
                            double amount = sc.nextDouble();
                            transfer(conn, accNo, toAcc, amount);
                        }
                        case 5 -> miniStatement(conn, accNo);
                        case 6 -> {
                            System.out.println("Thank you for using ATM!");
                            return;
                        }
                        default -> System.out.println("Invalid option.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean validateLogin(Connection conn, int accNo, int pin) throws SQLException {
        String query = "SELECT * FROM accounts WHERE account_no=? AND pin=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accNo);
            ps.setInt(2, pin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void checkBalance(Connection conn, int accNo) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE account_no=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Current Balance: ₹" + rs.getDouble("balance"));
                }
            }
        }
    }

    private static void deposit(Connection conn, int accNo, double amount) throws SQLException {
        String query = "UPDATE accounts SET balance = balance + ? WHERE account_no=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accNo);
            ps.executeUpdate();
            System.out.println("₹" + amount + " deposited successfully.");
        }
    }

    private static void withdraw(Connection conn, int accNo, double amount) throws SQLException {
        if (!isSufficient(conn, accNo, amount)) {
            System.out.println("Insufficient balance!");
            return;
        }

        String query = "UPDATE accounts SET balance = balance - ? WHERE account_no=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accNo);
            ps.executeUpdate();
            System.out.println("₹" + amount + " withdrawn successfully.");
        }
    }

    private static void transfer(Connection conn, int fromAcc, int toAcc, double amount) throws SQLException {
        if (!isSufficient(conn, fromAcc, amount)) {
            System.out.println("Insufficient balance for transfer!");
            return;
        }

        conn.setAutoCommit(false);
        try (
                PreparedStatement debit = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_no=?");
                PreparedStatement credit = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_no=?")
        ) {
            debit.setDouble(1, amount);
            debit.setInt(2, fromAcc);
            debit.executeUpdate();

            credit.setDouble(1, amount);
            credit.setInt(2, toAcc);
            credit.executeUpdate();

            conn.commit();
            System.out.println("₹" + amount + " transferred successfully.");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Transfer failed: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static boolean isSufficient(Connection conn, int accNo, double amount) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE account_no=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getDouble("balance") >= amount;
            }
        }
    }

    private static void miniStatement(Connection conn, int accNo) throws SQLException {
        String query = "SELECT * FROM accounts WHERE account_no=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Mini Statement ---");
                    System.out.println("Account No: " + rs.getInt("account_no"));
                    System.out.println("Name      : " + rs.getString("name"));
                    System.out.println("Balance   : ₹" + rs.getDouble("balance"));
                    System.out.println("----------------------");
                }
            }
        }
    }
}
