package seedu.finbro.logic.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests for the custom exceptions in the logic.exceptions package.
 */
public class ExceptionsTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void decimalPointException_handle_printsErrorMessage() {
        // When
        DecimalPointException.handle();

        // Then
        String output = outContent.toString();
        assertTrue(output.contains("INVALID INPUT: Exceed 2 Decimal Points"));
        assertTrue(output.contains("Please keep your input to 2 decimal places or less"));
    }

    @Test
    void emptyInputException_handle_printsErrorMessage() {
        // When
        EmptyInputException.handle();

        // Then
        String output = outContent.toString();
        assertTrue(output.contains("INVALID INPUT: Empty Input"));
        assertTrue(output.contains("This field cannot be empty"));
    }

    @Test
    void exceedCharCountException_handle_printsErrorMessage() {
        // When
        ExceedCharCountException.handle();

        // Then
        String output = outContent.toString();
        assertTrue(output.contains("INVALID INPUT: Input exceeds char count of 120"));
        assertTrue(output.contains("Please input a shorter string"));
    }

    @Test
    void indexExceedLimitException_handle_printsErrorMessage() {
        // When
        IndexExceedLimitException.handle();

        // Then
        String output = outContent.toString();
        assertTrue(output.contains("INVALID INPUT - index exceeds limit"));
        assertTrue(output.contains("Please input an index within the appropriate range"));
    }

    @Test
    void negativeNumberException_handle_printsErrorMessage() {
        // When
        NegativeNumberException.handle();

        // Then
        String output = outContent.toString();
        assertTrue(output.contains("INVALID INPUT: Negative Number Input"));
        assertTrue(output.contains("Please input a non-negative number"));
    }

    @Test
    void constructors_createExceptionsSuccessfully() {
        // Test that constructors don't throw errors
        assertDoesNotThrow(DecimalPointException::new);
        assertDoesNotThrow(EmptyInputException::new);
        assertDoesNotThrow(ExceedCharCountException::new);
        assertDoesNotThrow(IndexExceedLimitException::new);
        assertDoesNotThrow(NegativeNumberException::new);
    }
}
