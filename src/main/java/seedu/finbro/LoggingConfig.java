package seedu.finbro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Enhanced logging configuration with standardized levels, log rotation, and filtering.
 * This is an improved version of the LoggingConfig class.
 */
public class LoggingConfig {
    private static final Logger logger = Logger.getLogger(LoggingConfig.class.getName());
    private static final String LOG_CONFIG_FILE = "src/main/resources/logging.properties";
    private static final String LOG_DIRECTORY = "logs";
    private static final int LOG_FILE_SIZE_LIMIT = 1000000; // 1MB per file
    private static final int LOG_FILE_COUNT = 5; // Keep 5 log files for rotation
    private static boolean configured = false;

    /**
     * Initializes the logging configuration with improved rotation and standardized levels.
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
                System.err.println("Creating new logging configuration file: " + LOG_CONFIG_FILE);

                // Create properties file with improved configuration
                createLoggingPropertiesFile();

                // Apply configuration programmatically
                configureLogging();
            }

            configured = true;
            logger.info("Logging initialized successfully with file rotation and standardized levels");
        } catch (IOException e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
        }
    }

    /**
     * Creates an improved logging.properties file with file rotation and standardized level configuration.
     */
    private static void createLoggingPropertiesFile() {
        Properties props = new Properties();

        // Global properties with standardized levels
        props.setProperty(".level", "INFO");
        props.setProperty("handlers", "java.util.logging.FileHandler, java.util.logging.ConsoleHandler");

        // File handler config with rotation
        props.setProperty("java.util.logging.FileHandler.level", "FINE");
        props.setProperty("java.util.logging.FileHandler.pattern", LOG_DIRECTORY + "/finbro_%g.log");
        props.setProperty("java.util.logging.FileHandler.limit", String.valueOf(LOG_FILE_SIZE_LIMIT));
        props.setProperty("java.util.logging.FileHandler.count", String.valueOf(LOG_FILE_COUNT));
        props.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
        props.setProperty("java.util.logging.FileHandler.append", "true");

        // Console handler config - only SEVERE level
        props.setProperty("java.util.logging.ConsoleHandler.level", "SEVERE");
        props.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");

        // Format for SimpleFormatter - more detailed than default
        props.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s [%3$s] - %5$s%6$s%n");

        // Application specific levels - standardized across packages
        props.setProperty("seedu.finbro.level", "FINE");
        props.setProperty("seedu.finbro.logic.level", "FINE");
        props.setProperty("seedu.finbro.model.level", "FINE");
        props.setProperty("seedu.finbro.storage.level", "FINE");
        props.setProperty("seedu.finbro.ui.level", "FINE");

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
     * Configures logging programmatically with improved file rotation and standardized levels.
     */
    private static void configureLogging() throws IOException {
        // Get the root logger and remove all handlers
        Logger rootLogger = Logger.getLogger("");
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        // Add rotating file handler for detailed logging
        FileHandler fileHandler = new FileHandler(LOG_DIRECTORY + "/finbro_%g.log",
                LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, true);
        fileHandler.setLevel(Level.FINE);

        // Use a custom formatter for more detailed log entries
        SimpleFormatter formatter = new SimpleFormatter() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            @Override
            public String format(LogRecord record) {
                Instant instant = Instant.ofEpochMilli(record.getMillis());
                String formattedDate = dateTimeFormatter.format(instant.atZone(ZoneId.systemDefault()));

                return String.format("%s %s [%s] - %s%s%n",
                        formattedDate,
                        record.getLevel(),
                        record.getLoggerName(),
                        record.getMessage(),
                        record.getThrown() == null ? "" : "\n" + formatThrowable(record.getThrown()));
            }

            private String formatThrowable(Throwable throwable) {
                StringBuilder sb = new StringBuilder();
                sb.append(throwable.toString()).append("\n");
                for (StackTraceElement element : throwable.getStackTrace()) {
                    sb.append("\tat ").append(element.toString()).append("\n");
                }
                return sb.toString();
            }
        };

        fileHandler.setFormatter(formatter);
        rootLogger.addHandler(fileHandler);

        // Add console handler for SEVERE messages with sensitive info filter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.SEVERE);
        consoleHandler.setFormatter(new SimpleFormatter());

        // Add filter to remove sensitive information from console logs
        consoleHandler.setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                // Filter out logs containing potentially sensitive information
                String message = record.getMessage();
                if (message != null && (
                        message.contains("password") ||
                                message.contains("credit") ||
                                message.contains("card"))) {
                    return false;
                }
                return true;
            }
        });

        rootLogger.addHandler(consoleHandler);

        // Set root logger level
        rootLogger.setLevel(Level.INFO);

        // Set package specific levels - standardized across all packages
        Logger.getLogger("seedu.finbro").setLevel(Level.FINE);
        Logger.getLogger("seedu.finbro.logic").setLevel(Level.FINE);
        Logger.getLogger("seedu.finbro.model").setLevel(Level.FINE);
        Logger.getLogger("seedu.finbro.storage").setLevel(Level.FINE);
        Logger.getLogger("seedu.finbro.ui").setLevel(Level.FINE);
    }

    /**
     * Gets a logger for the specified class with standardized configuration.
     * This method ensures all loggers follow the same pattern.
     *
     * @param clazz The class to get a logger for
     * @return A configured logger for the class
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!configured) {
            init();
        }

        Logger classLogger = Logger.getLogger(clazz.getName());

        // Set standard level based on package
        if (clazz.getPackage().getName().startsWith("seedu.finbro")) {
            classLogger.setLevel(Level.FINE);
        }

        return classLogger;
    }
}
