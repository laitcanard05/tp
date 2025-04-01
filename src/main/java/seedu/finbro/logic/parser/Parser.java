package seedu.finbro.logic.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.finbro.logic.command.BalanceCommand;
import seedu.finbro.logic.command.ClearCommand;
import seedu.finbro.logic.command.Command;
import seedu.finbro.logic.command.DeleteCommand;
import seedu.finbro.logic.command.EditCommand;
import seedu.finbro.logic.command.ExportCommand;
import seedu.finbro.logic.command.ExpenseCommand;
import seedu.finbro.logic.command.FilterCommand;
import seedu.finbro.logic.command.HelpCommand;
import seedu.finbro.logic.command.InvalidCommand;
import seedu.finbro.logic.command.ListCommand;
import seedu.finbro.logic.command.SummaryCommand;
import seedu.finbro.logic.command.UnknownCommand;
import seedu.finbro.logic.command.ExitCommand;
import seedu.finbro.logic.command.IncomeCommand;
import seedu.finbro.logic.command.SetBudgetCommand;
import seedu.finbro.logic.command.TrackBudgetCommand;
import seedu.finbro.logic.command.SearchCommand;
import seedu.finbro.logic.command.SetSavingsGoalCommand;
import seedu.finbro.logic.command.TrackSavingsGoalCommand;
import seedu.finbro.logic.exceptions.EmptyInputException;
import seedu.finbro.logic.exceptions.IndexExceedLimitException;
import seedu.finbro.model.Expense;
import seedu.finbro.model.TransactionManager;
import seedu.finbro.storage.Storage;
import seedu.finbro.ui.Ui;

