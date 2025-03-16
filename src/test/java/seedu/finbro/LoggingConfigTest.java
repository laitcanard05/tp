package seedu.finbro;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the LoggingConfig class.
 */
public class LoggingConfigTest {
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private Field configuredField;
    private Field logDirField;
    private boolean originalConfigured;
    private String originalLogDir;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Capture System.err for testing error messages
        System.setErr(new PrintStream(errContent));

        // Get access to private static fields
        configuredField = LoggingConfig.class.getDeclaredField("configured");
        configuredField.setAccessible(true);
        
        logDirField = LoggingConfig.class.getDeclaredField("LOG_DIRECTORY");
        logDirField.setAccessible(true);
        
        // Store original values
        originalConfigured = (boolean) configuredField.get(null);
        originalLogDir = (String) logDirField.get(null);
    }

    @AfterEach
    public void tearDown() throws IllegalAccessException {
        // Restore System.err
        System.setErr(originalErr);
        
        // Restore original field values
        configuredField.set(null, originalConfigured);
        
        // Reset LogManager
        LogManager.getLogManager().reset();
    }

    @Test
    public void initCreatesLogDirectory() throws IllegalAccessException, InstantiationException {
        // Ensure configured is false
        configuredField.set(null, false);
        
        // Call init
        LoggingConfig.init();
        
        // Verify log directory was created
        assertTrue(Files.exists(Path.of(originalLogDir)), 
                "Log directory should be created during initialization");
    }

    @Test
    public void initSkipsIfAlreadyConfigured() throws IllegalAccessException {
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
    public void initHandlesIOException() throws IllegalAccessException, NoSuchFieldException, IOException {
        // Since we can't modify final fields, we'll need to manipulate the environment to force an IOException
        
        // Set as not configured
        configuredField.set(null, false);
        
        // Temporarily rename the logging.properties file to cause FileInputStream to fail
        Path configFile = Path.of("src/main/resources/logging.properties");
        Path backupPath = Path.of("src/main/resources/logging.properties.bak");
        
        boolean fileExists = Files.exists(configFile);
        if (fileExists) {
            Files.move(configFile, backupPath);
        }
        
        try {
            // Call init - this should fail with IOException when trying to load the config file
            LoggingConfig.init();
            
            // Verify error message was printed
            assertTrue(errContent.toString().contains("Failed to initialize logging configuration"), 
                    "Error message should be printed for IO exception");
        } finally {
            // Restore the original file
            if (fileExists && Files.exists(backupPath)) {
                Files.move(backupPath, configFile);
            }
        }
    }
}
