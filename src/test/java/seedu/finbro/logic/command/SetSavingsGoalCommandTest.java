package seedu.finbro.logic.command;

import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SetSavingsGoalCommandTest {

    @Test
    void parseCommandWord_validInputs_success() {
        // Prepare input stream with simulated user inputs
        String simulatedUserInput = "5\n2024\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof SetSavingsGoalCommand);
    }

    @Test
    void parseCommandWord_emptyDateUsesCurrent_success() {
        // Prepare input stream with empty month/year inputs
        String simulatedUserInput = "\n\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof SetSavingsGoalCommand);
    }

    @Test
    void parseCommandWord_invalidMonth_returnsInvalidCommand() {
        // Prepare input stream with invalid month
        String simulatedUserInput = "13\n2024\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_invalidYear_returnsInvalidCommand() {
        // Prepare input stream with future year (invalid)
        String simulatedUserInput = "5\n9999\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_yearTooLow_returnsInvalidCommand() {
        // Prepare input stream with year below lower bound
        String simulatedUserInput = "5\n999\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_negativeSavingsGoal_returnsInvalidCommand() {
        // Redirect System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Prepare input stream with negative savings goal
        String simulatedUserInput = "5\n2024\n-100\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Restore original System.out
        System.setOut(originalOut);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_zeroSavingsGoal_returnsInvalidCommand() {
        // Prepare input stream with zero savings goal
        String simulatedUserInput = "5\n2024\n0\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_nonNumericMonth_returnsInvalidCommand() {
        // Prepare input stream with non-numeric month
        String simulatedUserInput = "abc\n2024\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_nonNumericYear_returnsInvalidCommand() {
        // Prepare input stream with non-numeric year
        String simulatedUserInput = "5\nabc\n1000\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_nonNumericSavingsGoal_returnsInvalidCommand() {
        // Prepare input stream with non-numeric savings goal
        String simulatedUserInput = "5\n2024\nabc\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("setsavings", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }
}
