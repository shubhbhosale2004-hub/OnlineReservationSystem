package com.reservation.util;

import java.util.Date;
import java.util.regex.Pattern;

/*
 * ValidationUtil.java - Centralized input-checking helpers
 * Every public method is static so callers skip instantiation.
 */
public class ValidationUtil {

    // Compiled patterns for repeated matching
    private static final Pattern MAIL_RE =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern LOGIN_RE =
            Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    private static final Pattern DIGITS_10 =
            Pattern.compile("^\\d{10}$");

    /* --- field-level checks --- */

    public static boolean isValidUsername(String val) {
        return val != null && LOGIN_RE.matcher(val.trim()).matches();
    }

    public static boolean isValidPassword(String val) {
        return val != null && val.length() >= 6;
    }

    public static boolean isValidEmail(String val) {
        return val != null && MAIL_RE.matcher(val.trim()).matches();
    }

    public static boolean isValidPhone(String val) {
        if (isNullOrEmpty(val)) return true;   // phone is optional
        return DIGITS_10.matcher(val.trim()).matches();
    }

    public static boolean isValidAge(int val) {
        return val > 0 && val <= 120;
    }

    public static boolean isValidName(String val) {
        if (isNullOrEmpty(val)) return false;
        int len = val.trim().length();
        return len >= 2 && len <= 100;
    }

    public static boolean isNullOrEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }

    @SuppressWarnings("deprecation")
    public static boolean isValidDate(Date val) {
        if (val == null) return false;
        // strip time portion from today for date-only comparison
        Date now = new Date();
        now.setHours(0); now.setMinutes(0); now.setSeconds(0);
        return !val.before(now);
    }

    /* --- password strength meter --- */

    public static String getPasswordStrength(String pwd) {
        if (pwd == null || pwd.length() < 6) return "Weak";

        int traits = 0;
        for (char ch : pwd.toCharArray()) {
            if (Character.isUpperCase(ch))      { traits |= 1; }
            else if (Character.isLowerCase(ch))  { traits |= 2; }
            else if (Character.isDigit(ch))      { traits |= 4; }
            else                                 { traits |= 8; }
        }

        int bitCount = Integer.bitCount(traits);
        boolean longEnough = pwd.length() >= 8;

        if (bitCount >= 3 && longEnough) return "Strong";
        if (bitCount >= 2)               return "Medium";
        return "Weak";
    }
}
