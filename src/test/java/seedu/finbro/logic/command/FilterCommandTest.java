package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;
import seedu.finbro.model.Expense;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for ClearCommand.
 */
class FilterCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    /**
     * Tests that the execute method returns the message displaying the filtered transactions
     */
    @Test
    void execute_shouldDisplayFilteredTransactions() {
        transactionManager.addTransaction(new Expense(6.30, "lunch",
                LocalDate.parse("2025-03-14"), Expense.Category.FOOD, null));
        transactionManager.addTransaction(new Expense(14.00, "buy books",
                LocalDate.parse("2025-03-16"), Expense.Category.OTHERS, null));

        FilterCommand command = new FilterCommand(LocalDate.parse("2025-03-14"),
                LocalDate.parse("2025-03-15"));
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("Showing transactions from 2025-03-14 to 2025-03-15:\n" +
                "1. [Expense][Food] $6.30 - lunch", result);
    }
    
    /**
     * Tests that the execute method returns the correct message when no transactions are found
     */
    @Test
    void execute_noTransactionsFound_returnsEmptyMessage() {
        transactionManager.addTransaction(new Expense(6.30, "lunch",
                LocalDate.parse("2025-03-14"), Expense.Category.FOOD, null));
        transactionManager.addTransaction(new Expense(14.00, "buy books",
                LocalDate.parse("2025-03-16"), Expense.Category.OTHERS, null));

        FilterCommand command = new FilterCommand(LocalDate.parse("2025-02-01"),
                LocalDate.parse("2025-02-28"));
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("There are no transactions between Feb 1 2025 and Feb 28 2025.", result);
    }

    /**
     * Tests that isExit returns false.
     */
    @Test
    void isExit_returnsFalse() {
        FilterCommand command = new FilterCommand(LocalDate.parse("2025-03-14"),
                LocalDate.parse("2025-03-15"));
        assertFalse(command.isExit());
    }
}

