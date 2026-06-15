package com.reservation.controller;

import com.reservation.dao.PaymentDAO;
import com.reservation.dao.ReservationDAO;
import com.reservation.dao.RouteDAO;
import com.reservation.model.Payment;
import com.reservation.model.Reservation;
import com.reservation.model.Route;
import com.reservation.util.FareCalculator;
import com.reservation.util.IDGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * ReservationController.java - Central coordinator for booking workflows
 * Bridges the view layer with DAOs, fare engine, and ID generation.
 */
public class ReservationController {

    // Per-class seat limits (seats available per route per date)
    private static final int CAP_SLEEPER  = 72;
    private static final int CAP_AC3      = 64;
    private static final int CAP_AC2      = 48;
    private static final int CAP_AC1      = 24;

    private final ReservationDAO bookingRepo = new ReservationDAO();
    private final RouteDAO       routeRepo   = new RouteDAO();
    private final PaymentDAO     payRepo     = new PaymentDAO();

    /* ---- booking creation ---- */

    public String createReservation(int userId, int routeId, Date travelDate,
                                    String paxName, int paxAge,
                                    String paxGender, String berth) {
        try {
            Route corridor = routeRepo.getRouteById(routeId);
            if (corridor == null) {
                System.err.println("[BookingCtrl] No corridor found for id " + routeId);
                return null;
            }

            double price = FareCalculator.calculateFare(corridor.getBaseFare(), berth);
            String code  = IDGenerator.generateReservationId();

            Reservation bk = new Reservation(
                    code, userId, routeId,
                    new java.sql.Date(travelDate.getTime()),
                    paxName, paxAge, paxGender, berth, price);

            if (!bookingRepo.createReservation(bk)) {
                System.err.println("[BookingCtrl] Insert into reservations table failed.");
                return null;
            }

            // Record the corresponding payment
            Reservation saved = bookingRepo.getReservationById(code);
            if (saved != null) {
                Payment txn = new Payment(saved.getId(), price, "ONLINE");
                payRepo.createPayment(txn);
            }

            return code;

        } catch (Exception ex) {
            System.err.println("[BookingCtrl] createReservation error: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /* ---- single-record retrieval ---- */

    public Reservation getReservation(String code) {
        try { return bookingRepo.getReservationById(code); }
        catch (Exception ex) { ex.printStackTrace(); return null; }
    }

    /* ---- list queries ---- */

    public List<Reservation> getUserReservations(int uid) {
        try {
            List<Reservation> data = bookingRepo.getReservationsByUserId(uid);
            return data != null ? data : Collections.emptyList();
        } catch (Exception ex) { ex.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Reservation> getAllReservations() {
        try {
            List<Reservation> data = bookingRepo.getAllReservations();
            return data != null ? data : Collections.emptyList();
        } catch (Exception ex) { ex.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Reservation> searchReservations(String term) {
        try {
            List<Reservation> data = bookingRepo.searchReservations(term);
            return data != null ? data : Collections.emptyList();
        } catch (Exception ex) { ex.printStackTrace(); return new ArrayList<>(); }
    }

    /* ---- status changes ---- */

    public boolean cancelReservation(String code) {
        try { return bookingRepo.cancelReservation(code); }
        catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public boolean deleteReservation(String code) {
        try { return bookingRepo.deleteReservation(code); }
        catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    public boolean updateReservation(Reservation bk) {
        try { return bookingRepo.updateReservation(bk); }
        catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    /* ---- availability ---- */

    public boolean checkSeatAvailability(int routeId, Date when, String berth) {
        try {
            int cap = seatCapacity(berth);
            if (cap <= 0) return false;
            int taken = bookingRepo.getBookedSeatsCount(
                    routeId, new java.sql.Date(when.getTime()), berth);
            return taken < cap;
        } catch (Exception ex) { ex.printStackTrace(); return false; }
    }

    private int seatCapacity(String berth) {
        if (berth == null) return -1;
        switch (berth.toUpperCase()) {
            case "SLEEPER":  return CAP_SLEEPER;
            case "AC_3TIER": return CAP_AC3;
            case "AC_2TIER": return CAP_AC2;
            case "AC_FIRST": return CAP_AC1;
            default:         return -1;
        }
    }

    /* ---- route helpers ---- */

    public List<Route>  getAllRoutes()                          { return safe(routeRepo.getAllRoutes()); }
    public List<String> getAllSourceStations()                  { return safe(routeRepo.getAllSourceStations()); }
    public List<String> getDestinationStations(String origin)  { return safe(routeRepo.getDestinationStations(origin)); }
    public Route        getRouteByStations(String a, String b) { return routeRepo.getRouteByStations(a, b); }

    /* ---- pricing ---- */

    public double calculateFare(double base, String berth) {
        return FareCalculator.calculateFare(base, berth);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> safe(List<T> src) {
        return src != null ? src : Collections.emptyList();
    }
}
