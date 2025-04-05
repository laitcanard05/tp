package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.Optional;
import java.util.logging.Logger;

public class TrackSavingsGoalCommand implements Command {
    private static final Logger logger = Logger.getLogger(TrackSavingsGoalCommand.class.getName());
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
     * @param ui                The UI to interact with the user
     * @param storage           The storage to save data
     * @return A string indicating whether the savings goal was successfully tracked or not
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        assert month > 0 && month <= 12 : "Month should be between 1 and 12";
        assert year > 0 : "Year should be positive";

        logger.info("Executing track savings goal command");

        // Get savings goal as an Optional to better represent possibly missing value
        Optional<Double> savingsGoal = transactionManager.getSavingsGoalOptional(month, year);
        StringBuilder result = new StringBuilder();

        if (!savingsGoal.isPresent()) {
            return String.format("No savings goal set for %d/%d. Please set a savings goal first.", month, year);
        }

        result.append(String.format("Savings Goal for %d/%d: $%.2f\n", month, year, savingsGoal.get()));

        double totalIncome = transactionManager.getMonthlyTotalIncome(month, year);
        double totalExpense = transactionManager.getMonthlyTotalExpense(month, year);
        double savings = totalIncome - totalExpense;

        result.append(String.format("Total Income: $%.2f\n", totalIncome));
        result.append(String.format("Total Expenses: $%.2f\n", totalExpense));

        if (savings >= 0) {
            result.append(String.format("Total Savings: $%.2f\n", savings));
            if (savings >= savingsGoal.get()) {
                result.append("Congratulations! You have met your savings goal!");
            } else {
                result.append(String.format("You are short of your savings goal by $%.2f",
                        (savingsGoal.get() - savings)));
            }
        } else {
            result.append(String.format("Net Spending: $%.2f\n", Math.abs(savings)));
            result.append(String.format("You are short of your savings goal by $%.2f",
                    (savingsGoal.get() - savings)));
        }

        return result.toString();
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
