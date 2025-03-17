package seedu.finbro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages transactions in the FinBro application.
 */
public class TransactionManager {
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());
    private final List<Transaction> transactions;

    /**
     * Constructs a TransactionManager with an empty list of transactions.
     */
    public TransactionManager() {
        this.transactions = new ArrayList<>();
        logger.info("Created new TransactionManager");
    }

    /**
     * Adds a transaction to the manager.
     *
     * @param transaction The transaction to add
     */
    public void addTransaction(Transaction transaction) {
        assert transaction != null : "Cannot add null transaction";
        transactions.add(transaction);
        logger.info("Added " + transaction.getClass().getSimpleName() +
                " with amount $" + transaction.getAmount() +
                " and description: " + transaction.getDescription());
    }

    /**
     * Deletes a transaction at the specified index.
     *
     * @param index The index of the transaction to delete (1-based)
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void deleteTransaction(int index) {
        if (index < 1 || index > transactions.size()) {
            logger.warning("Attempt to delete transaction at invalid index: " + index);
            throw new IndexOutOfBoundsException("Transaction index out of range: " + index);
        }
        Transaction removed = transactions.remove(index - 1); // Convert from 1-based to 0-based
        logger.info("Deleted " + removed.getClass().getSimpleName() +
                " with amount $" + removed.getAmount() +
                " at index " + index);
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

        return listTransactions().stream()
                .filter(t -> keywords.stream()
                        .anyMatch(k -> t.getDescription().toLowerCase().contains(k.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions between the specified start and end dates.
     *
     * @param startDate The start date (inclusive)
     * @param endDate   The end date (inclusive)
     * @return List of transactions within the date range
     */
    public ArrayList<Transaction> filterTransactions(LocalDate startDate, LocalDate endDate) {
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
    public ArrayList<Transaction> getTransactionDuplicates(String description, double amount) {
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
     * Gets the total income for a specific month and year.
     *
     * @param month The month (1-12)
     * @param year The year
     * @return The total income for the specified month and year
     */
    public double getMonthlyIncome(int month, int year) {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .filter(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Gets the total expenses for a specific month and year.
     *
     * @param month The month (1-12)
     * @param year  The year
     * @return The total expenses for the specified month and year
     */
    public double getMonthlyExpenses(int month, int year) {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .filter(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Gets expenses by category for a specific month and year.
     *
     * @param month The month (1-12)
     * @param year  The year
     * @return A map of category to total expenses
     */
    public Map<Expense.Category, Double> getMonthlyExpensesByCategory(int month, int year) {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .filter(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year)
                .map(t -> (Expense) t)
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
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
}
