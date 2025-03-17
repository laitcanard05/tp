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
     * Calculates the total expenses for a specified month and year,
     * categorized by tags.
     *
     * @param month the month for which to calculate tagged expenses (1-12)
     * @param year the year for which to calculate tagged expenses
     * @return a map where keys are tags and values are total expenses associated with that tag
     */
    public Map<String, Double> getMonthlyTaggedExpenses(int month, int year) {
        Map<String, Double> taggedExpenses = new HashMap<>();

        ArrayList<Transaction> filteredTransactions = transactions.stream()
            .filter(t -> t instanceof Expense)
            .filter(t -> (t.getDate().getYear() == year && t.getDate().getMonthValue() == month))
            .collect(Collectors.toCollection(ArrayList::new));

        for (Transaction transaction : filteredTransactions) {
            List<String> transactionTags = transaction.getTags();
            for (String tag : transactionTags) {
                if (taggedExpenses.putIfAbsent(tag, transaction.getAmount()) != null) {
                    taggedExpenses.replace(tag, transaction.getAmount()+taggedExpenses.get(tag));
                }
            }
        }
        return taggedExpenses;
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
