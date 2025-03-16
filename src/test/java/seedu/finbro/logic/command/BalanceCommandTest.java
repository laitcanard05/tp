package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for BalanceCommand.
 */
class BalanceCommandTest {
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
     * Tests that the execute method returns the correct balance information.
     * Adds income and expense transactions and verifies the balance, total income,
     * and total expenses are correctly calculated and formatted.
     */
    @Test
    void execute_returnsCorrectBalance() {
        List<String> tags = new ArrayList<>();
        tags.add("Test");

        Transaction income = new Income(500.0, "Salary", LocalDate.now(), tags);
        Transaction expense = new Expense(100.0, "Groceries", Expense.Category.fromString("Food"), tags);

        transactionManager.addTransaction(income);
        transactionManager.addTransaction(expense);

        BalanceCommand command = new BalanceCommand();
        String result = command.execute(transactionManager, ui, storage);

        String expected = "Current Balance: $400.00\n" +
                "Total Income: $500.00\n" +
                "Total Expenses: $100.00";

        assertEquals(expected, result);
    }

    void execute_noTransactions_returnsZeroBalance() {
        // Ensure no transactions are in the system (default state)
        assertEquals(0, transactionManager.getTransactionCount(),
                "Transaction count should be zero at start");

        // Execute the command and verify results
        BalanceCommand command = new BalanceCommand();
        assertNotNull(command, "BalanceCommand should not be null");

        String result = command.execute(transactionManager, ui, storage);
        assertNotNull(result, "Command result should not be null");

        // Verify zero balance output format
        String expected = "Current Balance: $0.00\n" +
                "Total Income: $0.00\n" +
                "Total Expenses: $0.00";

        assertEquals(expected, result, "Balance should show zeroes when no transactions exist");
        assertFalse(command.isExit(), "BalanceCommand should not exit the program");
    }
}
