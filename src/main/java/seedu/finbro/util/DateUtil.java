package seedu.finbro.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for date-related operations.
 */
public class DateUtil {
    private static final Logger logger = Logger.getLogger(DateUtil.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validates a date string to ensure it represents a real, valid date.
     *
     * @param dateStr The date string in yyyy-MM-dd format
     * @return True if the date is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        try {
            // First, check basic format compliance
            if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                logger.warning("Date format does not match yyyy-MM-dd pattern: " + dateStr);
                return false;
            }

            // Parse the date parts
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Perform range checks
            if (year < 1 || year > 9999) {
                logger.warning("Invalid year in date: " + year);
                return false;
            }

            if (month < 1 || month > 12) {
                logger.warning("Invalid month in date: " + month);
                return false;
            }

            // Let LocalDate validate the day based on month/year
            // This handles leap years and different days in each month
            LocalDate.of(year, month, day);

            // Simple sanity check for future dates
            if (LocalDate.of(year, month, day).isAfter(LocalDate.now().plusYears(100))) {
                logger.warning("Date is more than 100 years in the future: " + dateStr);
                return false;
            }

            return true;
        } catch (DateTimeException e) {
            // This catches issues like February 30th
            logger.warning("Invalid date components: " + dateStr + " - " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warning("Error validating date: " + dateStr + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Parses a date string into a LocalDate object.
     * Returns null if the date is invalid.
     *
     * @param dateStr The date string in yyyy-MM-dd format
     * @return The parsed LocalDate, or null if invalid
     */
    public static LocalDate parseDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing date: " + dateStr, e);
            return null;
        }
    }

    /**
     * Provides a user-friendly error message for invalid date input.
     *
     * @param dateStr The invalid date string
     * @return An appropriate error message
     */
    public static String getValidationErrorMessage(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "Date cannot be empty.";
        }

        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "Date format should be YYYY-MM-DD.";
        }

        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 1 || year > 9999) {
                return "Year must be between 0001 and 9999.";
            }

            if (month < 1 || month > 12) {
                return "Month must be between 01 and 12.";
            }

            // This will validate the day based on month/year
            try {
                LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                return "Invalid day for the specified month and year.";
            }

            return "Invalid date.";
        } catch (Exception e) {
            return "Invalid date format. Please use YYYY-MM-DD.";
        }
    }
}
