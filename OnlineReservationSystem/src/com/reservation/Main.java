package com.reservation;

import com.reservation.view.LoginView;
import javax.swing.*;

/*
 * Main.java - Bootstrap class for the Rail Booking Platform
 * Sets native OS look-and-feel then opens the sign-in window.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            System.out.println("========================================");
            System.out.println("  Rail Booking Platform v1.0");
            System.out.println("  Launching user interface ...");
            System.out.println("========================================");
            new LoginView().setVisible(true);
        });
    }
}
