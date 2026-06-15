package com.reservation.util;

/*
 * FareCalculator.java - Pricing engine for ticket cost computation
 * Applies class-specific multipliers to the route's base price.
 *
 * Multiplier table:
 *   SLEEPER  → 1.0x   (economy)
 *   AC_3TIER → 1.8x
 *   AC_2TIER → 2.5x
 *   AC_FIRST → 3.5x   (premium)
 */
public class FareCalculator {

    // Pricing coefficients keyed by berth class
    private static final double FACTOR_SLEEPER  = 1.0;
    private static final double FACTOR_AC3      = 1.8;
    private static final double FACTOR_AC2      = 2.5;
    private static final double FACTOR_AC1      = 3.5;

    /**
     * Computes the final ticket cost by applying the berth-class
     * multiplier to the corridor base price.
     */
    public static double calculateFare(double basePrice, String berthClass) {
        double factor;
        switch (berthClass.toUpperCase()) {
            case "AC_3TIER": factor = FACTOR_AC3; break;
            case "AC_2TIER": factor = FACTOR_AC2; break;
            case "AC_FIRST": factor = FACTOR_AC1; break;
            default:         factor = FACTOR_SLEEPER; break;
        }
        // Round to two decimal places
        return Math.round(basePrice * factor * 100.0) / 100.0;
    }

    /** Produces a human-readable price string like "Rs. 1250.00". */
    public static String formatFare(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    /** Reduces fare by the given percentage (0–100 range). */
    public static double applyDiscount(double amount, double pct) {
        if (pct < 0 || pct > 100) return amount;
        return Math.round(amount * (1.0 - pct / 100.0) * 100.0) / 100.0;
    }

    /** Maps a berth code to its display label. */
    public static String getSeatTypeName(String code) {
        switch (code.toUpperCase()) {
            case "AC_3TIER": return "AC 3-Tier";
            case "AC_2TIER": return "AC 2-Tier";
            case "AC_FIRST": return "AC First Class";
            case "SLEEPER":  return "Sleeper";
            default:         return code;
        }
    }
}
