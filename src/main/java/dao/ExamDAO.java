package dao;

import model.Exam;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exam Data Access Object (DAO) Class
 * 
 * Handles all database operations related to Exam entities in the
 * Smart Examinee Management System (SEMS).
 * 
 * Responsibilities:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Search functionality
 * - Status updates
 * - Data validation and exception handling
 * 
 * All database operations use PreparedStatement to prevent SQL injection.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ExamDAO {
    
    // SQL Queries
    private static final String GET_ALL_EXAMS_QUERY = 
        "SELECT * FROM exams ORDER BY exam_date DESC";
    
    private static final String GET_EXAM_BY_ID_QUERY = 
        "SELECT * FROM exams WHERE exam_id = ?";
    
    private static final String SEARCH_EXAMS_QUERY = 
        "SELECT * FROM exams WHERE " +
        "LOWER(exam_name) LIKE ? OR " +
        "LOWER(exam_code) LIKE ? OR " +
        "LOWER(venue) LIKE ? " +
        "ORDER BY exam_date DESC";
    
    private static final String INSERT_EXAM_QUERY = 
        "INSERT INTO exams (exam_name, exam_code, description, exam_date, start_time, " +
        "end_time, duration_minutes, venue, max_capacity, current_registrations, " +
        "status, created_by) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_EXAM_QUERY = 
        "UPDATE exams SET exam_name = ?, exam_code = ?, description = ?, exam_date = ?, " +
        "start_time = ?, end_time = ?, duration_minutes = ?, venue = ?, max_capacity = ?, " +
        "current_registrations = ?, status = ? WHERE exam_id = ?";
    
    private static final String UPDATE_EXAM_STATUS_QUERY = 
        "UPDATE exams SET status = ? WHERE exam_id = ?";
    
    private static final String DELETE_EXAM_QUERY = 
        "DELETE FROM exams WHERE exam_id = ?";
    
    /**
     * Retrieves all exams from the database, ordered by exam date (most recent first).
     * 
     * @return List of Exam objects, empty list if no exams found
     */
    public List<Exam> getAllExams() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Exam> exams = new ArrayList<>();
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement
            preparedStatement = connection.prepareStatement(GET_ALL_EXAMS_QUERY);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Process results
            while (resultSet.next()) {
                Exam exam = mapResultSetToExam(resultSet);
                exams.add(exam);
            }
            
            System.out.println("Retrieved " + exams.size() + " exam(s) from database.");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all exams.");
            e.printStackTrace();
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return exams;
    }
    
    /**
     * Retrieves an exam by its ID.
     * 
     * @param examId the ID of the exam to retrieve
     * @return Exam object if found, null otherwise
     */
    public Exam getExamById(int examId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(GET_EXAM_BY_ID_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, examId);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Check if exam was found
            if (resultSet.next()) {
                Exam exam = mapResultSetToExam(resultSet);
                System.out.println("Exam retrieved successfully: " + exam.getExamCode());
                return exam;
            } else {
                System.out.println("Exam not found with ID: " + examId);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving exam by ID: " + examId);
            e.printStackTrace();
            return null;
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Searches for exams by keyword.
     * 
     * Searches in exam_name, exam_code, and venue fields (case-insensitive).
     * Uses LIKE pattern matching.
     * 
     * @param keyword the search keyword
     * @return List of matching Exam objects, empty list if no matches found
     */
    public List<Exam> searchExams(String keyword) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Exam> exams = new ArrayList<>();
        
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(SEARCH_EXAMS_QUERY);
            
            // Prepare search pattern (case-insensitive, wildcard matching)
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            
            // Set parameters (same pattern for all LIKE clauses)
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);
            preparedStatement.setString(3, searchPattern);
            
            // Execute query
            resultSet = preparedStatement.executeQuery();
            
            // Process results
            while (resultSet.next()) {
                Exam exam = mapResultSetToExam(resultSet);
                exams.add(exam);
            }
            
            System.out.println("Search found " + exams.size() + " exam(s) matching: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("Error searching exams with keyword: " + keyword);
            e.printStackTrace();
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return exams;
    }
    
    /**
     * Adds a new exam to the database.
     * 
     * Note: The createdBy field must be set (user ID who creates the exam).
     * The exam_id will be auto-generated by the database.
     * 
     * @param exam the Exam object containing the data to insert
     * @return true if the exam was added successfully, false otherwise
     */
    public boolean addExam(Exam exam) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (exam == null) {
                System.err.println("Error: Cannot add null exam.");
                return false;
            }
            
            if (exam.getExamCode() == null || exam.getExamCode().trim().isEmpty()) {
                System.err.println("Error: Exam code is required.");
                return false;
            }
            
            if (exam.getCreatedBy() <= 0) {
                System.err.println("Error: Created by user ID is required.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(INSERT_EXAM_QUERY);
            
            // Set parameters
            preparedStatement.setString(1, exam.getExamName());
            preparedStatement.setString(2, exam.getExamCode());
            preparedStatement.setString(3, exam.getDescription());
            preparedStatement.setDate(4, exam.getExamDate());
            preparedStatement.setTime(5, exam.getStartTime());
            preparedStatement.setTime(6, exam.getEndTime());
            
            // Handle duration_minutes (may be null)
            if (exam.getDurationMinutes() != null) {
                preparedStatement.setInt(7, exam.getDurationMinutes());
            } else {
                preparedStatement.setNull(7, Types.INTEGER);
            }
            
            preparedStatement.setString(8, exam.getVenue());
            preparedStatement.setInt(9, exam.getMaxCapacity());
            preparedStatement.setInt(10, exam.getCurrentRegistrations());
            preparedStatement.setString(11, exam.getStatus() != null ? exam.getStatus() : "scheduled");
            preparedStatement.setInt(12, exam.getCreatedBy());
            
            // Execute insert
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Exam added successfully: " + exam.getExamCode());
                return true;
            } else {
                System.err.println("Failed to add exam: No rows affected.");
                return false;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate exam code
            System.err.println("Error: Duplicate exam code.");
            System.err.println("Exam Code: " + exam.getExamCode());
            e.printStackTrace();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error adding exam: " + 
                             (exam != null ? exam.getExamCode() : "null"));
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Updates an existing exam in the database.
     * 
     * Updates the exam identified by exam_id. All fields except
     * exam_id, created_by, created_at, and updated_at can be updated.
     * 
     * @param exam the Exam object containing updated data
     * @return true if the exam was updated successfully, false otherwise
     */
    public boolean updateExam(Exam exam) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (exam == null) {
                System.err.println("Error: Cannot update null exam.");
                return false;
            }
            
            if (exam.getExamId() <= 0) {
                System.err.println("Error: Invalid exam ID.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(UPDATE_EXAM_QUERY);
            
            // Set parameters
            preparedStatement.setString(1, exam.getExamName());
            preparedStatement.setString(2, exam.getExamCode());
            preparedStatement.setString(3, exam.getDescription());
            preparedStatement.setDate(4, exam.getExamDate());
            preparedStatement.setTime(5, exam.getStartTime());
            preparedStatement.setTime(6, exam.getEndTime());
            
            // Handle duration_minutes (may be null)
            if (exam.getDurationMinutes() != null) {
                preparedStatement.setInt(7, exam.getDurationMinutes());
            } else {
                preparedStatement.setNull(7, Types.INTEGER);
            }
            
            preparedStatement.setString(8, exam.getVenue());
            preparedStatement.setInt(9, exam.getMaxCapacity());
            preparedStatement.setInt(10, exam.getCurrentRegistrations());
            preparedStatement.setString(11, exam.getStatus());
            
            // Set WHERE clause parameter
            preparedStatement.setInt(12, exam.getExamId());
            
            // Execute update
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Exam updated successfully: ID " + exam.getExamId());
                return true;
            } else {
                System.err.println("Failed to update exam: No rows affected. ID: " + exam.getExamId());
                return false;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate exam code
            System.err.println("Error: Duplicate exam code.");
            System.err.println("Exam Code: " + exam.getExamCode());
            e.printStackTrace();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error updating exam ID: " + 
                             (exam != null ? exam.getExamId() : "null"));
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Updates only the status of an exam.
     * 
     * This method is useful for status transitions (e.g., 'scheduled' → 'ongoing' → 'completed').
     * 
     * @param examId the ID of the exam to update
     * @param status the new status ('scheduled', 'ongoing', 'completed', 'cancelled')
     * @return true if the status was updated successfully, false otherwise
     */
    public boolean updateExamStatus(int examId, String status) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examId <= 0) {
                System.err.println("Error: Invalid exam ID.");
                return false;
            }
            
            if (status == null || status.trim().isEmpty()) {
                System.err.println("Error: Status cannot be null or empty.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(UPDATE_EXAM_STATUS_QUERY);
            
            // Set parameters
            preparedStatement.setString(1, status.trim());
            preparedStatement.setInt(2, examId);
            
            // Execute update
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Exam status updated successfully: ID " + examId + " → " + status);
                return true;
            } else {
                System.err.println("Failed to update exam status: No rows affected. ID: " + examId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating exam status for ID: " + examId);
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Deletes an exam from the database.
     * 
     * Note: Due to CASCADE constraints in the database schema, deleting an exam
     * will automatically delete all associated exam registrations and results.
     * 
     * @param examId the ID of the exam to delete
     * @return true if the exam was deleted successfully, false otherwise
     */
    public boolean deleteExam(int examId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examId <= 0) {
                System.err.println("Error: Invalid exam ID.");
                return false;
            }
            
            // Get database connection
            connection = DBConnection.getConnection();
            
            // Prepare SQL statement with parameters
            preparedStatement = connection.prepareStatement(DELETE_EXAM_QUERY);
            
            // Set parameter
            preparedStatement.setInt(1, examId);
            
            // Execute delete
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Exam deleted successfully: ID " + examId);
                return true;
            } else {
                System.err.println("Failed to delete exam: No rows affected. ID: " + examId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting exam ID: " + examId);
            e.printStackTrace();
            return false;
            
        } finally {
            // Close all resources
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Maps a ResultSet row to an Exam object.
     * 
     * This helper method extracts data from a ResultSet and creates
     * a populated Exam object. Used to avoid code duplication
     * across all retrieval methods.
     * 
     * @param resultSet the ResultSet containing exam data
     * @return an Exam object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Exam mapResultSetToExam(ResultSet resultSet) throws SQLException {
        Exam exam = new Exam();
        
        // Map all fields from ResultSet to Exam object
        exam.setExamId(resultSet.getInt("exam_id"));
        exam.setExamName(resultSet.getString("exam_name"));
        exam.setExamCode(resultSet.getString("exam_code"));
        exam.setDescription(resultSet.getString("description"));
        exam.setExamDate(resultSet.getDate("exam_date"));
        exam.setStartTime(resultSet.getTime("start_time"));
        exam.setEndTime(resultSet.getTime("end_time"));
        
        // Handle duration_minutes (may be null)
        int durationMinutes = resultSet.getInt("duration_minutes");
        if (!resultSet.wasNull()) {
            exam.setDurationMinutes(durationMinutes);
        } else {
            exam.setDurationMinutes(null);
        }
        
        exam.setVenue(resultSet.getString("venue"));
        exam.setMaxCapacity(resultSet.getInt("max_capacity"));
        exam.setCurrentRegistrations(resultSet.getInt("current_registrations"));
        exam.setStatus(resultSet.getString("status"));
        exam.setCreatedBy(resultSet.getInt("created_by"));
        
        // Handle timestamp fields (may be null)
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        exam.setCreatedAt(createdAt);
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        exam.setUpdatedAt(updatedAt);
        
        return exam;
    }
    
    /**
     * Test method to verify exam DAO operations.
     * 
     * This method demonstrates CRUD operations and search functionality.
     * Run this class directly to test the DAO.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("ExamDAO Test");
        System.out.println("=========================================");
        System.out.println();
        
        ExamDAO examDAO = new ExamDAO();
        
        // Test Case 1: Add a sample exam
        System.out.println("Test Case 1: Add a sample exam");
        Exam newExam = new Exam();
        newExam.setExamName("Midterm Examination 2024");
        newExam.setExamCode("MID2024");
        newExam.setDescription("Midterm examination for all students");
        newExam.setExamDate(java.sql.Date.valueOf("2024-12-15"));
        newExam.setStartTime(java.sql.Time.valueOf("10:00:00"));
        newExam.setEndTime(java.sql.Time.valueOf("13:00:00"));
        newExam.setDurationMinutes(180);
        newExam.setVenue("Main Hall");
        newExam.setMaxCapacity(200);
        newExam.setCurrentRegistrations(0);
        newExam.setStatus("scheduled");
        newExam.setCreatedBy(1); // Assuming user ID 1 exists
        
        boolean added = examDAO.addExam(newExam);
        if (added) {
            System.out.println("✓ Exam added successfully");
        } else {
            System.out.println("✗ Failed to add exam");
        }
        System.out.println();
        
        // Test Case 2: Retrieve all exams
        System.out.println("Test Case 2: List all exams");
        List<Exam> allExams = examDAO.getAllExams();
        System.out.println("Total exams: " + allExams.size());
        for (Exam e : allExams) {
            System.out.println("  - ID: " + e.getExamId() + 
                             ", Code: " + e.getExamCode() + 
                             ", Name: " + e.getExamName() +
                             ", Date: " + e.getExamDate() +
                             ", Status: " + e.getStatus());
        }
        System.out.println();
        
        // Test Case 3: Search for "Midterm"
        System.out.println("Test Case 3: Search for 'Midterm'");
        List<Exam> searchResults = examDAO.searchExams("Midterm");
        System.out.println("Search results: " + searchResults.size() + " exam(s)");
        for (Exam e : searchResults) {
            System.out.println("  - Found: " + e.getExamCode() + 
                             " - " + e.getExamName() + 
                             " (" + e.getVenue() + ")");
        }
        System.out.println();
        
        // Test Case 4: Get exam by ID (if any exists)
        if (!allExams.isEmpty()) {
            Exam firstExam = allExams.get(0);
            System.out.println("Test Case 4: Get exam by ID: " + firstExam.getExamId());
            Exam foundExam = examDAO.getExamById(firstExam.getExamId());
            if (foundExam != null) {
                System.out.println("✓ Exam found: " + foundExam.toString());
            } else {
                System.out.println("✗ Exam not found");
            }
            System.out.println();
            
            // Test Case 5: Update exam status
            System.out.println("Test Case 5: Update exam status to 'ongoing'");
            boolean statusUpdated = examDAO.updateExamStatus(firstExam.getExamId(), "ongoing");
            if (statusUpdated) {
                System.out.println("✓ Exam status updated successfully");
            } else {
                System.out.println("✗ Failed to update exam status");
            }
            System.out.println();
            
            // Test Case 6: Update exam
            System.out.println("Test Case 6: Update exam");
            foundExam.setVenue("Updated Hall");
            foundExam.setMaxCapacity(250);
            boolean updated = examDAO.updateExam(foundExam);
            if (updated) {
                System.out.println("✓ Exam updated successfully");
            } else {
                System.out.println("✗ Failed to update exam");
            }
            System.out.println();
            
            // Test Case 7: Delete the test exam (optional)
            System.out.println("Test Case 7: Delete the test exam (MID2024)");
            Exam testExam = null;
            for (Exam e : allExams) {
                if ("MID2024".equals(e.getExamCode())) {
                    testExam = e;
                    break;
                }
            }
            
            if (testExam != null) {
                boolean deleted = examDAO.deleteExam(testExam.getExamId());
                if (deleted) {
                    System.out.println("✓ Exam deleted successfully");
                } else {
                    System.out.println("✗ Failed to delete exam");
                }
            } else {
                System.out.println("Test exam not found for deletion");
            }
            System.out.println();
        }
        
        System.out.println("=========================================");
        System.out.println("Test Completed");
        System.out.println("=========================================");
    }
}

