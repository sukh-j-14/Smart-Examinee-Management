package model;

import java.sql.Timestamp;

/**
 * Exam Registration Model Class
 * 
 * Represents an exam registration entity that links examinees to exams
 * in the Smart Examinee Management System.
 * 
 * Maps to the 'exam_registrations' table in the database.
 * This table establishes a many-to-many relationship between examinees and exams.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ExamRegistration {
    
    // Private fields matching database columns
    private int registrationId;
    private int examineeId;  // Foreign key to examinees table
    private int examId;      // Foreign key to exams table
    private Timestamp registrationDate;
    private String status;   // 'registered', 'confirmed', 'cancelled'
    private String hallTicketNumber;
    private String remarks;
    
    /**
     * Default no-argument constructor
     * Creates an empty ExamRegistration object
     */
    public ExamRegistration() {
        // Empty constructor for object creation
        this.status = "registered";
    }
    
    /**
     * Full-argument constructor
     * Creates an ExamRegistration object with all fields initialized
     * 
     * @param registrationId the unique identifier for the registration
     * @param examineeId the ID of the examinee
     * @param examId the ID of the exam
     * @param registrationDate the date and time of registration
     * @param status the status of the registration
     * @param hallTicketNumber the hall ticket number assigned
     * @param remarks additional remarks or notes
     */
    public ExamRegistration(int registrationId, int examineeId, int examId,
                           Timestamp registrationDate, String status,
                           String hallTicketNumber, String remarks) {
        this.registrationId = registrationId;
        this.examineeId = examineeId;
        this.examId = examId;
        this.registrationDate = registrationDate;
        this.status = status;
        this.hallTicketNumber = hallTicketNumber;
        this.remarks = remarks;
    }
    
    // Getter and Setter methods
    
    /**
     * Gets the registration ID
     * 
     * @return the registration ID
     */
    public int getRegistrationId() {
        return registrationId;
    }
    
    /**
     * Sets the registration ID
     * 
     * @param registrationId the registration ID to set
     */
    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }
    
    /**
     * Gets the examinee ID
     * 
     * @return the examinee ID
     */
    public int getExamineeId() {
        return examineeId;
    }
    
    /**
     * Sets the examinee ID
     * 
     * @param examineeId the examinee ID to set
     */
    public void setExamineeId(int examineeId) {
        this.examineeId = examineeId;
    }
    
    /**
     * Gets the exam ID
     * 
     * @return the exam ID
     */
    public int getExamId() {
        return examId;
    }
    
    /**
     * Sets the exam ID
     * 
     * @param examId the exam ID to set
     */
    public void setExamId(int examId) {
        this.examId = examId;
    }
    
    /**
     * Gets the registration date
     * 
     * @return the registration date
     */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }
    
    /**
     * Sets the registration date
     * 
     * @param registrationDate the registration date to set
     */
    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    /**
     * Gets the status
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the status
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the hall ticket number
     * 
     * @return the hall ticket number
     */
    public String getHallTicketNumber() {
        return hallTicketNumber;
    }
    
    /**
     * Sets the hall ticket number
     * 
     * @param hallTicketNumber the hall ticket number to set
     */
    public void setHallTicketNumber(String hallTicketNumber) {
        this.hallTicketNumber = hallTicketNumber;
    }
    
    /**
     * Gets the remarks
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }
    
    /**
     * Sets the remarks
     * 
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    /**
     * Checks if the registration is confirmed
     * 
     * @return true if the status is 'confirmed', false otherwise
     */
    public boolean isConfirmed() {
        return "confirmed".equalsIgnoreCase(status);
    }
    
    /**
     * Checks if the registration is cancelled
     * 
     * @return true if the status is 'cancelled', false otherwise
     */
    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }
    
    /**
     * Returns a string representation of the ExamRegistration object
     * 
     * @return a string containing the registration's key information
     */
    @Override
    public String toString() {
        return "ExamRegistration{" +
                "registrationId=" + registrationId +
                ", examineeId=" + examineeId +
                ", examId=" + examId +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                ", hallTicketNumber='" + hallTicketNumber + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}

