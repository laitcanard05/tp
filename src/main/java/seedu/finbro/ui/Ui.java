package seedu.finbro.ui;

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

    @Override
    public String toString() {
        return "Ui{" +
                "scanner=" + scanner +
                '}';
    }
}
