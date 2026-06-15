package com.reservation.model;

import java.sql.Date;
import java.sql.Timestamp;

/*
 * Reservation.java - Captures all booking details for a journey
 * Includes transient route info populated via JOIN queries
 */
public class Reservation {

    private int recordId;
    private String bookingCode;
    private int travelerId;
    private int pathId;
    private Date travelDate;
    private String travelerName;
    private int travelerAge;
    private String travelerGender;
    private String berthCategory;
    private double ticketPrice;
    private String bookingState;
    private Timestamp bookedOn;

    // Transient fields filled from route JOIN
    private String originCity;
    private String arrivalCity;

    public Reservation() {}

    public Reservation(String bookingCode, int travelerId, int pathId, Date travelDate,
                       String travelerName, int travelerAge, String travelerGender,
                       String berthCategory, double ticketPrice) {
        this.bookingCode = bookingCode;
        this.travelerId = travelerId;
        this.pathId = pathId;
        this.travelDate = travelDate;
        this.travelerName = travelerName;
        this.travelerAge = travelerAge;
        this.travelerGender = travelerGender;
        this.berthCategory = berthCategory;
        this.ticketPrice = ticketPrice;
        this.bookingState = "CONFIRMED";
    }

    public Reservation(int recordId, String bookingCode, int travelerId, int pathId,
                       Date travelDate, String travelerName, int travelerAge,
                       String travelerGender, String berthCategory, double ticketPrice,
                       String bookingState, Timestamp bookedOn) {
        this.recordId = recordId;
        this.bookingCode = bookingCode;
        this.travelerId = travelerId;
        this.pathId = pathId;
        this.travelDate = travelDate;
        this.travelerName = travelerName;
        this.travelerAge = travelerAge;
        this.travelerGender = travelerGender;
        this.berthCategory = berthCategory;
        this.ticketPrice = ticketPrice;
        this.bookingState = bookingState;
        this.bookedOn = bookedOn;
    }

    // Accessors & Mutators
    public int getId() { return recordId; }
    public void setId(int recordId) { this.recordId = recordId; }

    public String getReservationId() { return bookingCode; }
    public void setReservationId(String bookingCode) { this.bookingCode = bookingCode; }

    public int getUserId() { return travelerId; }
    public void setUserId(int travelerId) { this.travelerId = travelerId; }

    public int getRouteId() { return pathId; }
    public void setRouteId(int pathId) { this.pathId = pathId; }

    public Date getJourneyDate() { return travelDate; }
    public void setJourneyDate(Date travelDate) { this.travelDate = travelDate; }

    public String getPassengerName() { return travelerName; }
    public void setPassengerName(String travelerName) { this.travelerName = travelerName; }

    public int getAge() { return travelerAge; }
    public void setAge(int travelerAge) { this.travelerAge = travelerAge; }

    public String getGender() { return travelerGender; }
    public void setGender(String travelerGender) { this.travelerGender = travelerGender; }

    public String getSeatType() { return berthCategory; }
    public void setSeatType(String berthCategory) { this.berthCategory = berthCategory; }

    public double getFare() { return ticketPrice; }
    public void setFare(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getStatus() { return bookingState; }
    public void setStatus(String bookingState) { this.bookingState = bookingState; }

    public Timestamp getCreatedAt() { return bookedOn; }
    public void setCreatedAt(Timestamp bookedOn) { this.bookedOn = bookedOn; }

    public String getSourceStation() { return originCity; }
    public void setSourceStation(String originCity) { this.originCity = originCity; }

    public String getDestinationStation() { return arrivalCity; }
    public void setDestinationStation(String arrivalCity) { this.arrivalCity = arrivalCity; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BookingRecord{");
        sb.append("code='").append(bookingCode).append("'");
        sb.append(", route=").append(originCity).append("->").append(arrivalCity);
        sb.append(", date=").append(travelDate);
        sb.append(", pax=").append(travelerName);
        sb.append(", berth=").append(berthCategory);
        sb.append(", amt=").append(ticketPrice);
        sb.append(", state=").append(bookingState);
        sb.append("}");
        return sb.toString();
    }
}
