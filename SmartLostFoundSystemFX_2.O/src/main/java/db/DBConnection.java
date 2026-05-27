package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection handles MySQL database connections.
 * Uses JDBC DriverManager to connect to the database.
 */
public class DBConnection {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lostfound";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Kanishk@123";
    
    private static Connection connection;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
    }
    
    /**
     * Get a database connection instance.
     * Uses singleton pattern - returns the same connection.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test the database connection.
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}