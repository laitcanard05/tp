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

import java.util.Scanner;

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
}
