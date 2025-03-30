package seedu.finbro.logic.command;

import seedu.finbro.model.Expense;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.List;

/**
 * Represents a command to add an expense.
 */
public class ExpenseCommand implements Command {
    private final double amount;
    private final String description;
    private final Expense.Category category;
    private final List<String> tags;

    /**
     * Constructs an ExpenseCommand with the specified amount, description, category, and tags.
     *
     * @param amount      The expense amount
     * @param description The expense description
     * @param category    The expense category
     * @param tags        The tags for the expense
     */
    public ExpenseCommand(double amount, String description, Expense.Category category, List<String> tags) {
        assert amount > 0 : "Expense amount must be greater than zero";
        assert description != null : "Description cannot be null";
        assert !description.trim().isEmpty() : "Description cannot be empty";
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.tags = tags;
    }

    /**
     * Executes the command to add an expense.
     *
     * @param transactionManager The transaction manager to add the expense to
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        
        Expense expense = new Expense(amount, description, category, tags);
        //if the list of this transaction's duplicates is NOT empty, warn the user
        if (!transactionManager.getTransactionDuplicates(amount, description).isEmpty()) {
            //if the user wishes to cancel the transaction, they will enter "no" which will return false
            if (!ui.warnDuplicate()) {
                return "Transaction cancelled by user";
            }
        }
        transactionManager.addTransaction(expense);
        storage.saveTransactions(transactionManager);
        String result = "New expense added: " + expense;

        //Uncomment to test budget tracking
        /*
        double budget = transactionManager.getBudget(expense.getDate().getMonthValue(), expense.getDate().getYear());
        if (budget != -1.0) {
            double totalExpense = transactionManager.getMonthlyTotalExpense(expense.getDate().getMonthValue(),
                    expense.getDate().getYear());
            if (totalExpense > budget) {
                result +=  String.format("\nWarning: You have exceeded your budget of $%.2f for this month", budget);
            }
        }
        */

        return result;
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
