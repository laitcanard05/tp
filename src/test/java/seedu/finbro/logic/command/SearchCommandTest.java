package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for SearchCommand.
 */
class SearchCommandTest {
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
     * Tests that the execute method returns the transactions containing the keyword.
     */
    @Test
    void execute_shouldDisplayTransactionsContainingKeyword() {
        // Add test transactions
        transactionManager.addTransaction(new Expense(100.0, "Grocery shopping",
                Expense.Category.fromString("Food"), Collections.emptyList()));
        transactionManager.addTransaction(new Expense(50.0, "Lunch with colleagues",
                Expense.Category.fromString("Food"), Collections.emptyList()));
        transactionManager.addTransaction(new Income(1000.0, "Monthly salary",
                Collections.emptyList()));

        // Search for "Lunch"
        SearchCommand command = new SearchCommand("Lunch");
        String result = command.execute(transactionManager, ui, storage);

        // Verify only the lunch transaction is returned
        assertEquals("[Expense][Food] $50.00 - Lunch with colleagues", result);
    }

    /**
     * Tests that execute method handles empty transaction list.
     */
    @Test
    void execute_emptyList_returnsNoTransactionsMessage() {
        // Search in an empty transaction manager
        SearchCommand command = new SearchCommand("keyword");
        String result = command.execute(transactionManager, ui, storage);

        // Verify appropriate message is returned
        assertEquals("No transactions found.", result);
    }

    /**
     * Tests that execute method returns multiple matching transactions on separate lines.
     */
    @Test
    void execute_multipleMatches_returnsAllMatchingTransactions() {
        // Add test transactions with common keyword
        transactionManager.addTransaction(new Expense(100.0, "Grocery bill",
                Expense.Category.fromString("Food"), Collections.emptyList()));
        transactionManager.addTransaction(new Expense(200.0, "Utility bill",
                Expense.Category.fromString("Bills"), Collections.emptyList()));

        // Search for common keyword "bill"
        SearchCommand command = new SearchCommand("bill");
        String result = command.execute(transactionManager, ui, storage);

        // Verify all matching transactions are returned with newline separator
        String expected = "[Expense][Food] $100.00 - Grocery bill\n" +
                "[Expense][Bills] $200.00 - Utility bill";
        assertEquals(expected, result);
    }

    /**
     * Tests that execute method handles transactions with tags correctly.
     */
    @Test
    void execute_transactionsWithTags_formatsOutputCorrectly() {
        // Add test transaction with tags
        List<String> tags = new ArrayList<>();
        tags.add("essential");

        transactionManager.addTransaction(new Expense(120.0, "Grocery shopping",
                Expense.Category.fromString("Food"), tags));

        // Search for the transaction
        SearchCommand command = new SearchCommand("Grocery");
        String result = command.execute(transactionManager, ui, storage);

        // Verify the transaction is formatted correctly with tags
        assertEquals("[Expense][Food] $120.00 - Grocery shopping [essential]", result);
    }

    /**
     * Tests that isExit returns false.
     */
    @Test
    void isExit_returnsFalse() {
        SearchCommand command = new SearchCommand("keyword");
        assertFalse(command.isExit());
    }
}
