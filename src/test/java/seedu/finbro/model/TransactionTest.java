package seedu.finbro.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Transaction class and its subclasses.
 */
class TransactionTest {

    @Test
    void incomeConstructorValidParametersSuccess() {
        double amount = 3000.00;
        String description = "Monthly salary";
        List<String> tags = Collections.singletonList("work");

        Income income = new Income(amount, description, tags);

        assertEquals(amount, income.getAmount());
        assertEquals(description, income.getDescription());
        assertEquals(LocalDate.now(), income.getDate());
        assertEquals(tags, income.getTags());
    }

    @Test
    void incomeToStringCorrectFormat() {
        Income income = new Income(3000.00, "Monthly salary", Collections.singletonList("work"));
        String result = income.toString();
        assertTrue(result.contains("[Income]"));
        assertTrue(result.contains("- Monthly salary [work]"));
        assertTrue(result.contains("$3,000.00") || result.contains("$3000.00"));
    }

    @Test
    void incomeToStringNoTagsCorrectFormat() {
        Income income = new Income(3000.00, "Monthly salary", Collections.emptyList());
        String result = income.toString();
        assertTrue(result.contains("[Income]"));
        assertTrue(result.contains("- Monthly salary"));
        assertTrue(result.contains("$3,000.00") || result.contains("$3000.00"));
        assertTrue(!result.contains("[work]"));
    }

    @Test
    void expenseConstructorValidParametersSuccess() {
        double amount = 25.50;
        String description = "Lunch";
        Expense.Category category = Expense.Category.FOOD;
        List<String> tags = Collections.singletonList("work");

        Expense expense = new Expense(amount, description, category, tags);

        assertEquals(amount, expense.getAmount());
        assertEquals(description, expense.getDescription());
        assertEquals(LocalDate.now(), expense.getDate());
        assertEquals(category, expense.getCategory());
        assertEquals(tags, expense.getTags());
    }

    @Test
    void expenseConstructorNullCategoryDefaultsToOthers() {
        Expense expense = new Expense(25.50, "Lunch", null, Collections.emptyList());
        assertEquals(Expense.Category.OTHERS, expense.getCategory());
    }

    @Test
    void expenseToStringCorrectFormat() {
        Expense expense = new Expense(25.50, "Lunch", Expense.Category.FOOD, Collections.singletonList("work"));
        String result = expense.toString();
        assertTrue(result.contains("[Expense][Food]"));
        assertTrue(result.contains("- Lunch [work]"));
        assertTrue(result.contains("$25.50"));
    }

    @Test
    void expenseToStringNoTagsCorrectFormat() {
        Expense expense = new Expense(25.50, "Lunch", Expense.Category.FOOD, Collections.emptyList());
        String result = expense.toString();
        assertTrue(result.contains("[Expense][Food]"));
        assertTrue(result.contains("- Lunch"));
        assertTrue(result.contains("$25.50"));
        assertTrue(!result.contains("[work]"));
    }

    @Test
    void expenseCategoryFromStringValidCategoryReturnsCorrectCategory() {
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("Food"));
        assertEquals(Expense.Category.TRANSPORT, Expense.Category.fromString("Transport"));
        assertEquals(Expense.Category.SHOPPING, Expense.Category.fromString("Shopping"));
        assertEquals(Expense.Category.BILLS, Expense.Category.fromString("Bills"));
        assertEquals(Expense.Category.ENTERTAINMENT, Expense.Category.fromString("Entertainment"));
        assertEquals(Expense.Category.OTHERS, Expense.Category.fromString("Others"));
    }

    @Test
    void expenseCategoryFromStringInvalidCategoryReturnsOthers() {
        assertEquals(Expense.Category.OTHERS, Expense.Category.fromString("NotACategory"));
        assertEquals(Expense.Category.OTHERS, Expense.Category.fromString(null));
    }

    @Test
    void expenseCategoryFromStringCaseInsensitive() {
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("food"));
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("FOOD"));
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("Food"));
    }
}
