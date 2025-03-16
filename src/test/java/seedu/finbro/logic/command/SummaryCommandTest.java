package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the SummaryCommand class.
 */
public class SummaryCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;
    private int currentMonth;
    private int currentYear;
    private int lastMonth;
    private int lastMonthYear;

    @BeforeEach
    public void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
        
        LocalDate now = LocalDate.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
        
        LocalDate lastMonthDate = now.minusMonths(1);
        lastMonth = lastMonthDate.getMonthValue();
        lastMonthYear = lastMonthDate.getYear();

        // Add current month transactions
        transactionManager.addTransaction(
                new Income(1000, "Salary",
                        LocalDate.of(currentYear, currentMonth, 15), Collections.emptyList()));
        transactionManager.addTransaction(
                new Expense(200, "Groceries",
                        LocalDate.of(currentYear, currentMonth, 16),
                        Expense.Category.FOOD, Collections.emptyList()));
        transactionManager.addTransaction(
                new Expense(100, "Electricity",
                        LocalDate.of(currentYear, currentMonth, 17),
                        Expense.Category.BILLS, Collections.emptyList()));
        
        // Add last month transactions
        transactionManager.addTransaction(
                new Income(900, "Last month salary",
                        LocalDate.of(lastMonthYear, lastMonth, 15), Collections.emptyList()));
        transactionManager.addTransaction(
                new Expense(150, "Last month groceries",
                        LocalDate.of(lastMonthYear, lastMonth, 16),
                        Expense.Category.FOOD, Collections.emptyList()));
    }

    @Test
    public void executeCurrentMonthSummarySuccess() {
        SummaryCommand command = new SummaryCommand(null, null);
        String result = command.execute(transactionManager, ui, storage);
        
        String monthName = Month.of(currentMonth).name();
        assertTrue(result.contains("Financial Summary for " +
                monthName.substring(0, 1) +
                monthName.substring(1).toLowerCase() + " " + currentYear));
        assertTrue(result.contains("Total Income: $1000.00"));
        assertTrue(result.contains("Total Expenses: $300.00"));
        assertTrue(result.contains("Net Balance: $700.00"));
        assertTrue(result.contains("Food: $200.00"));
        assertTrue(result.contains("Bills: $100.00"));
    }

    @Test
    public void executeSpecificMonthSummarySuccess() {
        SummaryCommand command = new SummaryCommand(lastMonth, lastMonthYear);
        String result = command.execute(transactionManager, ui, storage);
        
        String monthName = Month.of(lastMonth).name();
        assertTrue(result.contains("Financial Summary for " +
                monthName.substring(0, 1) +
                monthName.substring(1).toLowerCase() + " " + lastMonthYear));
        assertTrue(result.contains("Total Income: $900.00"));
        assertTrue(result.contains("Total Expenses: $150.00"));
        assertTrue(result.contains("Net Balance: $750.00"));
        assertTrue(result.contains("Food: $150.00"));
    }

    @Test
    public void executeEmptyMonthSummarySuccess() {
        // Use a month with no transactions
        int emptyMonth = (currentMonth % 12) + 1;
        if (emptyMonth == lastMonth) {
            emptyMonth = (emptyMonth % 12) + 1;
        }
        
        SummaryCommand command = new SummaryCommand(emptyMonth, currentYear);
        String result = command.execute(transactionManager, ui, storage);
        
        String monthName = Month.of(emptyMonth).name();
        assertTrue(result.contains("Financial Summary for " +
                monthName.substring(0, 1) +
                monthName.substring(1).toLowerCase() + " " + currentYear));
        assertTrue(result.contains("Total Income: $0.00"));
        assertTrue(result.contains("Total Expenses: $0.00"));
        assertTrue(result.contains("Net Balance: $0.00"));
        assertTrue(result.contains("No expenses recorded for this period"));
    }

    @Test
    public void executePercentageCalculationCorrect() {
        SummaryCommand command = new SummaryCommand(currentMonth, currentYear);
        String result = command.execute(transactionManager, ui, storage);
        
        // Food is $200 out of $300, which is 66.7%
        assertTrue(result.contains("Food: $200.00 (66.7%)"));
        // Bills is $100 out of $300, which is 33.3%
        assertTrue(result.contains("Bills: $100.00 (33.3%)"));
    }

    @Test
    public void isExitReturnsFalse() {
        SummaryCommand command = new SummaryCommand(null, null);
        assertFalse(command.isExit());
    }
}
