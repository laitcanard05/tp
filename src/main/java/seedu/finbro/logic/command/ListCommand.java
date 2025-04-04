package seedu.finbro.logic.command;

import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a command to list transactions.
 */
public class ListCommand implements Command {
    private static final Logger logger = Logger.getLogger(ListCommand.class.getName());
    private static final int INDEX_OFFSET = 1;
    private final Integer limit;
    private final LocalDate date;

    /**
     * Constructs a ListCommand with optional limit and date.
     *
     * @param limit The maximum number of transactions to list, or null for all
     * @param date  The date to filter transactions by, or null for all dates
     */
    public ListCommand(Integer limit, LocalDate date) {
        this.limit = limit;
        this.date = date;
        logger.fine("Constructed ListCommand with limit=" + limit + ", date=" + date);
    }

    /**
     * Default constructor for ListCommand with no parameters.
     * This maintains backward compatibility with existing code.
     */
    public ListCommand() {
        this.limit = null;
        this.date = null;
        logger.fine("Constructed ListCommand with default constructor");
    }

    /**
     * Executes the command to list transactions.
     *
     * @param transactionManager The transaction manager to list transactions from
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        logger.info("Executing ListCommand");

        List<Transaction> transactionsToList;
        int totalTransactionCount = transactionManager.getTransactionCount();
        StringBuilder response = new StringBuilder();
        boolean limitExceeded = false;

        if (date != null) {
            logger.fine("Filtering transactions from date: " + date);
            transactionsToList = transactionManager.listTransactionsFromDate(date);
            if (limit != null && limit > transactionsToList.size()) {
                limitExceeded = true;
            }
            if (limit != null) {
                logger.fine("Limiting to " + limit + " transactions after filtering by date");
                transactionsToList = transactionsToList.subList(0, Math.min(limit, transactionsToList.size()));
            }
        } else if (limit != null) {
            logger.fine("Limiting to " + limit + " most recent transactions");
            if (limit > totalTransactionCount) {
                limitExceeded = true;
            }
            transactionsToList = transactionManager.listTransactions(limit);
        } else {
            logger.fine("Listing all transactions");
            transactionsToList = transactionManager.listTransactions();
        }

        if (transactionsToList.isEmpty()) {
            logger.info("No transactions found");
            return "No transactions found.";
        }

        if (limitExceeded) {
            response.append(
                    "Note: Requested limit exceeds available transactions. Showing all available transactions.\n\n"
            );
        }

        response.append("Here are your transactions:\n");
        for (int i = 0; i < transactionsToList.size(); i++) {
            Transaction t = transactionsToList.get(i);
            logger.finer("Listing transaction: " + t);
            response.append(i + INDEX_OFFSET).append(". ").append(t);
            response.append(" (Date created: ").append(t.getDate()).append(")\n");
        }

        logger.info("Successfully listed " + transactionsToList.size() + " transactions");
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
