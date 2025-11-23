package dao;

import model.ExamRegistration;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exam Registration Data Access Object (DAO) Class
 * 
 * Handles all database operations related to exam registrations in the
 * Smart Examinee Management System (SEMS).
 * 
 * Responsibilities:
 * - Register examinees for exams
 * - Manage registration status (registered, confirmed, cancelled)
 * - Retrieve registrations by exam or examinee
 * - Generate hall ticket numbers
 * 
 * All database operations use PreparedStatement to prevent SQL injection.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ExamRegistrationDAO {
    
    // SQL Queries
    private static final String GET_REGISTRATIONS_BY_EXAM_QUERY = 
        "SELECT * FROM exam_registrations WHERE exam_id = ? ORDER BY registration_date DESC";
    
    private static final String GET_REGISTRATIONS_BY_EXAMINEE_QUERY = 
        "SELECT * FROM exam_registrations WHERE examinee_id = ? ORDER BY registration_date DESC";
    
    private static final String GET_REGISTRATION_BY_ID_QUERY = 
        "SELECT * FROM exam_registrations WHERE registration_id = ?";
    
    private static final String INSERT_REGISTRATION_QUERY = 
        "INSERT INTO exam_registrations (examinee_id, exam_id, hall_ticket_number, status) " +
        "VALUES (?, ?, ?, 'registered')";
    
    private static final String UPDATE_REGISTRATION_STATUS_QUERY = 
        "UPDATE exam_registrations SET status = ? WHERE registration_id = ?";
    
    private static final String GET_ACTIVE_REGISTRATION_COUNT_QUERY = 
        "SELECT COUNT(*) FROM exam_registrations WHERE exam_id = ? AND status != 'cancelled'";
    
    /**
     * Retrieves all registrations for a specific exam.
     * 
     * @param examId the ID of the exam
     * @return List of ExamRegistration objects, empty list if none found
     */
    public List<ExamRegistration> getRegistrationsByExamId(int examId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ExamRegistration> registrations = new ArrayList<>();
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_REGISTRATIONS_BY_EXAM_QUERY);
            preparedStatement.setInt(1, examId);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                registrations.add(mapResultSetToRegistration(resultSet));
            }
            
            System.out.println("Retrieved " + registrations.size() + " registration(s) for exam ID: " + examId);
            
        } catch (SQLException e) {
            System.err.println("Error retrieving registrations for exam ID: " + examId);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return registrations;
    }
    
    /**
     * Retrieves all registrations for a specific examinee.
     * 
     * @param examineeId the ID of the examinee
     * @return List of ExamRegistration objects, empty list if none found
     */
    public List<ExamRegistration> getRegistrationsByExamineeId(int examineeId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ExamRegistration> registrations = new ArrayList<>();
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_REGISTRATIONS_BY_EXAMINEE_QUERY);
            preparedStatement.setInt(1, examineeId);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                registrations.add(mapResultSetToRegistration(resultSet));
            }
            
            System.out.println("Retrieved " + registrations.size() + " registration(s) for examinee ID: " + examineeId);
            
        } catch (SQLException e) {
            System.err.println("Error retrieving registrations for examinee ID: " + examineeId);
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return registrations;
    }
    
    /**
     * Retrieves a registration by its ID.
     * 
     * @param registrationId the ID of the registration
     * @return ExamRegistration object if found, null otherwise
     */
    public ExamRegistration getRegistrationById(int registrationId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_REGISTRATION_BY_ID_QUERY);
            preparedStatement.setInt(1, registrationId);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                ExamRegistration registration = mapResultSetToRegistration(resultSet);
                System.out.println("Registration found: ID " + registrationId);
                return registration;
            } else {
                System.out.println("Registration not found: ID " + registrationId);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving registration by ID: " + registrationId);
            e.printStackTrace();
            return null;
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Registers an examinee for an exam.
     * 
     * @param examineeId the ID of the examinee
     * @param examId the ID of the exam
     * @param hallTicketNumber custom hall ticket number (optional, if null will be auto-generated)
     * @return true if registration was successful, false if duplicate or error
     */
    public boolean registerExamineeForExam(int examineeId, int examId, String hallTicketNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Validate input
            if (examineeId <= 0 || examId <= 0) {
                System.err.println("Invalid examinee or exam ID.");
                return false;
            }
            
            // Auto-generate hall ticket number if not provided
            if (hallTicketNumber == null || hallTicketNumber.trim().isEmpty()) {
                hallTicketNumber = "HT-" + examId + "-" + examineeId;
            }
            
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_REGISTRATION_QUERY);
            preparedStatement.setInt(1, examineeId);
            preparedStatement.setInt(2, examId);
            preparedStatement.setString(3, hallTicketNumber);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Examinee " + examineeId + " registered for exam " + examId + 
                                 " with hall ticket: " + hallTicketNumber);
                return true;
            } else {
                System.err.println("Failed to register examinee " + examineeId + " for exam " + examId);
                return false;
            }
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: Examinee is already registered for this exam.");
            System.err.println("Examinee ID: " + examineeId + ", Exam ID: " + examId);
            e.printStackTrace();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error registering examinee " + examineeId + " for exam " + examId);
            e.printStackTrace();
            return false;
            
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Cancels a registration by setting its status to 'cancelled'.
     * 
     * @param registrationId the ID of the registration to cancel
     * @return true if cancellation was successful, false otherwise
     */
    public boolean cancelRegistration(int registrationId) {
        return updateRegistrationStatus(registrationId, "cancelled");
    }
    
    /**
     * Confirms a registration by setting its status to 'confirmed'.
     * 
     * @param registrationId the ID of the registration to confirm
     * @return true if confirmation was successful, false otherwise
     */
    public boolean confirmRegistration(int registrationId) {
        return updateRegistrationStatus(registrationId, "confirmed");
    }
    
    /**
     * Updates the status of a registration.
     * 
     * @param registrationId the ID of the registration
     * @param status the new status ('registered', 'confirmed', 'cancelled')
     * @return true if update was successful, false otherwise
     */
    private boolean updateRegistrationStatus(int registrationId, String status) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            if (registrationId <= 0 || status == null || status.trim().isEmpty()) {
                System.err.println("Invalid registration ID or status.");
                return false;
            }
            
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_REGISTRATION_STATUS_QUERY);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, registrationId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Registration status updated: ID " + registrationId + " → " + status);
                return true;
            } else {
                System.err.println("Failed to update registration status: ID " + registrationId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating registration status: ID " + registrationId);
            e.printStackTrace();
            return false;
            
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, null);
        }
    }
    
    /**
     * Gets the count of active (non-cancelled) registrations for an exam.
     * 
     * @param examId the ID of the exam
     * @return number of active registrations
     */
    public int getRegistrationCountForExam(int examId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_ACTIVE_REGISTRATION_COUNT_QUERY);
            preparedStatement.setInt(1, examId);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting registration count for exam ID: " + examId);
            e.printStackTrace();
            return 0;
            
        } finally {
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
    }
    
    /**
     * Maps a ResultSet row to an ExamRegistration object.
     * 
     * @param resultSet the ResultSet containing registration data
     * @return an ExamRegistration object
     * @throws SQLException if a database access error occurs
     */
    private ExamRegistration mapResultSetToRegistration(ResultSet resultSet) throws SQLException {
        ExamRegistration registration = new ExamRegistration();
        
        registration.setRegistrationId(resultSet.getInt("registration_id"));
        registration.setExamineeId(resultSet.getInt("examinee_id"));
        registration.setExamId(resultSet.getInt("exam_id"));
        registration.setRegistrationDate(resultSet.getTimestamp("registration_date"));
        registration.setStatus(resultSet.getString("status"));
        registration.setHallTicketNumber(resultSet.getString("hall_ticket_number"));
        registration.setRemarks(resultSet.getString("remarks"));
        
        return registration;
    }
    
    /**
     * Test method to verify exam registration DAO operations.
     */
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("ExamRegistrationDAO Test");
        System.out.println("=========================================");
        System.out.println();
        
        ExamRegistrationDAO regDAO = new ExamRegistrationDAO();
        
        // Test Case 1: Register examinee 1 for exam 1
        System.out.println("Test Case 1: Register examinee 1 for exam 1");
        boolean registered = regDAO.registerExamineeForExam(1, 1, null);
        if (registered) {
            System.out.println("✓ Registration successful");
        } else {
            System.out.println("✗ Registration failed (may already exist)");
        }
        System.out.println();
        
        // Test Case 2: Get registrations for exam 1
        System.out.println("Test Case 2: Get registrations for exam 1");
        List<ExamRegistration> regs = regDAO.getRegistrationsByExamId(1);
        System.out.println("Total registrations: " + regs.size());
        for (ExamRegistration r : regs) {
            System.out.println("  - ID: " + r.getRegistrationId() + 
                             ", Examinee: " + r.getExamineeId() + 
                             ", Hall Ticket: " + r.getHallTicketNumber() + 
                             ", Status: " + r.getStatus());
        }
        System.out.println();
        
        // Test Case 3: Confirm registration (if any exists)
        if (!regs.isEmpty()) {
            int regId = regs.get(0).getRegistrationId();
            System.out.println("Test Case 3: Confirm registration ID " + regId);
            boolean confirmed = regDAO.confirmRegistration(regId);
            System.out.println(confirmed ? "✓ Confirmed" : "✗ Failed to confirm");
            System.out.println();
            
            // Test Case 4: Cancel registration
            System.out.println("Test Case 4: Cancel registration ID " + regId);
            boolean cancelled = regDAO.cancelRegistration(regId);
            System.out.println(cancelled ? "✓ Cancelled" : "✗ Failed to cancel");
            System.out.println();
        }
        
        // Test Case 5: Get registration count for exam 1
        System.out.println("Test Case 5: Active registration count for exam 1");
        int count = regDAO.getRegistrationCountForExam(1);
        System.out.println("Active registrations: " + count);
        System.out.println();
        
        System.out.println("=========================================");
        System.out.println("Test Completed");
        System.out.println("=========================================");
    }
}