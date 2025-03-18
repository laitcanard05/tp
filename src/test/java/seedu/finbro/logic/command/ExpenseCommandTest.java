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

/**
 * Tests for the ExpenseCommand class.
 */
class ExpenseCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    @Test
    void execute_validExpense_addsExpenseAndReturnsConfirmation() {
        double amount = 50.0;
        String description = "Test expense";
        Expense.Category category = Expense.Category.FOOD;
        List<String> tags = new ArrayList<>();
        tags.add("test");

        ExpenseCommand command = new ExpenseCommand(amount, description, category, tags);
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("New expense added: [Expense][Food] $50.00 - Test expense [test]", result);
        assertEquals(1, transactionManager.getTransactionCount());
        assertEquals(50.0, transactionManager.getTotalExpenses(), 0.001);
    }

    @Test
    void isExit_returnsFalse() {
        ExpenseCommand command = new ExpenseCommand(
                50.0, "Test", Expense.Category.OTHERS, new ArrayList<>());
        assertFalse(command.isExit());
    }
}
