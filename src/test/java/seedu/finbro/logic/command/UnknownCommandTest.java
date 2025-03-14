package seedu.finbro.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Test class for UnknownCommand.
 */
class UnknownCommandTest {
    private UnknownCommand unknownCommand;
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        // Initialize the command with an unknown command word
        unknownCommand = new UnknownCommand("invalidCmd");

        // Initialize dependencies (not mocked)
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();
    }

    @Test
    void execute_returnsCorrectErrorMessage() {
        // Expected error message
        String expectedMessage = "Unknown command: invalidCmd\nType 'help' to see available commands.";

        // Assert that execute() returns the correct error message
        assertEquals(expectedMessage, unknownCommand.execute(transactionManager, ui, storage));
    }

    @Test
    void isExit_returnsFalse() {
        // Assert that isExit() always returns false
        assertFalse(unknownCommand.isExit());
    }
}
