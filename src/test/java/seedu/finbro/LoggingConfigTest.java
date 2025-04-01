package seedu.finbro;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingConfigTest {

    private static final String LOG_DIR = "logs";
    private static final String CONFIG_FILE = "src/main/resources/logging.properties";

    private Path originalConfig = null;
    private Path backupConfig = null;
    private boolean configExisted = false;

    @BeforeEach
    public void setUp() throws Exception {
        // Reset the configured flag in LoggingConfig to allow re-initialization
        resetConfiguredFlag();

        // Backup existing config file if it exists
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            configExisted = true;
            backupConfig = Paths.get(CONFIG_FILE + ".bak");

            // Delete backup file if it already exists
            if (Files.exists(backupConfig)) {
                Files.delete(backupConfig);
            }

            Files.copy(configPath, backupConfig);
            Files.delete(configPath);
        }

        // Reset LogManager
        LogManager.getLogManager().reset();
    }

    /**
     * Attempts to delete a file with retries if it's locked
     */
    private void deleteFileWithRetry(Path path, int maxRetries) throws Exception {
        if (!Files.exists(path)) {
            return;
        }

        // Close all handlers to release file locks
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            handler.close();
        }
        LogManager.getLogManager().reset();

        Exception lastException = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                Files.delete(path);
                return; // Success
            } catch (FileSystemException e) {
                // File is locked, wait and retry
                lastException = e;
                Thread.sleep(200);
                System.gc(); // Encourage resource cleanup
            }
        }

        // If we got here, all retries failed
        if (lastException != null) {
            System.err.println("Warning: Could not delete file after " + maxRetries + " attempts: " + path);
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Reset LogManager and close all handlers first
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            handler.close();
        }
        LogManager.getLogManager().reset();

        try {
            // Restore config file if it was backed up
            if (configExisted) {
                Path configPath = Paths.get(CONFIG_FILE);
                if (Files.exists(configPath)) {
                    deleteFileWithRetry(configPath, 5);
                }

                if (Files.exists(backupConfig)) {
                    Files.copy(backupConfig, configPath);
                    deleteFileWithRetry(backupConfig, 5);
                }
            } else {
                // Delete created config file
                deleteFileWithRetry(Paths.get(CONFIG_FILE), 5);
            }
        } catch (Exception e) {
            System.err.println("Warning: Error during test cleanup: " + e.getMessage());
        }
    }

    /**
     * Helper method to reset the "configured" flag in LoggingConfig
     */
    private void resetConfiguredFlag() throws Exception {
        Field configuredField = LoggingConfig.class.getDeclaredField("configured");
        configuredField.setAccessible(true);
        configuredField.set(null, false);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testInitCreatesLogDirectory() throws Exception {
        // Backup existing log directory if it exists
        Path logPath = Paths.get(LOG_DIR);
        Path backupLogPath = null;
        boolean logDirExisted = Files.exists(logPath);

        if (logDirExisted) {
            backupLogPath = Paths.get(LOG_DIR + ".bak");
            if (Files.exists(backupLogPath)) {
                Files.walk(backupLogPath)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            Files.move(logPath, backupLogPath);
        }

        try {
            // Reset logging before initializing
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                handler.close();
            }
            LogManager.getLogManager().reset();

            // Initialize logging
            LoggingConfig.init();

            // Verify log directory was created
            assertTrue(Files.exists(logPath), "Log directory should be created");
        } finally {
            // Close all handlers before cleanup
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                handler.close();
            }
            LogManager.getLogManager().reset();

            // Restore original state
            try {
                if (Files.exists(logPath)) {
                    Files.walk(logPath)
                            .sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }

                if (logDirExisted && backupLogPath != null && Files.exists(backupLogPath)) {
                    Files.move(backupLogPath, logPath);
                }
            } catch (Exception e) {
                System.err.println("Warning: Error during test cleanup: " + e.getMessage());
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testInitCreatesPropertiesFileIfMissing() throws Exception {
        // Ensure config file doesn't exist
        Path configPath = Paths.get(CONFIG_FILE);
        Files.deleteIfExists(configPath);

        // Initialize logging
        LoggingConfig.init();

        // Verify properties file was created
        assertTrue(Files.exists(configPath), "Config file should be created");

        // Verify properties content
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        }

        assertEquals("SEVERE", props.getProperty("java.util.logging.ConsoleHandler.level"),
                "Console handler should be SEVERE level only");
        assertEquals("FINE", props.getProperty("java.util.logging.FileHandler.level"),
                "File handler should be FINE level");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testLoggerLevelsAreSetCorrectly() {
        // Initialize logging
        LoggingConfig.init();

        // Check logger levels
        Logger appLogger = Logger.getLogger("seedu.finbro");
        assertEquals(Level.FINE, appLogger.getLevel(), "App logger should be FINE level");

        // Check root logger configuration
        Logger rootLogger = Logger.getLogger("");
        assertEquals(Level.INFO, rootLogger.getLevel(), "Root logger should be INFO level");

        // Verify handlers on root logger
        boolean hasConsoleHandler = false;
        boolean hasFileHandler = false;

        for (Handler handler : rootLogger.getHandlers()) {
            if (handler.getClass().getName().contains("ConsoleHandler")) {
                hasConsoleHandler = true;
                assertEquals(Level.SEVERE, handler.getLevel(), "Console handler should have SEVERE level");
            }
            if (handler.getClass().getName().contains("FileHandler")) {
                hasFileHandler = true;
                assertEquals(Level.FINE, handler.getLevel(), "File handler should have FINE level");
            }
        }

        assertTrue(hasConsoleHandler, "Root logger should have ConsoleHandler");
        assertTrue(hasFileHandler, "Root logger should have FileHandler");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testRepeatedInitializationIsIgnored() throws Exception {
        // First initialization
        LoggingConfig.init();

        // Get number of handlers after first init
        Logger rootLogger = Logger.getLogger("");
        int initialHandlerCount = rootLogger.getHandlers().length;

        // Check that configured flag is true
        Field configuredField = LoggingConfig.class.getDeclaredField("configured");
        configuredField.setAccessible(true);
        assertTrue((boolean) configuredField.get(null), "configured flag should be true after initialization");

        // Try to initialize again
        LoggingConfig.init();

        // Verify no extra handlers were added
        assertEquals(initialHandlerCount, rootLogger.getHandlers().length,
                "Second initialization should not add more handlers");

        // Verify configured flag is still true
        assertTrue((boolean) configuredField.get(null), "configured flag should remain true");

        // Check that logger levels remain consistent
        Logger appLogger = Logger.getLogger("seedu.finbro");
        assertEquals(Level.FINE, appLogger.getLevel(), "App logger level should remain consistent");
    }
}
