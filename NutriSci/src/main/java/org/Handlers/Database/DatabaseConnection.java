package org.Handlers.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Controls database connectivity for the application.
 */
class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/nutrisci";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    /**
     * Method used to connect to the database.
     *
     * @return connection
     * @throws SQLException if database connection fails
     */
    static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("Make sure docker container is running: docker-compose up -d");
            throw e;
        }
    }

    /**
     * A tester method for the database's connection
     *
     * @return a valid check for the connection
     */
    static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
