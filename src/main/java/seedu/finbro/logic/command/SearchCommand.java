package seedu.finbro.logic.command;


import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.List;
import java.util.stream.Collectors;

// TODO add filtering of list by number and date

/**
 * Represents a command to list all transactions.
 */
public class SearchCommand implements Command {

    private final String keyword;

    public SearchCommand(String KEYWORD) {
        this.keyword = KEYWORD;
    }

    /**
     * Executes the command to search for transaction(s) containing certain keywords.
     *
     * @param transactionManager The transaction manager to retrieve the transactions from
     * @param ui                 The UI to interact with the user
     * @param storage            The storage, if needed
     * @return The response message containing the list of transactions
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        if (!transactionManager.listTransactions().isEmpty()) {
            return transactionManager.getTransactionsContainingKeyword(keyword)
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
