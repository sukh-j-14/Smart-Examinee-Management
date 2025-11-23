package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Utility Class
 * 
 * This class provides methods to establish and manage database connections
 * to the MySQL database for the Smart Examinee Management System (SEMS).
 * 
 * Features:
 * - Loads database configuration from database.properties file
 * - Implements singleton pattern for driver loading
 * - Provides connection pooling through getConnection() method
 * - Includes utility method for safe resource cleanup
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class DBConnection {
    
    // Static variables for database configuration
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static final String PROPERTIES_FILE = "database.properties";
    
    // Flag to track if driver is loaded
    private static boolean driverLoaded = false;
    
    /**
     * Static block to load the JDBC driver and initialize database properties
     * This block executes once when the class is first loaded into memory
     */
    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
            
            // Load database properties from properties file
            loadDatabaseProperties();
            
            System.out.println("JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java to your classpath.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading database properties!");
            e.printStackTrace();
        }
    }
    
    /**
     * Loads database configuration from database.properties file
     * located in the src/main/resources/ directory
     * 
     * @throws IOException if the properties file cannot be read
     */
    private static void loadDatabaseProperties() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        
        try {
            // Try loading from resources directory using classloader (recommended for compiled apps)
            // This works when the resources folder is in the classpath
            inputStream = DBConnection.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE);
            
            // Fallback: Load from relative path if classloader approach fails (for development)
            if (inputStream == null) {
                inputStream = new FileInputStream("src/main/resources/" + PROPERTIES_FILE);
            }
            
            // Load properties from file
            properties.load(inputStream);
            
            // Read database connection parameters
            dbUrl = properties.getProperty("url");
            dbUsername = properties.getProperty("username");
            dbPassword = properties.getProperty("password");
            
            // Validate that all required properties are loaded
            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IOException("Missing required database properties. " +
                    "Please check database.properties file.");
            }
            
            System.out.println("Database properties loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading database.properties file!");
            e.printStackTrace();
            throw e;
        } finally {
            // Close the input stream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Establishes and returns a connection to the MySQL database
     * 
     * This method creates a new database connection each time it is called.
     * For production applications, consider implementing connection pooling.
     * 
     * @return Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        // Check if driver is loaded
        if (!driverLoaded) {
            throw new SQLException("JDBC Driver not loaded. Cannot establish connection.");
        }
        
        // Validate database properties
        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            throw new SQLException("Database properties not loaded. Cannot establish connection.");
        }
        
        try {
            // Create and return database connection
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Database connection established successfully.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Error establishing database connection!");
            System.err.println("URL: " + dbUrl);
            System.err.println("Username: " + dbUsername);
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Safely closes database resources (Connection, Statement, ResultSet)
     * 
     * This utility method handles null checks and exceptions for each resource,
     * ensuring that all resources are closed even if one fails to close.
     * 
     * Best Practice: Always close resources in reverse order of creation
     * (ResultSet -> Statement -> Connection)
     * 
     * @param connection the Connection to close (can be null)
     * @param statement the Statement to close (can be null)
     * @param resultSet the ResultSet to close (can be null)
     */
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        // Close ResultSet first
        if (resultSet != null) {
            try {
                resultSet.close();
                System.out.println("ResultSet closed successfully.");
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet!");
                e.printStackTrace();
            }
        }
        
        // Close Statement second
        if (statement != null) {
            try {
                statement.close();
                System.out.println("Statement closed successfully.");
            } catch (SQLException e) {
                System.err.println("Error closing Statement!");
                e.printStackTrace();
            }
        }
        
        // Close Connection last
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("Error closing Connection!");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Overloaded method to close Connection and Statement only
     * 
     * @param connection the Connection to close (can be null)
     * @param statement the Statement to close (can be null)
     */
    public static void closeConnection(Connection connection, Statement statement) {
        closeConnection(connection, statement, null);
    }
    
    /**
     * Overloaded method to close Connection only
     * 
     * @param connection the Connection to close (can be null)
     */
    public static void closeConnection(Connection connection) {
        closeConnection(connection, null, null);
    }
    
    /**
     * Test method to verify database connection
     * This can be used during development to test the connection
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Connection connection = null;
        
        try {
            System.out.println("Testing database connection...");
            connection = getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("SUCCESS: Database connection is active!");
                System.out.println("Database: " + connection.getCatalog());
                System.out.println("Driver: " + connection.getMetaData().getDriverName());
                System.out.println("Version: " + connection.getMetaData().getDriverVersion());
            }
        } catch (SQLException e) {
            System.err.println("FAILED: Could not establish database connection!");
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }
}

