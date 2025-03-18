package seedu.finbro.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the Expense class.
 */
class ExpenseTest {

    @Test
    void constructorValidParametersSuccess() {
        double amount = 50.75;
        String description = "Dinner at restaurant";
        Expense.Category category = Expense.Category.FOOD;
        List<String> tags = Collections.singletonList("family");

        Expense expense = new Expense(amount, description, category, tags);

        assertEquals(amount, expense.getAmount());
        assertEquals(description, expense.getDescription());
        assertEquals(LocalDate.now(), expense.getDate());
        assertEquals(category, expense.getCategory());
        assertEquals(tags, expense.getTags());
    }

    @Test
    void constructorWithDateValidParametersSuccess() {
        double amount = 50.75;
        String description = "Dinner at restaurant";
        LocalDate date = LocalDate.of(2025, 3, 15);
        Expense.Category category = Expense.Category.FOOD;
        List<String> tags = Collections.singletonList("family");

        Expense expense = new Expense(amount, description, date, category, tags);

        assertEquals(amount, expense.getAmount());
        assertEquals(description, expense.getDescription());
        assertEquals(date, expense.getDate());
        assertEquals(category, expense.getCategory());
        assertEquals(tags, expense.getTags());
    }

    @Test
    void constructorNullCategoryDefaultsToOthers() {
        Expense expense = new Expense(50.75, "Miscellaneous expense", null, Collections.emptyList());
        assertEquals(Expense.Category.OTHERS, expense.getCategory());
    }

    @Test
    void constructorNullTagsUsesEmptyList() {
        Expense expense = new Expense(50.75, "Dinner", Expense.Category.FOOD, null);
        assertNotNull(expense.getTags());
        assertEquals(0, expense.getTags().size());
    }

    @Test
    void toStringCorrectFormat() {
        Expense expense = new Expense(50.75, "Dinner", Expense.Category.FOOD, 
                Arrays.asList("family", "weekend"));
        String expected = "[Expense][Food] $50.75 - Dinner [family, weekend]";
        assertEquals(expected, expense.toString());
    }

    @Test
    void categoryFromStringCaseInsensitiveMatch() {
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("food"));
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("FOOD"));
        assertEquals(Expense.Category.FOOD, Expense.Category.fromString("Food"));
    }

    @Test
    void categoryFromStringInvalidReturnsOthers() {
        assertEquals(Expense.Category.OTHERS, Expense.Category.fromString("invalid"));
        assertEquals(Expense.Category.OTHERS, Expense.Category.fromString(null));
    }

    @Test
    void categoryToStringCorrectDisplayName() {
        assertEquals("Food", Expense.Category.FOOD.toString());
        assertEquals("Transport", Expense.Category.TRANSPORT.toString());
        assertEquals("Shopping", Expense.Category.SHOPPING.toString());
        assertEquals("Bills", Expense.Category.BILLS.toString());
        assertEquals("Entertainment", Expense.Category.ENTERTAINMENT.toString());
        assertEquals("Others", Expense.Category.OTHERS.toString());
    }
}
