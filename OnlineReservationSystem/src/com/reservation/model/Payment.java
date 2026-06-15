package com.reservation.model;

import java.sql.Timestamp;

/*
 * Payment.java - Tracks financial transaction for each booking
 * One payment record corresponds to exactly one reservation
 */
public class Payment {

    private int txnId;
    private int linkedReservation;
    private double paidAmount;
    private Timestamp txnTimestamp;
    private String payMode;
    private String txnState;

    public Payment() {}

    public Payment(int linkedReservation, double paidAmount, String payMode) {
        this.linkedReservation = linkedReservation;
        this.paidAmount = paidAmount;
        this.payMode = payMode;
        this.txnState = "COMPLETED";
    }

    public Payment(int txnId, int linkedReservation, double paidAmount,
                   Timestamp txnTimestamp, String payMode, String txnState) {
        this.txnId = txnId;
        this.linkedReservation = linkedReservation;
        this.paidAmount = paidAmount;
        this.txnTimestamp = txnTimestamp;
        this.payMode = payMode;
        this.txnState = txnState;
    }

    public int getId() { return txnId; }
    public void setId(int txnId) { this.txnId = txnId; }

    public int getReservationId() { return linkedReservation; }
    public void setReservationId(int linkedReservation) { this.linkedReservation = linkedReservation; }

    public double getAmount() { return paidAmount; }
    public void setAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public Timestamp getPaymentDate() { return txnTimestamp; }
    public void setPaymentDate(Timestamp txnTimestamp) { this.txnTimestamp = txnTimestamp; }

    public String getPaymentMethod() { return payMode; }
    public void setPaymentMethod(String payMode) { this.payMode = payMode; }

    public String getPaymentStatus() { return txnState; }
    public void setPaymentStatus(String txnState) { this.txnState = txnState; }

    @Override
    public String toString() {
        return "Transaction[#" + txnId + ", booking=" + linkedReservation +
               ", ₹" + paidAmount + ", via=" + payMode +
               ", status=" + txnState + "]";
    }
}
