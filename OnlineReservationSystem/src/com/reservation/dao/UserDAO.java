package com.reservation.dao;

import com.reservation.database.DBConnection;
import com.reservation.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * UserDAO.java - Persistence layer for traveler accounts
 * Handles sign-up, authentication, lookup, and CRUD on the users table.
 * Uses parameterized statements throughout to guard against injection.
 */
public class UserDAO {

    /* ---------- CREATE ---------- */

    public boolean registerUser(User u) {
        String stmt = "INSERT INTO users (username, password, full_name, email, phone) "
                    + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(stmt)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            return ps.executeUpdate() >= 1;

        } catch (SQLException ex) {
            logError("registerUser", ex);
            return false;
        }
    }

    /* ---------- AUTHENTICATION ---------- */

    public User loginUser(String name, String pass) {
        String stmt = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(stmt)) {

            ps.setString(1, name);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? buildUser(rs) : null;
            }
        } catch (SQLException ex) {
            logError("loginUser", ex);
            return null;
        }
    }

    /* ---------- SINGLE-ROW QUERIES ---------- */

    public User getUserById(int pk) {
        return fetchOne("SELECT * FROM users WHERE id = ?", pk);
    }

    public User getUserByUsername(String name) {
        return fetchOneStr("SELECT * FROM users WHERE username = ?", name);
    }

    /* ---------- EXISTENCE CHECKS ---------- */

    public boolean isUsernameExists(String name) {
        return countWhere("SELECT COUNT(*) FROM users WHERE username = ?", name) > 0;
    }

    public boolean isEmailExists(String mail) {
        return countWhere("SELECT COUNT(*) FROM users WHERE email = ?", mail) > 0;
    }

    /* ---------- COLLECTION QUERY ---------- */

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String stmt = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(stmt);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(buildUser(rs));

        } catch (SQLException ex) {
            logError("getAllUsers", ex);
        }
        return list;
    }

    /* ---------- UPDATE ---------- */

    public boolean updateUser(User u) {
        String stmt = "UPDATE users SET username=?, password=?, full_name=?, "
                    + "email=?, phone=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(stmt)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            ps.setInt(6, u.getId());
            return ps.executeUpdate() >= 1;

        } catch (SQLException ex) {
            logError("updateUser", ex);
            return false;
        }
    }

    /* ---------- DELETE ---------- */

    public boolean deleteUser(int pk) {
        String stmt = "DELETE FROM users WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(stmt)) {

            ps.setInt(1, pk);
            return ps.executeUpdate() >= 1;

        } catch (SQLException ex) {
            logError("deleteUser", ex);
            return false;
        }
    }

    /* ---------- INTERNAL HELPERS ---------- */

    private User fetchOne(String sql, int param) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? buildUser(rs) : null;
            }
        } catch (SQLException ex) {
            logError("fetchOne(int)", ex);
            return null;
        }
    }

    private User fetchOneStr(String sql, String param) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? buildUser(rs) : null;
            }
        } catch (SQLException ex) {
            logError("fetchOneStr", ex);
            return null;
        }
    }

    private int countWhere(String sql, String param) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            logError("countWhere", ex);
            return 0;
        }
    }

    private User buildUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    private void logError(String method, SQLException ex) {
        System.err.println("[UserDAO." + method + "] " + ex.getMessage());
    }
}
