package com.reservation.dao;

import com.reservation.database.DBConnection;
import com.reservation.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * PaymentDAO.java - Persistence layer for payment transaction records.
 * Each booking produces one corresponding payment entry.
 */
public class PaymentDAO {

    public boolean createPayment(Payment pay) {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_method, payment_status) "
                   + "VALUES (?,?,?,?)";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt   (1, pay.getReservationId());
            ps.setDouble(2, pay.getAmount());
            ps.setString(3, pay.getPaymentMethod());
            ps.setString(4, pay.getPaymentStatus() != null ? pay.getPaymentStatus() : "COMPLETED");
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("[PaymentDAO.createPayment] " + ex.getMessage());
            return false;
        }
    }

    public Payment getPaymentByReservationId(int resId) {
        String sql = "SELECT * FROM payments WHERE reservation_id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, resId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPayment(rs) : null;
            }
        } catch (SQLException ex) {
            System.err.println("[PaymentDAO.getPaymentByReservationId] " + ex.getMessage());
            return null;
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapPayment(rs));

        } catch (SQLException ex) {
            System.err.println("[PaymentDAO.getAllPayments] " + ex.getMessage());
        }
        return list;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("id"),
            rs.getInt("reservation_id"),
            rs.getDouble("amount"),
            rs.getTimestamp("payment_date"),
            rs.getString("payment_method"),
            rs.getString("payment_status")
        );
    }
}
