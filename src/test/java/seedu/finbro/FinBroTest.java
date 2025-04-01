package seedu.finbro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.command.ExitCommand;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive test class for the FinBro application.
 * Tests all methods and execution paths in the FinBro class.
 *
 * @author alanwang
 */
class FinBroTest {
    // Test doubles
    private TestUi testUi;
    private TestStorage testStorage;
    private TestParser testParser;
    private TransactionManager testTransactionManager;

    // Original standard streams
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Captures for testing output
    private ByteArrayOutputStream outputCapture;

    private FinBro finBro;

    @BeforeEach
    void setUp() {
        // Initialize output capture
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));

        // Initialize test components
        testTransactionManager = new TransactionManager();
        testUi = new TestUi();
        testStorage = new TestStorage();
        testParser = new TestParser();

        // Set default behavior
        testStorage.shouldThrowException = false;
        testStorage.returnedTransactionManager = testTransactionManager;
    }

    /**
     * Custom Ui implementation for testing.
     */
    private static class TestUi extends Ui {
        private final List<String> commandInputs = new ArrayList<>();
        private final List<String> displayedMessages = new ArrayList<>();
        private final List<String> displayedErrors = new ArrayList<>();
        private int currentCommandIndex = 0;

        @Override
        public String readCommand() {
            if (currentCommandIndex < commandInputs.size()) {
                return commandInputs.get(currentCommandIndex++);
            }
            return "exit"; // Default to exit if no more commands
        }

        @Override
        public void showMessage(String message) {
            displayedMessages.add(message);
        }

        @Override
        public void showError(String message) {
            displayedErrors.add(message);
        }

        @Override
        public void showWelcome() {
            // Capture welcome message display
            displayedMessages.add("WELCOME");
        }

        @Override
        public void showGoodbye() {
            // Capture goodbye message display
            displayedMessages.add("GOODBYE");
        }

        public void addCommandInput(String command) {
            commandInputs.add(command);
        }
    }

    /**
     * Custom Storage implementation for testing.
     */
    private static class TestStorage extends Storage {
        public boolean shouldThrowException = false;
        public TransactionManager returnedTransactionManager;
        public boolean loadBudgetsCalled = false;
        public boolean loadSavingsGoalsCalled = false;
        public boolean saveTransactionsCalled = false;

        @Override
        public TransactionManager loadTransactions() {
            if (shouldThrowException) {
                throw new RuntimeException("Test exception");
            }
            return returnedTransactionManager;
        }

        @Override
        public void loadBudgets(TransactionManager transactionManager) {
            loadBudgetsCalled = true;
        }

        @Override
        public void loadSavingsGoals(TransactionManager transactionManager) {
            loadSavingsGoalsCalled = true;
        }

        @Override
        public void saveTransactions(TransactionManager transactionManager) {
            saveTransactionsCalled = true;
        }
    }

    /**
     * Custom Parser implementation for testing.
     */
    private static class TestParser extends Parser {
        public boolean throwExceptionOnNextParse = false;
        private final List<Command> commandSequence = new ArrayList<>();
        private int currentCommandIndex = 0;

        @Override
        public Command parseCommandWord(String commandWord, Ui ui) {
            if (throwExceptionOnNextParse) {
                throwExceptionOnNextParse = false;
                throw new RuntimeException("Command error");
            }

            if (currentCommandIndex < commandSequence.size()) {
                return commandSequence.get(currentCommandIndex++);
            }

            // Default to exit command if no commands are configured
            return new ExitCommand();
        }

        public void addCommandToSequence(Command command) {
            commandSequence.add(command);
        }
    }

    /**
     * Test command implementation for configurable behavior.
     */
    private static class TestCommand implements Command {
        private final String result;
        private final boolean isExitFlag;

        public TestCommand(String result, boolean isExitFlag) {
            this.result = result;
            this.isExitFlag = isExitFlag;
        }

        @Override
        public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
            return result;
        }

        @Override
        public boolean isExit() {
            return isExitFlag;
        }
    }

    /**
     * Tests the default constructor to ensure proper initialization.
     */
    @Test
    void testConstructor() {
        // Test the default constructor
        FinBro finBro = new FinBro();
        assertNotNull(finBro);
    }

    /**
     * Tests the complete application flow from start to exit.
     */
    @Test
    void testRun() throws Exception {
        // Create a FinBro instance
        finBro = new FinBro();

        // Create test commands
        TestCommand regularCommand = new TestCommand("Regular command executed", false);
        TestCommand exitCommand = new TestCommand("Exit command executed", true);

        // Setup test doubles
        testUi.addCommandInput("regular-command");
        testUi.addCommandInput("exit-command");

        testParser.addCommandToSequence(regularCommand);
        testParser.addCommandToSequence(exitCommand);

        // Use reflection to set test doubles
        setPrivateField(finBro, "ui", testUi);
        setPrivateField(finBro, "storage", testStorage);
        setPrivateField(finBro, "parser", testParser);

        // Run the application
        finBro.run();

        // Verify expected behavior
        assertTrue(testUi.displayedMessages.contains("WELCOME"));
        assertTrue(testStorage.loadBudgetsCalled);
        assertTrue(testStorage.loadSavingsGoalsCalled);
        assertTrue(testUi.displayedMessages.contains("Regular command executed"));
        assertTrue(testUi.displayedMessages.contains("Exit command executed"));
    }

    /**
     * Tests the start method when an exception occurs during data loading.
     */
    @Test
    void testStartWithException() throws Exception {
        // Create a FinBro instance
        finBro = new FinBro();

        // Setup test storage to throw exception
        testStorage.shouldThrowException = true;

        // Use reflection to set test doubles
        setPrivateField(finBro, "ui", testUi);
        setPrivateField(finBro, "storage", testStorage);

        // Call start method directly using reflection
        Method startMethod = FinBro.class.getDeclaredMethod("start");
        startMethod.setAccessible(true);
        startMethod.invoke(finBro);

        // Verify error handling
        assertTrue(testUi.displayedMessages.contains("WELCOME"));
        assertTrue(testUi.displayedErrors.get(0).contains("Problem loading data: Test exception"));
    }

    /**
     * Tests the runCommandLoop method's exception handling.
     */
    @Test
    void testRunCommandLoopException() throws Exception {
        // Create a FinBro instance
        finBro = new FinBro();

        // Setup test doubles
        testUi.addCommandInput("error-command");
        testUi.addCommandInput("exit-command");

        testParser.throwExceptionOnNextParse = true;
        testParser.addCommandToSequence(new ExitCommand());

        // Use reflection to set test doubles
        setPrivateField(finBro, "ui", testUi);
        setPrivateField(finBro, "storage", testStorage);
        setPrivateField(finBro, "parser", testParser);
        setPrivateField(finBro, "transactionManager", testTransactionManager);

        // Call runCommandLoop method directly using reflection
        Method runCommandLoopMethod = FinBro.class.getDeclaredMethod("runCommandLoop");
        runCommandLoopMethod.setAccessible(true);
        runCommandLoopMethod.invoke(finBro);

        // Verify error handling
        assertTrue(testUi.displayedErrors.size() > 0);
        assertTrue(testUi.displayedErrors.get(0).contains("An error occurred"));
    }

    /**
     * Tests the exit method.
     */
    @Test
    void testExit() throws Exception {
        // Create a FinBro instance
        finBro = new FinBro();

        // Use reflection to set test doubles
        setPrivateField(finBro, "ui", testUi);

        // Call exit method directly using reflection
        Method exitMethod = FinBro.class.getDeclaredMethod("exit");
        exitMethod.setAccessible(true);
        exitMethod.invoke(finBro);

        // Method only logs, so we just verify coverage
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
