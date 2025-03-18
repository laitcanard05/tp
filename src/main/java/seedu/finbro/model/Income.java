package seedu.finbro.model;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents an income transaction in the FinBro application.
 */
public class Income extends Transaction {
    private static final Logger logger = Logger.getLogger(Income.class.getName());
    /**
     * Constructs an Income with the specified amount, description, and optional tags.
     *
     * @param amount The amount of money received
     * @param description A description of the income
     * @param tags Optional tags for categorizing the income
     */
    public Income(double amount, String description, List<String> tags) {
        super(amount, description, tags);
        assert amount > 0 : "Income amount must be greater than zero";
        logger.fine("Created new income with amount $" + amount);
    }

    /**
     * Constructs an Income with the specified amount, description, date, and optional tags.
     *
     * @param amount The amount of money received
     * @param description A description of the income
     * @param date The date of the income
     * @param tags Optional tags for categorizing the income
     */
    public Income(double amount, String description, LocalDate date, List<String> tags) {
        super(amount, description, date, tags);
        assert amount > 0 : "Income amount must be greater than zero";
        logger.fine("Created new income with amount $" + amount + " for date " + date);
    }

    /**
     * Returns a string representation of the income.
     *
     * @return A string representation of the income
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Income] $").append(String.format("%.2f", amount))
                .append(" - ").append(description);

        if (!tags.isEmpty()) {
            sb.append(" [");
            for (int i = 0; i < tags.size(); i++) {
                sb.append(tags.get(i));
                if (i < tags.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
