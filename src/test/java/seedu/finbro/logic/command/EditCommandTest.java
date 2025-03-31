package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EditCommandTest {
    private TransactionManager transactionManager;
    private Storage storage;

    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        storage = new Storage();

        // Add test transaction
        transactionManager.addTransaction(new Expense(100.0, "Test Expense",
                LocalDate.now(), Expense.Category.FOOD, List.of("test")));
    }

    /**
     * Helper method to create a UI with simulated inputs
     */
    private Ui createUiWithInputs(String... inputs) {
        String inputString = String.join(System.lineSeparator(), inputs);
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        return new Ui(new Scanner(inputStream));
    }

    @Test
    void execute_validEdit_success() {
        // Prepare parameters for editing
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "200.0");
        parameters.put("d", "Updated Expense");
        parameters.put("date", "2023-05-01");
        parameters.put("c", "FOOD");
        parameters.put("t", "updated,tag");

        // Create EditCommand with index 0
        EditCommand command = new EditCommand(0, parameters);

        // Create UI with simulated inputs if needed for interactive editing
        Ui ui = createUiWithInputs("200.0", "Updated Expense", "2023-05-01", "FOOD", "updated,tag");

        // Execute command
        String result = command.execute(transactionManager, ui, storage);

        // Check transaction was updated correctly
        Transaction editedTransaction = transactionManager.getTransaction(0);
        assertEquals(200.0, editedTransaction.getAmount());
        assertEquals("Updated Expense", editedTransaction.getDescription());
        assertEquals(LocalDate.parse("2023-05-01"), editedTransaction.getDate());

        // Check for success message
        assertTrue(result.contains("successfully"));
    }

    @Test
    void execute_invalidIndex_returnsErrorMessage() {
        // Create EditCommand with invalid index
        Map<String, String> parameters = new HashMap<>();
        parameters.put("d", "Updated description");

        EditCommand command = new EditCommand(999, parameters);
        Ui ui = createUiWithInputs();

        String result = command.execute(transactionManager, ui, storage);

        // Check for invalid index message
        assertTrue(result.contains("Invalid index"));
    }

    @Test
    void execute_invalidAmount_returnsInvalidCommand() {
        // Prepare parameters with invalid amount
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "invalid");

        EditCommand command = new EditCommand(0, parameters);
        Ui ui = createUiWithInputs();

        String result = command.execute(transactionManager, ui, storage);

        // Check for failure message
        assertTrue(result.contains("Failed to update transaction"));
    }

    @Test
    void execute_invalidDate_returnsInvalidCommand() {
        // Prepare parameters with invalid date format
        Map<String, String> parameters = new HashMap<>();
        parameters.put("date", "01-01-2023");

        EditCommand command = new EditCommand(0, parameters);
        Ui ui = createUiWithInputs();

        String result = command.execute(transactionManager, ui, storage);

        // Check for failure message
        assertTrue(result.contains("Failed to update transaction"));
    }

    @Test
    void execute_partialEdit_success() {
        // Add another transaction to edit
        transactionManager.addTransaction(new Expense(100.0, "Grocery",
                LocalDate.of(2023, 1, 1), Expense.Category.FOOD, List.of("food")));

        // Prepare parameters for partial edit
        Map<String, String> parameters = new HashMap<>();
        parameters.put("d", "Supermarket");
        parameters.put("t", "grocery,shopping");

        // Create EditCommand with index 1
        EditCommand command = new EditCommand(1, parameters);
        Ui ui = createUiWithInputs();

        // Execute command
        String result = command.execute(transactionManager, ui, storage);

        // Check only specified fields were updated
        Transaction editedTransaction = transactionManager.getTransaction(1);
        assertEquals(100.0, editedTransaction.getAmount()); // Unchanged
        assertEquals("Supermarket", editedTransaction.getDescription()); // Changed
        assertEquals(LocalDate.of(2023, 1, 1), editedTransaction.getDate()); // Unchanged

        // Check for success message
        assertTrue(result.contains("successfully"));
    }
}
