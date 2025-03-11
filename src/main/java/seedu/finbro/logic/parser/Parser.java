package seedu.finbro.logic.parser;

import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.command.IncomeCommand;
import seedu.finbro.logic.command.ExpenseCommand;
import seedu.finbro.logic.command.ClearCommand;
import seedu.finbro.logic.command.ExitCommand;
import seedu.finbro.logic.command.HelpCommand;
import seedu.finbro.logic.command.ExportCommand;
import seedu.finbro.logic.command.UnknownCommand;
import seedu.finbro.logic.command.InvalidCommand;
import seedu.finbro.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author alanwang
 *
 * Parses user input and creates the corresponding command.
 */
public class Parser {
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /**
     * Parses user input into a Command.
     *
     * @param userInput Full user input string
     * @return The command based on the user input
     */
    public Command parseCommand(String userInput) {
        userInput = userInput.trim();
        if (userInput.isEmpty()) {
            return new HelpCommand();
        }

        String[] parts = userInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        return switch (commandWord) {
            case "income" -> parseIncomeCommand(arguments);
            case "expense" -> parseExpenseCommand(arguments);
            case "export" -> parseExportCommand(arguments);
            case "clear" -> parseClearCommand(arguments);
            case "exit" -> new ExitCommand();
            case "help" -> new HelpCommand();
            default -> new UnknownCommand(commandWord);
        };
    }

    /**
     * Parses arguments into an IncomeCommand.
     *
     * @param args Command arguments
     * @return The IncomeCommand
     */
    private Command parseIncomeCommand(String args) {
        try {
            Map<String, String> parameters = parseParameters(args);

            if (!parameters.containsKey("")) {
                return new InvalidCommand("Amount is required for income command.");
            }

            if (!parameters.containsKey("d")) {
                return new InvalidCommand("Description is required for income command.");
            }

            double amount = parseAmount(parameters.get(""));
            String description = parameters.get("d");
            List<String> tags = parseTags(parameters);

            return new IncomeCommand(amount, description, tags);
        } catch (NumberFormatException e) {
            return new InvalidCommand("Invalid amount format. Please provide a valid number with up to 2 decimal places.");
        } catch (Exception e) {
            return new InvalidCommand("Invalid income command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into an ExpenseCommand.
     *
     * @param args Command arguments
     * @return The ExpenseCommand
     */
    private Command parseExpenseCommand(String args) {
        try {
            Map<String, String> parameters = parseParameters(args);

            if (!parameters.containsKey("")) {
                return new InvalidCommand("Amount is required for expense command.");
            }

            if (!parameters.containsKey("d")) {
                return new InvalidCommand("Description is required for expense command.");
            }

            double amount = parseAmount(parameters.get(""));
            String description = parameters.get("d");

            Expense.Category category = Expense.Category.OTHERS;
            if (parameters.containsKey("c")) {
                category = Expense.Category.fromString(parameters.get("c"));
            }

            List<String> tags = parseTags(parameters);

            return new ExpenseCommand(amount, description, category, tags);
        } catch (NumberFormatException e) {
            return new InvalidCommand("Invalid amount format. Please provide a valid number with up to 2 decimal places.");
        } catch (Exception e) {
            return new InvalidCommand("Invalid expense command: " + e.getMessage());
        }
    }

    // TODO Parses arguments into a ListCommand.

    // TODO: Parses arguments into a DeleteCommand.

    // TODO Parses arguments into a SearchCommand.

    // TODO Parses arguments into a FilterCommand.

    // TODO Parses arguments into a SummaryCommand.

    /**
     * Parses arguments into an ExportCommand.
     *
     * @param args Command arguments
     * @return The ExportCommand
     */
    private Command parseExportCommand(String args) {
        try {
            Map<String, String> parameters = parseParameters(args);

            String format = "csv"; // Default format
            if (parameters.containsKey("f")) {
                format = parameters.get("f").toLowerCase();
                if (!format.equals("csv") && !format.equals("txt")) {
                    return new InvalidCommand("Export format must be either 'csv' or 'txt'.");
                }
            }

            return new ExportCommand(format);
        } catch (Exception e) {
            return new InvalidCommand("Invalid export command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a ClearCommand.
     *
     * @param args Command arguments
     * @return The ClearCommand
     */
    private Command parseClearCommand(String args) {
        boolean isConfirmed = args.trim().equalsIgnoreCase("confirm");
        return new ClearCommand(isConfirmed);
    }

    /**
     * Parses a string into parameters.
     *
     * @param paramString The string containing parameters
     * @return A map of parameter prefixes to values
     */
    private Map<String, String> parseParameters(String paramString) {
        Map<String, String> parameters = new HashMap<>();
        String[] tokens = paramString.trim().split("\\s+");

        // Handle the first parameter (usually amount)
        if (tokens.length > 0 && !tokens[0].contains("/")) {
            parameters.put("", tokens[0]);
        }

        // Process the rest of the parameters
        String currentPrefix = null;
        StringBuilder currentValue = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // Skip the first token if it was already processed as the main parameter
            if (i == 0 && parameters.containsKey("")) {
                continue;
            }

            // Check if this token starts a new parameter
            if (token.matches("[a-zA-Z]+/.*")) {
                // Save the previous parameter if it exists
                if (currentPrefix != null) {
                    parameters.put(currentPrefix, currentValue.toString().trim());
                    currentValue = new StringBuilder();
                }

                // Extract the new prefix and start building its value
                int slashIndex = token.indexOf('/');
                currentPrefix = token.substring(0, slashIndex);
                currentValue.append(token.substring(slashIndex + 1));
            } else if (currentPrefix != null) {
                // Continue building the current parameter value
                currentValue.append(" ").append(token);
            }
        }

        // Save the last parameter if it exists
        if (currentPrefix != null) {
            parameters.put(currentPrefix, currentValue.toString().trim());
        }

        return parameters;
    }

    /**
     * Parses a string into a valid amount.
     *
     * @param amountStr The string to parse
     * @return The amount as a double
     * @throws NumberFormatException if the string is not a valid amount
     */
    private double parseAmount(String amountStr) throws NumberFormatException {
        if (!AMOUNT_PATTERN.matcher(amountStr).matches()) {
            throw new NumberFormatException("Invalid amount format.");
        }
        return Double.parseDouble(amountStr);
    }

    /**
     * Extracts tags from parameters.
     *
     * @param parameters The parameters map
     * @return A list of tags
     */
    private List<String> parseTags(Map<String, String> parameters) {
        List<String> tags = new ArrayList<>();

        // Look for parameters with prefix 't'
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (entry.getKey().equals("t")) {
                tags.add(entry.getValue());
            }
        }

        return tags;
    }
}
