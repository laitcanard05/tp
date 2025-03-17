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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for Ui.
 * Tests the user interface functionality by capturing system output and
 * simulating user input to verify correct behavior.
 */
class SummaryCommandTest { ;
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

        assertEquals("Financial Summary for March 2025:\n\n" +
            "Total Income: $0.00\nTotal Expenses: $0.00\n", result);
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

        SummaryCommand command = new SummaryCommand(3, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("""
            Financial Summary for March 2025:
            
            Total Income: $2000.00
            Total Expenses: $20.30
           
            Top Expense Categories:
            1. Others: $20.30
            """, result);
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

        SummaryCommand command = new SummaryCommand(3, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("""
            Financial Summary for March 2025:
           
            Total Income: $0.00
            Total Expenses: $138.09
            
            Top Expense Categories:
            1. Shopping: $75.00
            2. Entertainment: $45.99
            3. Transport: $10.80
            """, result);
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

        SummaryCommand command = new SummaryCommand(3, 2025);
        String result = command.execute(transactionManager, ui, storage);

        assertEquals("""
            Financial Summary for March 2025:
           
            Total Income: $3000.00
            Total Expenses: $313.79
            
            Top Expense Categories:
            1. Bills: $150.20
            2. Shopping: $75.00
            3. Entertainment: $45.99
            
            Tags Summary:
            1. monthly: $3150.20
            2. work: $3025.50
            3. home: $150.20
            4. weekend: $45.99
            """, result);
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
