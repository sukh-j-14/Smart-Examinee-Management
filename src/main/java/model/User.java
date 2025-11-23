package model;

import java.sql.Timestamp;

/**
 * User Model Class
 * 
 * Represents a user entity in the Smart Examinee Management System.
 * Users can be either 'admin' or 'staff' with role-based access control.
 * 
 * Maps to the 'users' table in the database.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class User {
    
    // Private fields matching database columns
    private int userId;
    private String username;
    private String password;
    private String role;  // 'admin' or 'staff'
    private String fullName;
    private String email;
    private String phone;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    
    /**
     * Default no-argument constructor
     * Creates an empty User object
     */
    public User() {
        // Empty constructor for object creation
    }
    
    /**
     * Full-argument constructor
     * Creates a User object with all fields initialized
     * 
     * @param userId the unique identifier for the user
     * @param username the username for login
     * @param password the user's password
     * @param role the user's role ('admin' or 'staff')
     * @param fullName the full name of the user
     * @param email the user's email address
     * @param phone the user's phone number
     * @param createdAt the timestamp when the user was created
     * @param lastLogin the timestamp of the user's last login
     * @param isActive whether the user account is active
     */
    public User(int userId, String username, String password, String role, 
                String fullName, String email, String phone, 
                Timestamp createdAt, Timestamp lastLogin, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }
    
    // Getter and Setter methods
    
    /**
     * Gets the user ID
     * 
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID
     * 
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the username
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gets the user role
     * 
     * @return the role ('admin' or 'staff')
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user role
     * 
     * @param role the role to set ('admin' or 'staff')
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the full name
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Sets the full name
     * 
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    /**
     * Gets the email address
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the phone number
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the phone number
     * 
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
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
     * Gets the last login timestamp
     * 
     * @return the last login timestamp
     */
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets the last login timestamp
     * 
     * @param lastLogin the last login timestamp to set
     */
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Checks if the user is active
     * 
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Sets the active status
     * 
     * @param isActive the active status to set
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Returns a string representation of the User object
     * 
     * @return a string containing the user's key information
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}

