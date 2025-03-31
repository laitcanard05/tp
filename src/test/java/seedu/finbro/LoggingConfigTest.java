package seedu.finbro;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the LoggingConfig class.
 */
class LoggingConfigTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private Field configuredField;
    private Field logDirField;
    private Field logConfigFileField;
    private boolean originalConfigured;
    private String originalLogDir;
    private String originalLogConfigFile;
    private Path tempLogConfigFile;
    private Path backupLogConfigFile;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
        // Capture System.err for testing error messages
        System.setErr(new PrintStream(errContent));

        // Get access to private static fields
        configuredField = LoggingConfig.class.getDeclaredField("configured");
        configuredField.setAccessible(true);

        logDirField = LoggingConfig.class.getDeclaredField("LOG_DIRECTORY");
        logDirField.setAccessible(true);

        logConfigFileField = LoggingConfig.class.getDeclaredField("LOG_CONFIG_FILE");
        logConfigFileField.setAccessible(true);

        // Store original values
        originalConfigured = (boolean) configuredField.get(null);
        originalLogDir = (String) logDirField.get(null);
        originalLogConfigFile = (String) logConfigFileField.get(null);

        // Back up the logging config file if it exists
        tempLogConfigFile = Paths.get(originalLogConfigFile);
        backupLogConfigFile = Paths.get(originalLogConfigFile + ".backup");
        if (Files.exists(tempLogConfigFile)) {
            Files.copy(tempLogConfigFile, backupLogConfigFile);
        }
    }

    @AfterEach
    public void tearDown() throws IllegalAccessException, IOException {
        // Restore System.err
        System.setErr(originalErr);

        // Restore original field values
        configuredField.set(null, originalConfigured);

        // Reset LogManager
        LogManager.getLogManager().reset();

        // Restore the original config file if backup exists
        if (Files.exists(backupLogConfigFile)) {
            if (Files.exists(tempLogConfigFile)) {
                Files.delete(tempLogConfigFile);
            }
            Files.move(backupLogConfigFile, tempLogConfigFile);
        } else if (Files.exists(tempLogConfigFile)) {
            // If this was a generated file, delete it
            Files.delete(tempLogConfigFile);
        }
    }

    @Test
    void initCreatesLogDirectory() throws IllegalAccessException {
        // Ensure configured is false
        configuredField.set(null, false);

        // Call init
        LoggingConfig.init();

        // Verify log directory was created
        assertTrue(Files.exists(Path.of(originalLogDir)),
                "Log directory should be created during initialization");
    }

    @Test
    void initSkipsIfAlreadyConfigured() throws IllegalAccessException {
        // Set as already configured
        configuredField.set(null, true);

        // Capture logger output
        Logger testLogger = Logger.getLogger(LoggingConfig.class.getName());
        testLogger.setLevel(Level.ALL);

        // Call init
        LoggingConfig.init();

        assertTrue((boolean) configuredField.get(null),
                "Configured flag should remain true after reinitialization");
    }

    @Test
    void initHandlesMissingConfigFile() throws IllegalAccessException, IOException {
        // Set as not configured
        configuredField.set(null, false);

        // Temporarily remove the logging.properties file
        if (Files.exists(tempLogConfigFile)) {
            Files.delete(tempLogConfigFile);
        }

        // Call init - this should create a new config file
        LoggingConfig.init();

        // Verify the config file was created
        assertTrue(Files.exists(tempLogConfigFile),
                "Logging config file should be created when missing");

        // Verify the config specifies only file logging (no console handlers)
        Properties props = new Properties();
        props.load(Files.newInputStream(tempLogConfigFile));
        assertTrue(props.getProperty("handlers").contains("FileHandler"),
                "FileHandler should be configured");
        assertFalse(props.getProperty("handlers").contains("ConsoleHandler"),
                "ConsoleHandler should not be configured");
    }

    @Test
    void initShouldCreateSilentLoggingConfiguration() throws IllegalAccessException, IOException {
        // Set as not configured
        configuredField.set(null, false);

        // Ensure the logging config file doesn't exist
        if (Files.exists(tempLogConfigFile)) {
            Files.delete(tempLogConfigFile);
        }

        // Call init
        LoggingConfig.init();

        // Verify error message contains the expected text
        assertTrue(errContent.toString().contains("Warning: " + originalLogConfigFile),
                "Should warn about missing config file");
        assertTrue(errContent.toString().contains("creating file"),
                "Should indicate a new file is being created");

        // Check the log file was generated with silent configuration
        assertTrue(Files.exists(tempLogConfigFile),
                "Logging properties file should be created");

        // Verify the correct configuration was set
        Properties props = new Properties();
        props.load(Files.newInputStream(tempLogConfigFile));
        assertEquals("java.util.logging.FileHandler", props.getProperty("handlers"),
                "Only FileHandler should be configured");
        assertEquals("FINE", props.getProperty("java.util.logging.FileHandler.level"),
                "FileHandler level should be FINE");
        assertEquals("INFO", props.getProperty(".level"),
                "Root logger level should be INFO");
    }

    // Helper method to simplify assertions
    private void assertEquals(String expected, String actual, String message) {
        assertTrue(expected.equals(actual), message + " - Expected: " + expected + ", Actual: " + actual);
    }
}
