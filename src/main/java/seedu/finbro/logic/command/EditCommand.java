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
 * Uses SearchCommand functionality to find the transaction to edit.
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
    }

    /**
     * Executes the command to edit a transaction found using search functionality.
     *
     * @param transactionManager The transaction manager to edit the transaction in
     * @param ui The UI to interact with the user
     * @param storage The storage to save the updated transaction list
     * @return The response message indicating the result of the edit operation
     */
    @Override
    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
        logger.info("Executing edit command with keyword: " + keyword);

        // Use SearchCommand to find matching transactions
        SearchCommand searchCommand = new SearchCommand(keyword);
        String searchResult = searchCommand.execute(transactionManager, ui, storage);

        if (searchResult.equals("No transactions found.")) {
            return "No matching transaction found for '" + keyword + "'.";
        }

        // Parse the search results to get the transactions
        List<Transaction> matchingTransactions = parseSearchResults(searchResult, transactionManager);

        if (matchingTransactions.isEmpty() || matchingTransactions.size() > 1) {
            return "Please provide a specific keyword that matches exactly one transaction.";
        }

        // Update the transaction
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
                logger.warning("Failed to find transaction to update");
            }
        }
        return "Failed to update transaction.";
    }

    /**
     * Parses search results to extract matching transactions.
     *
     * @param searchResult the result string from SearchCommand
     * @param manager the transaction manager to verify transactions
     * @return a list of matching transactions
     */
    private List<Transaction> parseSearchResults(String searchResult, TransactionManager manager) {
        List<Transaction> results = new ArrayList<>();

        if (searchResult.isEmpty() || searchResult.equals("No transactions found.")) {
            return results;
        }

        String[] lines = searchResult.split("\n");
        for (String line : lines) {
            for (Transaction transaction : manager.listTransactions()) {
                if (transaction.toString().equals(line)) {
                    results.add(transaction);
                    break;
                }
            }
        }

        return results;
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
                    tags.add(tag.trim());
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
