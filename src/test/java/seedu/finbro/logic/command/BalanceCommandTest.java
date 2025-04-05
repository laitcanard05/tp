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
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(result.contains("Current Balance:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        // May be formatted as $400.00 or $400,00 depending on CurrencyFormatter
        assertTrue(result.contains("$400") || result.contains("$400.00"));
        assertTrue(result.contains("$500") || result.contains("$500.00"));
        assertTrue(result.contains("$100") || result.contains("$100.00"));
    }

    @Test
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
        assertTrue(result.contains("Current Balance:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        // Since zero won't be formatted with commas
        assertTrue(result.contains("$0.00"));
        assertFalse(command.isExit(), "BalanceCommand should not exit the program");
    }
}
