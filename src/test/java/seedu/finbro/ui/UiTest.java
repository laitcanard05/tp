package seedu.finbro.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for Ui.
 * Tests the user interface functionality by capturing system output and
 * simulating user input to verify correct behavior.
 */
class UiTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private Ui ui;

    /**
     * Sets up the test environment before each test.
     * Redirects System.out to allow output verification.
     */
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Cleans up the test environment after each test.
     * Restores the original System.out and System.in.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Tests that the welcome message is displayed correctly.
     */
    @Test
    void showWelcome_shouldDisplayWelcomeMessage() {
        ui = new Ui(new Scanner(System.in));

        ui.showWelcome();

        String output = outContent.toString();
        assertTrue(output.contains("Welcome to FinBro"));
        assertTrue(output.contains("Type 'help' to see available commands"));
    }

    /**
     * Tests that the goodbye message is displayed correctly.
     */
    @Test
    void showGoodbye_shouldDisplayGoodbyeMessage() {
        ui = new Ui(new Scanner(System.in));

        ui.showGoodbye();

        String output = outContent.toString();
        assertTrue(output.contains("Thank you for using FinBro"));
        assertTrue(output.contains("Goodbye!"));
    }

    /**
     * Tests that regular messages are displayed correctly.
     */
    @Test
    void showMessage_shouldDisplayFormattedMessage() {
        ui = new Ui(new Scanner(System.in));
        String testMessage = "This is a test message";

        ui.showMessage(testMessage);

        String output = outContent.toString();
        assertTrue(output.contains(testMessage));
    }

    /**
     * Tests that error messages are displayed correctly with the ERROR prefix.
     */
    @Test
    void showError_shouldDisplayFormattedErrorMessage() {
        ui = new Ui(new Scanner(System.in));
        String errorMessage = "This is an error message";

        ui.showError(errorMessage);

        String output = outContent.toString();
        assertTrue(output.contains("ERROR: " + errorMessage));
    }

    /**
     * Tests that user input is correctly read by the readCommand method.
     */
    @Test
    void readCommand_shouldReturnUserInput() {
        String expectedInput = "add expense 50.00 food";
        ByteArrayInputStream inContent = new ByteArrayInputStream((expectedInput + "\n").getBytes());
        System.setIn(inContent);
        ui = new Ui(new Scanner(System.in));

        String result = ui.readCommand();
        assertEquals(expectedInput, result);
    }

    /**
     * Tests that the confirmation prompt returns true when user inputs 'y'.
     */
    @Test
    void readConfirmation_shouldReturnTrueForYesInput() {
        String input = "y\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        ui = new Ui(new Scanner(System.in));

        boolean result = ui.readConfirmation("Do you want to continue?");
        assertTrue(result);
    }

    /**
     * Tests that the confirmation prompt returns true when user inputs 'yes'.
     */
    @Test
    void readConfirmation_shouldReturnTrueForYesFullInput() {
        String input = "yes\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        ui = new Ui(new Scanner(System.in));

        boolean result = ui.readConfirmation("Do you want to continue?");
        assertTrue(result);
    }

    /**
     * Tests that the confirmation prompt returns false when user inputs anything other than 'y' or 'yes'.
     */
    @Test
    void readConfirmation_shouldReturnFalseForNonYesInput() {
        String input = "n\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        ui = new Ui(new Scanner(System.in));

        boolean result = ui.readConfirmation("Do you want to continue?");
        assertFalse(result);
    }
}
