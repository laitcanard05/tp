package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the ExitCommand class.
 */
class ExitCommandTest {
    private ExitCommand exitCommand;
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        exitCommand = new ExitCommand();
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    @Test
    void execute_shouldReturnGoodbyeMessage() {
        String result = exitCommand.execute(transactionManager, ui, storage);
        assertEquals("Thank you for using FinBro. Your finances are now in better shape!\n"
                + "Goodbye!", result);
    }

    @Test
    void isExit_shouldReturnTrue() {
        assertTrue(exitCommand.isExit());
    }
}
