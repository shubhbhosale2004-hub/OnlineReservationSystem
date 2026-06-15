package com.reservation.controller;

import com.reservation.dao.PaymentDAO;
import com.reservation.dao.ReservationDAO;
import com.reservation.dao.UserDAO;
import com.reservation.model.Reservation;
import com.reservation.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * AdminController.java - Privileged operations for system administrators
 * Provides data retrieval, record management, and aggregate statistics.
 */
public class AdminController {

    private final ReservationDAO bookingRepo = new ReservationDAO();
    private final UserDAO        userRepo    = new UserDAO();
    private final PaymentDAO     payRepo     = new PaymentDAO();

    /* -- Data retrieval -- */

    public List<Reservation> getAllReservations() {
        return guardList(bookingRepo.getAllReservations());
    }

    public List<User> getAllUsers() {
        return guardList(userRepo.getAllUsers());
    }

    public List<Reservation> searchReservations(String term) {
        return guardList(bookingRepo.searchReservations(term));
    }

    /* -- Record removal -- */

    public boolean deleteReservation(String code) {
        try { return bookingRepo.deleteReservation(code); }
        catch (Exception ex) {
            System.err.println("[AdminCtrl] Remove booking error: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int pk) {
        try { return userRepo.deleteUser(pk); }
        catch (Exception ex) {
            System.err.println("[AdminCtrl] Remove user error: " + ex.getMessage());
            return false;
        }
    }

    /* -- Dashboard statistics -- */

    public Map<String, Object> getReservationStats() {
        Map<String, Object> metrics = new HashMap<>();
        try {
            List<Reservation> all = bookingRepo.getAllReservations();
            if (all == null) all = new ArrayList<>();

            int active = 0, dropped = 0;
            double revenue = 0.0;

            for (Reservation bk : all) {
                String st = bk.getStatus();
                if (st == null) continue;
                if (st.equalsIgnoreCase("CONFIRMED")) {
                    active++;
                    revenue += bk.getFare();
                } else if (st.equalsIgnoreCase("CANCELLED")) {
                    dropped++;
                }
            }

            metrics.put("totalReservations",     all.size());
            metrics.put("confirmedReservations", active);
            metrics.put("cancelledReservations", dropped);
            metrics.put("totalRevenue",          revenue);

        } catch (Exception ex) {
            System.err.println("[AdminCtrl] Stats computation error: " + ex.getMessage());
            metrics.put("totalReservations",     0);
            metrics.put("confirmedReservations", 0);
            metrics.put("cancelledReservations", 0);
            metrics.put("totalRevenue",          0.0);
        }
        return metrics;
    }

    /* -- helper -- */

    private <T> List<T> guardList(List<T> src) {
        return src != null ? src : new ArrayList<>();
    }
}
