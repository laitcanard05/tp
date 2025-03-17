package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

// TODO add filtering of list by date

/**
 * Represents a command to list all transactions.
 */
public class ListCommand implements Command {

    /**
     * Executes the command to list all transactions.
     *
     * @param transactionManager The transaction manager to retrieve the transactions from
     * @param ui                 The UI to interact with the user
     * @param storage            The storage, if needed
     * @return The response message containing the list of transactions
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        if (transactionManager.listTransactions().isEmpty()) {
            return "No transactions found.";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < transactionManager.listTransactions().size(); i++) {
            String entry = transactionManager.getIndexNum(i) + ". " +
                           transactionManager.listTransactions().get(i).toString();
            result.append(entry).append("\n");
        }
        return result.toString();
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
