package seedu.finbro.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.logic.parser.Parser;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Tests for private methods in the Parser class using reflection.
 */
public class ParserPrivateMethodsTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parseParameters_validParameterString_returnsCorrectMap() throws Exception {
        // Get the private method using reflection
        Method parseParametersMethod = Parser.class.getDeclaredMethod("parseParameters", String.class);
        parseParametersMethod.setAccessible(true);

        // Test with a typical parameter string
        String paramString = "100 d/Test description c/Food t/tag1,tag2";

        // When
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) parseParametersMethod.invoke(parser, paramString);

        // Then
        assertEquals("100", result.get(""));
        assertEquals("Test description", result.get("d"));
        assertEquals("Food", result.get("c"));
        assertEquals("tag1,tag2", result.get("t"));
    }

    @Test
    void parseParameters_noSlashParameters_onlyMainParameter() throws Exception {
        // Get the private method using reflection
        Method parseParametersMethod = Parser.class.getDeclaredMethod("parseParameters", String.class);
        parseParametersMethod.setAccessible(true);

        // Test with only main parameter
        String paramString = "100";

        // When
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) parseParametersMethod.invoke(parser, paramString);

        // Then
        assertEquals(1, result.size());
        assertEquals("100", result.get(""));
    }

    @Test
    void parseParameters_multipleSlashParameters_parsesAllParameters() throws Exception {
        // Get the private method using reflection
        Method parseParametersMethod = Parser.class.getDeclaredMethod("parseParameters", String.class);
        parseParametersMethod.setAccessible(true);

        // Test with multiple parameters
        String paramString = "d/Description with spaces t/tag1 tag2 date/2025-03-15";

        // When
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) parseParametersMethod.invoke(parser, paramString);

        // Then
        assertEquals("Description with spaces", result.get("d"));
        assertEquals("tag1 tag2", result.get("t"));
        assertEquals("2025-03-15", result.get("date"));
    }

    @Test
    void parseAmount_validAmount_returnsCorrectValue() throws Exception {
        // Get the private method using reflection
        Method parseAmountMethod = Parser.class.getDeclaredMethod("parseAmount", String.class);
        parseAmountMethod.setAccessible(true);

        // Test with valid amount
        String amountStr = "123.45";

        // When
        double result = (double) parseAmountMethod.invoke(parser, amountStr);

        // Then
        assertEquals(123.45, result, 0.001);
    }

    @Test
    void parseAmount_wholeNumber_returnsCorrectValue() throws Exception {
        // Get the private method using reflection
        Method parseAmountMethod = Parser.class.getDeclaredMethod("parseAmount", String.class);
        parseAmountMethod.setAccessible(true);

        // Test with whole number
        String amountStr = "100";

        // When
        double result = (double) parseAmountMethod.invoke(parser, amountStr);

        // Then
        assertEquals(100.0, result, 0.001);
    }
}
