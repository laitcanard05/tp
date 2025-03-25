package seedu.finbro.model;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents an expense transaction in the FinBro application.
 */
public class Expense extends Transaction {
    private static final Logger logger = Logger.getLogger(Expense.class.getName());
    public enum Category {
        FOOD(1, "Food"),
        TRANSPORT(2, "Transport"),
        SHOPPING(3, "Shopping"),
        BILLS(4, "Bills"),
        ENTERTAINMENT(5, "Entertainment"),
        OTHERS(6, "Others");

        private final int index;
        private final String displayName;

        Category(int index, String displayName) {
            this.index = index;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        /**
         * Gets the numerical index of the category.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Gets a Category from a string, case-insensitive.
         *
         * @param str The string to convert to a Category
         * @return The corresponding Category, or OTHERS if no match is found
         */
        public static Category fromString(String str) {
            if (str == null) {
                logger.fine("Null category string provided, defaulting to OTHERS");
                return OTHERS;
            }

            for (Category category : Category.values()) {
                if (category.toString().equalsIgnoreCase(str)) {
                    logger.fine("Category string '" + str + "' matched to " + category);
                    return category;
                }
            }

            logger.fine("No matching category found for '" + str + "', defaulting to OTHERS");
            return OTHERS;
        }

        /**
         * Parses an index (e.g., 1) into a Category.
         */
        public static Category fromIndex(int index) {
            for (Category category : Category.values()) {
                if (category.index == index) {
                    return category;
                }
            }
            return OTHERS;
        }
    }

    private final Category category;

    /**
     * Constructs an Expense with the specified amount, description, category, and optional tags.
     *
     * @param amount The amount of money spent
     * @param description A description of the expense
     * @param category The category of the expense
     * @param tags Optional tags for categorizing the expense
     */
    public Expense(double amount, String description, Category category, List<String> tags) {
        super(amount, description, tags);
        assert amount > 0 : "Expense amount must be greater than zero";
        this.category = category != null ? category : Category.OTHERS;
        logger.fine("Created new expense in category " + this.category + " with amount $" + amount);
    }

    /**
     * Constructs an Expense with the specified amount, description, date, category, and optional tags.
     *
     * @param amount The amount of money spent
     * @param description A description of the expense
     * @param date The date of the expense
     * @param category The category of the expense
     * @param tags Optional tags for categorizing the expense
     */
    public Expense(double amount, String description, LocalDate date, Category category, List<String> tags) {
        super(amount, description, date, tags);
        assert amount > 0 : "Expense amount must be greater than zero";
        this.category = category != null ? category : Category.OTHERS;
        logger.fine("Created new expense in category "
                + this.category + " with amount $" + amount + " for date " + date);
    }

    /**
     * Returns the category of the expense.
     *
     * @return The category of the expense
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns a string representation of the expense.
     *
     * @return A string representation of the expense
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Expense][").append(category).append("] $")
                .append(String.format("%.2f", amount))
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
