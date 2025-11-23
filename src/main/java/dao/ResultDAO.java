package dao;

import model.Result;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Result Data Access Object (DAO) Class
 * 
 * Handles all database operations related to exam results in the
 * Smart Examinee Management System (SEMS).
 * 
 * Responsibilities:
 * - Enter and update exam results
 * - Retrieve results by registration, exam, or examinee
 * - Validate marks and auto-calculate percentage/grade
 * 
 * All database operations use PreparedStatement to prevent SQL injection.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ResultDAO {
    
    // SQL Queries
    private static final String GET_RESULT_BY_REGISTRATION_QUERY = 
        "SELECT * FROM results WHERE registration_id = ?";
    
    private static final String GET_RESULTS_BY_EXAM_QUERY = 
        "SELECT r.* FROM results r " +
        "INNER JOIN exam_registrations er ON r.registration_id = er.registration_id " +
        "WHERE er.exam_id = ? " +
        "ORDER BY r.percentage DESC";
    
    private static final String GET_RESULTS_BY_EXAMINEE_QUERY = 
        "SELECT r.* FROM results r " +
        "INNER JOIN exam_registrations er ON r.registration_id = er.registration_id " +
        "WHERE er.examinee_id = ? " +
        "ORDER BY r.entered_at DESC";
    
    private static final String INSERT_RESULT_QUERY = 
        "INSERT INTO results (registration_id, marks_obtained, max_marks, " +
        "percentage, grade, remarks, entered_by) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_RESULT_QUERY = 
        "UPDATE results SET marks_obtained = ?, max_marks = ?, " +
        "percentage = ?, grade = ?, remarks = ? WHERE result_id = ?";
    
    private static final String DELETE_RESULT_QUERY = 
        "DELETE FROM results WHERE result_id = ?";
    
    /**
     * Retrieves a result by registration ID.
     * 
     * @param registrationId the registration ID
     * @return Result object if found, null otherwise
     */
    public Result getResultByRegistrationId(int registrationId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_RESULT_BY_REGISTRATION_QUERY);
            preparedStatement.setInt(1, registrationId);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToResult(resultSet);
            }
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error retrieving result for registration ID: " + registrationId);
            e.printStackTrace();
            return null;
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Retrieves all results for a specific exam.
     * 
     * @param examId the exam ID
     * @return List of Result objects ordered by percentage (desc)
     */
    public List<Result> getResultsByExamId(int examId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Result> results = new ArrayList<>();
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_RESULTS_BY_EXAM_QUERY);
            preparedStatement.setInt(1, examId);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                results.add(mapResultSetToResult(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving results for exam ID: " + examId);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Retrieves all results for a specific examinee.
     * 
     * @param examineeId the examinee ID
     * @return List of Result objects ordered by entered date (desc)
     */
    public List<Result> getResultsByExamineeId(int examineeId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Result> results = new ArrayList<>();
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_RESULTS_BY_EXAMINEE_QUERY);
            preparedStatement.setInt(1, examineeId);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                results.add(mapResultSetToResult(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving results for examinee ID: " + examineeId);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return results;
    }
    
    /**
     * Saves a new result or updates an existing one.
     * 
     * @param result the Result object to save
     * @param enteredByUserId the ID of the user entering the result
     * @return true if successful, false otherwise
     */
    public boolean saveResult(Result result, int enteredByUserId) {
        // Auto-calculate percentage if not set
        if (result.getPercentage() == null && result.getMarksObtained() != null && result.getMaxMarks() != null) {
            BigDecimal percentage = result.getMarksObtained()
                .divide(result.getMaxMarks(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
            result.setPercentage(percentage);
        }
        
        // Auto-assign grade based on percentage
        if (result.getPercentage() != null && result.getGrade() == null) {
            result.setGrade(calculateGrade(result.getPercentage()));
        }
        
        // Check if result already exists for this registration
        Result existing = getResultByRegistrationId(result.getRegistrationId());
        if (existing != null) {
            // Update existing result
            result.setResultId(existing.getResultId());
            return updateResult(result);
        } else {
            // Insert new result
            result.setEnteredBy(enteredByUserId);
            return insertResult(result);
        }
    }
    
    /**
     * Inserts a new result into the database.
     * 
     * @param result the Result object to insert
     * @return true if successful, false otherwise
     */
    private boolean insertResult(Result result) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_RESULT_QUERY);
            preparedStatement.setInt(1, result.getRegistrationId());
            preparedStatement.setBigDecimal(2, result.getMarksObtained());
            preparedStatement.setBigDecimal(3, result.getMaxMarks());
            preparedStatement.setBigDecimal(4, result.getPercentage());
            preparedStatement.setString(5, result.getGrade());
            preparedStatement.setString(6, result.getRemarks());
            preparedStatement.setInt(7, result.getEnteredBy());
            
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting result for registration ID: " + result.getRegistrationId());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Updates an existing result in the database.
     * 
     * @param result the Result object to update
     * @return true if successful, false otherwise
     */
    private boolean updateResult(Result result) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_RESULT_QUERY);
            preparedStatement.setBigDecimal(1, result.getMarksObtained());
            preparedStatement.setBigDecimal(2, result.getMaxMarks());
            preparedStatement.setBigDecimal(3, result.getPercentage());
            preparedStatement.setString(4, result.getGrade());
            preparedStatement.setString(5, result.getRemarks());
            preparedStatement.setInt(6, result.getResultId());
            
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating result ID: " + result.getResultId());
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Deletes a result from the database.
     * 
     * @param resultId the result ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteResult(int resultId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_RESULT_QUERY);
            preparedStatement.setInt(1, resultId);
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting result ID: " + resultId);
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Calculates grade based on percentage.
     * 
     * @param percentage the percentage score
     * @return grade string (e.g., "A", "B", "Fail")
     */
    private String calculateGrade(BigDecimal percentage) {
        if (percentage.compareTo(new BigDecimal(90)) >= 0) return "A+";
        if (percentage.compareTo(new BigDecimal(80)) >= 0) return "A";
        if (percentage.compareTo(new BigDecimal(70)) >= 0) return "B";
        if (percentage.compareTo(new BigDecimal(60)) >= 0) return "C";
        if (percentage.compareTo(new BigDecimal(50)) >= 0) return "D";
        if (percentage.compareTo(new BigDecimal(40)) >= 0) return "E";
        return "Fail";
    }
    
    /**
     * Maps a ResultSet row to a Result object.
     * 
     * @param resultSet the ResultSet containing result data
     * @return a Result object
     * @throws SQLException if a database access error occurs
     */
    private Result mapResultSetToResult(ResultSet resultSet) throws SQLException {
        Result result = new Result();
        result.setResultId(resultSet.getInt("result_id"));
        result.setRegistrationId(resultSet.getInt("registration_id"));
        result.setMarksObtained(resultSet.getBigDecimal("marks_obtained"));
        result.setMaxMarks(resultSet.getBigDecimal("max_marks"));
        result.setPercentage(resultSet.getBigDecimal("percentage"));
        result.setGrade(resultSet.getString("grade"));
        result.setRemarks(resultSet.getString("remarks"));
        result.setEnteredBy(resultSet.getInt("entered_by"));
        result.setEnteredAt(resultSet.getTimestamp("entered_at"));
        result.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return result;
    }
    
    /**
     * Test method to verify result DAO operations.
     */
    public static void main(String[] args) {
        ResultDAO resultDAO = new ResultDAO();
        
        // Create a sample result
        Result result = new Result();
        result.setRegistrationId(1); // Assuming registration ID 1 exists
        result.setMarksObtained(new BigDecimal("85.50"));
        result.setMaxMarks(new BigDecimal("100.00"));
        // percentage and grade will be auto-calculated
        
        boolean saved = resultDAO.saveResult(result, 1); // entered by user ID 1
        System.out.println("Result saved: " + saved);
        
        // Retrieve results for exam ID 1
        List<Result> results = resultDAO.getResultsByExamId(1);
        System.out.println("Results for exam 1: " + results.size());
        for (Result r : results) {
            System.out.println("Result ID: " + r.getResultId() + 
                             ", Marks: " + r.getMarksObtained() + 
                             ", Grade: " + r.getGrade());
        }
    }
}