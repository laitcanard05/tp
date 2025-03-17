package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Arrays;

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
                        LocalDate.of(currentYear, currentMonth, 15), 
                        Arrays.asList("work")));
        transactionManager.addTransaction(
                new Expense(200, "Groceries",
                        LocalDate.of(currentYear, currentMonth, 16),
                        Expense.Category.FOOD, Arrays.asList("essential")));
        transactionManager.addTransaction(
                new Expense(100, "Electricity",
                        LocalDate.of(currentYear, currentMonth, 17),
                        Expense.Category.BILLS, Arrays.asList("home", "monthly")));
        
        // Add last month transactions
        transactionManager.addTransaction(
                new Income(900, "Last month salary",
                        LocalDate.of(lastMonthYear, lastMonth, 15), 
                        Arrays.asList("work")));
        transactionManager.addTransaction(
                new Expense(150, "Last month groceries",
                        LocalDate.of(lastMonthYear, lastMonth, 16),
                        Expense.Category.FOOD, Arrays.asList("essential")));
    }

    @Test
    public void executeCurrentMonthSummarySuccess() {
        SummaryCommand command = new SummaryCommand(currentMonth, currentYear);
        String result = command.execute(transactionManager, ui, storage);
        
        String monthName = new DateFormatSymbols().getMonths()[currentMonth-1];
        assertTrue(result.contains("Financial Summary for " + monthName + " " + currentYear));
        assertTrue(result.contains("Total Income: $1000.00"));
        assertTrue(result.contains("Total Expenses: $300.00"));
        assertTrue(result.contains("1. FOOD: $200.00"));
        assertTrue(result.contains("2. BILLS: $100.00"));
        assertTrue(result.contains("Tags Summary"));
        assertTrue(result.contains("essential: $200.00"));
        assertTrue(result.contains("home: $100.00"));
        assertTrue(result.contains("monthly: $100.00"));
    }

    @Test
    public void executeSpecificMonthSummarySuccess() {
        SummaryCommand command = new SummaryCommand(lastMonth, lastMonthYear);
        String result = command.execute(transactionManager, ui, storage);
        
        String monthName = new DateFormatSymbols().getMonths()[lastMonth-1];
        assertTrue(result.contains("Financial Summary for " + monthName + " " + lastMonthYear));
        assertTrue(result.contains("Total Income: $900.00"));
        assertTrue(result.contains("Total Expenses: $150.00"));
        assertTrue(result.contains("1. FOOD: $150.00"));
        assertTrue(result.contains("Tags Summary"));
        assertTrue(result.contains("essential: $150.00"));
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
        
        String monthName = new DateFormatSymbols().getMonths()[emptyMonth-1];
        assertTrue(result.contains("Financial Summary for " + monthName + " " + currentYear));
        assertTrue(result.contains("Total Income: $0.00"));
        assertTrue(result.contains("Total Expenses: $0.00"));
        // The new implementation doesn't include the "No expenses recorded" message
        // It will just not show the categories and tags sections
    }

    @Test
    public void isExitReturnsFalse() {
        SummaryCommand command = new SummaryCommand(currentMonth, currentYear);
        assertFalse(command.isExit());
    }
}
