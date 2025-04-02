package seedu.finbro.logic.command;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

public class SetBudgetCommand implements Command {
    private final double budget;
    private final int month;
    private final int year;

    /**
     * Constructs a SetBudgetCommand with the specified budget, month, and year.
     *
     * @param budget the budget to be set
     * @param month  the month for which the budget is set
     * @param year   the year for which the budget is set
     */
    public SetBudgetCommand(double budget, int month, int year) {
        this.budget = budget;
        this.month = month;
        this.year = year;
    }

    /**
     * Executes the command to set a budget for a specific month and year.
     *
     * @param transactionManager The transaction manager to execute the command on
     * @return A string indicating whether the budget was successfully set or not
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        transactionManager.setBudget(month, year, budget);
        storage.saveBudgets(transactionManager);
        return ("Budget of " + budget + " has been set for " + month + "/" + year);
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
