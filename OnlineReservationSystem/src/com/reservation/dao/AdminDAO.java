package com.reservation.dao;

import com.reservation.database.DBConnection;
import com.reservation.model.Admin;

import java.sql.*;

/*
 * AdminDAO.java - Persistence operations for administrator accounts
 * Currently supports credential verification only (admins are pre-seeded via SQL).
 */
public class AdminDAO {

    /** Verifies admin login credentials against the admins table. */
    public Admin loginAdmin(String account, String key) {
        String qry = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(qry)) {

            ps.setString(1, account);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
            }
        } catch (SQLException ex) {
            System.err.println("[AdminDAO.loginAdmin] " + ex.getMessage());
        }
        return null;
    }

    /** Looks up an admin by primary key. */
    public Admin getAdminById(int pk) {
        String qry = "SELECT * FROM admins WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(qry)) {

            ps.setInt(1, pk);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAdmin(rs) : null;
            }
        } catch (SQLException ex) {
            System.err.println("[AdminDAO.getAdminById] " + ex.getMessage());
            return null;
        }
    }

    /* Maps a ResultSet row into an Admin object */
    private Admin mapAdmin(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getTimestamp("created_at")
        );
    }
}
