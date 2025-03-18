package seedu.finbro.logic.command;

import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Represents a command to filter transactions by date.
 */
public class FilterCommand implements Command {
    private static final Logger logger = Logger.getLogger(FilterCommand.class.getName());
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Constructs a FilterCommand with the specified start and end dates.
     *
     * @param startDate The start date for filtering
     * @param endDate   The end date of the filter, should not be before startDate
     */
    public FilterCommand(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate != null ? endDate : LocalDate.now();
    }

    /**
     * Executes the command to filter transactions by date.
     *
     * @param transactionManager The transaction manager to filter transactions in
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        logger.info("Executing filter command");

        logger.info("Filtering transactions between " + startDate + " and " + endDate);
        ArrayList<Transaction> filteredTransactions = transactionManager.getFilteredTransactions(startDate, endDate);

        if (filteredTransactions.isEmpty()) {
            return String.format("There are no transactions between %s and %s.",
                    startDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")),
                    endDate.format(DateTimeFormatter.ofPattern("MMM d yyyy")));
        }

        logger.info("Generating filtered transactions display");
        StringBuilder response = new StringBuilder("Showing transactions from " +
                startDate + " to " + endDate + ":\n");
        for (int i = 0; i < filteredTransactions.size(); i++) {
            response.append(i + 1).append(". ").append(filteredTransactions.get(i)).append("\n");
        }

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
