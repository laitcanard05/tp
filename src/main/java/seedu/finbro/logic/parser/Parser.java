package seedu.finbro.logic.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import seedu.finbro.model.Expense;
import seedu.finbro.logic.command.BalanceCommand;
import seedu.finbro.logic.command.ClearCommand;
import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.command.ExitCommand;
import seedu.finbro.logic.command.ExpenseCommand;
import seedu.finbro.logic.command.ExportCommand;
import seedu.finbro.logic.command.FilterCommand;
import seedu.finbro.logic.command.HelpCommand;
import seedu.finbro.logic.command.IncomeCommand;
import seedu.finbro.logic.command.InvalidCommand;
import seedu.finbro.logic.command.ListCommand;
import seedu.finbro.logic.command.SearchCommand;
import seedu.finbro.logic.command.UnknownCommand;
import seedu.finbro.logic.command.SummaryCommand;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Parses user input and creates the corresponding command.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    // Track if a clear confirmation is pending
    private boolean clearCommandPending = false;

    /**
     * Parses user input into a Command.
     *
     * @param userInput Full user input string
     * @return The command based on the user input
     */
    public Command parseCommand(String userInput) {
        assert userInput != null : "User input cannot be null";

        logger.fine("Parsing user input: " + userInput);
        userInput = userInput.trim();
        if (userInput.isEmpty()) {
            logger.fine("Empty input, returning HelpCommand");
            clearCommandPending = false;
            return new HelpCommand();
        }

        String[] parts = userInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        // Handle y/n responses if a clear confirmation is pending
        if (clearCommandPending) {
            if (commandWord.equals("y") || commandWord.equals("yes")) {
                clearCommandPending = false;
                logger.fine("Clear command confirmed with 'y'");
                return new ClearCommand(true);
            } else if (commandWord.equals("n") || commandWord.equals("no")) {
                clearCommandPending = false;
                logger.fine("Clear command cancelled with 'n'");
                return new Command() {
                    @Override
                    public String execute(TransactionManager transactionManager, Ui ui, Storage storage) {
                        return "Clear operation cancelled.";
                    }

                    @Override
                    public boolean isExit() {
                        return false;
                    }
                };
            }
            // If not y/n, reset the pending flag
            clearCommandPending = false;
        }

        assert commandWord != null && !commandWord.isEmpty() : "Command word cannot be null or empty";
        logger.fine("Command word: " + commandWord + ", Arguments: " + arguments);

        Command parsedCommand;
        switch (commandWord) {

        case "search":
            parsedCommand = parseSearchCommand(arguments);
            break;
        case "income":
            parsedCommand = parseIncomeCommand(arguments);
            break;
        case "expense":
            parsedCommand = parseExpenseCommand(arguments);
            break;
        case "filter":
            parsedCommand = parseFilterCommand(arguments);
            break;
        case "summary":
            parsedCommand = parseSummaryCommand(arguments);
            break;
        case "export":
            parsedCommand = parseExportCommand(arguments);
            break;
        case "clear":
            parsedCommand = parseClearCommand(arguments);
            // Set the flag if this is the initial clear command (without confirm)
            if (arguments.trim().isEmpty()) {
                clearCommandPending = true;
            }
            break;
        case "exit":
            parsedCommand = new ExitCommand();
            break;
        case "help":
            parsedCommand = new HelpCommand();
            break;
        case "list":
            parsedCommand = new ListCommand();
            break;
        case "view":
            parsedCommand = new BalanceCommand();
            break;
        default:
            logger.warning("Unknown command: " + commandWord);
            parsedCommand = new UnknownCommand(commandWord);
            break;
        }

        assert parsedCommand != null : "Parsed command cannot be null";
        logger.fine("Parsed command: " + parsedCommand.getClass().getSimpleName());
        return parsedCommand;
    }

    /**
     * Parses arguments into a SearchCommand.
     *
     * @param args Command arguments
     * @return The SearchCommand
     */
    private Command parseSearchCommand(String args) {
        logger.fine("Parsing search command with arguments: " + args);
        try {
            Map<String, String> parameters = parseParameters(args);
            logger.fine("Parsed parameters: " + parameters);

            if (!parameters.containsKey("")) {
                logger.warning("Missing keyword for search command");
                return new InvalidCommand("keyword is required for search command.");
            }

            logger.fine("Searching transactions with keyword=" + args);
            return new SearchCommand(args);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing income command", e);
            return new InvalidCommand("Invalid income command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into an IncomeCommand.
     *
     * @param args Command arguments
     * @return The IncomeCommand
     */
    private Command parseIncomeCommand(String args) {
        logger.fine("Parsing income command with arguments: " + args);
        try {
            Map<String, String> parameters = parseParameters(args);
            logger.fine("Parsed parameters: " + parameters);

            if (!parameters.containsKey("")) {
                logger.warning("Missing amount parameter for income command");
                return new InvalidCommand("Amount is required for income command.");
            }

            if (!parameters.containsKey("d")) {
                logger.warning("Missing description parameter for income command");
                return new InvalidCommand("Description is required for income command.");
            }

            double amount = parseAmount(parameters.get(""));
            String description = parameters.get("d");
            List<String> tags = parseTags(parameters);

            logger.fine("Creating IncomeCommand with amount=" + amount +
                    ", description=" + description + ", tags=" + tags);
            return new IncomeCommand(amount, description, tags);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid amount format in income command", e);
            return new InvalidCommand(
                    "Invalid amount format. Please provide a valid number with up to 2 decimal places."
            );
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing income command", e);
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
        logger.fine("Parsing expense command with arguments: " + args);
        try {
            Map<String, String> parameters = parseParameters(args);
            logger.fine("Parsed parameters: " + parameters);

            if (!parameters.containsKey("")) {
                logger.warning("Missing amount parameter for expense command");
                return new InvalidCommand("Amount is required for expense command.");
            }

            if (!parameters.containsKey("d")) {
                logger.warning("Missing description parameter for expense command");
                return new InvalidCommand("Description is required for expense command.");
            }

            double amount = parseAmount(parameters.get(""));
            String description = parameters.get("d");

            Expense.Category category = Expense.Category.OTHERS;
            if (parameters.containsKey("c")) {
                category = Expense.Category.fromString(parameters.get("c"));
            }

            List<String> tags = parseTags(parameters);

            logger.fine("Creating ExpenseCommand with amount=" + amount +
                    ", description=" + description + ", category=" + category +
                    ", tags=" + tags);
            return new ExpenseCommand(amount, description, category, tags);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid amount format in expense command", e);
            return new InvalidCommand(
                    "Invalid amount format. Please provide a valid number with up to 2 decimal places."
            );
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing expense command", e);
            return new InvalidCommand("Invalid expense command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a FilterCommand.
     *
     * @param args Command arguments
     * @return The FilterCommand
     */
    private Command parseFilterCommand(String args) {
        logger.fine("Parsing filter command with arguments: " + args);
        try {
            Map<String, String> parameters = parseParameters(args);
            logger.fine("Parsed parameters: " + parameters);

            if (!parameters.containsKey("d")) {
                logger.warning("Missing date parameter for filter command");
                return new InvalidCommand("Start date must be specified for filter command.");
            }

            LocalDate startDate = LocalDate.parse(parameters.get("d"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate;

            if (!parameters.containsKey("to")) {
                logger.fine("No end date specified, using current date");
                endDate = LocalDate.now();
            } else {
                endDate = LocalDate.parse(parameters.get("to"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            if (startDate.isAfter(endDate)) {
                logger.warning("Start date is after end date: " + startDate + " > " + endDate);
                return new InvalidCommand("Start date cannot be after end date.");
            }

            logger.fine("Creating FilterCommand with startDate=" + startDate +
                    ", endDate=" + endDate);
            return new FilterCommand(startDate, endDate);
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Date format error in filter command", e);
            return new InvalidCommand("Date must be specified in the format YYYY-MM-DD.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing filter command", e);
            return new InvalidCommand("Invalid filter command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a SummaryCommand with specified month and year
     * @param args Command arguments
     * @return The SummaryCommand
     */
    private Command parseSummaryCommand(String args) {
        Map<String, String> parameters = parseParameters(args);

        try {
            int month;
            int year;
            if (!parameters.containsKey("m")) {
                month = LocalDate.now().getMonthValue();
            } else {
                month = Integer.parseInt(parameters.get("m"));
                if (month < 1 || month > 12) {
                    return new InvalidCommand("Month input must be between 1 and 12.");
                }
            }
            if (!parameters.containsKey("y")) {
                year = LocalDate.now().getYear();
            } else {
                year = Integer.parseInt(parameters.get("y"));
                if (year % 1000 == 0 || year > LocalDate.now().getYear()) {
                    return new InvalidCommand("Year input must be 4-digit.");
                }
            }
            return new SummaryCommand(month, year);
        } catch (Exception e) {
            return new InvalidCommand("Invalid summary command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into an ExportCommand.
     *
     * @param args Command arguments
     * @return The ExportCommand
     */
    private Command parseExportCommand(String args) {
        logger.fine("Parsing export command with arguments: " + args);
        try {
            Map<String, String> parameters = parseParameters(args);
            logger.fine("Parsed parameters: " + parameters);

            String format = "csv"; // Default format
            if (parameters.containsKey("f")) {
                format = parameters.get("f").toLowerCase();
                if (!format.equals("csv") && !format.equals("txt")) {
                    logger.warning("Invalid export format: " + format);
                    return new InvalidCommand("Export format must be either 'csv' or 'txt'.");
                }
            }

            logger.fine("Creating ExportCommand with format=" + format);
            return new ExportCommand(format);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing export command", e);
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
        logger.fine("Parsing clear command with arguments: " + args);
        boolean isConfirmed = args.trim().equalsIgnoreCase("confirm");
        logger.fine("Clear confirmation status: " + isConfirmed);
        return new ClearCommand(isConfirmed);
    }

    /**
     * Parses a string into parameters.
     *
     * @param paramString The string containing parameters
     * @return A map of parameter prefixes to values
     */
    private Map<String, String> parseParameters(String paramString) {
        assert paramString != null : "Parameter string cannot be null";

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
            assert token != null : "Token cannot be null";

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
                assert slashIndex >= 0 : "Expected slash in parameter token";
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

        assert parameters != null : "Parsed parameters map cannot be null";
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
        assert amountStr != null : "Amount string cannot be null";
        assert !amountStr.trim().isEmpty() : "Amount string cannot be empty";

        if (!AMOUNT_PATTERN.matcher(amountStr).matches()) {
            throw new NumberFormatException("Invalid amount format.");
        }

        double amount = Double.parseDouble(amountStr);
        assert amount > 0 : "Amount must be greater than zero";
        return amount;
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
