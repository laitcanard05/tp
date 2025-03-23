package seedu.finbro.ui;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Text-based UI for the FinBro application.
 */
public class Ui {
    private static final Logger logger = Logger.getLogger(Ui.class.getName());
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Constructs a Ui with System.in as input source.
     */
    public Ui() {
        logger.fine("Initializing UI with System.in");
        this.scanner = new Scanner(System.in);
    }

    /**
     * Constructs a Ui with a custom input source.
     *
     * @param scanner The scanner to use for input
     */
    public Ui(Scanner scanner) {
        logger.fine("Initializing UI with custom scanner");
        this.scanner = scanner;
    }

    /**
     * Shows a welcome message.
     */
    public void showWelcome() {
        logger.info("Showing welcome message");
        System.out.println(LINE);
        System.out.println("Welcome to FinBro - Your Personal Finance Manager!");
        System.out.println("Type 'help' to see available commands.");
        System.out.println(LINE);
    }

    /**
     * Shows a goodbye message.
     */
    public void showGoodbye() {
        logger.info("Showing goodbye message");
        System.out.println(LINE);
        System.out.println("Thank you for using FinBro. Your finances are now in better shape!");
        System.out.println("Goodbye!");
        System.out.println(LINE);
    }

    /**
     * Shows a message to the user.
     *
     * @param message The message to show
     */
    public void showMessage(String message) {
        logger.fine("Showing message: " + message);
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message The error message to show
     */
    public void showError(String message) {
        logger.warning("Showing error message: " + message);
        System.out.println(LINE);
        System.out.println("ERROR: " + message);
        System.out.println(LINE);
    }

    /**
     * Reads a command from the user.
     *
     * @return The command entered by the user
     */
    public String readCommand() {
        logger.fine("Waiting for user input");
        System.out.print("> ");
        String command = scanner.nextLine();
        logger.fine("User input received: " + command);
        return command;
    }

    /**
     * Reads user confirmation (y/n).
     *
     * @param message The confirmation message to show
     * @return true if the user confirms, false otherwise
     */
    //TODO: bulletproof readConfirmation inputs to just y/n and its allowed variants, repeat if invalid
    public boolean readConfirmation(String message) {
        logger.fine("Requesting user confirmation: " + message);
        System.out.println(LINE);
        System.out.println(message + " (y/n)");
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean confirmed = input.equals("y") || input.equals("yes");
        logger.fine("User confirmation result: " + confirmed);
        return confirmed;
    }

    public Boolean warnDuplicate() {
        logger.info("Showing duplicate warning message");
        System.out.println(LINE);
        System.out.println("WARNING: one or more transactions with this name and description already exist.");
        System.out.println("Please confirm if you would like to proceed entering this transaction anyway\n");
        return readConfirmation("input duplicate transaction?");
    }

    /**
     * Reads user input for start and end dates for filtering transactions.
     *
     * @return The dates entered by the user
     */
    public String[] readDates() {
        String[] dates = new String[2];
        logger.fine("Requesting user input for dates");
        System.out.println(LINE);
        System.out.println("Please enter the start date in the format yyyy-mm-dd.");
        System.out.print("> ");
        String startDate = scanner.nextLine();
        dates[0] = startDate;
        System.out.println(LINE);
        System.out.println("Please enter the end date in the format yyyy-mm-dd.");
        System.out.print("> ");
        String endDate = scanner.nextLine();
        dates[1] = endDate;
        System.out.println(LINE);
        logger.fine("User input for dates received: " + startDate + " to " + endDate);
        return dates;
    }

    public Integer[] readMonthYear() {
        Integer[] monthYear = new Integer[2];
        logger.fine("Requesting user input for month and year");
        System.out.println(LINE);
        System.out.println("Please enter the month (1-12) for the summary.");
        System.out.print("> ");
        String input = scanner.nextLine();
        Integer month;
        if (input.isEmpty()) {
            month = null;
        } else {
            month = Integer.parseInt(input);
        }
        monthYear[0] = month;
        System.out.println(LINE);
        System.out.println("Please enter the year for the summary.");
        System.out.print("> ");
        input = scanner.nextLine();
        Integer year;
        if (input.isEmpty()) {
            year = null;
        } else {
            year = Integer.parseInt(input);
        }
        monthYear[1] = year;
        System.out.println(LINE);
        logger.fine("User input for month and year received: " + month + " " + year);
        return monthYear;
    }

    @Override
    public String toString() {
        return "Ui{" +
                "scanner=" + scanner +
                '}';
    }
}
