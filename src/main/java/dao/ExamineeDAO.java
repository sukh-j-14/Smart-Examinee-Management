package dao;

import model.Examinee;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Examinee Data Access Object (DAO) Class
 * 
 * Handles all database operations related to Examinee entities in the
 * Smart Examinee Management System (SEMS).
 * 
 * Responsibilities:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Search functionality
 * - Data validation and exception handling
 * 
 * All database operations use PreparedStatement to prevent SQL injection.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ExamineeDAO {
    
    // SQL Queries
    private static final String GET_ALL_EXAMINEES_QUERY = 
        "SELECT * FROM examinees ORDER BY registration_number ASC";
    
    private static final String GET_EXAMINEE_BY_ID_QUERY = 
        "SELECT * FROM examinees WHERE examinee_id = ?";
    
    private static final String GET_EXAMINEE_BY_REG_NUMBER_QUERY = 
        "SELECT * FROM examinees WHERE registration_number = ?";
    
    private static final String SEARCH_EXAMINEES_QUERY = 
        "SELECT * FROM examinees WHERE " +
        "LOWER(first_name) LIKE ? OR " +
        "LOWER(last_name) LIKE ? OR " +
        "LOWER(email) LIKE ? OR " +
        "LOWER(registration_number) LIKE ? " +
        "ORDER BY registration_number ASC";
    
    private static final String INSERT_EXAMINEE_QUERY = 
        "INSERT INTO examinees (registration_number, first_name, last_name, email, " +
        "phone, date_of_birth, address, city, state, pincode) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_EXAMINEE_QUERY = 
        "UPDATE examinees SET registration_number = ?, first_name = ?, last_name = ?, " +
        "email = ?, phone = ?, date_of_birth = ?, address = ?, city = ?, " +
        "state = ?, pincode = ? WHERE examinee_id = ?";
    
    private static final String DELETE_EXAMINEE_QUERY = 
        "DELETE FROM examinees WHERE examinee_id = ?";
    
    /**
     * Retrieves all examinees from the database, ordered by registration number.
     * 
     * @return List of Examinee objects, empty list if no examinees found
     */
    public List<Examinee> getAllExaminees() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Examinee> examinees = new ArrayList<>();
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement
            preparedStatement = connection.prepareStatement(GET_ALL_EXAMINEES_QUERY);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Process results
            while (resultSet.next()) {
                Examinee examinee = mapResultSetToExaminee(resultSet);
                examinees.add(examinee);
            }
            
            System.out.println("Retrieved " + examinees.size() + " examinee(s) from database.");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all examinees.");
            e.printStackTrace();
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return examinees;
    }
    
    /**
     * Retrieves an examinee by their ID.
     * 
     * @param examineeId the ID of the examinee to retrieve
     * @return Examinee object if found, null otherwise
     */
    public Examinee getExamineeById(int examineeId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(GET_EXAMINEE_BY_ID_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, examineeId);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if examinee was found
            if (resultSet.next()) {
                Examinee examinee = mapResultSetToExaminee(resultSet);
                System.out.println("Examinee retrieved successfully: " + examinee.getRegistrationNumber());
                return examinee;
            } else {
                System.out.println("Examinee not found with ID: " + examineeId);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving examinee by ID: " + examineeId);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Retrieves an examinee by their registration number.
     * 
     * @param regNumber the registration number of the examinee to retrieve
     * @return Examinee object if found, null otherwise
     */
    public Examinee getExamineeByRegistrationNumber(String regNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(GET_EXAMINEE_BY_REG_NUMBER_QUERY);
            
            // Set parameter
            preparedStatement.setString(1, regNumber);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if examinee was found
            if (resultSet.next()) {
                Examinee examinee = mapResultSetToExaminee(resultSet);
                System.out.println("Examinee retrieved successfully: " + examinee.getRegistrationNumber());
                return examinee;
            } else {
                System.out.println("Examinee not found with registration number: " + regNumber);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving examinee by registration number: " + regNumber);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Searches for examinees by keyword.
     * 
     * Searches in first_name, last_name, email, and registration_number fields
     * (case-insensitive). Uses LIKE pattern matching.
     * 
     * @param keyword the search keyword
     * @return List of matching Examinee objects, empty list if no matches found
     */
    public List<Examinee> searchExaminees(String keyword) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Examinee> examinees = new ArrayList<>();
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(SEARCH_EXAMINEES_QUERY);
            
            // Prepare search pattern (case-insensitive, wildcard matching)
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            
            // Set parameters (same pattern for all LIKE clauses)
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);
            preparedStatement.setString(3, searchPattern);
            preparedStatement.setString(4, searchPattern);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Process results
            while (resultSet.next()) {
                Examinee examinee = mapResultSetToExaminee(resultSet);
                examinees.add(examinee);
            }
            
            System.out.println("Search found " + examinees.size() + " examinee(s) matching: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("Error searching examinees with keyword: " + keyword);
            e.printStackTrace();
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return examinees;
    }
    
    /**
     * Adds a new examinee to the database.
     * 
     * Note: The registration_number must be provided and must be unique.
     * The examinee_id will be auto-generated by the database.
     * 
     * @param examinee the Examinee object containing the data to insert
     * @return true if the examinee was added successfully, false otherwise
     */
    public boolean addExaminee(Examinee examinee) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examinee == null) {
                System.err.println("Error: Cannot add null examinee.");
                return false;
            }
            
            if (examinee.getRegistrationNumber() == null || examinee.getRegistrationNumber().trim().isEmpty()) {
                System.err.println("Error: Registration number is required.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(INSERT_EXAMINEE_QUERY);
            
            // Set parameters
            preparedStatement.setString(1, examinee.getRegistrationNumber());
            preparedStatement.setString(2, examinee.getFirstName());
            preparedStatement.setString(3, examinee.getLastName());
            preparedStatement.setString(4, examinee.getEmail());
            preparedStatement.setString(5, examinee.getPhone());
            
            // Handle date of birth (may be null)
            if (examinee.getDateOfBirth() != null) {
                preparedStatement.setDate(6, examinee.getDateOfBirth());
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }
            
            preparedStatement.setString(7, examinee.getAddress());
            preparedStatement.setString(8, examinee.getCity());
            preparedStatement.setString(9, examinee.getState());
            preparedStatement.setString(10, examinee.getPincode());
            
            // Execute insert
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Examinee added successfully: " + examinee.getRegistrationNumber());
                return true;
            } else {
                System.err.println("Failed to add examinee: No rows affected.");
                return false;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate email or registration number
            System.err.println("Error: Duplicate email or registration number.");
            System.err.println("Email: " + examinee.getEmail() + 
                             ", Registration Number: " + examinee.getRegistrationNumber());
            e.printStackTrace();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error adding examinee: " + 
                             (examinee != null ? examinee.getRegistrationNumber() : "null"));
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Updates an existing examinee in the database.
     * 
     * Updates the examinee identified by examinee_id. All fields except
     * examinee_id, created_at, and updated_at can be updated.
     * 
     * @param examinee the Examinee object containing updated data
     * @return true if the examinee was updated successfully, false otherwise
     */
    public boolean updateExaminee(Examinee examinee) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examinee == null) {
                System.err.println("Error: Cannot update null examinee.");
                return false;
            }
            
            if (examinee.getExamineeId() <= 0) {
                System.err.println("Error: Invalid examinee ID.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(UPDATE_EXAMINEE_QUERY);
            
            // Set parameters
            preparedStatement.setString(1, examinee.getRegistrationNumber());
            preparedStatement.setString(2, examinee.getFirstName());
            preparedStatement.setString(3, examinee.getLastName());
            preparedStatement.setString(4, examinee.getEmail());
            preparedStatement.setString(5, examinee.getPhone());
            
            // Handle date of birth (may be null)
            if (examinee.getDateOfBirth() != null) {
                preparedStatement.setDate(6, examinee.getDateOfBirth());
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }
            
            preparedStatement.setString(7, examinee.getAddress());
            preparedStatement.setString(8, examinee.getCity());
            preparedStatement.setString(9, examinee.getState());
            preparedStatement.setString(10, examinee.getPincode());
            
            // Set WHERE clause parameter
            preparedStatement.setInt(11, examinee.getExamineeId());
            
            // Execute update
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Examinee updated successfully: ID " + examinee.getExamineeId());
                return true;
            } else {
                System.err.println("Failed to update examinee: No rows affected. ID: " + examinee.getExamineeId());
                return false;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate email or registration number
            System.err.println("Error: Duplicate email or registration number.");
            System.err.println("Email: " + examinee.getEmail() + 
                             ", Registration Number: " + examinee.getRegistrationNumber());
            e.printStackTrace();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error updating examinee ID: " + 
                             (examinee != null ? examinee.getExamineeId() : "null"));
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Deletes an examinee from the database.
     * 
     * Note: Due to CASCADE constraints in the database schema, deleting an examinee
     * will automatically delete all associated exam registrations and results.
     * 
     * @param examineeId the ID of the examinee to delete
     * @return true if the examinee was deleted successfully, false otherwise
     */
    public boolean deleteExaminee(int examineeId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examineeId <= 0) {
                System.err.println("Error: Invalid examinee ID.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(DELETE_EXAMINEE_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, examineeId);
            
            // Execute delete
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Examinee deleted successfully: ID " + examineeId);
                return true;
            } else {
                System.err.println("Failed to delete examinee: No rows affected. ID: " + examineeId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting examinee ID: " + examineeId);
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Maps a ResultSet row to an Examinee object.
     * 
     * This helper method extracts data from a ResultSet and creates
     * a populated Examinee object. Used to avoid code duplication
     * across all retrieval methods.
     * 
     * @param resultSet the ResultSet containing examinee data
     * @return an Examinee object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Examinee mapResultSetToExaminee(ResultSet resultSet) throws SQLException {
        Examinee examinee = new Examinee();
        
        // Map all fields from ResultSet to Examinee object
        examinee.setExamineeId(resultSet.getInt("examinee_id"));
        examinee.setRegistrationNumber(resultSet.getString("registration_number"));
        examinee.setFirstName(resultSet.getString("first_name"));
        examinee.setLastName(resultSet.getString("last_name"));
        examinee.setEmail(resultSet.getString("email"));
        examinee.setPhone(resultSet.getString("phone"));
        
        // Handle date of birth (may be null)
        Date dateOfBirth = resultSet.getDate("date_of_birth");
        examinee.setDateOfBirth(dateOfBirth);
        
        examinee.setAddress(resultSet.getString("address"));
        examinee.setCity(resultSet.getString("city"));
        examinee.setState(resultSet.getString("state"));
        examinee.setPincode(resultSet.getString("pincode"));
        
        // Handle timestamp fields (may be null)
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        examinee.setCreatedAt(createdAt);
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        examinee.setUpdatedAt(updatedAt);
        
        return examinee;
    }
    
    /**
     * Test method to verify examinee DAO operations.
     * 
     * This method demonstrates CRUD operations and search functionality.
     * Run this class directly to test the DAO.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("ExamineeDAO Test");
        System.out.println("=========================================");
        System.out.println();
        
        ExamineeDAO examineeDAO = new ExamineeDAO();
        
        // Test Case 1: Add a sample examinee
        System.out.println("Test Case 1: Add a sample examinee");
        Examinee newExaminee = new Examinee();
        newExaminee.setRegistrationNumber("REG999");
        newExaminee.setFirstName("Alice");
        newExaminee.setLastName("Johnson");
        newExaminee.setEmail("alice.johnson@example.com");
        newExaminee.setPhone("5551234567");
        newExaminee.setDateOfBirth(java.sql.Date.valueOf("2000-05-15"));
        newExaminee.setAddress("123 Test Street");
        newExaminee.setCity("Test City");
        newExaminee.setState("Test State");
        newExaminee.setPincode("12345");
        
        boolean added = examineeDAO.addExaminee(newExaminee);
        if (added) {
            System.out.println("✓ Examinee added successfully");
        } else {
            System.out.println("✗ Failed to add examinee");
        }
        System.out.println();
        
        // Test Case 2: Retrieve all examinees
        System.out.println("Test Case 2: List all examinees");
        List<Examinee> allExaminees = examineeDAO.getAllExaminees();
        System.out.println("Total examinees: " + allExaminees.size());
        for (Examinee e : allExaminees) {
            System.out.println("  - ID: " + e.getExamineeId() + 
                             ", Reg#: " + e.getRegistrationNumber() + 
                             ", Name: " + e.getFirstName() + " " + e.getLastName());
        }
        System.out.println();
        
        // Test Case 3: Search for "Alice"
        System.out.println("Test Case 3: Search for 'Alice'");
        List<Examinee> searchResults = examineeDAO.searchExaminees("Alice");
        System.out.println("Search results: " + searchResults.size() + " examinee(s)");
        for (Examinee e : searchResults) {
            System.out.println("  - Found: " + e.getRegistrationNumber() + 
                             " - " + e.getFirstName() + " " + e.getLastName() + 
                             " (" + e.getEmail() + ")");
        }
        System.out.println();
        
        // Test Case 4: Get examinee by registration number
        System.out.println("Test Case 4: Get examinee by registration number 'REG999'");
        Examinee foundExaminee = examineeDAO.getExamineeByRegistrationNumber("REG999");
        if (foundExaminee != null) {
            System.out.println("✓ Examinee found: " + foundExaminee.toString());
        } else {
            System.out.println("✗ Examinee not found");
        }
        System.out.println();
        
        // Test Case 5: Update examinee (if found)
        if (foundExaminee != null) {
            System.out.println("Test Case 5: Update examinee");
            foundExaminee.setPhone("5559998888");
            foundExaminee.setCity("Updated City");
            boolean updated = examineeDAO.updateExaminee(foundExaminee);
            if (updated) {
                System.out.println("✓ Examinee updated successfully");
            } else {
                System.out.println("✗ Failed to update examinee");
            }
            System.out.println();
        }
        
        // Test Case 6: Delete the added examinee (optional)
        if (foundExaminee != null) {
            System.out.println("Test Case 6: Delete the test examinee");
            int examineeId = foundExaminee.getExamineeId();
            boolean deleted = examineeDAO.deleteExaminee(examineeId);
            if (deleted) {
                System.out.println("✓ Examinee deleted successfully");
            } else {
                System.out.println("✗ Failed to delete examinee");
            }
            System.out.println();
        }
        
        System.out.println("=========================================");
        System.out.println("Test Completed");
        System.out.println("=========================================");
    }
}

