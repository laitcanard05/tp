package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.logging.Logger;

public class TrackSavingsGoalCommand implements Command {
    private static final Logger logger = Logger.getLogger(TrackSavingsGoalCommand.class.getName());
    private static final double DEFAULT_SAVINGS_GOAL = -1.0;
    private final int month;
    private final int year;

    /**
     * Constructs a TrackSavingsGoalCommand with the specified month and year.
     *
     * @param month the month for which the savings goal is to be tracked
     * @param year  the year for which the savings goal is to be tracked
     */
    public TrackSavingsGoalCommand(int month, int year) {
        this.month = month;
        this.year = year;
    }

    /**
     * Executes the command to track a savings goal for a specific month and year.
     *
     * @param transactionManager The transaction manager to execute the command on
     * @return A string indicating whether the savings goal was successfully tracked or not
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        assert month > 0 && month <= 12 : "Month should be between 1 and 12";
        assert year > 0 : "Year should be positive";

        logger.info("Executing track savings goal command");
        double savingsGoal = transactionManager.getSavingsGoal(month, year);
        String result = "";
        if (savingsGoal == DEFAULT_SAVINGS_GOAL) {
            result += String.format("No savings goal set for %d/%d", month, year);
        }
        result += String.format("Savings Goal for %d/%d: $%.2f\n", month, year, savingsGoal);

        double totalIncome = transactionManager.getMonthlyTotalIncome(month, year);
        double totalExpense = transactionManager.getMonthlyTotalExpense(month, year);
        double savings = totalIncome - totalExpense;

        result += String.format("Total Income: $%.2f\n", totalIncome);
        result += String.format("Total Expenses: $%.2f\n", totalExpense);

        if (savings >= 0) {
            result += String.format("Total Savings: $%.2f\n", savings);
            if (savings >= savingsGoal) {
                result += "Congratulations! You have met your savings goal!";
            } else {
                result += "You are short of your savings goal by $" + (savingsGoal - savings);
            }
        } else {
            result += String.format("Net Spending: $%.2f\n", Math.abs(savings));
            result += "You are short of your savings goal by $" + (savingsGoal - savings);
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
