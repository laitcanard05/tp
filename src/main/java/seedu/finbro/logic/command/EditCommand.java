package seedu.finbro.logic.command;

import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a command to edit a transaction.
 */
public class EditCommand implements Command {
    private static final Logger logger = Logger.getLogger(EditCommand.class.getName());

    private final String keyword;
    private final Map<String, String> parameters;

    /**
     * Constructs an EditCommand with the given keyword and parameters.
     *
     * @param keyword the keyword to search for the transaction to edit
     * @param parameters the parameters to update in the transaction
     */
    public EditCommand(String keyword, Map<String, String> parameters) {
        this.keyword = keyword;
        this.parameters = parameters;
        logger.fine("Constructed EditCommand with keyword=" + keyword + ", parameters=" + parameters);
    }

    /**
     * Executes the command to edit a transaction.
     *
     * @param transactionManager The transaction manager to modify
     * @param ui The UI to interact with the user
     * @param storage The storage to save data
     * @return The response message from executing the command
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        logger.info("Executing EditCommand with keyword: " + keyword);

        List<Transaction> transactions = transactionManager.listTransactions();
        List<Transaction> matchingTransactions = new ArrayList<>();

        // Find matching transactions
        for (Transaction transaction : transactions) {
            if (transaction.toString().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTransactions.add(transaction);
            }
        }

        if (matchingTransactions.isEmpty()) {
            logger.info("No transactions found matching keyword: " + keyword);
            return "No matching transaction found for '" + keyword + "'.";
        }

        if (matchingTransactions.size() > 1) {
            logger.info("Multiple transactions found matching keyword: " + keyword);
            return "Please provide a more specific keyword. Multiple transactions matched.";
        }

        // Update the transaction
        Transaction originalTransaction = matchingTransactions.get(0);
        Transaction updatedTransaction = createUpdatedTransaction(originalTransaction);

        if (updatedTransaction != null) {
            boolean success = transactionManager.updateTransaction(originalTransaction, updatedTransaction);
            if (success) {
                logger.info("Transaction updated successfully");
                storage.saveTransactions(transactionManager);
                return "Transaction updated successfully:\n" + updatedTransaction;
            } else {
                logger.warning("Failed to update transaction");
            }
        }

        return "Failed to update transaction.";
    }

    /**
     * Creates an updated transaction based on the original transaction and the parameters to update.
     *
     * @param original the original transaction to update
     * @return the updated transaction, or null if the update failed
     */
    private Transaction createUpdatedTransaction(Transaction original) {
        try {
            // Default values from original transaction
            double amount = original.getAmount();
            String description = original.getDescription();
            LocalDate date = original.getDate();
            List<String> tags = new ArrayList<>(original.getTags());

            // Update values based on parameters
            if (parameters.containsKey("a")) {
                amount = Double.parseDouble(parameters.get("a"));
            }

            if (parameters.containsKey("d")) {
                description = parameters.get("d");
            }

            if (parameters.containsKey("date")) {
                date = LocalDate.parse(parameters.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            if (parameters.containsKey("t")) {
                // Replace all tags if new ones are provided
                tags.clear();
                String[] tagArray = parameters.get("t").split(",");
                for (String tag : tagArray) {
                    String trimmed = tag.trim();
                    if (!trimmed.isEmpty()) {
                        tags.add(trimmed);
                    }
                }
            }

            // Create new transaction based on the type of the original
            if (original instanceof Income) {
                return new Income(amount, description, date, tags);
            } else if (original instanceof Expense) {
                Expense originalExpense = (Expense) original;
                Expense.Category category = originalExpense.getCategory();

                if (parameters.containsKey("c")) {
                    category = Expense.Category.fromString(parameters.get("c"));
                }

                return new Expense(amount, description, date, category, tags);
            }

            return null;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid amount format in edit command", e);
            return null;
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Invalid date format in edit command", e);
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating updated transaction", e);
            return null;
        }
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
