package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void execute_validEdit_success() {
        // Prepare input stream with simulated user inputs
        String simulatedUserInput = "Test Expense\n" + // Keyword
                "200.0\n" + // New amount
                "Updated Expense\n" + // New description
                "2023-05-01\n" + // New date
                "1\n" + // New category (FOOD = 1)
                "updated,tag\n"; // New tags

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        // Parse command using parser
        Command command = parser.parseCommandWord("edit", ui);
        String result = command.execute(transactionManager, ui, storage);

        // Check transaction was updated correctly
        Transaction editedTransaction = transactionManager.getTransaction(0);
        assertEquals(200.0, editedTransaction.getAmount());
        assertEquals("Updated Expense", editedTransaction.getDescription());
        assertEquals(LocalDate.parse("2023-05-01"), editedTransaction.getDate());

        // Check for any success message, not specifically "Transaction edited"
        assertTrue(result.contains("successfully") || result.contains("Successfully") ||
                result.contains("updated") || result.contains("Updated") ||
                result.contains("edited") || result.contains("Edited"));
    }

    @Test
    void execute_partialEdit_success() {
        // Add another transaction to edit
        transactionManager.addTransaction(new Expense(100.0, "Grocery",
                LocalDate.of(2023, 1, 1), Expense.Category.FOOD, List.of("food")));

        // Prepare input stream with simulated user inputs
        String simulatedUserInput = "Grocery\n" + // Keyword
                "\n" + // Skip amount
                "Supermarket\n" + // New description
                "\n" + // Skip date
                "\n" + // Skip category
                "grocery,shopping\n"; // New tags

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        // Parse command using parser
        Command command = parser.parseCommandWord("edit", ui);
        String result = command.execute(transactionManager, ui, storage);

        // Check only specified fields were updated
        Transaction editedTransaction = transactionManager.findTransactionByDescription("Supermarket");
        assertEquals(100.0, editedTransaction.getAmount()); // Unchanged
        assertEquals("Supermarket", editedTransaction.getDescription()); // Changed
        assertEquals(LocalDate.of(2023, 1, 1), editedTransaction.getDate()); // Unchanged

        // Check for any success message with more flexible pattern matching
        assertTrue(result.contains("successfully") || result.contains("Successfully") ||
                result.contains("updated") || result.contains("Updated") ||
                result.contains("edited") || result.contains("Edited"));
    }

    @Test
    void execute_invalidAmount_returnsInvalidCommand() {
        // Prepare input stream with invalid amount
        String simulatedUserInput = "Test Expense\n" + // Keyword
                "invalid\n" + // Invalid amount
                "\n\n\n\n"; // Skip other fields

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("edit", ui);

        assertTrue(command instanceof InvalidCommand);
        String result = command.execute(transactionManager, ui, storage);
        assertTrue(result.contains("Invalid amount") || result.contains("invalid amount"));
    }

    @Test
    void execute_invalidDate_returnsInvalidCommand() {
        // Prepare input stream with invalid date format
        String simulatedUserInput = "Test Expense\n" + // Keyword
                "\n" + // Skip amount
                "\n" + // Skip description
                "01-01-2023\n" + // Wrong date format (should be YYYY-MM-DD)
                "\n\n"; // Skip other fields

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("edit", ui);

        assertTrue(command instanceof InvalidCommand);
        String result = command.execute(transactionManager, ui, storage);
        assertTrue(result.contains("Invalid date") || result.contains("invalid date"));
    }

    @Test
    void execute_emptyEdit_returnsInvalidCommand() {
        // Prepare input stream with skipping all fields
        String simulatedUserInput = "Test Expense\n\n\n\n\n\n";

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner scanner = new Scanner(inputStream);
        Ui ui = new Ui(scanner);
        Parser parser = new Parser();

        Command command = parser.parseCommandWord("edit", ui);

        assertTrue(command instanceof InvalidCommand);
        String result = command.execute(transactionManager, ui, storage);
        assertTrue(result.contains("parameter") || result.contains("Parameter"));
    }
}
