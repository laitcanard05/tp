package seedu.finbro.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a financial transaction in the FinBro application.
 */
public abstract class Transaction {
    private static final Logger logger = Logger.getLogger(Transaction.class.getName());
    protected final double amount;
    protected final String description;
    protected final LocalDate date;
    protected final List<String> tags;
    protected int indexNum;

    /**
     * Constructs a Transaction with the specified amount, description, and optional tags.
     *
     * @param amount The amount of money involved in the transaction
     * @param description A description of the transaction
     * @param tags Optional tags for categorizing the transaction
     */
    public Transaction(double amount, String description, List<String> tags) {
        assert amount >= 0 : "Transaction amount should be non-negative";
        assert description != null : "Transaction description cannot be null";
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now(); // Default to current date
        this.tags = tags != null ? tags : new ArrayList<>();
        logger.fine("Created new transaction with amount $" + amount + " and description: " + description);
    }

    /**
     * Constructs a Transaction with the specified amount, description, date, and optional tags.
     *
     * @param amount The amount of money involved in the transaction
     * @param description A description of the transaction
     * @param date The date of the transaction
     * @param tags Optional tags for categorizing the transaction
     */
    public Transaction(double amount, String description, LocalDate date, List<String> tags) {
        assert amount >= 0 : "Transaction amount should be non-negative";
        assert description != null : "Transaction description cannot be null";
        assert date != null : "Transaction date cannot be null";
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.tags = tags != null ? tags : new ArrayList<>();
        logger.fine("Created new transaction with amount $"
                + amount + ", description: " + description + ", date: " + date);
    }

    /**
     * Returns the amount of the transaction.
     *
     * @return The amount of the transaction
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the description of the transaction.
     *
     * @return The description of the transaction
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date of the transaction.
     *
     * @return The date of the transaction
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the tags associated with the transaction.
     *
     * @return The tags associated with the transaction
     */
    public List<String> getTags() {
        assert tags != null : "Tags list cannot be null";
        return new ArrayList<>(tags); // Return a copy to prevent modification
    }

    /**
     * Returns a string representation of the transaction.
     *
     * @return A string representation of the transaction
     */
    @Override
    public abstract String toString();
}
