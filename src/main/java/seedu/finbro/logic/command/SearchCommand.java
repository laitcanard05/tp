package seedu.finbro.logic.command;


import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.List;
import java.util.logging.Logger;



/**
 * Represents a command to list all transactions that contain the keyword.
 */
public class SearchCommand implements Command {

    private static final Logger logger = Logger.getLogger(SearchCommand.class.getName());
    private static final int INDEX_OFFSET = 1;
    private final String keyword;


    public SearchCommand(String keyword) {
        this.keyword = keyword;
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

        logger.info("Executing SearchCommand");

        List<Transaction> matchingTransactionsList;
        matchingTransactionsList = transactionManager.getTransactionsContainingKeyword(keyword);

        StringBuilder response = new StringBuilder("Here are the transactions containing the keyword:"
                + keyword + "\n");

        for (int i = 0; i < matchingTransactionsList.size(); i++) {
            Transaction t = matchingTransactionsList.get(i);
            logger.finer("Listing transaction: " + t);
            response.append(i + INDEX_OFFSET).append(". ").append(t);
            response.append(" (Date created: ").append(t.getDate()).append(")\n");
        }

        if (matchingTransactionsList.isEmpty()) {
            logger.info("No matching transactions for " + keyword);
            return "No transactions found to contain the keyword: " + "\"" + keyword + "\"";
        }

        logger.info("Successfully listed " + matchingTransactionsList.size() + " transactions");
        return response.toString().trim();
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
