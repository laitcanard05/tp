package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the ListCommand class.
 */
class ListCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;
    private LocalDate today;
    private LocalDate yesterday;

    @BeforeEach
    public void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
        today = LocalDate.now();
        yesterday = today.minusDays(1);

        // Add some test transactions with different dates
        Income income1 = new Income(1000, "Salary", today, Collections.emptyList());
        Income income2 = new Income(500, "Bonus", yesterday, Collections.emptyList());
        Expense expense1 = new Expense(50, "Groceries", today, Expense.Category.FOOD, Collections.emptyList());
        Expense expense2 = new Expense(20, "Bus fare", yesterday, Expense.Category.TRANSPORT, Collections.emptyList());

        transactionManager.addTransaction(income1);
        transactionManager.addTransaction(income2);
        transactionManager.addTransaction(expense1);
        transactionManager.addTransaction(expense2);
    }

    @Test
    void executeNoParametersListsAllTransactions() {
        ListCommand command = new ListCommand(null, null);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        // Check income transaction formatting
        assertTrue(result.contains("1."));
        assertTrue(result.contains("[Income]"));
        assertTrue(result.contains("Salary"));
        assertTrue(result.contains("(Date created: " + today + ")"));
        assertTrue(result.contains("$1,000.00") || result.contains("$1000"));
        
        // Check expense transaction formatting
        assertTrue(result.contains("2."));
        assertTrue(result.contains("[Expense][Food]"));
        assertTrue(result.contains("Groceries"));
        assertTrue(result.contains("$50.00") || result.contains("$50"));
        
        // Check other transactions are present
        assertTrue(result.contains("Bonus"));
        assertTrue(result.contains("Bus fare"));
        assertTrue(result.contains("$500"));
        assertTrue(result.contains("$20"));
    }

    @Test
    void executeWithLimitListsLimitedTransactions() {
        ListCommand command = new ListCommand(2, null);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        // Check first two transactions are present
        assertTrue(result.contains("1."));
        assertTrue(result.contains("Salary"));
        assertTrue(result.contains("2."));
        assertTrue(result.contains("Groceries"));
        
        // Ensure third transaction is not present
        assertFalse(result.contains("3."));
        assertFalse(result.contains("Bonus"));
    }

    @Test
    void executeWithDateListsFilteredTransactions() {
        ListCommand command = new ListCommand(null, today);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        // Check only today's transactions are present
        assertTrue(result.contains("Salary"));
        assertTrue(result.contains("Groceries"));
        
        // Yesterday's transactions should not be present
        assertFalse(result.contains("Bonus"));
        assertFalse(result.contains("Bus fare"));
    }

    @Test
    void executeWithDateAndLimitListsFilteredLimitedTransactions() {
        ListCommand command = new ListCommand(1, today);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        // Only first transaction from today should be present
        assertTrue(result.contains("1."));
        assertTrue(result.contains("Salary"));
        
        // Second transaction from today should not be present
        assertFalse(result.contains("2."));
        assertFalse(result.contains("Groceries"));
        
        // Yesterday's transactions should not be present
        assertFalse(result.contains("Bonus"));
        assertFalse(result.contains("Bus fare"));
    }

    @Test
    void executeEmptyTransactionManagerNoTransactionsFound() {
        transactionManager = new TransactionManager(); // Empty transaction manager
        ListCommand command = new ListCommand(null, null);
        String result = command.execute(transactionManager, ui, storage);
        
        assertEquals("No transactions found.", result);
    }

    @Test
    void executeWithDateNoMatchingTransactionsFound() {
        ListCommand command = new ListCommand(null, today.plusDays(10));
        String result = command.execute(transactionManager, ui, storage);
        
        assertEquals("No transactions found.", result);
    }

    @Test
    void isExitReturnsFalse() {
        ListCommand command = new ListCommand(null, null);
        assertFalse(command.isExit());
    }
}
