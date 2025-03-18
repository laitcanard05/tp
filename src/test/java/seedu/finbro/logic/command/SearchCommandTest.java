package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for ClearCommand.
 */
class SearchCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    private final String aExists = "[Expense][Others] $1.00 - a";
    private final String abExists = "[Income] $1.00 - ab";
    private final String abcExists = "[Expense][Others] $1.00 - abc";
    private final String abcdExists = "[Income] $1.00 - abcd";

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();

        transactionManager.addTransaction(new Expense(1, "a", null, null));
        transactionManager.addTransaction(new Income(1, "ab", null));
        transactionManager.addTransaction(new Expense(1, "abc", null, null));
        transactionManager.addTransaction(new Income(1, "abcd", null));
    }


    /**
     * The following 4 tests ensure that the search method returns only messages containing the keyword
     */
    @Test
    void searchSmallA_shouldDisplayATransactions() {
        SearchCommand command = new SearchCommand("a");
        String result = command.execute(transactionManager, ui, storage);


        assertEquals(aExists +"\n"+ abExists +"\n"+ abcExists +"\n"+ abcdExists, result);
    }

    @Test
    void searchSmallC_shouldDisplayCTransactions() {
        SearchCommand command = new SearchCommand("c");
        String result = command.execute(transactionManager, ui, storage);

        assertEquals(abcExists +"\n"+ abcdExists, result);
    }

    @Test
    void searchBigZ_shouldDisplayNoTransactions() {
        SearchCommand command = new SearchCommand("Z");
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("", result);
    }

    @Test
    void searchBigA_shouldDisplayNoTransactions() {
        SearchCommand command = new SearchCommand("A");
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("", result);
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

