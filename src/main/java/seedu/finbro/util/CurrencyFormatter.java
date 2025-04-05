package seedu.finbro.util;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Utility class for formatting currency amounts in a consistent way throughout the application.
 */
public class CurrencyFormatter {
    // Maximum allowed amount for validation
    public static final double MAX_AMOUNT = 1_000_000_000.00; // 1 billion

    private static final Logger logger = Logger.getLogger(CurrencyFormatter.class.getName());

    // Standard currency format with grouping
    private static final DecimalFormat STANDARD_FORMAT = new DecimalFormat("$#,##0.00");

    // Million and billion abbreviation formats
    private static final DecimalFormat MILLION_FORMAT = new DecimalFormat("$#,##0.00M");
    private static final DecimalFormat BILLION_FORMAT = new DecimalFormat("$#,##0.00B");

    private CurrencyFormatter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Formats a monetary amount with appropriate grouping for large numbers.
     * Uses abbreviations (M/B) for very large numbers.
     *
     * @param amount The amount to format
     * @return The formatted amount string with currency symbol
     */
    public static String format(double amount) {
        if (amount < 1_000_000) {
            // Standard format for amounts under 1 million
            return STANDARD_FORMAT.format(amount);
        } else if (amount < 1_000_000_000) {
            // Use millions format for large amounts (e.g., $1.23M)
            return MILLION_FORMAT.format(amount / 1_000_000);
        } else {
            // Use billions format for very large amounts (e.g., $1.23B)
            return BILLION_FORMAT.format(amount / 1_000_000_000);
        }
    }

    /**
     * Always formats the full amount with proper grouping, regardless of size.
     * This is useful for detailed financial reports where abbreviated formats
     * are not appropriate.
     *
     * @param amount The amount to format
     * @return The formatted amount string with currency symbol and digit grouping
     */
    public static String formatVerbose(double amount) {
        return STANDARD_FORMAT.format(amount);
    }

    /**
     * Validates if an amount is within the acceptable range for the application.
     *
     * @param amount The amount to validate
     * @return true if the amount is valid, false otherwise
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= MAX_AMOUNT;
    }
}
