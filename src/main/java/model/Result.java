package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Result Model Class
 * 
 * Represents an exam result entity in the Smart Examinee Management System.
 * Stores marks, percentage, grade, and other result-related information
 * for examinees who have taken exams.
 * 
 * Maps to the 'results' table in the database.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class Result {
    
    // Private fields matching database columns
    private int resultId;
    private int registrationId;  // Foreign key to exam_registrations table
    private BigDecimal marksObtained;
    private BigDecimal maxMarks;
    private BigDecimal percentage;
    private String grade;
    private String remarks;
    private int enteredBy;  // Foreign key to users table
    private Timestamp enteredAt;
    private Timestamp updatedAt;
    
    /**
     * Default no-argument constructor
     * Creates an empty Result object
     */
    public Result() {
        // Empty constructor for object creation
    }
    
    /**
     * Full-argument constructor
     * Creates a Result object with all fields initialized
     * 
     * @param resultId the unique identifier for the result
     * @param registrationId the registration ID this result belongs to
     * @param marksObtained the marks obtained by the examinee
     * @param maxMarks the maximum marks for the exam
     * @param percentage the percentage calculated
     * @param grade the grade assigned
     * @param remarks additional remarks or notes
     * @param enteredBy the user ID who entered this result
     * @param enteredAt the timestamp when the result was entered
     * @param updatedAt the timestamp when the result was last updated
     */
    public Result(int resultId, int registrationId, BigDecimal marksObtained,
                  BigDecimal maxMarks, BigDecimal percentage, String grade,
                  String remarks, int enteredBy, Timestamp enteredAt, Timestamp updatedAt) {
        this.resultId = resultId;
        this.registrationId = registrationId;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.remarks = remarks;
        this.enteredBy = enteredBy;
        this.enteredAt = enteredAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter and Setter methods
    
    /**
     * Gets the result ID
     * 
     * @return the result ID
     */
    public int getResultId() {
        return resultId;
    }
    
    /**
     * Sets the result ID
     * 
     * @param resultId the result ID to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
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
     * Gets the marks obtained
     * 
     * @return the marks obtained
     */
    public BigDecimal getMarksObtained() {
        return marksObtained;
    }
    
    /**
     * Sets the marks obtained
     * 
     * @param marksObtained the marks obtained to set
     */
    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }
    
    /**
     * Gets the maximum marks
     * 
     * @return the maximum marks
     */
    public BigDecimal getMaxMarks() {
        return maxMarks;
    }
    
    /**
     * Sets the maximum marks
     * 
     * @param maxMarks the maximum marks to set
     */
    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }
    
    /**
     * Gets the percentage
     * 
     * @return the percentage
     */
    public BigDecimal getPercentage() {
        return percentage;
    }
    
    /**
     * Sets the percentage
     * 
     * @param percentage the percentage to set
     */
    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
    
    /**
     * Calculates and sets the percentage based on marks obtained and max marks
     * 
     * @return the calculated percentage
     */
    public BigDecimal calculatePercentage() {
        if (maxMarks != null && maxMarks.compareTo(BigDecimal.ZERO) > 0 && marksObtained != null) {
            percentage = marksObtained.divide(maxMarks, 4, BigDecimal.ROUND_HALF_UP)
                                     .multiply(new BigDecimal(100))
                                     .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return percentage;
    }
    
    /**
     * Gets the grade
     * 
     * @return the grade
     */
    public String getGrade() {
        return grade;
    }
    
    /**
     * Sets the grade
     * 
     * @param grade the grade to set
     */
    public void setGrade(String grade) {
        this.grade = grade;
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
     * Gets the user ID who entered this result
     * 
     * @return the entered by user ID
     */
    public int getEnteredBy() {
        return enteredBy;
    }
    
    /**
     * Sets the user ID who entered this result
     * 
     * @param enteredBy the entered by user ID to set
     */
    public void setEnteredBy(int enteredBy) {
        this.enteredBy = enteredBy;
    }
    
    /**
     * Gets the entered at timestamp
     * 
     * @return the entered at timestamp
     */
    public Timestamp getEnteredAt() {
        return enteredAt;
    }
    
    /**
     * Sets the entered at timestamp
     * 
     * @param enteredAt the entered at timestamp to set
     */
    public void setEnteredAt(Timestamp enteredAt) {
        this.enteredAt = enteredAt;
    }
    
    /**
     * Gets the updated at timestamp
     * 
     * @return the updated at timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the updated at timestamp
     * 
     * @param updatedAt the updated at timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if the result is a pass (assuming pass percentage is 40)
     * Can be customized based on requirements
     * 
     * @return true if percentage >= 40, false otherwise
     */
    public boolean isPass() {
        if (percentage != null) {
            return percentage.compareTo(new BigDecimal(40)) >= 0;
        }
        return false;
    }
    
    /**
     * Returns a string representation of the Result object
     * 
     * @return a string containing the result's key information
     */
    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", registrationId=" + registrationId +
                ", marksObtained=" + marksObtained +
                ", maxMarks=" + maxMarks +
                ", percentage=" + percentage +
                ", grade='" + grade + '\'' +
                ", remarks='" + remarks + '\'' +
                ", enteredBy=" + enteredBy +
                ", enteredAt=" + enteredAt +
                '}';
    }
}

