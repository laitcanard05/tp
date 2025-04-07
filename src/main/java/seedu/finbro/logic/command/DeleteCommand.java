package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command to delete a transaction.
 */
public class DeleteCommand implements Command {
    private static final int INDEX_OFFSET = 1;
    private final int startIndex;
    private final int endIndex;

    public DeleteCommand(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Executes the command to delete one transaction or a range of transactions.
     *
     * @param transactionManager The transaction manager to delete the transaction from
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        try {
            int total = transactionManager.getTransactionCount();
            if (total == 0) {
                return "There are no transactions to delete.";
            }
            else if (total == 1 && startIndex > 1) {
                return "Invalid index range. There is only " + total + " transaction.";
            }
            else if (startIndex < 1 || endIndex > total || startIndex > endIndex) {
                return "Invalid index range. There are only " + total + " transactions.";
            }

            List<String> deletedTransactions = new ArrayList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                String transactionStr = transactionManager.listTransactions().get(i - INDEX_OFFSET).toString();
                deletedTransactions.add(transactionStr);
            }

            //Delete in reverse to avoid index shifting issues
            for (int i = endIndex; i >= startIndex; i--) {
                transactionManager.deleteTransaction(i);
            }

            StringBuilder result = new StringBuilder("Deleted transactions:\n");
            for (String transaction : deletedTransactions) {
                result.append("- ").append(transaction).append("\n");
            }

            storage.saveTransactions(transactionManager);
            return result.toString().trim();
        } catch (Exception e) {
            return "Error during deletion: " + e.getMessage();
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
