package com.reservation.model;

/*
 * Route.java - Defines a travel corridor between two stations
 * Holds distance and base pricing information
 */
public class Route {

    private int routeCode;
    private String departurePoint;
    private String terminusPoint;
    private double corridorLength;
    private double standardPrice;

    public Route() {}

    public Route(int routeCode, String departurePoint, String terminusPoint,
                 double corridorLength, double standardPrice) {
        this.routeCode = routeCode;
        this.departurePoint = departurePoint;
        this.terminusPoint = terminusPoint;
        this.corridorLength = corridorLength;
        this.standardPrice = standardPrice;
    }

    public Route(String departurePoint, String terminusPoint,
                 double corridorLength, double standardPrice) {
        this.departurePoint = departurePoint;
        this.terminusPoint = terminusPoint;
        this.corridorLength = corridorLength;
        this.standardPrice = standardPrice;
    }

    public int getId() { return routeCode; }
    public void setId(int routeCode) { this.routeCode = routeCode; }

    public String getSourceStation() { return departurePoint; }
    public void setSourceStation(String departurePoint) { this.departurePoint = departurePoint; }

    public String getDestinationStation() { return terminusPoint; }
    public void setDestinationStation(String terminusPoint) { this.terminusPoint = terminusPoint; }

    public double getDistanceKm() { return corridorLength; }
    public void setDistanceKm(double corridorLength) { this.corridorLength = corridorLength; }

    public double getBaseFare() { return standardPrice; }
    public void setBaseFare(double standardPrice) { this.standardPrice = standardPrice; }

    @Override
    public String toString() {
        return "TrainRoute[" + departurePoint + " → " + terminusPoint +
               ", " + corridorLength + "km, base₹" + standardPrice + "]";
    }
}
