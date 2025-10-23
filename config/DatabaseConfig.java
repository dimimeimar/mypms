package org.pms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Κλάση για τη διαχείριση της σύνδεσης με τη βάση δεδομένων
 */
public class DatabaseConfig {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mypms";
    private static final String DB_USER = "root"; // Αλλάξτε με τα δικά σας στοιχεία
    private static final String DB_PASSWORD = "$$jimpms2025$$"; // Αλλάξτε με τα δικά σας στοιχεία
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static DatabaseConfig instance;
    private Connection connection;

    private DatabaseConfig() {
        try {
            Class.forName(DB_DRIVER);
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Σφάλμα σύνδεσης με τη βάση δεδομένων: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα επανασύνδεσης: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κλεισίματος σύνδεσης: " + e.getMessage());
        }
    }
}