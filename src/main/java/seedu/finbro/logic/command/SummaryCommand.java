package seedu.finbro.logic.command;
import seedu.finbro.model.Expense;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.text.DateFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class SummaryCommand implements Command {
    private final int month;
    private final int year;

    /**
     * Constructs a SummaryCommand with the specified month and end year.
     *
     * @param month the month in which transactions will be used for the summary
     * @param year the year in which transactions will be used for the summary
     */
    public SummaryCommand(int month, int year) {
        this.month = month;
        this.year = year;
    }

    /**
     * Executes the command to display a summary of the transactions in the specified month and year
     * Summary includes total income, total expenses, top 5 expense categories
     *
     * @param transactionManager The transaction manager to execute the command on
     * @param ui                 The UI to interact with the user
     * @param storage            The storage to save data
     * @return The string representation of the summary
     */
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        String monthString = new DateFormatSymbols().getMonths()[month-1];
        String summaryDisplay = String.format("Financial Summary for %s %d:\n\n",  monthString, year);
        summaryDisplay += String.format("Total Income: $%.2f\n", transactionManager.getMonthlyTotalIncome(month, year));
        summaryDisplay += String.format("Total Expenses: $%.2f\n", transactionManager.getMonthlyTotalExpense(month, year));

        Map<Expense.Category, Double> sortedCategorisedExpenses = transactionManager.getMonthlyCategorisedExpenses(month, year)
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Expense.Category, Double> comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        if (sortedCategorisedExpenses.isEmpty()) {
            return summaryDisplay;
        }

        summaryDisplay += "\nTop Expense Categories\n";
        int categoryCount = 0;
            final int  MAXIMUM_CATEGORIES_TO_DISPLAY = 5;
        for (Map.Entry<Expense.Category, Double> expenseInCategory : sortedCategorisedExpenses.entrySet()) {
            categoryCount++;
            if (expenseInCategory.getValue() == 0) {
                break;
            }
            summaryDisplay += String.format("%d. %s: $%.2f\n", categoryCount,
                    expenseInCategory.getKey().toString(),
                    expenseInCategory.getValue());
            if (categoryCount >= MAXIMUM_CATEGORIES_TO_DISPLAY) {
                break;
            }
        }

        Map<String, Double> sortedTaggedExpenses = transactionManager.getMonthlyTaggedExpenses(month, year)
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double> comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        if (sortedTaggedExpenses.isEmpty()) {
            return summaryDisplay;
        }

        summaryDisplay += "\nTags Summary\n";
        int tagCount = 0;
        for (Map.Entry<String, Double> expenseInTag : sortedTaggedExpenses.entrySet()) {
            tagCount++;
            if (expenseInTag.getValue() == 0) {
                break;
            }
            summaryDisplay += String.format("%d. %s: $%.2f\n", tagCount,
                    expenseInTag.getKey(),
                    expenseInTag.getValue());
        }

        return summaryDisplay;


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
