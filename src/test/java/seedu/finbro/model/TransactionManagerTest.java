package seedu.finbro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the TransactionManager class.
 */
public class TransactionManagerTest {
    private TransactionManager transactionManager;
    private Income income1;
    private Income income2;
    private Expense expense1;
    private Expense expense2;
    private final LocalDate february2025 = LocalDate.of(2025, 2, 1);
    private final LocalDate march2025 = LocalDate.of(2025, 3, 1);

    @BeforeEach
    public void setUp() {
        transactionManager = new TransactionManager();

        // Create test transactions
        income1 = new Income(3000.00, "Monthly salary",
                LocalDate.of(2025, 2, 1), Collections.singletonList("work"));
        income2 = new Income(500.00, "Bonus",
                LocalDate.of(2025, 2, 15), Collections.singletonList("work"));
        expense1 = new Expense(25.50, "Lunch",
                LocalDate.of(2025, 2, 10), Expense.Category.FOOD, Collections.singletonList("work"));
        expense2 = new Expense(75.00, "New shoes",
                LocalDate.of(2025, 2, 20), Expense.Category.SHOPPING, Collections.emptyList());

        // Add transactions to manager
        transactionManager.addTransaction(income1);
        transactionManager.addTransaction(income2);
        transactionManager.addTransaction(expense1);
        transactionManager.addTransaction(expense2);
    }

    @Test
    public void addTransaction_validTransaction_success() {
        TransactionManager manager = new TransactionManager();
        assertEquals(0, manager.getTransactionCount());

        Income income = new Income(1000.00, "Test", Collections.emptyList());
        manager.addTransaction(income);

        assertEquals(1, manager.getTransactionCount());
    }

    @Test
    public void getFilteredTransactions_validDateRange_returnsCorrectTransactions() {
        // Filter February 2025 transactions
        ArrayList<Transaction> filteredTransactions = transactionManager.getFilteredTransactions(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28));
        
        assertEquals(4, filteredTransactions.size());
        
        // Filter for specific date range
        filteredTransactions = transactionManager.getFilteredTransactions(
                LocalDate.of(2025, 2, 10),
                LocalDate.of(2025, 2, 15));
        
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.contains(expense1));
        assertTrue(filteredTransactions.contains(income2));
        
        // Filter for future month (should be empty)
        filteredTransactions = transactionManager.getFilteredTransactions(
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 31));
        
        assertTrue(filteredTransactions.isEmpty());
    }

    @Test
    public void searchTransactions_validKeyword_returnsMatchingTransactions() {
        List<String> keywords = Arrays.asList("salary");
        List<Transaction> results = transactionManager.searchTransactions(keywords);
        
        assertEquals(1, results.size());
        assertEquals(income1, results.get(0));
        
        keywords = Arrays.asList("shoes", "lunch");
        results = transactionManager.searchTransactions(keywords);
        
        assertEquals(2, results.size());
        assertTrue(results.contains(expense1));
        assertTrue(results.contains(expense2));
    }

    @Test
    public void listTransactionsFromDate_validDate_returnsTransactionsFromDate() {
        List<Transaction> results = transactionManager.listTransactionsFromDate(LocalDate.of(2025, 2, 15));
        
        assertEquals(2, results.size());
        assertTrue(results.contains(income2));
        assertTrue(results.contains(expense2));
    }

    @Test
    public void getMonthlyTotalIncome_validMonth_returnsCorrectTotal() {
        double totalIncome = transactionManager.getMonthlyTotalIncome(2, 2025);
        assertEquals(3500.0, totalIncome);
        
        // Test empty month
        totalIncome = transactionManager.getMonthlyTotalIncome(3, 2025);
        assertEquals(0.0, totalIncome);
    }

    @Test
    public void getMonthlyTotalExpense_validMonth_returnsCorrectTotal() {
        double totalExpense = transactionManager.getMonthlyTotalExpense(2, 2025);
        assertEquals(100.5, totalExpense);
        
        // Test empty month
        double emptyExpense = transactionManager.getMonthlyTotalExpense(3, 2025);
        assertEquals(0.0, emptyExpense);
    }

    @Test
    public void getMonthlyCategorisedExpenses_validMonth_returnsCorrectCategories() {
        Map<Expense.Category, Double> categorizedExpenses = transactionManager.getMonthlyCategorisedExpenses(2, 2025);
        
        assertEquals(2, categorizedExpenses.size());
        assertTrue(categorizedExpenses.containsKey(Expense.Category.FOOD));
        assertTrue(categorizedExpenses.containsKey(Expense.Category.SHOPPING));
        assertEquals(25.5, categorizedExpenses.get(Expense.Category.FOOD));
        assertEquals(75.0, categorizedExpenses.get(Expense.Category.SHOPPING));
    }

    @Test
    public void getMonthlyTaggedExpenses_validMonth_returnsCorrectTags() {
        Map<String, Double> taggedExpenses = transactionManager.getMonthlyTaggedExpenses(2, 2025);
        
        assertEquals(1, taggedExpenses.size());
        assertTrue(taggedExpenses.containsKey("work"));
        assertEquals(25.5, taggedExpenses.get("work"));
    }

    @Test
    public void clearTransactions_removesAllTransactions() {
        assertEquals(4, transactionManager.getTransactionCount());

        transactionManager.clearTransactions();

        assertEquals(0, transactionManager.getTransactionCount());
        assertTrue(transactionManager.listTransactions().isEmpty());
    }
}
