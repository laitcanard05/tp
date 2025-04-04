package seedu.finbro.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Income class.
 */
class IncomeTest {

    @Test
    void constructorValidParametersSuccess() {
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
    void constructorWithDateValidParametersSuccess() {
        double amount = 3000.00;
        String description = "Monthly salary";
        LocalDate date = LocalDate.of(2025, 3, 15);
        List<String> tags = Collections.singletonList("work");

        Income income = new Income(amount, description, date, tags);

        assertEquals(amount, income.getAmount());
        assertEquals(description, income.getDescription());
        assertEquals(date, income.getDate());
        assertEquals(tags, income.getTags());
    }

    @Test
    void constructorNullTagsUsesEmptyList() {
        Income income = new Income(3000.00, "Monthly salary", null);
        assertNotNull(income.getTags());
        assertEquals(0, income.getTags().size());
    }

    @Test
    void toStringCorrectFormat() {
        Income income = new Income(3000.00, "Monthly salary", Collections.singletonList("work"));
        // Using CurrencyFormatter, so we need to check that the format is correct
        String actual = income.toString();
        assertTrue(actual.contains("[Income]"));
        assertTrue(actual.contains("- Monthly salary [work]"));
        assertTrue(actual.contains("$3,000.00"));
    }

    @Test
    void toStringMultipleTagsCorrectFormat() {
        Income income = new Income(3000.00, "Monthly salary", Arrays.asList("work", "january"));
        // Using CurrencyFormatter, so we need to check that the format is correct
        String actual = income.toString();
        assertTrue(actual.contains("[Income]"));
        assertTrue(actual.contains("- Monthly salary [work, january]"));
        assertTrue(actual.contains("$3,000.00"));
    }

    @Test
    void toStringNoTagsCorrectFormat() {
        Income income = new Income(3000.00, "Monthly salary", Collections.emptyList());
        // Using CurrencyFormatter, so we need to check that the format is correct
        String actual = income.toString();
        assertTrue(actual.contains("[Income]"));
        assertTrue(actual.contains("- Monthly salary"));
        assertTrue(actual.contains("$3,000.00"));
        assertFalse(actual.contains("[work"));
    }
}