/**
 * Parses user input and creates the corresponding command.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Track if a clear confirmation is pending
    private boolean clearCommandPending = false;

    /**
     * Parses user input into a Command.
     *
     * @param commandWord The command word to parse
     * @param ui The UI to interact with the user
     */
    public Command parseCommandWord(String commandWord, Ui ui) {
        logger.fine("Command word: " + commandWord);

        commandWord = commandWord.trim();
        if (commandWord.isEmpty()) {
            logger.fine("Empty input, returning HelpCommand");
            clearCommandPending = false;
            return new HelpCommand();
        }

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

        Command parsedCommand;
        switch (commandWord) {
        case "search":
            parsedCommand = parseSearchCommand(ui);
            break;
        case "income":
            parsedCommand = parseIncomeCommand(ui);
            break;
        case "expense":
            parsedCommand = parseExpenseCommand(ui);
            break;
        case "list":
            parsedCommand = parseListCommand(ui);
            break;
        case "delete":
            parsedCommand = parseDeleteCommand(ui);
            break;
        case "filter":
            parsedCommand = parseFilterCommand(ui);
            break;
        case "balance":
        case "view": // Support for "view" as an alias for "balance"
            parsedCommand = new BalanceCommand();
            break;
        case "summary":
            parsedCommand = parseSummaryCommand(ui);
            break;
        case "export":
            parsedCommand = parseExportCommand(ui);
            break;
        case "clear":
            parsedCommand = parseClearCommand(ui);
            break;
        case "exit":
            parsedCommand = new ExitCommand();
            break;
        case "help":
            parsedCommand = new HelpCommand();
            break;
        case "setbudget":
            parsedCommand = parseSetBudgetCommand(ui);
            break;
        case "trackbudget":
            parsedCommand = parseTrackBudgetCommand(ui);
            break;
        case "setsavings":
            parsedCommand = parseSetSavingsGoalCommand(ui);
            break;
        case "tracksavings":
            parsedCommand = parseTrackSavingsGoalCommand(ui);
            break;
        case "edit":
            parsedCommand = parseEditCommand(ui);
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

    private Command parseEditCommand(Ui ui) {
        logger.fine("Parsing edit command");
        try {
            // Keyword is required - cannot be skipped
            String keyword = ui.readString("Enter the keyword to find the transaction to edit:\n> ");
            if (keyword.trim().isEmpty()) {
                return new InvalidCommand("Please provide a keyword to find the transaction to edit.");
            }

            Map<String, String> parameters = new HashMap<>();

            // Amount (allow skipping)
            String amountStr = ui.readAmount("Enter new amount (press Enter to skip):\n> ");
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        return new InvalidCommand("Amount must be positive.");
                    }
                    parameters.put("a", amountStr);
                } catch (NumberFormatException e) {
                    return new InvalidCommand("Invalid amount format.");
                }
            }

            // Description (allow skipping)
            String description = ui.readDescription("Enter new description (press Enter to skip):\n> ");
            if (!description.isEmpty()) {
                parameters.put("d", description);
            }

            // Date (allow skipping)
            String dateStr = ui.readDate("Enter new date (YYYY-MM-DD) (press Enter to skip):\n> ");
            if (!dateStr.isEmpty()) {
                try {
                    LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    parameters.put("date", dateStr);
                } catch (DateTimeParseException e) {
                    return new InvalidCommand("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            // Category (allow skipping)
            String categoryInput = ui.readCategory("Enter new category " +
                    "(press Enter to skip, 'y' to select from menu):\n> ");
            if (!categoryInput.isEmpty()) {
                if (categoryInput.toLowerCase().startsWith("y")) {
                    Expense.Category category = parseCategory(ui);
                    parameters.put("c", category.toString());
                } else {
                    try {
                        Expense.Category category = Expense.Category.fromString(categoryInput);
                        parameters.put("c", category.toString());
                    } catch (IllegalArgumentException e) {
                        return new InvalidCommand("Invalid category.");
                    }
                }
            }

            // Tags (allow skipping)
            String tagsInput = ui.readTags("Enter new tags (comma separated, press Enter to skip, 'y' to select):\n> ");
            if (!tagsInput.isEmpty()) {
                if (tagsInput.toLowerCase().startsWith("y")) {
                    List<String> tags = parseTags(ui);
                    if (!tags.isEmpty()) {
                        parameters.put("t", String.join(",", tags));
                    }
                } else {
                    List<String> tags = Arrays.stream(tagsInput.split(","))
                            .map(String::trim)
                            .filter(tag -> !tag.isEmpty())
                            .collect(Collectors.toList());
                    if (!tags.isEmpty()) {
                        parameters.put("t", String.join(",", tags));
                    }
                }
            }

            if (parameters.isEmpty()) {
                return new InvalidCommand("Please specify at least one parameter to edit.");
            }

            return new EditCommand(keyword, parameters);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing edit command", e);
            return new InvalidCommand("Invalid edit command: " + e.getMessage());
        }
    }

    /**
     * Parses dates read from the UI into ListCommand.
     *
     * @param ui The UI to interact with the user
     * @return The ListCommand
     */
    private Command parseListCommand(Ui ui) {
        logger.fine("Parsing list command");
        try {
            String startDateInput = ui.readStartDate(); //only uses start date
            logger.fine("List start date: " + startDateInput);

            LocalDate date = null;
            if (startDateInput != null && !startDateInput.isEmpty()) {
                date = LocalDate.parse(startDateInput.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            Integer limit = ui.readLimit();
            if (limit != null && limit <= 0) {
                logger.warning("Invalid limit: " + limit);
                return new InvalidCommand("Number of transactions must be a positive integer.");
            }

            logger.fine("Creating ListCommand with limit=" + limit + ", date=" + date);
            return new ListCommand(limit, date);

        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Date format error in list command", e);
            return new InvalidCommand("Date must be specified in the format YYYY-MM-DD.");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format for list limit", e);
            return new InvalidCommand("Limit must be a valid number.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing list command", e);
            return new InvalidCommand("Invalid list command: " + e.getMessage());
        }
    }

    /**
     * Parses index read from the UI into a DeleteCommand.
     *
     * @param ui The UI to interact with the user
     * @return The DeleteCommand
     */
    private Command parseDeleteCommand(Ui ui) {
        logger.fine("Parsing delete command");
        try {
            int[] index = ui.readIndexRange("Enter a number or range to delete. (e.g., '1' or '2-5')\n> ");
            int start = index[0];
            int end = index[1];
            if (start == -1 && end == -1) {
                throw new EmptyInputException();
            }
            logger.fine("Creating DeleteCommand with start=" + start + ", end=" + end);
            return new DeleteCommand(start, end);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing delete command", e);
            return new InvalidCommand("Invalid delete command: " + e.getMessage());
        }
    }

    /**
     * Parses dates read from the UI into a FilterCommand.
     *
     * @param ui The UI to interact with the user
     * @return The FilterCommand
     */
    //method for parsing filter command for friendly CLI
    private Command parseFilterCommand(Ui ui) {
        logger.fine("Parsing filter command");
        try {
            String[] filterDates = ui.readDates();
            logger.fine("Filter dates: " + filterDates[0] + " to " + filterDates[1]);

            if (filterDates[0] == null || filterDates[0].isEmpty()) {
                logger.warning("Missing start date parameter for filter command");
                return new InvalidCommand("Start date must be specified for filter command.");
            }

            LocalDate startDate = LocalDate.parse(
                filterDates[0].trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            );

            LocalDate endDate;
            if (filterDates[1] == null || filterDates[1].isEmpty()) {
                logger.fine("No end date specified, using current date");
                endDate = LocalDate.now();
            } else {
                endDate = LocalDate.parse(
                    filterDates[1].trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                );
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
     * Parses month and year read from ui into a SummaryCommand.
     *
     * @param ui The UI to interact with the user
     * @return The SummaryCommand
     */
    //method for parsing summary command for friendly CLI
    private Command parseSummaryCommand(Ui ui) {
        logger.fine("Parsing summary command");
        try {
            Integer[] monthYear = ui.readMonthYear();
            Integer month = monthYear[0];
            Integer year = monthYear[1];

            if (month == null) {
                logger.info("No month input, using current month");
                month = LocalDate.now().getMonthValue();
            } else if (month < 1 || month > 12) {
                logger.warning("Invalid month input: " + month);
                return new InvalidCommand("Month input must be between 1 and 12.");
            }
            if (year == null) {
                logger.info("No year input, using current year");
                year = LocalDate.now().getYear();
            } else if (year < 1000 || year > 9999 || year > LocalDate.now().getYear()) {
                logger.warning("Invalid year input: " + year);
                return new InvalidCommand("Year input must be a 4-digit number and cannot be after the current year.");
            }
            return new SummaryCommand(month, year);
        } catch (Exception e) {
            return new InvalidCommand("Invalid summary command: " + e.getMessage());
        }
    }

    /**
     * Parses user input into an ExportCommand using interactive UI.
     *
     * @param ui The UI to interact with the user
     * @return The ExportCommand
     */
    private Command parseExportCommand(Ui ui) {
        logger.fine("Parsing export command interactively");
        try {
            String format = ui.readString("Enter export format (csv/txt) or press Enter for default (csv):\n> ");
            format = format.trim().isEmpty() ? "csv" : format.toLowerCase();

            if (!format.equals("csv") && !format.equals("txt")) {
                logger.warning("Invalid export format: " + format);
                return new InvalidCommand("Export format must be either 'csv' or 'txt'.");
            }

            logger.fine("Creating ExportCommand with format=" + format);
            return new ExportCommand(format);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing export command", e);
            return new InvalidCommand("Invalid export command: " + e.getMessage());
        }
    }

    /**
     * Parses user input into a ClearCommand using interactive UI.
     *
     * @param ui The UI to interact with the user
     * @return The ClearCommand
     */
    private Command parseClearCommand(Ui ui) {
        logger.fine("Parsing clear command interactively");

        // Set the flag to indicate a confirmation is pending
        clearCommandPending = true;

        ui.showMessage("Warning: This will delete all your data. Are you sure? (y/n)");
        logger.fine("Waiting for clear command confirmation");

        // Return an unconfirmed ClearCommand - the confirmation will be handled in parseCommandWord
        return new ClearCommand(false);
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
     * Prompts the user to input up to 3 tags and returns them as a list.
     * Tags are split by commas or spaces and trimmed. Empty input returns an empty list.
     *
     * @param ui The UI to interact with the user.
     * @return A list of up to 3 trimmed tags.
     */
    private List<String> parseTags(Ui ui) {
        List<String> tags = new ArrayList<>();
        String input = ui.readTags("Enter up to 3 tags (separated by space or comma), or press Enter to skip:\n> ");

        if (input.isEmpty()) {
            return tags;
        }

        String[] rawTags = input.split("[,\\s]+");

        for (String tag : rawTags) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                tags.add(trimmed);
            }
            if (tags.size() == 3) {
                break;
            }
        }
        return tags;
    }

    /**
     * ONE COMMAND ONE ACTION OVERRIDE
     * Parses arguments into an ExpenseCommand.
     *
     * @return The ExpenseCommand
     */
    private Command parseExpenseCommand(Ui ui) {
        try {
            logger.fine("Parsing expense command with ONE COMMAND ONE ACTION ");
            double amount = ui.readDouble("Enter amount:\n> ");

            String description = ui.readString("Enter description:\n> ");

            Expense.Category category = parseCategory(ui);

            List<String> tags = parseTags(ui);

            return new ExpenseCommand(amount, description, category, tags);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unknown error parsing expense command", e);
            return new InvalidCommand("Something went wrong, please try again.");
        }
    }

    /**
     * ONE COMMAND ONE ACTION OVERRIDE
     * Parses arguments into an IncomeCommand.
     *
     * @return The IncomeCommand
     */
    private Command parseIncomeCommand(Ui ui) {
        logger.fine("Parsing income command with friendly CLI ");
        try {
            double amount = ui.readDouble("Enter amount:\n");

            String description = ui.readString("Enter description:\n");

            List<String> tags = parseTags(ui);

            return new IncomeCommand(amount, description, tags);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Unknown error parsing income command", e);
            return new InvalidCommand("Something went wrong, please try again.");
        }
    }

    /**
     * ONE COMMAND ONE ACTION OVERRIDE
     *  Parses arguments into a SearchCommand.
     *
     * @return The SearchCommand
     */
    private Command parseSearchCommand(Ui ui) {
        logger.fine("Parsing search command with friendly CLI: ");
        try {
            String keyword = ui.readString("Enter keyword or string to search:\n>");

            logger.fine("Searching transactions with keyword=" + keyword);

            return new SearchCommand(keyword);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing search command", e);
            return new InvalidCommand("Invalid search command: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to select a category by index (0–5) and returns the corresponding category.
     *
     * @param ui The UI to read user input.
     * @return The selected category.
     * @throws IndexExceedLimitException If the index exceeds the valid range (0–5).
     */
    private Expense.Category parseCategory(Ui ui) {
        try{
            String message = "Please select a category by entering its corresponding index\n" +
                    "0 - OTHERS\n" +
                    "1 - FOOD\n" +
                    "2 - TRANSPORT\n" +
                    "3 - SHOPPING\n" +
                    "4 - BILLS\n" +
                    "5 - ENTERTAINMENT\n" +
                    "> ";

            int catIndex = ui.readIndex(message);

            if (catIndex > 5) {
                throw new IndexExceedLimitException();
            }
            assert catIndex >= 0;
            switch (catIndex) {
            case 0: return Expense.Category.OTHERS;
            case 1: return Expense.Category.FOOD;
            case 2: return Expense.Category.TRANSPORT;
            case 3: return Expense.Category.SHOPPING;
            case 4: return Expense.Category.BILLS;
            case 5: return Expense.Category.ENTERTAINMENT;
            default: {
                assert catIndex >5 || catIndex < 0;
                System.out.println("INVALID INDEX. PICK AN INDEX FROM 0-5");
                return parseCategory(ui);
            }
            }
        } catch (IndexExceedLimitException e) {
            logger.log(Level.WARNING, "Category input exceeds 5. Invalid selection.", e);
            IndexExceedLimitException.handle();
        }
        return parseCategory(ui);
    }

    /**
     * Parses arguments into a SetBudgetCommand.
     *
     * @param ui The inputs read from the UI.
     * @return The setBudgetCommand
     */
    private Command parseSetBudgetCommand(Ui ui) {
        logger.fine("Parsing set budget command");
        try {
            Integer[] monthYear = ui.readMonthYear();
            Integer month = monthYear[0];
            Integer year = monthYear[1];

            if (month == null) {
                logger.info("No month input, using current month");
                month = LocalDate.now().getMonthValue();
            } else if (month < 1 || month > 12) {
                logger.warning("Invalid month input: " + month);
                return new InvalidCommand("Month input must be between 1 and 12.");
            }
            if (year == null) {
                logger.info("No year input, using current year");
                year = LocalDate.now().getYear();
            } else if (year < 1000 || year > 9999 || year > LocalDate.now().getYear()) {
                logger.warning("Invalid year input: " + year);
                return new InvalidCommand("Year input must be a 4-digit number and cannot be after the current year.");
            }
            double budget = ui.readDouble("Enter your budget:\n> ");
            if (budget <= 0) {
                return new InvalidCommand("Budget must be a positive number.");
            }
            return new SetBudgetCommand(budget, month, year);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format for budget", e);
            return new InvalidCommand("Invalid budget format. Please provide a valid number.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing set budget command", e);
            return new InvalidCommand("Invalid set budget command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a TrackBudgetCommand.
     *
     * @param ui The inputs read from the UI.
     * @return The trackBudgetCommand
     */
    private Command parseTrackBudgetCommand(Ui ui) {
        logger.fine("Parsing track budget command");
        try {
            Integer[] monthYear = ui.readMonthYear();
            Integer month = monthYear[0];
            Integer year = monthYear[1];

            if (month == null) {
                logger.info("No month input, using current month");
                month = LocalDate.now().getMonthValue();
            } else if (month < 1 || month > 12) {
                logger.warning("Invalid month input: " + month);
                return new InvalidCommand("Month input must be between 1 and 12.");
            }
            if (year == null) {
                logger.info("No year input, using current year");
                year = LocalDate.now().getYear();
            } else if (year < 1000 || year > 9999 || year > LocalDate.now().getYear()) {
                logger.warning("Invalid year input: " + year);
                return new InvalidCommand("Year input must be a 4-digit number and cannot be after the current year.");
            }
            return new TrackBudgetCommand(month, year);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing track budget command", e);
            return new InvalidCommand("Invalid track budget command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a SetSavingsCommand.
     *
     * @param ui The inputs read from the UI.
     * @return The setSavingsCommand
     */
    private Command parseSetSavingsGoalCommand(Ui ui) {
        logger.fine("Parsing set savings command");
        try {
            Integer[] monthYear = ui.readMonthYear();
            Integer month = monthYear[0];
            Integer year = monthYear[1];

            if (month == null) {
                logger.info("No month input, using current month");
                month = LocalDate.now().getMonthValue();
            } else if (month < 1 || month > 12) {
                logger.warning("Invalid month input: " + month);
                return new InvalidCommand("Month input must be between 1 and 12.");
            }
            if (year == null) {
                logger.info("No year input, using current year");
                year = LocalDate.now().getYear();
            } else if (year < 1000 || year > 9999 || year > LocalDate.now().getYear()) {
                logger.warning("Invalid year input: " + year);
                return new InvalidCommand("Year input must be a 4-digit number and cannot be after the current year.");
            }

            double savingsGoal = ui.readDouble("Enter your savings goal:\n> ");
            if (savingsGoal <= 0) {
                return new InvalidCommand("Savings goal must be a positive value.");
            }
            return new SetSavingsGoalCommand(savingsGoal, month, year);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format for savings goal", e);
            return new InvalidCommand("Invalid savings goal format. Please provide a valid number.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing set savings goal command", e);
            return new InvalidCommand("Invalid set savings goal command: " + e.getMessage());
        }
    }

    /**
     * Parses arguments into a TrackSavingsGoalCommand.
     *
     * @param ui The inputs read from the UI.
     * @return The trackSavingsGoalCommand
     */
    private Command parseTrackSavingsGoalCommand(Ui ui) {
        logger.fine("Parsing track savings goal command");
        try {
            Integer[] monthYear = ui.readMonthYear();
            Integer month = monthYear[0];
            Integer year = monthYear[1];

            if (month == null) {
                logger.info("No month input, using current month");
                month = LocalDate.now().getMonthValue();
            } else if (month < 1 || month > 12) {
                logger.warning("Invalid month input: " + month);
                return new InvalidCommand("Month input must be between 1 and 12.");
            }
            if (year == null) {
                logger.info("No year input, using current year");
                year = LocalDate.now().getYear();
            } else if (year < 1000 || year > 9999 || year > LocalDate.now().getYear()) {
                logger.warning("Invalid year input: " + year);
                return new InvalidCommand("Year input must be a 4-digit number and cannot be after the current year.");
            }
            return new TrackSavingsGoalCommand(month, year);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing track savings goal command", e);
            return new InvalidCommand("Invalid track savings goal command: " + e.getMessage());
        }
    }
}

