package com.reservation.util;

import com.reservation.model.Reservation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * PDFExporter.java - Generates formatted plain-text reports
 * (avoids third-party PDF library dependencies).
 * Output files use aligned columns and box-drawing separators.
 */
public class PDFExporter {

    private static final SimpleDateFormat TS_FMT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("dd/MM/yyyy");

    private static final String THICK_LINE = "=".repeat(115);
    private static final String THIN_LINE  = "-".repeat(115);

    /* centre-pads a string within the given width */
    private static String centred(String txt, int w) {
        int pad = Math.max(0, (w - txt.length()) / 2);
        return " ".repeat(pad) + txt;
    }

    /* truncates long strings for table columns */
    private static String clip(String txt, int max) {
        if (txt == null) return "N/A";
        return txt.length() > max ? txt.substring(0, max - 2) + ".." : txt;
    }

    /**
     * Writes a multi-reservation report to the given file path.
     * Includes a tabular listing plus summary statistics.
     */
    public static boolean exportReservationReport(
            List<Reservation> bookings, String outputPath) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            // Report header
            bw.write(THICK_LINE); bw.newLine();
            bw.write(centred("RAIL BOOKING PLATFORM", 115)); bw.newLine();
            bw.write(centred("Reservation Listing Report", 115)); bw.newLine();
            bw.write(THICK_LINE); bw.newLine();
            bw.write("Generated : " + TS_FMT.format(new Date())); bw.newLine();
            bw.write("Records   : " + bookings.size()); bw.newLine();
            bw.write(THIN_LINE); bw.newLine(); bw.newLine();

            // Column headings
            bw.write(String.format("%-17s %-14s %-14s %-11s %-16s %-4s %-7s %-11s %-9s %-9s",
                    "Booking ID", "Origin", "Terminus", "Travel Date",
                    "Passenger", "Age", "Gender", "Berth", "Price", "State"));
            bw.newLine();
            bw.write(THIN_LINE); bw.newLine();

            double revenueTotal = 0;
            int okCount = 0, cancelCount = 0;

            for (Reservation bk : bookings) {
                String dt = bk.getJourneyDate() != null
                        ? DATE_FMT.format(bk.getJourneyDate()) : "—";

                bw.write(String.format("%-17s %-14s %-14s %-11s %-16s %-4d %-7s %-11s Rs.%-6.0f %-9s",
                        bk.getReservationId(),
                        clip(bk.getSourceStation(), 13),
                        clip(bk.getDestinationStation(), 13),
                        dt,
                        clip(bk.getPassengerName(), 15),
                        bk.getAge(),
                        bk.getGender(),
                        bk.getSeatType(),
                        bk.getFare(),
                        bk.getStatus()));
                bw.newLine();

                revenueTotal += bk.getFare();
                if ("CONFIRMED".equalsIgnoreCase(bk.getStatus())) okCount++;
                else if ("CANCELLED".equalsIgnoreCase(bk.getStatus())) cancelCount++;
            }

            // Summary block
            bw.newLine(); bw.write(THIN_LINE); bw.newLine();
            bw.write(centred("— SUMMARY —", 115)); bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write("  Total bookings   : " + bookings.size()); bw.newLine();
            bw.write("  Active           : " + okCount);          bw.newLine();
            bw.write("  Cancelled        : " + cancelCount);      bw.newLine();
            bw.write(String.format("  Gross revenue    : Rs. %.2f", revenueTotal));
            bw.newLine();
            bw.write(THICK_LINE); bw.newLine();
            bw.write(centred("— End of Report —", 115)); bw.newLine();
            bw.write(THICK_LINE); bw.newLine();

            return true;
        } catch (IOException ioe) {
            System.err.println("[PDFExporter] Report write failed: " + ioe.getMessage());
            ioe.printStackTrace();
            return false;
        }
    }

    /** Writes a single-booking ticket receipt to the given path. */
    public static boolean exportSingleReservation(
            Reservation bk, String outputPath) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write(THICK_LINE); bw.newLine();
            bw.write(centred("BOOKING CONFIRMATION TICKET", 115)); bw.newLine();
            bw.write(THICK_LINE); bw.newLine(); bw.newLine();

            bw.write("  Booking Code      : " + bk.getReservationId()); bw.newLine();
            bw.write("  Current State     : " + bk.getStatus());        bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write("  JOURNEY");                                      bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write("  Departure         : " +
                    (bk.getSourceStation() != null ? bk.getSourceStation() : "—")); bw.newLine();
            bw.write("  Arrival           : " +
                    (bk.getDestinationStation() != null ? bk.getDestinationStation() : "—")); bw.newLine();
            bw.write("  Date of Travel    : " +
                    (bk.getJourneyDate() != null ? DATE_FMT.format(bk.getJourneyDate()) : "—")); bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write("  TRAVELER");                                     bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write("  Name              : " + bk.getPassengerName()); bw.newLine();
            bw.write("  Age               : " + bk.getAge());          bw.newLine();
            bw.write("  Gender            : " + bk.getGender());       bw.newLine();
            bw.write("  Berth Class       : " + bk.getSeatType());     bw.newLine();
            bw.write(THIN_LINE); bw.newLine();
            bw.write(String.format("  AMOUNT CHARGED    : Rs. %.2f", bk.getFare()));
            bw.newLine(); bw.newLine();
            bw.write(THICK_LINE); bw.newLine();
            bw.write(centred("Thank you for using Rail Booking Platform!", 115)); bw.newLine();
            bw.write(THICK_LINE); bw.newLine();

            return true;
        } catch (IOException ioe) {
            System.err.println("[PDFExporter] Ticket write failed: " + ioe.getMessage());
            ioe.printStackTrace();
            return false;
        }
    }
}
