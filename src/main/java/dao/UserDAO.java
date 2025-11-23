package dao;

import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * User Data Access Object (DAO) Class
 * 
 * Handles all database operations related to User entities in the
 * Smart Examinee Management System (SEMS).
 * 
 * Responsibilities:
 * - User authentication (login)
 * - User data retrieval
 * - User account management
 * 
 * All database operations use PreparedStatement to prevent SQL injection.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class UserDAO {
    
    // SQL Queries
    private static final String AUTHENTICATE_USER_QUERY = 
        "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
    
    private static final String UPDATE_LAST_LOGIN_QUERY = 
        "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
    
    private static final String GET_USER_BY_ID_QUERY = 
        "SELECT * FROM users WHERE user_id = ?";
    
    private static final String GET_USER_BY_USERNAME_QUERY = 
        "SELECT * FROM users WHERE username = ?";
    
    /**
     * Authenticates a user by checking username and password.
     * 
     * This method verifies that:
     * - The username exists in the database
     * - The password matches
     * - The user account is active
     * 
     * Note: Currently uses plain-text password comparison.
     * For production, consider implementing password hashing (e.g., BCrypt).
     * 
     * @param username the username to authenticate
     * @param password the password to verify
     * @return User object if credentials are valid and account is active,
     *         null if invalid, inactive, or error occurs
     */
    public User authenticateUser(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(AUTHENTICATE_USER_QUERY);
            
            // Set parameters (prevent SQL injection)
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if user was found
            if (resultSet.next()) {
                // Create and populate User object from ResultSet
                User user = mapResultSetToUser(resultSet);
                
                // Update last login timestamp
                updateLastLogin(user.getUserId());
                
                System.out.println("User authenticated successfully: " + username);
                return user;
            } else {
                System.out.println("Authentication failed: Invalid credentials or inactive account");
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + username);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Updates the last login timestamp for a user.
     * 
     * This method sets the last_login field to the current timestamp
     * when a user successfully logs in.
     * 
     * @param userId the ID of the user whose last login should be updated
     * @return true if the update was successful, false otherwise
     */
    public boolean updateLastLogin(int userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(UPDATE_LAST_LOGIN_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, userId);
            
            // Execute update
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Last login updated successfully for user ID: " + userId);
                return true;
            } else {
                System.out.println("No rows updated for user ID: " + userId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating last login for user ID: " + userId);
            e.printStackTrace();
            return false;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Retrieves a user by their user ID.
     * 
     * @param userId the ID of the user to retrieve
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(GET_USER_BY_ID_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, userId);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if user was found
            if (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                System.out.println("User retrieved successfully: " + user.getUsername());
                return user;
            } else {
                System.out.println("User not found with ID: " + userId);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + userId);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Retrieves a user by their username.
     * 
     * @param username the username of the user to retrieve
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME_QUERY);
            
            // Set parameter
            preparedStatement.setString(1, username);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if user was found
            if (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                System.out.println("User retrieved successfully: " + user.getUsername());
                return user;
            } else {
                System.out.println("User not found with username: " + username);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + username);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Maps a ResultSet row to a User object.
     * 
     * This helper method extracts data from a ResultSet and creates
     * a populated User object.
     * 
     * @param resultSet the ResultSet containing user data
     * @return a User object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        
        // Map all fields from ResultSet to User object
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(resultSet.getString("role"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        
        // Handle timestamp fields (may be null)
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        user.setCreatedAt(createdAt);
        
        Timestamp lastLogin = resultSet.getTimestamp("last_login");
        user.setLastLogin(lastLogin);
        
        // Handle boolean field
        user.setActive(resultSet.getBoolean("is_active"));
        
        return user;
    }
    
    /**
     * Test method to verify user authentication.
     * 
     * This method can be used during development to test the login functionality.
     * Run this class directly to test authentication.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("UserDAO Authentication Test");
        System.out.println("=========================================");
        System.out.println();
        
        UserDAO userDAO = new UserDAO();
        
        // Test Case 1: Valid admin credentials
        System.out.println("Test Case 1: Valid admin credentials");
        System.out.println("Username: admin, Password: admin123");
        User adminUser = userDAO.authenticateUser("admin", "admin123");
        
        if (adminUser != null) {
            System.out.println("✓ Authentication SUCCESSFUL!");
            System.out.println("User Details:");
            System.out.println("  - User ID: " + adminUser.getUserId());
            System.out.println("  - Username: " + adminUser.getUsername());
            System.out.println("  - Role: " + adminUser.getRole());
            System.out.println("  - Full Name: " + adminUser.getFullName());
            System.out.println("  - Email: " + adminUser.getEmail());
            System.out.println("  - Active: " + adminUser.isActive());
            System.out.println();
        } else {
            System.out.println("✗ Authentication FAILED!");
            System.out.println();
        }
        
        // Test Case 2: Invalid credentials
        System.out.println("Test Case 2: Invalid credentials");
        System.out.println("Username: admin, Password: wrongpassword");
        User invalidUser = userDAO.authenticateUser("admin", "wrongpassword");
        
        if (invalidUser == null) {
            System.out.println("✓ Correctly rejected invalid credentials");
            System.out.println();
        } else {
            System.out.println("✗ ERROR: Should have rejected invalid credentials!");
            System.out.println();
        }
        
        // Test Case 3: Non-existent username
        System.out.println("Test Case 3: Non-existent username");
        System.out.println("Username: nonexistent, Password: anypassword");
        User nonExistentUser = userDAO.authenticateUser("nonexistent", "anypassword");
        
        if (nonExistentUser == null) {
            System.out.println("✓ Correctly rejected non-existent username");
            System.out.println();
        } else {
            System.out.println("✗ ERROR: Should have rejected non-existent username!");
            System.out.println();
        }
        
        // Test Case 4: Valid staff credentials (if exists)
        System.out.println("Test Case 4: Valid staff credentials");
        System.out.println("Username: staff1, Password: staff123");
        User staffUser = userDAO.authenticateUser("staff1", "staff123");
        
        if (staffUser != null) {
            System.out.println("✓ Authentication SUCCESSFUL!");
            System.out.println("User Details:");
            System.out.println("  - User ID: " + staffUser.getUserId());
            System.out.println("  - Username: " + staffUser.getUsername());
            System.out.println("  - Role: " + staffUser.getRole());
            System.out.println("  - Full Name: " + staffUser.getFullName());
            System.out.println();
        } else {
            System.out.println("✗ Authentication FAILED (staff user may not exist in database)");
            System.out.println();
        }
        
        // Test Case 5: Retrieve user by ID
        System.out.println("Test Case 5: Retrieve user by ID");
        User retrievedUser = userDAO.getUserById(1);
        
        if (retrievedUser != null) {
            System.out.println("✓ User retrieved successfully!");
            System.out.println("Retrieved User: " + retrievedUser.toString());
            System.out.println();
        } else {
            System.out.println("✗ Failed to retrieve user by ID");
            System.out.println();
        }
        
        System.out.println("=========================================");
        System.out.println("Test Completed");
        System.out.println("=========================================");
    }
}

