package seedu.finbro;

import java.util.logging.Level;
import java.util.logging.Logger;
import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Main class for the FinBro application.
 */
public class FinBro {
    private static final Logger logger = Logger.getLogger(FinBro.class.getName());

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private TransactionManager transactionManager;

    /**
     * Constructs a new FinBro application with default components.
     */
    public FinBro() {
        logger.info("Initializing FinBro application");
        ui = new Ui();
        storage = new Storage();
        parser = new Parser();
        transactionManager = new TransactionManager();
        logger.fine("FinBro components initialized");
    }

    /**
     * Runs the application.
     */
    public void run() {
        logger.info("Starting FinBro application");
        start();
        runCommandLoop();
        exit();
        logger.info("FinBro application terminated");
    }

    /**
     * Starts the application.
     */
    private void start() {
        logger.fine("Displaying welcome message");
        ui.showWelcome();
        try {
            logger.fine("Loading transaction data");
            transactionManager = storage.loadTransactions();
            logger.info("Transaction data loaded successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading transaction data", e);
            ui.showError("Problem loading data: " + e.getMessage());
            logger.info("Initializing with empty transaction manager");
            transactionManager = new TransactionManager(); // Start with empty data if loading fails
        }
    }

    /**
     * Runs the main command loop.
     */
    private void runCommandLoop() {
        boolean isExit = false;
        logger.fine("Entering command loop");

        while (!isExit) {
            try {
                String userInput = ui.readCommand();
                logger.fine("User input: " + userInput);

                Command command = parser.parseCommand(userInput);
                logger.fine("Command parsed: " + command.getClass().getSimpleName());

                String result = command.execute(transactionManager, ui, storage);
                logger.fine("Command execution result: " + result);

                ui.showMessage(result);
                isExit = command.isExit();

                if (isExit) {
                    logger.info("Exit command received");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing command", e);
                ui.showError("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Exits the application.
     */
    private void exit() {
        logger.fine("Showing goodbye message");
        ui.showGoodbye();
    }

    /**
     * Main entry point of the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize logging configuration
        LoggingConfig.init();
        logger.info("FinBro main method invoked");
        new FinBro().run();
    }
}
