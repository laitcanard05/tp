package seedu.finbro.ui;

import seedu.finbro.logic.exceptions.DecimalPointException;
import seedu.finbro.logic.exceptions.EmptyInputException;
import seedu.finbro.logic.exceptions.NegativeNumberException;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Text-based UI for the FinBro application.
 */
public class Ui {
    public static final String LINE = "____________________________________________________________";
    private static final Logger logger = Logger.getLogger(Ui.class.getName());
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
        System.out.print("Enter command word:\n> ");
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
    public boolean readConfirmation(String message) {
        logger.fine("Requesting user confirmation: " + message);
        System.out.println(LINE);
        System.out.println(message + " (y/n)");
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean confirmed;
        if(input.equals("y") || input.equals("yes")) {
            logger.fine("User confirmation result: TRUE");
            return true;
        }
        if(input.equals("n") || input.equals("no")) {
            logger.fine("User confirmation result: FALSE");
            return false;
        } else {
            System.out.println(LINE);
            System.out.println("INVALID INPUT, PLEASE TRY AGAIN");
            logger.warning("invalid user input: " + input + ".\n trying again.");
            return readConfirmation(message);
        }
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
        System.out.println("Please enter the end date in the format yyyy-mm-dd." +
                            " (Leave blank to show transactions up to current date.)");
        System.out.print("> ");
        String endDate = scanner.nextLine();
        dates[1] = endDate;
        System.out.println(LINE);
        logger.fine("User input for dates received: " + startDate + " to " + endDate);
        return dates;
    }

    /**
     * Reads user input for start date only. Used in ListCommand.
     *
     * @return The date entered by the user
     */
    public String readStartDate() {
        logger.fine("Requesting user input for start date");
        System.out.println(LINE);
        System.out.println("Please enter the start date in the format yyyy-mm-dd. " +
                           "(Leave blank to show all transactions. " +
                           "You can choose the number of transactions to show after this prompt.)");
        System.out.print("> ");
        String date = scanner.nextLine();
        System.out.println(LINE);
        logger.fine("User input for date received: " + date);
        return date;
    }

