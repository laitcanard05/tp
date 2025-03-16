package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the SearchCommand class.
 */
public class SearchCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();

        // Add some test transactions with various descriptions
        transactionManager.addTransaction(new Income(1000, "Monthly salary", Collections.emptyList()));
        transactionManager.addTransaction(new Income(500, "Freelance work payment", Collections.emptyList()));
        transactionManager.addTransaction(new Expense(50, "Grocery shopping",
                Expense.Category.FOOD, Collections.emptyList()));
        transactionManager.addTransaction(new Expense(20, "Bus fare to work",
                Expense.Category.TRANSPORT, Collections.emptyList()));
    }

    @Test
    public void executeOneKeywordOneMatchSuccess() {
        List<String> keywords = Collections.singletonList("salary");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Found 1 matching transaction(s)"));
        assertTrue(result.contains("Monthly salary"));
        assertFalse(result.contains("Freelance"));
    }

    @Test
    public void executeOneKeywordMultipleMatchesSuccess() {
        List<String> keywords = Collections.singletonList("work");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Found 2 matching transaction(s)"));
        assertTrue(result.contains("Freelance work payment"));
        assertTrue(result.contains("Bus fare to work"));
    }

    @Test
    public void executeMultipleKeywordsMultipleMatchesSuccess() {
        List<String> keywords = Arrays.asList("salary", "grocery");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Found 2 matching transaction(s)"));
        assertTrue(result.contains("Monthly salary"));
        assertTrue(result.contains("Grocery shopping"));
    }

    @Test
    public void executeCaseInsensitiveMatching() {
        List<String> keywords = Arrays.asList("SALARY", "grocery");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Found 2 matching transaction(s)"));
        assertTrue(result.contains("Monthly salary"));
        assertTrue(result.contains("Grocery shopping"));
    }

    @Test
    public void executePartialWordMatching() {
        List<String> keywords = Collections.singletonList("sal");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertTrue(result.contains("Found 1 matching transaction(s)"));
        assertTrue(result.contains("Monthly salary"));
    }

    @Test
    public void executeNoMatchingTransactionsFound() {
        List<String> keywords = Collections.singletonList("nonexistent");
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertEquals("No matching transactions found.", result);
    }

    @Test
    public void executeEmptyKeywordsListReturnsNoMatches() {
        List<String> keywords = Collections.emptyList();
        SearchCommand command = new SearchCommand(keywords);
        String result = command.execute(transactionManager, ui, storage);
        
        assertEquals("No matching transactions found.", result);
    }

    @Test
    public void isExitReturnsFalse() {
        SearchCommand command = new SearchCommand(Collections.singletonList("keyword"));
        assertFalse(command.isExit());
    }
}
