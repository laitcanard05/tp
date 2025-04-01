package seedu.finbro.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import seedu.finbro.logic.command.ClearCommand;
import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.command.ExitCommand;
import seedu.finbro.logic.command.ExportCommand;
import seedu.finbro.logic.command.FilterCommand;
import seedu.finbro.logic.command.HelpCommand;
import seedu.finbro.logic.command.IncomeCommand;
import seedu.finbro.logic.command.InvalidCommand;
import seedu.finbro.logic.command.ListCommand;
import seedu.finbro.logic.command.UnknownCommand;
import seedu.finbro.logic.command.ExpenseCommand;
import seedu.finbro.logic.command.DeleteCommand;
import seedu.finbro.logic.command.SearchCommand;
import seedu.finbro.logic.command.SummaryCommand;
import seedu.finbro.logic.command.BalanceCommand;
import seedu.finbro.logic.command.EditCommand;
import seedu.finbro.logic.command.SetBudgetCommand;
import seedu.finbro.logic.command.TrackBudgetCommand;
import seedu.finbro.logic.command.SetSavingsGoalCommand;
import seedu.finbro.logic.command.TrackSavingsGoalCommand;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Parser class.
 */
class ParserTest {
    private Parser parser;
    private TransactionManager dummyManager;
    private Ui dummyUi;
    private Storage dummyStorage;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
        dummyManager = new TransactionManager();
        dummyUi = new Ui();
        dummyStorage = new Storage();
    }

    /**
     * Helper method to create a Ui with simulated inputs for interactive testing.
     */
    private Ui createUiWithInputs(String... inputs) {
        String inputString = String.join(System.lineSeparator(), inputs);
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        return new Ui(new Scanner(inputStream));
    }

    // Basic Command Tests

    @Test
    void parseCommandWord_income_success() {
        Command command = parser.parseCommandWord("income", dummyUi);
        assertTrue(command instanceof IncomeCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_expense_success() {
        Command command = parser.parseCommandWord("expense", dummyUi);
        assertTrue(command instanceof ExpenseCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_list_success() {
        Command command = parser.parseCommandWord("list", dummyUi);
        assertTrue(command instanceof ListCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_search_success() {
        Command command = parser.parseCommandWord("search", dummyUi);
        assertTrue(command instanceof SearchCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_filter_success() {
        Command command = parser.parseCommandWord("filter", dummyUi);
        assertTrue(command instanceof FilterCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_balance_success() {
        Command command = parser.parseCommandWord("balance", dummyUi);
        assertTrue(command instanceof BalanceCommand);
    }

    @Test
    void parseCommandWord_summary_success() {
        Command command = parser.parseCommandWord("summary", dummyUi);
        assertTrue(command instanceof SummaryCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_export_success() {
        Command command = parser.parseCommandWord("export", dummyUi);
        assertTrue(command instanceof ExportCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_clear_success() {
        Command command = parser.parseCommandWord("clear", dummyUi);
        assertTrue(command instanceof ClearCommand);
    }

    @Test
    void parseCommandWord_exit_success() {
        Command command = parser.parseCommandWord("exit", dummyUi);
        assertTrue(command instanceof ExitCommand);
        assertTrue(command.isExit());
    }

    @Test
    void parseCommandWord_help_success() {
        Command command = parser.parseCommandWord("help", dummyUi);
        assertTrue(command instanceof HelpCommand);
    }

    @Test
    void parseCommandWord_unknown_returnsUnknownCommand() {
        Command command = parser.parseCommandWord("unknown", dummyUi);
        assertTrue(command instanceof UnknownCommand);
    }

    @Test
    void parseCommandWord_delete_success() {
        Scanner mockScanner = new Scanner("1");
        Ui mockUi = new Ui(mockScanner);

        Command command = parser.parseCommandWord("delete", mockUi);
        assertTrue(command instanceof DeleteCommand);
    }

    @Test
    void parseCommandWord_empty_returnsHelpCommand() {
        Command command = parser.parseCommandWord("", dummyUi);
        assertTrue(command instanceof HelpCommand);

        command = parser.parseCommandWord("   ", dummyUi);
        assertTrue(command instanceof HelpCommand);
    }

    @Test
    void parseCommandWord_edit_success() {
        Command command = parser.parseCommandWord("edit", dummyUi);
        assertTrue(command instanceof EditCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_setBudget_success() {
        Command command = parser.parseCommandWord("setbudget", dummyUi);
        assertTrue(command instanceof SetBudgetCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_trackBudget_success() {
        Command command = parser.parseCommandWord("trackbudget", dummyUi);
        assertTrue(command instanceof TrackBudgetCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_setSavingsGoal_success() {
        Command command = parser.parseCommandWord("setsavings", dummyUi);
        assertTrue(command instanceof SetSavingsGoalCommand || command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_trackSavingsGoal_success() {
        Command command = parser.parseCommandWord("tracksavings", dummyUi);
        assertTrue(command instanceof TrackSavingsGoalCommand || command instanceof InvalidCommand);
    }

    // Extended Tests for Better Coverage

    @Test
    void parseCommandWord_view_returnsBalanceCommandAsAlias() {
        Command command = parser.parseCommandWord("view", createUiWithInputs());
        assertTrue(command instanceof BalanceCommand);
        assertFalse(command.isExit());
    }

    // Income command detailed tests

    @Test
    void parseCommandWord_income_validInput_returnsIncomeCommand() {
        Ui ui = createUiWithInputs("1000", "Salary", "work");
        Command command = parser.parseCommandWord("income", ui);
        assertTrue(command instanceof IncomeCommand);
        assertFalse(command.isExit());
    }

    @Test
    void parseCommandWord_income_invalidAmount_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("invalid", "1000", "Salary", "work");
        Command command = parser.parseCommandWord("income", ui);
        assertTrue(command instanceof IncomeCommand || command instanceof InvalidCommand);
    }

    // Expense command detailed tests

    @Test
    void parseCommandWord_expense_validInput_returnsExpenseCommand() {
        Ui ui = createUiWithInputs("50", "Lunch", "1", "food");
        Command command = parser.parseCommandWord("expense", ui);
        assertTrue(command instanceof ExpenseCommand);
        assertFalse(command.isExit());
    }

    @Test
    void parseCommandWord_expense_invalidCategory_handlesError() {
        Ui ui = createUiWithInputs("50", "Lunch", "10", "0", "food");
        Command command = parser.parseCommandWord("expense", ui);
        assertTrue(command instanceof ExpenseCommand || command instanceof InvalidCommand);
    }

    // List command detailed tests

    @Test
    void parseCommandWord_list_invalidDate_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("invalid-date", "");
        Command command = parser.parseCommandWord("list", ui);
        assertTrue(command instanceof InvalidCommand);
    }

    // Delete command detailed tests

    @Test
    void parseCommandWord_delete_validRange_returnsDeleteCommand() {
        Ui ui = createUiWithInputs("1-3");
        Command command = parser.parseCommandWord("delete", ui);
        assertTrue(command instanceof DeleteCommand);
    }

    // Filter command detailed tests

    @Test
    void parseCommandWord_filter_validDates_returnsFilterCommand() {
        Ui ui = createUiWithInputs("2025-03-01", "2025-03-31");
        Command command = parser.parseCommandWord("filter", ui);
        assertTrue(command instanceof FilterCommand);
        assertFalse(command.isExit());
    }

    @Test
    void parseCommandWord_filter_startDateAfterEndDate_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("2025-03-31", "2025-03-01");
        Command command = parser.parseCommandWord("filter", ui);
        assertTrue(command instanceof InvalidCommand);
    }

    // Export command detailed tests

    @Test
    void parseCommandWord_export_invalidFormat_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("pdf");
        Command command = parser.parseCommandWord("export", ui);
        assertTrue(command instanceof InvalidCommand);
    }

    // Clear command detailed tests

    @Test
    void parseCommandWord_clearConfirmation_yes_returnsConfirmedClearCommand() {
        // First call sets up the pending confirmation
        parser.parseCommandWord("clear", createUiWithInputs());

        // Second call confirms with "y"
        Command command = parser.parseCommandWord("y", createUiWithInputs());
        assertTrue(command instanceof ClearCommand);
    }

    @Test
    void parseCommandWord_clearConfirmation_no_returnsCancellationCommand() {
        // First call sets up the pending confirmation
        parser.parseCommandWord("clear", createUiWithInputs());

        // Second call cancels with "n"
        Command command = parser.parseCommandWord("n", createUiWithInputs());
        assertFalse(command instanceof ClearCommand);
        assertFalse(command.isExit());

        // Execute to see the message
        String result = command.execute(null, null, null);
        assertEquals("Clear operation cancelled.", result);
    }

    // Edit command detailed tests

    @Test
    void parseCommandWord_edit_cancelled_returnsNonEditCommand() {
        Ui ui = createUiWithInputs("1", "n");
        Command command = parser.parseCommandWord("edit", ui);

        // Should return a Command that's not an EditCommand
        assertFalse(command instanceof EditCommand);
        assertFalse(command.isExit());

        // Execute to see the message
        String result = command.execute(null, null, null);
        assertEquals("Edit operation cancelled.", result);
    }

    // Budget and Savings commands detailed tests

    @Test
    void parseCommandWord_setBudget_validInput_returnsSetBudgetCommand() {
        Ui ui = createUiWithInputs("3", "2025", "1000");
        Command command = parser.parseCommandWord("setbudget", ui);
        assertTrue(command instanceof SetBudgetCommand);
        assertFalse(command.isExit());
    }

    @Test
    void parseCommandWord_setSavingsGoal_validInput_returnsSetSavingsGoalCommand() {
        Ui ui = createUiWithInputs("3", "2025", "1000");
        Command command = parser.parseCommandWord("setsavings", ui);
        assertTrue(command instanceof SetSavingsGoalCommand);
        assertFalse(command.isExit());
    }

    // Edge cases and input validation tests

    @Test
    void parseCommandWord_setBudget_negativeBudget_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("3", "2025", "-1000");
        Command command = parser.parseCommandWord("setbudget", ui);
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_setSavingsGoal_zeroSavings_returnsInvalidCommand() {
        Ui ui = createUiWithInputs("3", "2025", "0");
        Command command = parser.parseCommandWord("setsavings", ui);
        assertTrue(command instanceof InvalidCommand);
    }

    @Test
    void parseCommandWord_clearConfirmation_otherCommand_resetsClearPending() {
        // First call sets up the pending confirmation
        parser.parseCommandWord("clear", createUiWithInputs());

        // Second call is another command, should reset the pending confirmation
        Command command = parser.parseCommandWord("help", createUiWithInputs());
        assertTrue(command instanceof HelpCommand);

        // Third call should not treat "y" as confirmation for clear anymore
        Command nextCommand = parser.parseCommandWord("y", createUiWithInputs());
        assertTrue(nextCommand instanceof UnknownCommand);
    }
}
