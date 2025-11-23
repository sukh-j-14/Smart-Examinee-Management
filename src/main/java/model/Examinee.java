package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Examinee Model Class
 * 
 * Represents an examinee (student) entity in the Smart Examinee Management System.
 * Stores personal and contact information for examinees who register for exams.
 * 
 * Maps to the 'examinees' table in the database.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class Examinee {
    
    // Private fields matching database columns
    private int examineeId;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default no-argument constructor
     * Creates an empty Examinee object
     */
    public Examinee() {
        // Empty constructor for object creation
    }
    
    /**
     * Full-argument constructor
     * Creates an Examinee object with all fields initialized
     * 
     * @param examineeId the unique identifier for the examinee
     * @param registrationNumber the unique registration number
     * @param firstName the first name of the examinee
     * @param lastName the last name of the examinee
     * @param email the examinee's email address
     * @param phone the examinee's phone number
     * @param dateOfBirth the date of birth
     * @param address the address of the examinee
     * @param city the city
     * @param state the state
     * @param pincode the pincode/postal code
     * @param createdAt the timestamp when the examinee record was created
     * @param updatedAt the timestamp when the examinee record was last updated
     */
    public Examinee(int examineeId, String registrationNumber, String firstName, 
                    String lastName, String email, String phone, Date dateOfBirth,
                    String address, String city, String state, String pincode,
                    Timestamp createdAt, Timestamp updatedAt) {
        this.examineeId = examineeId;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter and Setter methods
    
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
     * Gets the registration number
     * 
     * @return the registration number
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    /**
     * Sets the registration number
     * 
     * @param registrationNumber the registration number to set
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    /**
     * Gets the first name
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the full name (first name + last name)
     * 
     * @return the full name
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + 
               (lastName != null ? " " + lastName : "").trim();
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
     * Gets the date of birth
     * 
     * @return the date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Sets the date of birth
     * 
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    /**
     * Gets the address
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the address
     * 
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Gets the city
     * 
     * @return the city
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Sets the city
     * 
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     * Gets the state
     * 
     * @return the state
     */
    public String getState() {
        return state;
    }
    
    /**
     * Sets the state
     * 
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     * Gets the pincode
     * 
     * @return the pincode
     */
    public String getPincode() {
        return pincode;
    }
    
    /**
     * Sets the pincode
     * 
     * @param pincode the pincode to set
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
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
     * Returns a string representation of the Examinee object
     * 
     * @return a string containing the examinee's key information
     */
    @Override
    public String toString() {
        return "Examinee{" +
                "examineeId=" + examineeId +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", pincode='" + pincode + '\'' +
                '}';
    }
}

