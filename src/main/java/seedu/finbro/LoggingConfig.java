package seedu.finbro;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configures the logging settings for the application.
 */
public class LoggingConfig {
    private static final Logger logger = Logger.getLogger(LoggingConfig.class.getName());
    private static final String LOG_CONFIG_FILE = "src/main/resources/logging.properties";
    private static final String LOG_DIRECTORY = "logs";
    private static boolean configured = false;

    /**
     * Initializes the logging configuration.
     */
    public static void init() {
        if (configured) {
            logger.fine("Logging already configured, skipping initialization");
            return;
        }

        try {
            // Ensure log directory exists
            Path logDir = Paths.get(LOG_DIRECTORY);
            if (!Files.exists(logDir)) {
                logger.info("Creating log directory: " + LOG_DIRECTORY);
                Files.createDirectory(logDir);
            } else {
                logger.fine("Log directory already exists: " + LOG_DIRECTORY);
            }

            // Try to load logging configuration from file
            try {
                logger.fine("Loading logging configuration from: " + LOG_CONFIG_FILE);
                LogManager.getLogManager().readConfiguration(new FileInputStream(LOG_CONFIG_FILE));
                configured = true;
                logger.info("Logging initialized successfully from properties file");
            } catch (IOException e) {
                // If config file not found, set up basic console logging
                System.err.println("Warning: " + e.getMessage() + " - using default logging configuration");
                
                // Set up some basic defaults
                Logger rootLogger = Logger.getLogger("");
                rootLogger.setLevel(Level.INFO);
                
                // Remove existing handlers to avoid duplicates
                for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
                    rootLogger.removeHandler(handler);
                }
                
                // Add console handler
                ConsoleHandler handler = new ConsoleHandler();
                handler.setLevel(Level.INFO);
                SimpleFormatter formatter = new SimpleFormatter();
                handler.setFormatter(formatter);
                rootLogger.addHandler(handler);
                
                configured = true;
                logger.info("Logging initialized with default configuration");
            }
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
