package seedu.finbro.logic.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Ui.
 * Tests the user interface functionality by capturing system output and
 * simulating user input to verify correct behavior.
 */
class IncomeCommandTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private IncomeCommand income;
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    /**
     * Sets up the test environment before each test.
     * Redirects System.out to allow output verification.
     */
    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Cleans up the test environment after each test.
     * Restores the original System.out and System.in.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Tests that Income constructor works correctly
     */
    @Test
    void incomeConstructor_shouldReturnCorrectAmount() {
        double testAmt = 3.14;
        String testDescription = "Test description";
        List<String> testTags = List.of("Test tag 1", "Test tag 2", "Test tag 3");
        income = new IncomeCommand(testAmt, testDescription, testTags);

        String result = income.execute(transactionManager, ui, storage);
        String expectedResult = "New income added: [Income] $3.14 - " +
                "Test description [Test tag 1, Test tag 2, Test tag 3]";
        assertEquals(expectedResult, result);

    }

}
