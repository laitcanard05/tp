package seedu.finbro.logic.command;

import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.util.logging.Logger;

// TODO: WRITE JUNIT TESTS FOR THIS COMMAND WHEN IT IS IMPLEMENTED

public class SetSavingsGoalCommand implements Command {
    private static final Logger logger = Logger.getLogger(SetSavingsGoalCommand.class.getName());
    private final double savingsGoal;
    private final int month;
    private final int year;

    /**
     * Constructs a SetSavingsCommand with the specified savings, month, and year.
     *
     * @param savingsGoal the savings goal to be set
     * @param month   the month for which the savings goal is to be set
     * @param year    the year for which the savings goal is to be set
     */
    public SetSavingsGoalCommand(double savingsGoal, int month, int year) {
        this.savingsGoal = savingsGoal;
        this.month = month;
        this.year = year;
    }

    /**
     * Executes the command to set a savings for a specific month and year.
     *
     * @param transactionManager The transaction manager to execute the command on
     * @return A string indicating whether the savings was successfully set or not
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        assert savingsGoal >= 0 : "Savings goal should be non-negative";
        assert month > 0 && month <= 12 : "Month should be between 1 and 12";
        assert year > 0 : "Year should be positive";

        logger.info("Executing set savings goal command");
        transactionManager.setSavingsGoal(month, year, savingsGoal);
        storage.saveSavingsGoals(transactionManager);
        return ("Savings Goal of " + savingsGoal + " has been set for " + month + "/" + year);
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
