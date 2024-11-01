package com.infoman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private String url = "jdbc:mysql://localhost:3306/infoman_db"; // Update with your DB URL
    private String user = "app"; // Update with your DB username
    private String password = "1234"; // Update with your DB password
    private Connection connection;

    public DatabaseConnection() {
        try {
            // Establish the connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage(), e);
        }
    }

    // Method to return the connection object
    public Connection getConnection() {
        return connection;
    }

    // Optional: Method to close the connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }
}
