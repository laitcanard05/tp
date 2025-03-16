package seedu.finbro.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;

/**
 * Handles saving and loading of data.
 */
public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = "finbro.txt";
    private static final String DEFAULT_EXPORT_DIRECTORY = "exports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String dataFilePath;
    private final String exportDirectoryPath;

    /**
     * Constructs a Storage with default file paths.
     */
    public Storage() {
        logger.fine("Initializing Storage with default paths");
        this.dataFilePath = DATA_DIRECTORY + File.separator + DATA_FILE;
        this.exportDirectoryPath = DEFAULT_EXPORT_DIRECTORY;
        createDirectories();
    }

    /**
     * Constructs a Storage with custom file paths.
     *
     * @param dataFilePath The path to the data file
     * @param exportDirectoryPath The path to the export directory
     */
    public Storage(String dataFilePath, String exportDirectoryPath) {
        logger.fine("Initializing Storage with custom paths: dataFile=" + dataFilePath +
                ", exportDir=" + exportDirectoryPath);
        this.dataFilePath = dataFilePath;
        this.exportDirectoryPath = exportDirectoryPath;
        createDirectories();
    }

    /**
     * Creates necessary directories.
     */
    private void createDirectories() {
        try {
            Path dataDirectory = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataDirectory)) {
                logger.info("Creating data directory: " + dataDirectory);
                Files.createDirectory(dataDirectory);
            }

            Path exportDirectory = Paths.get(exportDirectoryPath);
            if (!Files.exists(exportDirectory)) {
                logger.info("Creating export directory: " + exportDirectory);
                Files.createDirectory(exportDirectory);
            }

            logger.fine("Directory setup completed successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating directories", e);
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }

    /**
     * Loads transactions from the data file.
     *
     * @return A TransactionManager containing loaded transactions
     */
    public TransactionManager loadTransactions() {
        logger.info("Loading transactions from: " + dataFilePath);
        TransactionManager transactionManager = new TransactionManager();

        try {
            File file = new File(dataFilePath);

            if (!file.exists()) {
                logger.info("Data file does not exist. Starting with empty transactions.");
                return transactionManager;
            }

            Scanner scanner = new Scanner(file);
            int lineCount = 0;
            int successCount = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineCount++;

                if (line.trim().isEmpty()) {
                    logger.fine("Skipping empty line at line " + lineCount);
                    continue;
                }

                logger.fine("Parsing line " + lineCount + ": " + line);
                Transaction transaction = parseTransaction(line);
                if (transaction != null) {
                    transactionManager.addTransaction(transaction);
                    successCount++;
                    logger.fine("Successfully parsed transaction: " + transaction);
                }
            }

            scanner.close();
            logger.info("Loaded " + successCount + " transactions from " + lineCount + " lines");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Data file not found", e);
            System.err.println("Data file not found: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading data", e);
            System.err.println("Error loading data: " + e.getMessage());
        }

        return transactionManager;
    }

    /**
     * Parses a line of text into a Transaction.
     *
     * @param line The line to parse
     * @return The parsed Transaction, or null if parsing fails
     */
    private Transaction parseTransaction(String line) {
        try {
            String[] parts = line.split("\\|");

            if (parts.length < 5) {
                logger.warning("Invalid data format: " + line);
                System.err.println("Invalid data format: " + line);
                return null;
            }

            String type = parts[0];
            LocalDate date = LocalDate.parse(parts[1], DATE_FORMATTER);
            double amount = Double.parseDouble(parts[2]);
            String description = parts[3];

            List<String> tags = new ArrayList<>();
            if (parts.length > 5 && !parts[5].isEmpty()) {
                tags = Arrays.asList(parts[5].split(","));
            }

            if ("INCOME".equals(type)) {
                logger.fine("Parsed income transaction: date=" + date +
                        ", amount=" + amount + ", description=" + description);
                return new Income(amount, description, date, tags);
            } else if ("EXPENSE".equals(type)) {
                Expense.Category category = Expense.Category.fromString(parts[4]);
                logger.fine("Parsed expense transaction: date=" + date +
                        ", amount=" + amount + ", description=" + description +
                        ", category=" + category);
                return new Expense(amount, description, date, category, tags);
            } else {
                logger.warning("Unknown transaction type: " + type);
                System.err.println("Unknown transaction type: " + type);
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing transaction: " + line, e);
            System.err.println("Error parsing transaction: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves transactions to the data file.
     *
     * @param transactionManager The TransactionManager containing transactions to save
     */
    public void saveTransactions(TransactionManager transactionManager) {
        logger.info("Saving transactions to: " + dataFilePath);
        try {
            FileWriter writer = new FileWriter(dataFilePath);
            List<Transaction> transactions = transactionManager.listTransactions();

            logger.fine("Writing " + transactions.size() + " transactions to file");
            for (Transaction transaction : transactions) {
                String formattedTransaction = formatTransaction(transaction);
                writer.write(formattedTransaction + "\n");
                logger.fine("Wrote transaction: " + formattedTransaction);
            }

            writer.close();
            logger.info("Successfully saved " + transactions.size() + " transactions");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving data", e);
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Formats a Transaction as a string for saving.
     *
     * @param transaction The Transaction to format
     * @return The formatted string
     */
    private String formatTransaction(Transaction transaction) {
        StringBuilder sb = new StringBuilder();

        if (transaction instanceof Income) {
            sb.append("INCOME");
        } else if (transaction instanceof Expense) {
            sb.append("EXPENSE");
        }

        sb.append("|").append(transaction.getDate().format(DATE_FORMATTER));
        sb.append("|").append(transaction.getAmount());
        sb.append("|").append(transaction.getDescription());

        if (transaction instanceof Expense) {
            sb.append("|").append(((Expense) transaction).getCategory());
        } else {
            sb.append("|"); // Empty category for income
        }

        sb.append("|");
        if (!transaction.getTags().isEmpty()) {
            sb.append(String.join(",", transaction.getTags()));
        }

        return sb.toString();
    }

    /**
     * Exports transactions to a CSV file.
     *
     * @param transactionManager The TransactionManager containing transactions to export
     * @return The path to the exported file
     * @throws IOException if an I/O error occurs
     */
    public String exportToCsv(TransactionManager transactionManager) throws IOException {
        String fileName = "finbro_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        String filePath = exportDirectoryPath + File.separator + fileName;

        logger.info("Exporting transactions to CSV: " + filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("Type,Date,Amount,Description,Category,Tags\n");
            logger.fine("Wrote CSV header");

            // Write transactions
            List<Transaction> transactions = transactionManager.listTransactions();
            for (Transaction transaction : transactions) {
                String type = transaction instanceof Income ? "Income" : "Expense";
                String category = transaction instanceof Expense
                        ? ((Expense) transaction).getCategory().toString()
                        : "";
                String tags = String.join(";", transaction.getTags());

                // Escape CSV values properly
                String description = "\"" + transaction.getDescription().replace("\"", "\"\"") + "\"";

                String line = String.format("%s,%s,%.2f,%s,%s,%s\n",
                        type,
                        transaction.getDate().format(DATE_FORMATTER),
                        transaction.getAmount(),
                        description,
                        category,
                        tags);

                writer.write(line);
                logger.fine("Wrote CSV transaction: " + line.trim());
            }

            logger.info("Successfully exported " + transactions.size() + " transactions to CSV");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting to CSV", e);
            throw e;
        }

        return filePath;
    }

    /**
     * Exports transactions to a TXT file.
     *
     * @param transactionManager The TransactionManager containing transactions to export
     * @return The path to the exported file
     * @throws IOException if an I/O error occurs
     */
    public String exportToTxt(TransactionManager transactionManager) throws IOException {
        String fileName = "finbro_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        String filePath = exportDirectoryPath + File.separator + fileName;

        logger.info("Exporting transactions to TXT: " + filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("FinBro Export - " + LocalDate.now().format(DATE_FORMATTER) + "\n\n");
            writer.write("TRANSACTIONS:\n");
            writer.write("--------------------------------------------------------------------------------\n");
            logger.fine("Wrote TXT header");

            // Write transactions
            List<Transaction> transactions = transactionManager.listTransactions();
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                writer.write((i + 1) + ". " + transaction.toString() + "\n");
                writer.write("    Date: " + transaction.getDate().format(DATE_FORMATTER) + "\n");

                if (transaction instanceof Expense) {
                    writer.write("    Category: " + ((Expense) transaction).getCategory() + "\n");
                }

                if (!transaction.getTags().isEmpty()) {
                    writer.write("    Tags: " + String.join(", ", transaction.getTags()) + "\n");
                }

                writer.write("--------------------------------------------------------------------------------\n");
                logger.fine("Wrote TXT transaction #" + (i + 1));
            }

            // Write summary
            writer.write("\nSUMMARY:\n");
            writer.write("--------------------------------------------------------------------------------\n");
            writer.write("Total Income: $" + String.format("%.2f", transactionManager.getTotalIncome()) + "\n");
            writer.write("Total Expenses: $" + String.format("%.2f", transactionManager.getTotalExpenses()) + "\n");
            writer.write("Current Balance: $" + String.format("%.2f", transactionManager.getBalance()) + "\n");
            logger.fine("Wrote TXT summary");

            logger.info("Successfully exported " + transactions.size() + " transactions to TXT");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting to TXT", e);
            throw e;
        }

        return filePath;
    }
}
