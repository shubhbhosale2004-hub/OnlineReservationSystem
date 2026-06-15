package com.reservation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * DBConnection.java - Manages the JDBC link to MySQL
 * Implements a lazy-init singleton so one connection is reused
 * throughout the application lifecycle.
 *
 * CONFIGURATION: Update JDBC_URL, DB_ACCOUNT, and DB_SECRET
 * to match your local MySQL installation before running.
 */
public class DBConnection {

    // JDBC endpoint for the reservation schema
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/online_reservation_system";

    // MySQL credentials — change these to suit your setup
    private static final String DB_ACCOUNT = "root";
    private static final String DB_SECRET  = "";

    // Shared connection handle
    private static Connection sharedLink = null;

    // Prevent external construction
    private DBConnection() {}

    /**
     * Provides the active database connection, creating one when necessary.
     * The JDBC driver class is loaded explicitly for older JDK compatibility.
     */
    public static Connection getConnection() {
        try {
            if (sharedLink == null || sharedLink.isClosed()) {
                // Explicitly register the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                sharedLink = DriverManager.getConnection(
                        JDBC_URL, DB_ACCOUNT, DB_SECRET);

                System.out.println("[DBConnection] Link to MySQL established.");
            }
        } catch (ClassNotFoundException cnf) {
            System.err.println("[DBConnection] Driver JAR missing from classpath!");
            System.err.println("  → Place mysql-connector-java-x.x.jar in the lib/ folder.");
            cnf.printStackTrace();
        } catch (SQLException sqle) {
            System.err.println("[DBConnection] Could not reach MySQL at " + JDBC_URL);
            System.err.println("  → Verify the server is running and credentials are valid.");
            sqle.printStackTrace();
        }
        return sharedLink;
    }

    /** Gracefully shuts down the shared connection. */
    public static void closeConnection() {
        try {
            if (sharedLink != null && !sharedLink.isClosed()) {
                sharedLink.close();
                sharedLink = null;
                System.out.println("[DBConnection] Link closed.");
            }
        } catch (SQLException sqle) {
            System.err.println("[DBConnection] Problem while closing link.");
            sqle.printStackTrace();
        }
    }

    /** Quick connectivity check — returns true when a live link exists. */
    public static boolean testConnection() {
        return getConnection() != null;
    }
}
