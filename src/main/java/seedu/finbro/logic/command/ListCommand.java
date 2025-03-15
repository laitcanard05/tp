package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.stream.Collectors;

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
        if (!transactionManager.listTransactions().isEmpty()) {
            return transactionManager.listTransactions()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
        } else {
            return "No transactions found.";
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
