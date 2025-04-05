package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for Ui.
 * Tests the user interface functionality by capturing system output and
 * simulating user input to verify correct behavior.
 */
class SummaryCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;
    private Parser parser;

    /**
     * Sets up the test environment before each test.
     * Redirects System.out to allow output verification.
     */
    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
        parser = new Parser();
    }

    /**
     * Tests that the financial summary for a month with no transactions
     *     displays zero total income and zero total expenses only
     */
    @Test
    void noTransactionsInMonth_returnsZeroIncomeAndExpenses() {
        SummaryCommand command = new SummaryCommand(LocalDate.now().getMonthValue(),
            LocalDate.now().getYear());
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Financial Summary for April 2025:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        assertTrue(result.contains("$0.00"));
    }

    /**
     * Tests that the financial summary for a month with transactions
     *     that have no specified category and no tags
     *     displays only the category "Others" and no tags summary
     */
    @Test
    void noCategoriesOrTags_returnsIncomeAndExpenses() {
        transactionManager.addTransaction(new Expense(6.30,
            "lunch", null, null));
        transactionManager.addTransaction(new Expense(14.00,
            "buy books", null, null));
        transactionManager.addTransaction(new Income(2000,
            "Monthly salary", null));

        SummaryCommand command = new SummaryCommand(4, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Financial Summary for April 2025:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        assertTrue(result.contains("Top Expense Categories:"));
        assertTrue(result.contains("1. Others:"));
        
        // Check for formatted currency values
        assertTrue(result.contains("$2,000.00") || result.contains("$2000"));
        assertTrue(result.contains("$20.30"));
    }

    /**
     * Tests that the financial summary only displays top three categories
     *     even if there are more than 3 categories for all transactions
     *     in the month
     */
    @Test
    void moreThanThreeCategories_returnsTopThreeCategories() {
        transactionManager.addTransaction(new Expense(6.30,
            "lunch", Expense.Category.fromString("Food"), null));
        transactionManager.addTransaction(new Expense(75,
            "New Shoes", Expense.Category.fromString("Shopping"), null));
        transactionManager.addTransaction(new Expense(10.80,
            "Bus fare", Expense.Category.fromString("Transport"), null));
        transactionManager.addTransaction(new Expense(45.99,
            "Movie tickets", Expense.Category.fromString("Entertainment"), null));

        SummaryCommand command = new SummaryCommand(4, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Financial Summary for April 2025:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        assertTrue(result.contains("Top Expense Categories:"));
        
        // Check for category names in order
        assertTrue(result.contains("1. Shopping:"));
        assertTrue(result.contains("2. Entertainment:"));
        assertTrue(result.contains("3. Transport:"));
        
        // Check for formatted currency values
        assertTrue(result.contains("$0.00"));
        assertTrue(result.contains("$138.09") || result.contains("$138.10") || result.contains("$138.1"));
        assertTrue(result.contains("$75.00") || result.contains("$75"));
        assertTrue(result.contains("$45.99") || result.contains("$46"));
        assertTrue(result.contains("$10.80") || result.contains("$10.8"));
    }

    /**
     * Tests that the financial summary for a month with transactions
     *     with multiple tags and categories displays the total expenses
     *     in the top three categories and all tags
     */
    @Test
    void multipleCategoriesAndTags_returnsTopCategoriesAndAllTags() {
        List<String> tagSet1 = List.of("monthly", "home");
        List<String> tagSet2 = List.of("work", "monthly");
        List<String> tagSet3 = List.of("weekend");
        List<String> tagSet4 = List.of("work");
        transactionManager.addTransaction(new Expense(6.30,
            "lunch", Expense.Category.fromString("Food"), null));
        transactionManager.addTransaction(new Expense(75,
            "New Shoes", Expense.Category.fromString("Shopping"), null));
        transactionManager.addTransaction(new Expense(10.80,
            "Bus fare", Expense.Category.fromString("Transport"), null));
        transactionManager.addTransaction(new Expense(45.99,
            "Movie tickets", Expense.Category.fromString("Entertainment"), tagSet3));
        transactionManager.addTransaction(new Expense(150.20,
            "Electricity bill", Expense.Category.fromString("Bills"), tagSet1));
        transactionManager.addTransaction(new Income(3000,
            "Monthly salary", tagSet2));
        transactionManager.addTransaction(new Expense(25.50,
            "Lunch with colleagues", Expense.Category.fromString("Food"), tagSet4));

        SummaryCommand command = new SummaryCommand(4, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Financial Summary for April 2025:"));
        assertTrue(result.contains("Total Income:"));
        assertTrue(result.contains("Total Expenses:"));
        assertTrue(result.contains("Top Expense Categories:"));
        assertTrue(result.contains("Tags Summary:"));
        
        // Check for category names in order
        assertTrue(result.contains("1. Bills:"));
        assertTrue(result.contains("2. Shopping:"));
        assertTrue(result.contains("3. Entertainment:"));
        
        // Check for tag names in order
        assertTrue(result.contains("1. work:"));
        assertTrue(result.contains("2. monthly:"));
        assertTrue(result.contains("3. weekend:"));
        assertTrue(result.contains("4. home:"));
        
        // Check for formatted currency values (more flexible check)
        assertTrue(result.contains("$3,000") || result.contains("$3000"));
        assertTrue(result.contains("$313"));
        assertTrue(result.contains("$150"));
        assertTrue(result.contains("$75"));
        assertTrue(result.contains("$45"));
        
        // Tag values
        assertTrue(result.contains("monthly:") && (result.contains("$2,849") || result.contains("$2849")));
        assertTrue(result.contains("work:") && (result.contains("$2,974") || result.contains("$2974")));
        assertTrue(result.contains("home:") && (result.contains("-$150.20")));
        assertTrue(result.contains("weekend:") && (result.contains("-$45.99")));
    }

    /**
     * Tests that isExit returns false.
     */
    @Test
    void isExit_returnsFalse() {
        SummaryCommand command = new SummaryCommand(LocalDate.now().getMonthValue(),
            LocalDate.now().getYear());
        assertFalse(command.isExit());
    }


}
