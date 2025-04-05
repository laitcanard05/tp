package seedu.finbro.logic.command;

import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Represents a command to view the current balance.
 * This command calculates and displays the current balance,
 * which is the sum of all income transactions minus the sum of all expense transactions.
 */
public class BalanceCommand implements Command {
    private static final Logger logger = Logger.getLogger(BalanceCommand.class.getName());
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");

    /**
     * Executes the command to view the current balance.
     * Shows the total balance, total income, and total expenses.
     *
     * @param transactionManager the transaction manager containing the transactions
     * @param ui the user interface
     * @param storage the storage for persistent data
     * @return a string representation of the current balance information
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        assert transactionManager != null : "TransactionManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";

        logger.info("Executing balance command");

        double balance = transactionManager.getBalance();
        double totalIncome = transactionManager.getTotalIncome();
        double totalExpenses = transactionManager.getTotalExpenses();

        // Verify the relationship between balance, income, and expenses
        assert Math.abs((totalIncome - totalExpenses) - balance) < 0.001 :
                "Balance calculation error: " + balance + " != " + totalIncome + " - " + totalExpenses;

        String formattedBalance = seedu.finbro.util.CurrencyFormatter.format(balance);
        String formattedIncome = seedu.finbro.util.CurrencyFormatter.format(totalIncome);
        String formattedExpenses = seedu.finbro.util.CurrencyFormatter.format(totalExpenses);

        return "Current Balance: " + formattedBalance + "\n" +
                "Total Income: " + formattedIncome + "\n" +
                "Total Expenses: " + formattedExpenses;
    }

    /**
     * Returns whether the command exits the program.
     *
     * @return false as this command does not exit the program
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
