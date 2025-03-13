package seedu.finbro.logic.command;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.model.Transaction;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.util.ArrayList;

public class FilterCommand implements Command {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public FilterCommand(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Executes the command to filter transactions between the specified start date and end date
     *
     * @param transactionManager The transaction manager to execute the command on
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The string representation of the filtered commands
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        ArrayList<Transaction>  filteredTransactions = transactionManager.getFilteredTransactions(startDate, endDate);
        String filteredTransactionsDisplay = "";
        for (int i = 0; i < filteredTransactions.size(); i++) {
            filteredTransactionsDisplay += filteredTransactions.get(i).toString() + "\n";
        }
        return filteredTransactionsDisplay;
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

