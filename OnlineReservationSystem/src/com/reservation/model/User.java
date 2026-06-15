package com.reservation.model;

import java.sql.Timestamp;

/*
 * User.java - Stores traveler account information
 * Part of the RailBooking reservation platform
 * Developed for academic coursework - June 2026
 */
public class User {

    private int userId;
    private String loginName;
    private String secretKey;
    private String mailAddress;
    private String displayName;
    private String contactNumber;
    private Timestamp registrationDate;

    // Empty initialization
    public User() {}

    // Builder for fresh registration (DB assigns ID and timestamp)
    public User(String loginName, String secretKey, String mailAddress,
                String displayName, String contactNumber) {
        this.loginName = loginName;
        this.secretKey = secretKey;
        this.mailAddress = mailAddress;
        this.displayName = displayName;
        this.contactNumber = contactNumber;
    }

    // Complete initialization with all attributes
    public User(int userId, String loginName, String secretKey, String mailAddress,
                String displayName, String contactNumber, Timestamp registrationDate) {
        this.userId = userId;
        this.loginName = loginName;
        this.secretKey = secretKey;
        this.mailAddress = mailAddress;
        this.displayName = displayName;
        this.contactNumber = contactNumber;
        this.registrationDate = registrationDate;
    }

    /* --- Accessor and Mutator Methods --- */

    public int getId() { return this.userId; }
    public void setId(int userId) { this.userId = userId; }

    public String getUsername() { return this.loginName; }
    public void setUsername(String loginName) { this.loginName = loginName; }

    public String getPassword() { return this.secretKey; }
    public void setPassword(String secretKey) { this.secretKey = secretKey; }

    public String getEmail() { return this.mailAddress; }
    public void setEmail(String mailAddress) { this.mailAddress = mailAddress; }

    public String getFullName() { return this.displayName; }
    public void setFullName(String displayName) { this.displayName = displayName; }

    public String getPhone() { return this.contactNumber; }
    public void setPhone(String contactNumber) { this.contactNumber = contactNumber; }

    public Timestamp getCreatedAt() { return this.registrationDate; }
    public void setCreatedAt(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserAccount[id=").append(userId);
        sb.append(", login=").append(loginName);
        sb.append(", mail=").append(mailAddress);
        sb.append(", name=").append(displayName);
        sb.append(", phone=").append(contactNumber != null ? contactNumber : "N/A");
        sb.append(", since=").append(registrationDate);
        sb.append("]");
        return sb.toString();
    }
}
