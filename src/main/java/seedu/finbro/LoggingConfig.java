package seedu.finbro;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

            // Load logging configuration
            LogManager.getLogManager().readConfiguration(new FileInputStream(LOG_CONFIG_FILE));
            configured = true;
            logger.info("Logging initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize logging configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
