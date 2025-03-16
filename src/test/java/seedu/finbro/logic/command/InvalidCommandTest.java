package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for the InvalidCommand class.
 */
public class InvalidCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    @Test
    void execute_returnsErrorMessage() {
        String errorMessage = "Test error message";
        InvalidCommand command = new InvalidCommand(errorMessage);

        String result = command.execute(transactionManager, ui, storage);

        assertEquals("Invalid command: " + errorMessage, result);
    }

    @Test
    void isExit_returnsFalse() {
        InvalidCommand command = new InvalidCommand("Test error");
        assertFalse(command.isExit());
    }
}
