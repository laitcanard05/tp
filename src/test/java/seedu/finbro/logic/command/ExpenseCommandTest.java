package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for the ExpenseCommand class.
 */
class ExpenseCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;
    private ExpenseCommand expense;

    private final double testAmt = 50.0;
    private final String testDesc = "Test expense";
    private final Expense.Category testCat = Expense.Category.FOOD;
    private final List<String> testTags = List.of("test tag1", "test tag2");



    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    @Test
    void execute_validExpense_addsExpenseAndReturnsConfirmation() {
        expense = new ExpenseCommand(testAmt, testDesc, testCat, testTags);
        String result = expense.execute(transactionManager, ui, storage);

        assertEquals("New expense added: [Expense][Food] $50.00 - Test expense [test tag1, test tag2]", result);
        assertEquals(1, transactionManager.getTransactionCount());
        assertEquals(50.0, transactionManager.getTotalExpenses(), 0.001);
    }

    /**
     * Tests that duplicate income transactions are detected
     * //TODO: implement testing with simulated user inputs
     */
    @Test
    void duplicateExpenseConstructor_shouldWarn() {
        expense = new ExpenseCommand(testAmt, testDesc,  null, null);
        expense.execute(transactionManager, ui, storage);
        boolean duplicateDetected = !transactionManager.getTransactionDuplicates(testAmt, testDesc).isEmpty();
        assertTrue(duplicateDetected);
    }

    @Test
    void isExit_returnsFalse() {
        ExpenseCommand command = new ExpenseCommand(
                50.0, "Test", Expense.Category.OTHERS, new ArrayList<>());
        assertFalse(command.isExit());
    }
}
