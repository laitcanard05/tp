package seedu.finbro.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.finbro.model.Income;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the ExportCommand class.
 */
public class ExportCommandTest {
    private TransactionManager transactionManager;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    void setUp() {
        transactionManager = new TransactionManager();
        ui = new Ui();
        storage = new Storage();

        // Add a sample transaction
        transactionManager.addTransaction(new Income(100.0, "Test income", new ArrayList<>()));
    }

    @Test
    void execute_defaultFormat_returnsCsvExportMessage() {
        ExportCommand command = new ExportCommand(null);
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Data exported successfully"));
        assertTrue(result.contains(".csv"));
    }

    @Test
    void execute_csvFormat_returnsCsvExportMessage() {
        ExportCommand command = new ExportCommand("csv");
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Data exported successfully"));
        assertTrue(result.contains(".csv"));
    }

    @Test
    void execute_txtFormat_returnsTxtExportMessage() {
        ExportCommand command = new ExportCommand("txt");
        String result = command.execute(transactionManager, ui, storage);

        assertTrue(result.contains("Data exported successfully"));
        assertTrue(result.contains(".txt"));
    }

    @Test
    void isExit_returnsFalse() {
        ExportCommand command = new ExportCommand("csv");
        assertFalse(command.isExit());
    }
}
