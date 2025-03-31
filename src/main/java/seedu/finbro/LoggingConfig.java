package seedu.finbro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.FileHandler;
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
            return;
        }

        try {
            // Ensure log directory exists
            Path logDir = Paths.get(LOG_DIRECTORY);
            if (!Files.exists(logDir)) {
                Files.createDirectory(logDir);
            }

            // Try to load logging configuration from file
            try {
                LogManager.getLogManager().readConfiguration(new FileInputStream(LOG_CONFIG_FILE));
            } catch (IOException e) {
                // If config file not found, create a new one and set programmatic configuration
                System.err.println("Warning: " + LOG_CONFIG_FILE + " (No such file or directory) - creating file");

                // Create properties file with silent console configuration
                createSilentLoggingPropertiesFile();

                // Apply configuration programmatically
                configureSilentLogging();
            }

            configured = true;
            logger.info("Logging initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
        }
    }

    /**
     * Creates a logging.properties file that disables console output.
     */
    private static void createSilentLoggingPropertiesFile() {
        Properties props = new Properties();

        // Global properties
        props.setProperty(".level", "INFO");
        props.setProperty("handlers", "java.util.logging.FileHandler");

        // File handler config
        props.setProperty("java.util.logging.FileHandler.level", "FINE");
        props.setProperty("java.util.logging.FileHandler.pattern", "logs/finbro.log");
        props.setProperty("java.util.logging.FileHandler.limit", "50000");
        props.setProperty("java.util.logging.FileHandler.count", "1");
        props.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");

        // Format for SimpleFormatter
        props.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s: %5$s%6$s%n");

        // Application specific levels
        props.setProperty("seedu.finbro.level", "FINE");

        try {
            Path configDir = Paths.get("src/main/resources");
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            props.store(new FileOutputStream(LOG_CONFIG_FILE), "FinBro Logging Configuration");
        } catch (IOException e) {
            System.err.println("Failed to create logging properties file: " + e.getMessage());
        }
    }

    /**
     * Configures logging programmatically to disable console output.
     */
    private static void configureSilentLogging() throws IOException {
        // Get the root logger and remove all handlers
        Logger rootLogger = Logger.getLogger("");
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        // Add file handler (no console handler)
        FileHandler fileHandler = new FileHandler("logs/finbro.log");
        fileHandler.setLevel(Level.FINE);
        fileHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(fileHandler);

        // Set root logger level
        rootLogger.setLevel(Level.INFO);

        // Set package specific levels
        Logger.getLogger("seedu.finbro").setLevel(Level.FINE);
    }
}
