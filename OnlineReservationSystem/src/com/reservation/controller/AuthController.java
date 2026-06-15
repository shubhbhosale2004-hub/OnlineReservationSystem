package com.reservation.controller;

import com.reservation.dao.AdminDAO;
import com.reservation.dao.UserDAO;
import com.reservation.model.Admin;
import com.reservation.model.User;
import com.reservation.util.ValidationUtil;

/*
 * AuthController.java - Mediates sign-up, sign-in, and session
 * logic for both traveler and administrator roles.
 */
public class AuthController {

    private final UserDAO userRepo   = new UserDAO();
    private final AdminDAO adminRepo = new AdminDAO();

    private User   activeUser;
    private Admin  activeAdmin;
    private String lastError;

    /* ========== REGISTRATION ========== */

    public boolean registerUser(String login, String pass, String mail,
                                String name, String phone) {
        // Field-level validations
        if (!ValidationUtil.isValidUsername(login)) {
            lastError = "Username must be 3–20 characters (letters, digits, underscore).";
            return false;
        }
        if (!ValidationUtil.isValidPassword(pass)) {
            lastError = "Password needs at least 6 characters.";
            return false;
        }
        if (!ValidationUtil.isValidEmail(mail)) {
            lastError = "The e-mail address format is not valid.";
            return false;
        }
        if (!ValidationUtil.isValidName(name)) {
            lastError = "Name should be between 2 and 100 characters.";
            return false;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            lastError = "Phone number must contain exactly 10 digits.";
            return false;
        }

        // Uniqueness checks
        try {
            if (userRepo.isUsernameExists(login)) {
                lastError = "'" + login + "' is already in use — pick another username.";
                return false;
            }
            if (userRepo.isEmailExists(mail)) {
                lastError = "An account with e-mail '" + mail + "' already exists.";
                return false;
            }

            User fresh = new User(login, pass, mail, name, phone);
            boolean ok = userRepo.registerUser(fresh);
            if (!ok) lastError = "Account creation failed — please retry later.";
            return ok;

        } catch (Exception ex) {
            lastError = "System error during registration: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }

    /* ========== USER LOGIN ========== */

    public User loginUser(String login, String pass) {
        if (ValidationUtil.isNullOrEmpty(login)) { lastError = "Enter your username."; return null; }
        if (ValidationUtil.isNullOrEmpty(pass))  { lastError = "Enter your password."; return null; }

        try {
            User u = userRepo.loginUser(login, pass);
            if (u != null) {
                activeUser = u;
                activeAdmin = null;
                lastError = null;
            } else {
                lastError = "Credentials did not match any account.";
            }
            return u;
        } catch (Exception ex) {
            lastError = "Login error: " + ex.getMessage();
            ex.printStackTrace();
            return null;
        }
    }

    /* ========== ADMIN LOGIN ========== */

    public Admin loginAdmin(String login, String pass) {
        if (ValidationUtil.isNullOrEmpty(login)) { lastError = "Admin username is required."; return null; }
        if (ValidationUtil.isNullOrEmpty(pass))  { lastError = "Admin password is required."; return null; }

        try {
            Admin a = adminRepo.loginAdmin(login, pass);
            if (a != null) {
                activeAdmin = a;
                activeUser = null;
                lastError = null;
            } else {
                lastError = "Administrator credentials are incorrect.";
            }
            return a;
        } catch (Exception ex) {
            lastError = "Admin login error: " + ex.getMessage();
            ex.printStackTrace();
            return null;
        }
    }

    /* ========== SESSION ========== */

    public void logout() {
        activeUser = null;
        activeAdmin = null;
        lastError = null;
    }

    public User  getCurrentUser()   { return activeUser; }
    public Admin getCurrentAdmin()  { return activeAdmin; }
    public boolean isUserLoggedIn() { return activeUser  != null; }
    public boolean isAdminLoggedIn(){ return activeAdmin != null; }
    public String getValidationError() { return lastError; }
}
