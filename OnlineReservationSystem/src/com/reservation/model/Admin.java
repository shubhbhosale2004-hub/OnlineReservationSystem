package com.reservation.model;

import java.sql.Timestamp;

/*
 * Admin.java - Holds administrator credentials and metadata
 * Used for privileged system access in the reservation platform
 */
public class Admin {

    private int adminId;
    private String adminLogin;
    private String adminPass;
    private String adminMail;
    private Timestamp joinedOn;

    public Admin() {}

    public Admin(int adminId, String adminLogin, String adminPass,
                 String adminMail, Timestamp joinedOn) {
        this.adminId = adminId;
        this.adminLogin = adminLogin;
        this.adminPass = adminPass;
        this.adminMail = adminMail;
        this.joinedOn = joinedOn;
    }

    public Admin(String adminLogin, String adminPass, String adminMail) {
        this.adminLogin = adminLogin;
        this.adminPass = adminPass;
        this.adminMail = adminMail;
    }

    public int getId() { return adminId; }
    public void setId(int adminId) { this.adminId = adminId; }

    public String getUsername() { return adminLogin; }
    public void setUsername(String adminLogin) { this.adminLogin = adminLogin; }

    public String getPassword() { return adminPass; }
    public void setPassword(String adminPass) { this.adminPass = adminPass; }

    public String getEmail() { return adminMail; }
    public void setEmail(String adminMail) { this.adminMail = adminMail; }

    public Timestamp getCreatedAt() { return joinedOn; }
    public void setCreatedAt(Timestamp joinedOn) { this.joinedOn = joinedOn; }

    @Override
    public String toString() {
        return new StringBuilder("AdminProfile[")
                .append("id=").append(adminId)
                .append(", user=").append(adminLogin)
                .append(", mail=").append(adminMail)
                .append(", joined=").append(joinedOn)
                .append("]").toString();
    }
}
