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
        assertTrue(result.contains("1. [Income] $1000.00 - Salary (Date created: " + today + ")"));
        assertTrue(result.contains("2. [Expense][Food] $50.00 - Groceries (Date created: " + today + ")"));
        assertTrue(result.contains("3. [Income] $500.00 - Bonus (Date created: " + yesterday + ")"));
        assertTrue(result.contains("4. [Expense][Transport] $20.00 - Bus fare (Date created: " + yesterday + ")"));
    }

    @Test
    void executeWithLimitListsLimitedTransactions() {
        ListCommand command = new ListCommand(2, null);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        assertTrue(result.contains("1. [Income] $1000.00 - Salary (Date created: " + today + ")"));
        assertTrue(result.contains("2. [Expense][Food] $50.00 - Groceries (Date created: " + today + ")"));
        assertFalse(result.contains("3. [Income] $500.00 - Bonus  (Date created: " + yesterday + ")"));
    }

    @Test
    void executeWithDateListsFilteredTransactions() {
        ListCommand command = new ListCommand(null, today);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        assertTrue(result.contains("1. [Income] $1000.00 - Salary (Date created: " + today + ")"));
        assertTrue(result.contains("2. [Expense][Food] $50.00 - Groceries (Date created: " + today + ")"));
        assertFalse(result.contains("Bonus"));
        assertFalse(result.contains("Bus fare"));
    }

    @Test
    void executeWithDateAndLimitListsFilteredLimitedTransactions() {
        ListCommand command = new ListCommand(1, today);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Here are your transactions"));
        assertTrue(result.contains("1. [Income] $1000.00 - Salary (Date created: " + today + ")"));
        assertFalse(result.contains("Groceries"));
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
