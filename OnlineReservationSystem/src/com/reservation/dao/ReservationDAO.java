package com.reservation.dao;

import com.reservation.database.DBConnection;
import com.reservation.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * ReservationDAO.java - Data-access layer for the reservations table
 * Every query that returns booking objects joins routes to fill
 * origin / terminus city names into transient model fields.
 */
public class ReservationDAO {

    // Reusable SELECT fragment that pulls route station names via a join
    private static final String BASE_QUERY =
            "SELECT r.*, rt.source_station, rt.destination_station " +
            "FROM reservations r JOIN routes rt ON r.route_id = rt.id";

    /* ========== INSERT ========== */

    public boolean createReservation(Reservation bk) {
        String sql = "INSERT INTO reservations "
                   + "(reservation_id, user_id, route_id, journey_date, "
                   + " passenger_name, age, gender, seat_type, fare, status) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, bk.getReservationId());
            ps.setInt   (2, bk.getUserId());
            ps.setInt   (3, bk.getRouteId());
            ps.setDate  (4, bk.getJourneyDate());
            ps.setString(5, bk.getPassengerName());
            ps.setInt   (6, bk.getAge());
            ps.setString(7, bk.getGender());
            ps.setString(8, bk.getSeatType());
            ps.setDouble(9, bk.getFare());
            ps.setString(10, bk.getStatus());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            err("createReservation", ex);
            return false;
        }
    }

    /* ========== SINGLE-ROW QUERIES ========== */

    public Reservation getReservationById(String code) {
        String sql = BASE_QUERY + " WHERE r.reservation_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            err("getReservationById", ex);
            return null;
        }
    }

    /* ========== LIST QUERIES ========== */

    public List<Reservation> getReservationsByUserId(int uid) {
        return fetchList(BASE_QUERY + " WHERE r.user_id = ? ORDER BY r.created_at DESC", uid);
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(BASE_QUERY + " ORDER BY r.created_at DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException ex) {
            err("getAllReservations", ex);
        }
        return list;
    }

    public List<Reservation> searchReservations(String keyword) {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY
                   + " WHERE r.reservation_id LIKE ? OR r.passenger_name LIKE ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            err("searchReservations", ex);
        }
        return list;
    }

    /* ========== UPDATE / CANCEL ========== */

    public boolean updateReservation(Reservation bk) {
        String sql = "UPDATE reservations SET "
                   + "passenger_name=?, journey_date=?, seat_type=?, "
                   + "age=?, gender=?, fare=?, status=? "
                   + "WHERE reservation_id=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, bk.getPassengerName());
            ps.setDate  (2, bk.getJourneyDate());
            ps.setString(3, bk.getSeatType());
            ps.setInt   (4, bk.getAge());
            ps.setString(5, bk.getGender());
            ps.setDouble(6, bk.getFare());
            ps.setString(7, bk.getStatus());
            ps.setString(8, bk.getReservationId());
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            err("updateReservation", ex);
            return false;
        }
    }

    public boolean cancelReservation(String code) {
        return execSingle("UPDATE reservations SET status='CANCELLED' WHERE reservation_id=?", code);
    }

    public boolean deleteReservation(String code) {
        return execSingle("DELETE FROM reservations WHERE reservation_id=?", code);
    }

    /* ========== AVAILABILITY ========== */

    public int getBookedSeatsCount(int routeId, Date when, String berth) {
        String sql = "SELECT COUNT(*) FROM reservations "
                   + "WHERE route_id=? AND journey_date=? AND seat_type=? AND status='CONFIRMED'";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt   (1, routeId);
            ps.setDate  (2, when);
            ps.setString(3, berth);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            err("getBookedSeatsCount", ex);
            return 0;
        }
    }

    /* ========== INTERNAL HELPERS ========== */

    private List<Reservation> fetchList(String sql, int intParam) {
        List<Reservation> list = new ArrayList<>();
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, intParam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            err("fetchList", ex);
        }
        return list;
    }

    private boolean execSingle(String sql, String param) {
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, param);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            err("execSingle", ex);
            return false;
        }
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation bk = new Reservation();
        bk.setId(rs.getInt("id"));
        bk.setReservationId(rs.getString("reservation_id"));
        bk.setUserId(rs.getInt("user_id"));
        bk.setRouteId(rs.getInt("route_id"));
        bk.setJourneyDate(rs.getDate("journey_date"));
        bk.setPassengerName(rs.getString("passenger_name"));
        bk.setAge(rs.getInt("age"));
        bk.setGender(rs.getString("gender"));
        bk.setSeatType(rs.getString("seat_type"));
        bk.setFare(rs.getDouble("fare"));
        bk.setStatus(rs.getString("status"));
        bk.setCreatedAt(rs.getTimestamp("created_at"));
        bk.setSourceStation(rs.getString("source_station"));
        bk.setDestinationStation(rs.getString("destination_station"));
        return bk;
    }

    private void err(String ctx, SQLException ex) {
        System.err.println("[ReservationDAO." + ctx + "] " + ex.getMessage());
    }
}
