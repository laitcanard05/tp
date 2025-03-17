package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Represents a command to delete a transaction.
 */
public class DeleteCommand implements Command {

    private final int transactionIndex;

    /**
     * Constructs a deleteCommand with the specified transaction index.
     *
     * @param transactionIndex The index of the transaction to delete.
     */
    public DeleteCommand(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    /**
     * Executes the delete command to remove a transaction.
     *
     * @param transactionManager The transaction manager to manage transactions.
     * @param ui                 The UI to interact with the user.
     * @param storage            The storage for persistence (if needed).
     * @return The response message after deleting the transaction.
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        if (transactionIndex < 1 || transactionIndex > transactionManager.listTransactions().size()) {
            return "Invalid transaction index.";
        }
        transactionManager.deleteTransaction(transactionIndex);
        return "Transaction " + transactionIndex + " has been deleted.";
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