    //TODO: consider splitting this up so we can recursively ask for month and year each time a wrong input is given
    public Integer[] readMonthYear() {
        Integer[] monthYear = new Integer[2];
        logger.fine("Requesting user input for month and year");
        System.out.println(LINE);
        System.out.println("Please enter the month (1-12). (Leave blank for current month)");
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
        System.out.println("Please enter the year. (Leave blank for current year)");
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

    /**
     * Reads the number of transactions to list from the user. Used in ListCommand.
     *
     * @return Integer limit or null if not specified
     * @throws NumberFormatException if input is not a valid number
     */
    public Integer readLimit() {
        System.out.println("Enter number of transactions to list. (Leave blank to show all transactions.)");
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        return Integer.parseInt(input);
    }

    /**
     * Reads the index of the transaction to delete from the user. Used in DeleteCommand.
     *
     * @return The index entered by the user, index >= 0, no empty input
     */
    public int readIndex(String message) {
        System.out.println(LINE);
        System.out.print(message);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()){
                throw new EmptyInputException();
            }
            int output = Integer.parseInt(input);
            if (output < 0) {
                throw new NegativeNumberException();
            }
            return output;
        } catch (NumberFormatException e) {
            System.out.println("INVALID INPUT: Non-number Input.\nPlease enter an integer.");
            logger.log(Level.WARNING, "Integer input invalid - non-number input", e);
        } catch (EmptyInputException e) {
            EmptyInputException.handle();
        } catch (NegativeNumberException e) {
            logger.log(Level.WARNING, "Integer input invalid - negative input", e);
            NegativeNumberException.handle();
        }
        return readIndex(message);
    }

    /**
     * Reads a single index or index range from the user (e.g., "3" or "2-5").
     *
     * @return An int array: [start, end], where start == end for single index
     */
    public int[] readIndexRange(String message) {
        System.out.println(LINE);
        System.out.print(message);

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    throw new EmptyInputException();
                }

                if (input.contains("-")) {
                    String[] parts = input.split("-");
                    if (parts.length != 2) {
                        throw new NumberFormatException("Invalid range format.");
                    }

                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());

                    if (start < 1 || end < 1 || start > end) {
                        throw new IllegalArgumentException("Start and end must be positive, and start <= end.");
                    }

                    return new int[]{start, end};
                } else {
                    int index = Integer.parseInt(input);
                    if (index < 1) {
                        throw new IllegalArgumentException("Index must be a positive integer.");
                    }
                    return new int[]{index, index};
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number or range (e.g., '1' or '2-5')");
                logger.log(Level.WARNING, "Invalid delete index input", e);
            }
        }
    }


    /**
     * Reads the double input by user. Used to parse amount.
     *
     * @return The double entered by the user, double >= 0, <=2dp, no empty input
     */
    public double readDouble(String message) {
        System.out.println(LINE);
        System.out.print(message);

        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                throw new EmptyInputException();
            }
            double output = Double.parseDouble(input);
            if (output < 0) {
                throw new NegativeNumberException();
            }
            if (!input.matches("^\\d+(\\.\\d{1,2})?$")) {
                throw new DecimalPointException();
            }
            return output;
        } catch (NumberFormatException e) {
            System.out.println("INVALID INPUT: Non-number Input.\nPlease enter a number up to 2 decimal places.");
            logger.log(Level.WARNING, "Double input invalid - non-number input", e);
        } catch (NegativeNumberException e) {
            logger.log(Level.WARNING, "Double input invalid - negative input", e);
            NegativeNumberException.handle();
        } catch (EmptyInputException e) {
            logger.log(Level.WARNING, "Double input invalid - user input empty.", e);
            EmptyInputException.handle();
        } catch (DecimalPointException e) {
            logger.log(Level.WARNING, "Double input invalid - exceeds 2dp.", e);
            DecimalPointException.handle();
        }
        return readDouble(message);
    }

    /**
     * Reads string input by user. Used to parse description for Income / Expense.
     *
     * @return The string entered by the user, no empty input
     */
    public String readString(String message) {
        while (true) {
            System.out.println(LINE);
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("INVALID INPUT: empty input.\nPlease enter a string.");
        }
    }

    /**
     * Prompts the user for tag input and returns the trimmed input string.
     *
     * @param message The prompt message to display to the user
     * @return The user's tag input as a trimmed string, accepts empty input
     */
    public String readTags(String message) {
        System.out.println(LINE);
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    /**
     * Reads user input for amount. Used in EditCommand.
     * Allows empty input to skip the field.
     *
     * @param message The prompt message to display
     * @return The amount entered by the user, can be empty
     */
    public String readAmount(String message) {
        logger.fine("Requesting user input for amount");
        System.out.println(LINE);
        System.out.print(message);
        String amount = scanner.nextLine().trim();
        logger.fine("User input for amount received: " + amount);
        return amount;
    }

    /**
     * Reads user input for description. Used in EditCommand.
     * Allows empty input to skip the field.
     *
     * @param message The prompt message to display
     * @return The description entered by the user, can be empty
     */
    public String readDescription(String message) {
        logger.fine("Requesting user input for description");
        System.out.println(LINE);
        System.out.print(message);
        String description = scanner.nextLine().trim();
        logger.fine("User input for description received: " + description);
        return description;
    }

    /**
     * Reads user input for date. Used in EditCommand.
     * Allows empty input to skip the field.
     *
     * @param message The prompt message to display
     * @return The date entered by the user, can be empty
     */
    public String readDate(String message) {
        logger.fine("Requesting user input for date");
        System.out.println(LINE);
        System.out.print(message);
        String date = scanner.nextLine().trim();
        logger.fine("User input for date received: " + date);
        return date;
    }

    /**
     * Reads user input for category. Used in EditCommand.
     * Allows empty input to skip the field.
     *
     * @param message The prompt message to display
     * @return The category entered by the user, can be empty
     */
    public String readCategory(String message) {
        logger.fine("Requesting user input for category");
        System.out.println(LINE);
        System.out.print(message);
        String category = scanner.nextLine().trim();
        logger.fine("User input for category received: " + category);
        return category;
    }
}
