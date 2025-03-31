package seedu.finbro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manages transactions in the FinBro application.
 */
public class TransactionManager {
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());
    private static final int INDEX_OFFSET = 1;
    private static final double DEFAULT_BUDGET = -1.0;
    private static final double DEFAULT_SAVINGS_GOAL = -1.0;
    private final List<Transaction> transactions;
    private final Map<String, Double> budgets = new HashMap<>();
    private final Map<String, Double> savingsGoals = new HashMap<>();

    /**
     * Constructs a TransactionManager with an empty list of transactions.
     */
    public TransactionManager() {
        this.transactions = new ArrayList<>();
        logger.info("Created new TransactionManager");
    }

    /**
     * Adds a transaction to the list of transactions and increments the transaction index.
     *
     * @param transaction The transaction to be added. Must not be null.
     */
    public void addTransaction(Transaction transaction) {
        assert transaction != null : "Cannot add null transaction";
        transactions.add(transaction);
        transaction.indexNum = transactions.size();
        logger.info("Added " + transaction.getClass().getSimpleName() +
                " with amount $" + transaction.getAmount() +
                " and description: " + transaction.getDescription());
    }

    /**
     * Deletes a transaction at the specified index or range.
     *
     * @param index The index of the transaction to delete (1-based)
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void deleteTransaction(int index) {
        assert index >= 0 : "Index must be non-negative";
        assert index <= transactions.size() : "Index must be within the bounds of the transaction list";

        if (index < 1 || index > transactions.size()) {
            logger.warning("Attempt to delete transaction at invalid index: " + index);
            throw new IndexOutOfBoundsException("Transaction index out of range: " + index);
        }
        Transaction removed = transactions.remove(index - INDEX_OFFSET);

        for (int i = index; i < transactions.size(); i++) {
            transactions.get(i).indexNum -= INDEX_OFFSET;
        }
        
        logger.info("Deleted " + removed.getClass().getSimpleName() +
                " with amount $" + removed.getAmount() +
                " at index " + index);
    }

    /**
     * Gets the index number of a transaction at the specified position.
     *
     * @param index The index in the list (0-based)
     * @return The transaction's index number (1-based)
     */
    public int getIndexNum(int index) {
        assert index >= 0 : "Index must be non-negative";
        assert index < transactions.size() : "Index must be within the bounds of the transaction list";
        return transactions.get(index).indexNum;
    }

    /**
     * Lists all transactions in reverse chronological order.
     *
     * @return List of all transactions in reverse chronological order
     */
    public List<Transaction> listTransactions() {
        assert transactions != null : "Transactions list cannot be null";
        // Sort by date in reverse chronological order
        List<Transaction> sortedTransactions = new ArrayList<>(transactions);
        Collections.sort(sortedTransactions, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        return sortedTransactions;
    }

    /**
     * Lists a limited number of transactions in reverse chronological order.
     *
     * @param limit The maximum number of transactions to return
     * @return List of transactions, limited to the specified number
     */
    public List<Transaction> listTransactions(int limit) {
        List<Transaction> allTransactions = listTransactions();
        return allTransactions.subList(0, Math.min(limit, allTransactions.size()));
    }

    /**
     * Lists transactions from a specific date in reverse chronological order.
     *
     * @param date The date to filter transactions from
     * @return List of transactions from the specified date
     */
    public List<Transaction> listTransactionsFromDate(LocalDate date) {
        return listTransactions().stream()
                .filter(t -> !t.getDate().isBefore(date))
                .collect(Collectors.toList());
    }

    /**
     * Searches for transactions whose descriptions contain any of the given keywords.
     *
     * @param keywords The keywords to search for
     * @return List of transactions matching the search criteria
     */
    public List<Transaction> searchTransactions(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }
        assert keywords != null : "Keywords list should have been detected as null";
        return listTransactions().stream()
                .filter(t -> keywords.stream()
                        .anyMatch(k -> t.getDescription().toLowerCase().contains(k.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of transactions that occurred within the specified date range, inclusive.
     *
     * @param startDate the start date of the filter range (inclusive)
     * @param endDate the end date of the filter range (inclusive)
     * @return a list of transactions that fall within the specified date range;
     *     returns an empty list if no transactions match
     */
    public ArrayList<Transaction> getFilteredTransactions(LocalDate startDate, LocalDate endDate) {
        assert startDate != null : "Start date cannot be null";
        assert endDate != null : "End date cannot be null";
        assert !startDate.isAfter(endDate) : "Start date cannot be after end date";
        return transactions.stream()
            .filter(t -> (t.getDate().isEqual(startDate) || t.getDate().isAfter(startDate)) &&
                    (t.getDate().isEqual(endDate) || t.getDate().isBefore(endDate)))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return stream of all transactions that contain keyword
     */
    public ArrayList<Transaction> getTransactionsContainingKeyword(String keyword) {
        assert keyword != null : "Search keyword cannot be null";
        assert !keyword.trim().isEmpty() : "Search keyword cannot be empty";
        return transactions.stream()
            .filter(t -> (t.getDescription().contains(keyword)))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return stream of all transactions that have exact same name and amount
     */
    public ArrayList<Transaction> getTransactionDuplicates(double amount, String description) {
        assert description != null : "Description cannot be null";
        assert amount > 0 : "Amount must be greater than zero";
        return transactions.stream()
            .filter(t -> (t.getDescription().equals(description) && t.getAmount() == amount ))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Calculates the current balance based on all transactions.
     *
     * @return The current balance
     */
    public double getBalance() {
        double balance = 0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof Income) {
                balance += transaction.getAmount();
            } else if (transaction instanceof Expense) {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }

    /**
     * Calculates the total income from all transactions.
     *
     * @return The total income
     */
    public double getTotalIncome() {
        return transactions.stream()
            .filter(t -> t instanceof Income)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    /**
     * Calculates the total expenses from all transactions.
     *
     * @return The total expenses
     */
    public double getTotalExpenses() {
        return transactions.stream()
            .filter(t -> t instanceof Expense)
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    /**
     * Calculates the total income for a specified month and year.
     *
     * @param month the month for which to calculate total income (1-12)
     * @param year the year for which to calculate total income
     * @return the total income for the specified month and year,
     *     or 0.0 if there are no transactions
     */
    public double getMonthlyTotalIncome(int month, int year) {
        return transactions.stream()
            .filter(t -> t instanceof Income)
            .filter(t -> (t.getDate().getYear() == year &&
                t.getDate().getMonthValue() == month))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    /**
     * Calculates the total expense for a specified month and year.
     *
     * @param month the month for which to calculate total expenses (1-12)
     * @param year the year for which to calculate total expenses
     * @return the total expense for the specified month and year,
     *     or 0.0 if there are no matching transactions
     */
    public double getMonthlyTotalExpense(int month, int year) {
        return transactions.stream()
            .filter(t -> t instanceof Expense)
            .filter(t -> (t.getDate().getYear() == year && t.getDate().getMonthValue() == month))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }

    /**
     * Calculates the total expenses for a specified month and year,
     * categorized by expense type.
     *
     * @param month the month for which to calculate categorized expenses (1-12)
     * @param year the year for which to calculate categorized expenses
     * @return a map where keys are expense categories and values are
     *     total expenses for that category
     */
    public Map<Expense.Category, Double> getMonthlyCategorisedExpenses(int month, int year) {
        Map<Expense.Category, Double> categorisedExpenses = new HashMap<>();

        ArrayList<Transaction> filteredExpenses = transactions.stream()
             .filter(t -> t instanceof Expense)
             .filter(t -> (t.getDate().getYear() == year && t.getDate().getMonthValue() == month))
             .collect(Collectors.toCollection(ArrayList::new));
        for (Transaction transaction : filteredExpenses) {
            if (transaction instanceof Expense) {
                Expense.Category category = ((Expense) transaction).getCategory();
                if (categorisedExpenses.putIfAbsent(category, transaction.getAmount()) != null) {
                    categorisedExpenses.replace(category, transaction.getAmount()+categorisedExpenses.get(category));
                }
            }
        }
        return categorisedExpenses;
    }

    /**
     * Calculates the total transactions for a specified month and year,
     *     categorized by tags.
     *
     * @param month the month for which to calculate tagged transactions (1-12)
     * @param year the year for which to calculate tagged transactions
     * @return a map where keys are tags and values are total transactions associated with that tag
     */
    public Map<String, Double> getMonthlyTaggedTransactions(int month, int year) {
        Map<String, Double> taggedTransactions = new HashMap<>();


        ArrayList<Transaction> filteredTransactions = transactions.stream()
            .filter(t -> (t.getDate().getYear() == year && t.getDate().getMonthValue() == month))
            .collect(Collectors.toCollection(ArrayList::new));

        for (Transaction transaction : filteredTransactions) {
            List<String> transactionTags = transaction.getTags();
            for (String tag : transactionTags) {
                if (taggedTransactions.putIfAbsent(tag, transaction.getAmount()) != null) {
                    taggedTransactions.replace(tag, transaction.getAmount()+taggedTransactions.get(tag));
                }
            }
        }
        return taggedTransactions;
    }

    /**
     * Updates an existing transaction with a new transaction.
     *
     * @param originalTransaction The transaction to be updated
     * @param updatedTransaction The new transaction with updated details
     * @return true if the transaction was successfully updated, false otherwise
     */
    public boolean updateTransaction(Transaction originalTransaction, Transaction updatedTransaction) {
        int index = -1;

        // Find the index of the original transaction
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).equals(originalTransaction)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        // Remove the original and add the updated transaction at the same index
        transactions.remove(index);
        transactions.add(index, updatedTransaction);
        return true;
    }

    /**
     * Clears all transactions.
     */
    public void clearTransactions() {
        int count = transactions.size();
        transactions.clear();
        logger.info("Cleared " + count + " transactions");
    }

    /**
     * Returns the number of transactions.
     *
     * @return The number of transactions
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    /**
     * Sets the budget for the current month and year.
     *
     * @param month The month for which to set the budget (1-12)
     * @param year The year for which to set the budget
     * @param budget The budget amount to set
     */
    public void setBudget(int month, int year, double budget) {
        assert month >= 1 && month <= 12 : "Month must be between 1 and 12";
        assert year > 0 : "Year must be positive";
        assert budget >= 0 : "Budget must be non-negative";

        String budgetKey = year + "-" + month;
        budgets.put(budgetKey, budget);
        logger.info("Set budget of $" + budget + " for " + month + "/" + year);
    }

    /**
     * Retrieves the budget for a specific month and year.
     *
     * @param month The month for which to retrieve the budget (1-12)
     * @param year The year for which to retrieve the budget
     * @return The budget amount for the specified month and year,
     *     or -1.0 if no budget is set
     */
    public double getBudget(int month, int year) {
        String budgetKey = year + "-" + month;
        return budgets.getOrDefault(budgetKey, DEFAULT_BUDGET);
    }

    /**
     * Retrieves all the budget that user has inputted. Used in Storage
     *
     * @return The hashmap of budgets.
     */
    public Map<String, Double> getAllBudgets() {
        return new HashMap<>(budgets); // returns a copy for safety
    }

    /**
     * Sets the savings goal for a specific month and year.
     *
     * @param month The month for which to set the savings goal (1-12)
     * @param year The year for which to set the savings goal
     * @param savingsGoal The savings goal amount to set
     */
    public void setSavingsGoal(int month, int year, double savingsGoal) {
        assert month >= 1 && month <= 12 : "Month must be between 1 and 12";
        assert year > 0 : "Year must be positive";
        assert savingsGoal >= 0 : "Savings goal must be non-negative";

        String savingsKey = year + "-" + month;
        savingsGoals.put(savingsKey, savingsGoal);
        logger.info("Set savings goal of $" + savingsGoal + " for " + month + "/" + year);
    }

    /**
     * Retrieves the savings goal for a specific month and year.
     *
     * @param month The month for which to retrieve the savings goal (1-12)
     * @param year The year for which to retrieve the savings goal
     * @return The savings goal amount for the specified month and year,
     *     or -1.0 if no savings goal is set
     */
    public double getSavingsGoal(int month, int year) {
        String savingsKey = year + "-" + month;
        return savingsGoals.getOrDefault(savingsKey, DEFAULT_SAVINGS_GOAL);
    }

    /**
     * Retrieves all the savings goals that user has inputted. Used in Storage
     *
     * @return The hashmap of savings goals.
     */
    public Map<String, Double> getAllSavingsGoals() {
        return new HashMap<>(savingsGoals); // returns a copy for safety
    }

    /**
     * Returns a specific transaction by index.
     *
     * @param index The index of the transaction to retrieve
     * @return The transaction at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Transaction getTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            throw new IndexOutOfBoundsException("Transaction index out of range: " + index);
        }
        return transactions.get(index);
    }

    /**
     * Finds the first transaction that contains the given description.
     *
     * @param description The description to search for
     * @return The first transaction containing the description, or null if not found
     */
    public Transaction findTransactionByDescription(String description) {
        for (Transaction transaction : transactions) {
            if (transaction.getDescription().contains(description)) {
                return transaction;
            }
        }
        return null;
    }
}
