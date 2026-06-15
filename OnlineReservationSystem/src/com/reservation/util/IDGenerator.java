package com.reservation.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/*
 * IDGenerator.java - Produces unique alphanumeric identifiers
 * for bookings and payment receipts.
 *
 * Format examples:
 *   Reservation → RES-20260615-4827
 *   Payment     → PAY-20260615-3159
 */
public class IDGenerator {

    private static final SimpleDateFormat STAMP_FMT =
            new SimpleDateFormat("yyyyMMdd");

    private static final Random RNG = new Random();

    /** Builds a reservation identifier: RES-YYYYMMDD-NNNN */
    public static String generateReservationId() {
        String todayStamp = STAMP_FMT.format(new Date());
        int suffix = 1000 + RNG.nextInt(9000);
        return "RES-" + todayStamp + "-" + suffix;
    }

    /** Builds a payment identifier: PAY-YYYYMMDD-NNNN */
    public static String generatePaymentId() {
        String todayStamp = STAMP_FMT.format(new Date());
        int suffix = 1000 + RNG.nextInt(9000);
        return "PAY-" + todayStamp + "-" + suffix;
    }
}
