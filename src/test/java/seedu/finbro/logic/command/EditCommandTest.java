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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Contains unit tests for {@code EditCommand}.
 * Tests the functionality of editing transactions through the EditCommand.
 */
public class EditCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;
    private Transaction testIncome;
    private Transaction testExpense;

    /**
     * Sets up the test environment before each test.
     * Creates a transaction manager with test transactions,
     * and initializes UI and storage components.
     */
    @BeforeEach
    public void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();

        // Create test transactions
        List<String> tags = new ArrayList<>();
        tags.add("test");

        testIncome = new Income(100.0, "Test Income", LocalDate.now(), tags);
        testExpense = new Expense(50.0, "Test Expense", LocalDate.now(), Expense.Category.FOOD, tags);

        // Add transactions to the manager
        transactionManager.addTransaction(testIncome);
        transactionManager.addTransaction(testExpense);
    }

    /**
     * Tests that an Income transaction can be successfully edited
     * with new amount and description values.
     */
    @Test
    public void execute_editIncome_success() {
        // Create parameters to update
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "200.0");
        parameters.put("d", "Updated Income");

        // Create edit command
        EditCommand editCommand = new EditCommand("Test Income", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        // Verify results
        assertTrue(result.contains("Transaction updated successfully"));
        assertTrue(result.contains("200.0"));
        assertTrue(result.contains("Updated Income"));

        // Verify transaction was updated in the manager
        Transaction updatedTransaction = findTransactionByDescription("Updated Income");
        assertNotNull(updatedTransaction);
        assertEquals(200.0, updatedTransaction.getAmount());
        assertEquals("Updated Income", updatedTransaction.getDescription());
    }

    /**
     * Tests that an Expense transaction can be successfully edited
     * with new amount and category values.
     */
    @Test
    public void execute_editExpense_success() {
        // Create parameters to update category and amount
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "75.0");
        parameters.put("c", "TRANSPORT");

        // Create edit command
        EditCommand editCommand = new EditCommand("Test Expense", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        // Verify results
        assertTrue(result.contains("Transaction updated successfully"));

        // Find the updated expense in the list
        Transaction updatedTransaction = findTransactionByDescription("Test Expense");
        assertNotNull(updatedTransaction);
        assertTrue(updatedTransaction instanceof Expense);
        assertEquals(75.0, updatedTransaction.getAmount());
        assertEquals(Expense.Category.TRANSPORT, ((Expense) updatedTransaction).getCategory());
    }

    /**
     * Tests that transaction tags can be successfully edited.
     */
    @Test
    public void execute_editTags_success() {
        // Create parameters to update tags
        Map<String, String> parameters = new HashMap<>();
        parameters.put("t", "updated,tags");

        // Create edit command
        EditCommand editCommand = new EditCommand("Test Income", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        // Verify results
        assertTrue(result.contains("Transaction updated successfully"));

        // Verify tags were updated
        Transaction updatedTransaction = findTransactionByDescription("Test Income");
        assertNotNull(updatedTransaction);
        List<String> updatedTags = updatedTransaction.getTags();
        assertEquals(2, updatedTags.size());
        assertTrue(updatedTags.contains("updated"));
        assertTrue(updatedTags.contains("tags"));
    }

    /**
     * Tests that transaction date can be successfully edited.
     */
    @Test
    public void execute_editDate_success() {
        // Create parameters to update date
        Map<String, String> parameters = new HashMap<>();
        parameters.put("date", "2023-12-25"); // Use "date" as the key

        // Create edit command
        EditCommand editCommand = new EditCommand("Test Income", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        // Verify results
        assertTrue(result.contains("Transaction updated successfully"));

        // Find the updated transaction
        Transaction updatedTransaction = findTransactionByDescription("Test Income");
        assertNotNull(updatedTransaction);
        assertEquals(LocalDate.parse("2023-12-25"), updatedTransaction.getDate());
    }

    /**
     * Tests that an appropriate error message is returned when
     * trying to edit a non-existent transaction.
     */
    @Test
    public void execute_nonExistentTransaction_returnsErrorMessage() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "200.0");

        EditCommand editCommand = new EditCommand("Non-existent", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        // Match the exact error message from EditCommand
        assertEquals("Please provide a specific keyword that matches exactly one transaction.", result);
    }

    /**
     * Tests that an appropriate error message is returned when
     * multiple transactions match the search keyword.
     */
    @Test
    public void execute_multipleMatches_returnsErrorMessage() {
        // Add another income with similar description
        List<String> tags = new ArrayList<>();
        tags.add("test");
        Transaction anotherIncome = new Income(150.0, "Test Income Extra", LocalDate.now(), tags);
        transactionManager.addTransaction(anotherIncome);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "200.0");

        EditCommand editCommand = new EditCommand("Test", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        assertEquals("Please provide a specific keyword that matches exactly one transaction.", result);

        // Verify original transactions are still the same
        List<Transaction> transactions = transactionManager.listTransactions();
        assertEquals(3, transactions.size());
        assertEquals("Test Income", transactions.get(0).getDescription());
        assertEquals("Test Expense", transactions.get(1).getDescription());
        assertEquals("Test Income Extra", transactions.get(2).getDescription());
    }

    /**
     * Tests that an appropriate error message is returned when
     * invalid amount format is provided.
     */
    @Test
    public void execute_invalidAmountFormat_returnsErrorMessage() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "invalid");

        EditCommand editCommand = new EditCommand("Test Income", parameters);
        String result = editCommand.execute(transactionManager, ui, storage);

        assertEquals("Failed to update transaction.", result);

        // Verify original transactions are still the same
        Transaction originalTransaction = findTransactionByDescription("Test Income");
        assertNotNull(originalTransaction);
        assertEquals(100.0, originalTransaction.getAmount());
    }

    /**
     * Tests that isExit returns false for EditCommand.
     */
    @Test
    public void isExit_returnsFalse() {
        EditCommand editCommand = new EditCommand("test", new HashMap<>());
        assertFalse(editCommand.isExit());
    }

    /**
     * Helper method to find a transaction by its description.
     *
     * @param description The description to search for
     * @return The matching transaction or null if not found
     */
    private Transaction findTransactionByDescription(String description) {
        for (Transaction t : transactionManager.listTransactions()) {
            if (t.getDescription().equals(description)) {
                return t;
            }
        }
        return null;
    }
}

