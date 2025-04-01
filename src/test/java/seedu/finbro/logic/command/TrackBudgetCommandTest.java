package seedu.finbro.logic.command;

import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackBudgetCommandTest {

    @Test
    void parseCommandWord_trackBudgetCommand_success() {
        // Prepare input stream with simulated user inputs
        String simulatedUserInput = "5\n2024\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof TrackBudgetCommand);
    }

    @Test
    void parseCommandWord_trackBudgetCommandEmptyDate_success() {
        // Prepare input stream with empty month/year inputs (using default values)
        String simulatedUserInput = "\n\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof TrackBudgetCommand);
    }

    @Test
    void parseCommandWord_invalidMonth_returnsInvalidCommand() {
        // Prepare input stream with invalid month (13), valid year
        String simulatedUserInput = "13\n2024\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_invalidYear_returnsInvalidCommand() {
        // Prepare input stream with valid month but future year (invalid)
        String simulatedUserInput = "5\n9999\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_yearTooLow_returnsInvalidCommand() {
        // Prepare input stream with year below lower bound
        String simulatedUserInput = "5\n999\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_nonNumericMonth_returnsInvalidCommand() {
        // Prepare input stream with non-numeric month
        String simulatedUserInput = "abc\n2024\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_nonNumericYear_returnsInvalidCommand() {
        // Prepare input stream with non-numeric year
        String simulatedUserInput = "5\nabc\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("trackbudget", ui);

        // Verify command type
        assertTrue(command instanceof InvalidCommand);
    }
}
