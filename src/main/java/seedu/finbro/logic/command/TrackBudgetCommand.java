package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.logging.Logger;
import static java.lang.Math.abs;

//TODO: WRITE JUNIT TESTS FOR THIS COMMAND WHEN IT IS IMPLEMENTED

public class TrackBudgetCommand implements Command {
    private static final Logger logger = Logger.getLogger(TrackBudgetCommand.class.getName());
    private static final double DEFAULT_BUDGET = -1.0;
    private final int month;
    private final int year;

    /**
     * Constructs a TrackBudgetCommand with the specified month and year.
     *
     * @param month the month for which the budget is tracked
     * @param year  the year for which the budget is tracked
     */
    public TrackBudgetCommand(int month, int year) {
        this.month = month;
        this.year = year;
    }

    /**
     * Executes the command to track the budget for a specific month and year.
     *
     * @param transactionManager The transaction manager to execute the command on
     * @param ui                The UI to interact with the user
     * @param storage           The storage to save data
     * @return A string indicating how much of the budget has been used
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        assert month > 0 && month <= 12 : "Month must be between 1 and 12";
        assert year > 0 : "Year must be a positive integer";

        double budget = transactionManager.getBudget(month, year);
        logger.info("Executing track budget command");
        if (budget == DEFAULT_BUDGET) {
            logger.info(String.format("No budget set for %d/%d", month, year));
            return String.format("No budget set for %d/%d. Please set a budget first.", month, year);
        }
        double totalExpense = transactionManager.getMonthlyTotalExpense(month, year);
        double remainingBudget = budget - totalExpense;

        String result = String.format("Budget for %02d/%d: $%.2f\n", month, year, budget);
        result += String.format("Total Expenses: $%.2f\n", totalExpense);
        if (remainingBudget < 0) {
            logger.info("Exceeded budget");
            result += String.format("You have exceeded your budget by $%.2f!", abs(remainingBudget));
        } else {
            logger.info("Within budget");
            result += "You are within your budget.\n";
            result += String.format("Remaining Budget: $%.2f", remainingBudget);
        }

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
