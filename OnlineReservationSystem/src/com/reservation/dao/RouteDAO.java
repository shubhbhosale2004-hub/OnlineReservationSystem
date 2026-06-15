package com.reservation.dao;

import com.reservation.database.DBConnection;
import com.reservation.model.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * RouteDAO.java - Data layer for the routes table
 * Supports corridor lookup, station listing, and CRUD.
 */
public class RouteDAO {

    public List<String> getAllSourceStations() {
        List<String> stations = new ArrayList<>();
        String sql = "SELECT DISTINCT source_station FROM routes ORDER BY source_station";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) stations.add(rs.getString("source_station"));

        } catch (SQLException ex) {
            System.err.println("[RouteDAO.getAllSourceStations] " + ex.getMessage());
        }
        return stations;
    }

    public List<String> getDestinationStations(String origin) {
        List<String> stations = new ArrayList<>();
        String sql = "SELECT DISTINCT destination_station FROM routes "
                   + "WHERE source_station = ? ORDER BY destination_station";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, origin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) stations.add(rs.getString("destination_station"));
            }
        } catch (SQLException ex) {
            System.err.println("[RouteDAO.getDestinationStations] " + ex.getMessage());
        }
        return stations;
    }

    public Route getRouteByStations(String origin, String terminus) {
        String sql = "SELECT * FROM routes WHERE source_station=? AND destination_station=?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, origin);
            ps.setString(2, terminus);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRoute(rs) : null;
            }
        } catch (SQLException ex) {
            System.err.println("[RouteDAO.getRouteByStations] " + ex.getMessage());
            return null;
        }
    }

    public Route getRouteById(int pk) {
        String sql = "SELECT * FROM routes WHERE id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, pk);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRoute(rs) : null;
            }
        } catch (SQLException ex) {
            System.err.println("[RouteDAO.getRouteById] " + ex.getMessage());
            return null;
        }
    }

    public List<Route> getAllRoutes() {
        List<Route> list = new ArrayList<>();
        String sql = "SELECT * FROM routes ORDER BY source_station";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRoute(rs));

        } catch (SQLException ex) {
            System.err.println("[RouteDAO.getAllRoutes] " + ex.getMessage());
        }
        return list;
    }

    private Route mapRoute(ResultSet rs) throws SQLException {
        return new Route(
            rs.getInt("id"),
            rs.getString("source_station"),
            rs.getString("destination_station"),
            rs.getDouble("distance_km"),
            rs.getDouble("base_fare")
        );
    }
}
