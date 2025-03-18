package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Represents a command to delete a transaction.
 */
public class DeleteCommand implements Command {
    private static final int INDEX_OFFSET = 1;
    private final int transactionIndex;

    /**
     * Constructs a DeleteCommand with the specified index.
     *
     * @param transactionIndex The index of the transaction to delete (1-based)
     *
     */
    public DeleteCommand(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    /**
     * Executes the command to delete a transaction.
     *
     * @param transactionManager The transaction manager to delete the transaction from
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        try {
            if (transactionIndex < 1 || transactionIndex > transactionManager.getTransactionCount()) {
                return "Invalid transaction index. There are only " +
                        transactionManager.getTransactionCount() + " transactions.";
            }

            // Get the transaction to display in the confirmation message
            String transactionToDelete = transactionManager.listTransactions().get(transactionIndex - INDEX_OFFSET).toString();

            // Delete the transaction
            transactionManager.deleteTransaction(transactionIndex);
            storage.saveTransactions(transactionManager);

            return "Transaction deleted: " + transactionToDelete;
        } catch (IndexOutOfBoundsException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns false since this is not an exit command.
     *
     * @return false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
