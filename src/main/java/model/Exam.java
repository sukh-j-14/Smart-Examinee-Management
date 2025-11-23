package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Exam Model Class
 * 
 * Represents an exam entity in the Smart Examinee Management System.
 * Stores information about exams including date, time, venue, capacity, and status.
 * 
 * Maps to the 'exams' table in the database.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class Exam {
    
    // Private fields matching database columns
    private int examId;
    private String examName;
    private String examCode;
    private String description;
    private Date examDate;
    private Time startTime;
    private Time endTime;
    private Integer durationMinutes;
    private String venue;
    private int maxCapacity;
    private int currentRegistrations;
    private String status;  // 'scheduled', 'ongoing', 'completed', 'cancelled'
    private int createdBy;  // Foreign key to users table
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default no-argument constructor
     * Creates an empty Exam object
     */
    public Exam() {
        // Empty constructor for object creation
        this.status = "scheduled";
        this.maxCapacity = 100;
        this.currentRegistrations = 0;
    }
    
    /**
     * Full-argument constructor
     * Creates an Exam object with all fields initialized
     * 
     * @param examId the unique identifier for the exam
     * @param examName the name of the exam
     * @param examCode the unique code for the exam
     * @param description the description of the exam
     * @param examDate the date of the exam
     * @param startTime the start time of the exam
     * @param endTime the end time of the exam
     * @param durationMinutes the duration of the exam in minutes
     * @param venue the venue/location of the exam
     * @param maxCapacity the maximum number of examinees allowed
     * @param currentRegistrations the current number of registrations
     * @param status the status of the exam
     * @param createdBy the user ID who created this exam
     * @param createdAt the timestamp when the exam was created
     * @param updatedAt the timestamp when the exam was last updated
     */
    public Exam(int examId, String examName, String examCode, String description,
                Date examDate, Time startTime, Time endTime, Integer durationMinutes,
                String venue, int maxCapacity, int currentRegistrations, String status,
                int createdBy, Timestamp createdAt, Timestamp updatedAt) {
        this.examId = examId;
        this.examName = examName;
        this.examCode = examCode;
        this.description = description;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.venue = venue;
        this.maxCapacity = maxCapacity;
        this.currentRegistrations = currentRegistrations;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter and Setter methods
    
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
     * Gets the exam name
     * 
     * @return the exam name
     */
    public String getExamName() {
        return examName;
    }
    
    /**
     * Sets the exam name
     * 
     * @param examName the exam name to set
     */
    public void setExamName(String examName) {
        this.examName = examName;
    }
    
    /**
     * Gets the exam code
     * 
     * @return the exam code
     */
    public String getExamCode() {
        return examCode;
    }
    
    /**
     * Sets the exam code
     * 
     * @param examCode the exam code to set
     */
    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }
    
    /**
     * Gets the description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the exam date
     * 
     * @return the exam date
     */
    public Date getExamDate() {
        return examDate;
    }
    
    /**
     * Sets the exam date
     * 
     * @param examDate the exam date to set
     */
    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }
    
    /**
     * Gets the start time
     * 
     * @return the start time
     */
    public Time getStartTime() {
        return startTime;
    }
    
    /**
     * Sets the start time
     * 
     * @param startTime the start time to set
     */
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Gets the end time
     * 
     * @return the end time
     */
    public Time getEndTime() {
        return endTime;
    }
    
    /**
     * Sets the end time
     * 
     * @param endTime the end time to set
     */
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Gets the duration in minutes
     * 
     * @return the duration in minutes
     */
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    /**
     * Sets the duration in minutes
     * 
     * @param durationMinutes the duration in minutes to set
     */
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    /**
     * Gets the venue
     * 
     * @return the venue
     */
    public String getVenue() {
        return venue;
    }
    
    /**
     * Sets the venue
     * 
     * @param venue the venue to set
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }
    
    /**
     * Gets the maximum capacity
     * 
     * @return the maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    /**
     * Sets the maximum capacity
     * 
     * @param maxCapacity the maximum capacity to set
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    /**
     * Gets the current number of registrations
     * 
     * @return the current registrations count
     */
    public int getCurrentRegistrations() {
        return currentRegistrations;
    }
    
    /**
     * Sets the current number of registrations
     * 
     * @param currentRegistrations the current registrations count to set
     */
    public void setCurrentRegistrations(int currentRegistrations) {
        this.currentRegistrations = currentRegistrations;
    }
    
    /**
     * Checks if the exam has available slots
     * 
     * @return true if there are available slots, false otherwise
     */
    public boolean hasAvailableSlots() {
        return currentRegistrations < maxCapacity;
    }
    
    /**
     * Gets the remaining capacity
     * 
     * @return the remaining capacity
     */
    public int getRemainingCapacity() {
        return Math.max(0, maxCapacity - currentRegistrations);
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
    
    /**Screenshot from 2025-11-23 12-16-46
     * Gets the user ID who created this exam
     * 
     * @return the created by user ID
     */
    public int getCreatedBy() {
        return createdBy;
    }
    
    /**
     * Sets the user ID who created this exam
     * 
     * @param createdBy the created by user ID to set
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * Gets the creation timestamp
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the update timestamp
     * 
     * @return the update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the update timestamp
     * 
     * @param updatedAt the update timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Returns a string representation of the Exam object
     * 
     * @return a string containing the exam's key information
     */
    @Override
    public String toString() {
        return "Exam{" +
                "examId=" + examId +
                ", examName='" + examName + '\'' +
                ", examCode='" + examCode + '\'' +
                ", examDate=" + examDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", venue='" + venue + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentRegistrations=" + currentRegistrations +
                ", status='" + status + '\'' +
                '}';
    }
}

