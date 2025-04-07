package seedu.finbro.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Base64;
import java.util.UUID;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;

/**
 * Handles saving and loading of data with robust protection against corruption.
 */
public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = "finbro.txt";
    private static final String BUDGET_FILE = "budgets.txt";
    private static final String SAVINGS_FILE = "savings_goals.txt";
    private static final String DEFAULT_EXPORT_DIRECTORY = "exports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CURRENT_FORMAT_VERSION = "FinBro1.0";
    private static final String VERSION_PREFIX = "#VERSION:";
    private static final String CHECKSUM_PREFIX = "#CHECKSUM:";

    // Constants for character escaping
    private static final String PIPE_DELIMITER = "|";
    private static final String ESCAPED_PIPE = "\\|";
    private static final String BACKSLASH = "\\";
    private static final String ESCAPED_BACKSLASH = "\\\\";

    // Character encoding for all files
    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;

    // Timestamp for emergency backups
    private static long lastEmergencyBackup = 0;

    // File paths
    private final String dataFilePath;
    private final String budgetFilePath;
    private final String savingsFilePath;
    private final String exportDirectoryPath;

    /**
     * Constructs a Storage with default file paths.
     */
    public Storage() {
        logger.fine("Initializing Storage with default paths");
        this.dataFilePath = DATA_DIRECTORY + File.separator + DATA_FILE;
        this.budgetFilePath = DATA_DIRECTORY + File.separator + BUDGET_FILE;
        this.savingsFilePath = DATA_DIRECTORY + File.separator + SAVINGS_FILE;
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
        if (dataFilePath == null) {
            throw new IllegalArgumentException("Data file path cannot be null");
        }
        if (exportDirectoryPath == null) {
            throw new IllegalArgumentException("Export directory path cannot be null");
        }

        logger.fine("Initializing Storage with custom paths: dataFile=" + dataFilePath +
                ", exportDir=" + exportDirectoryPath);
        this.dataFilePath = dataFilePath;

        // Derive other file paths based on data file path
        Path dataPath = Paths.get(dataFilePath);
        Path parent = dataPath.getParent();
        String filename = dataPath.getFileName().toString();

        this.budgetFilePath = parent.resolve("budgets.txt").toString();
        this.savingsFilePath = parent.resolve("savings_goals.txt").toString();
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
     * Loads transactions from the data file with comprehensive error recovery.
     *
     * @return A TransactionManager containing loaded transactions
     */
    public TransactionManager loadTransactions() {
        logger.info("Loading transactions from: " + dataFilePath);

        return withFileLock(Paths.get(dataFilePath), () -> {
            Path mainFile = Paths.get(dataFilePath);
            Path backupFile = Paths.get(dataFilePath + ".bak");
            Path emergencyFile = Paths.get(dataFilePath + ".emergency");

            // Try all available files in priority order
            List<Path> filesToTry = new ArrayList<>();
            filesToTry.add(mainFile);
            filesToTry.add(backupFile);
            filesToTry.add(emergencyFile);

            for (Path file : filesToTry) {
                if (!Files.exists(file)) {
                    logger.fine("File does not exist: " + file);
                    continue;
                }

                try {
                    // First verify checksum if file is not empty
                    if (Files.size(file) > 0 && !verifyFileIntegrity(file)) {
                        logger.warning("File integrity verification failed for: " + file);
                        continue;
                    }

                    // Try to parse
                    List<Transaction> transactions = parseTransactions(file);

                    // Create and populate transaction manager
                    TransactionManager tm = new TransactionManager();
                    for (Transaction t : transactions) {
                        if (isValidTransaction(t)) {
                            tm.addTransaction(t);
                        } else {
                            logger.warning("Skipping invalid transaction: " + t);
                        }
                    }

                    // If successful and this isn't the main file, restore this file as main
                    if (!file.equals(mainFile) && Files.exists(file)) {
                        Files.copy(file, mainFile, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("Restored main file from: " + file);
                    }

                    // Create emergency backup periodically (once a day)
                    long now = System.currentTimeMillis();
                    if (now - lastEmergencyBackup > 24 * 60 * 60 * 1000) {  // 24 hours
                        Files.copy(mainFile, emergencyFile, StandardCopyOption.REPLACE_EXISTING);
                        lastEmergencyBackup = now;
                        logger.info("Created emergency backup");
                    }

                    logger.info("Successfully loaded " + tm.getTransactionCount() + " transactions");
                    return tm;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to load from " + file, e);
                }
            }

            // All recovery attempts failed, return empty manager
            logger.severe("All recovery attempts failed, starting with empty data");
            return new TransactionManager();
        }, new TransactionManager());  // Default value if locking fails
    }

    /**
     * Parses transactions from a file.
     *
     * @param path The file path to read from
     * @return List of parsed transactions
     * @throws IOException if an I/O error occurs
     */
    private List<Transaction> parseTransactions(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, FILE_CHARSET);

        // Handle file version if present
        if (!lines.isEmpty() && lines.get(0).startsWith(VERSION_PREFIX)) {
            String version = lines.get(0).substring(VERSION_PREFIX.length());
            lines.remove(0);

            if (!CURRENT_FORMAT_VERSION.equals(version)) {
                lines = migrateFromVersion(version, lines);
            }
        }

        // Remove checksum line if present
        if (!lines.isEmpty() && lines.get(lines.size() - 1).startsWith(CHECKSUM_PREFIX)) {
            lines.remove(lines.size() - 1);
        }

        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            try {
                Transaction transaction = parseTransaction(line);
                if (transaction != null) {
                    transactions.add(transaction);
                } else {
                    logger.warning("Failed to parse transaction at line " + (i + 1));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error parsing line " + (i + 1) + ": " + line, e);
                throw new IOException("Invalid transaction data at line " + (i + 1), e);
            }
        }

        return transactions;
    }

    /**
     * Saves transactions to the data file with atomic operations and integrity protection.
     *
     * @param transactionManager The TransactionManager containing transactions to save
     */
    public void saveTransactions(TransactionManager transactionManager) {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }
        if (dataFilePath == null) {
            throw new IllegalArgumentException("Data file path cannot be null");
        }

        logger.info("Saving transactions to: " + dataFilePath);

        withFileLock(Paths.get(dataFilePath), () -> {
            Path originalFile = Paths.get(dataFilePath);
            Path tempFile = Paths.get(dataFilePath + ".temp");
            Path backupFile = Paths.get(dataFilePath + ".bak");

            try {
                // Create backup of existing file if it exists
                if (Files.exists(originalFile)) {
                    Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                    logger.fine("Created backup of transactions file");
                }

                // Check available disk space
                List<Transaction> transactions = transactionManager.listTransactions();
                List<String> lines = new ArrayList<>();

                // Add version line
                lines.add(VERSION_PREFIX + CURRENT_FORMAT_VERSION);

                // Add transaction lines
                for (Transaction transaction : transactions) {
                    String formattedTransaction = formatTransaction(transaction);
                    lines.add(formattedTransaction);
                }

                // Check disk space before writing
                checkDiskSpace(tempFile, lines);

                // Write to temporary file with checksum
                writeWithChecksum(tempFile, lines);

                // Verify the temporary file
                if (!verifyFileIntegrity(tempFile)) {
                    throw new IOException("Verification of temp file failed");
                }

                // Replace original file with temporary file (atomic operation if supported)
                moveFileSafely(tempFile, originalFile);

                logger.info("Successfully saved " + transactions.size() + " transactions");
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving data", e);

                // Try to restore from backup if write failed
                try {
                    if (Files.exists(backupFile)) {
                        moveFileSafely(backupFile, originalFile);
                        logger.info("Restored original file from backup after failed save");
                    }
                } catch (IOException restoreError) {
                    logger.log(Level.SEVERE, "Failed to restore backup", restoreError);
                }

                System.err.println("Error saving data: " + e.getMessage());
                return false;
            } finally {
                // Clean up temporary files
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error cleaning up temporary files", e);
                }
            }
        }, false);  // Default value if locking fails
    }

    /**
     * Loads budgets from the budget file with error recovery.
     *
     * @param transactionManager The TransactionManager to load budgets into
     */
    public void loadBudgets(TransactionManager transactionManager) {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        withFileLock(Paths.get(budgetFilePath), () -> {
            Path mainFile = Paths.get(budgetFilePath);
            Path backupFile = Paths.get(budgetFilePath + ".bak");

            // Try main file first
            boolean mainLoaded = false;
            if (Files.exists(mainFile)) {
                try {
                    if (Files.size(mainFile) > 0 && !verifyFileIntegrity(mainFile)) {
                        logger.warning("Budget file integrity check failed");
                    } else {
                        mainLoaded = loadBudgetsFromFile(mainFile, transactionManager);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading budgets from main file", e);
                }
            }

            // Try backup if main failed
            if (!mainLoaded && Files.exists(backupFile)) {
                try {
                    if (!verifyFileIntegrity(backupFile)) {
                        logger.warning("Budget backup file integrity check failed");
                    } else {
                        boolean backupLoaded = loadBudgetsFromFile(backupFile, transactionManager);
                        if (backupLoaded) {
                            // Restore main file
                            Files.copy(backupFile, mainFile, StandardCopyOption.REPLACE_EXISTING);
                            logger.info("Restored budget file from backup");
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading budgets from backup file", e);
                }
            }

            if (!mainLoaded) {
                logger.info("No valid budget file found, skipping budget load.");
            }

            return true;
        }, false);  // Default value if locking fails
    }

    /**
     * Saves budgets to the budget file with integrity protection.
     *
     * @param transactionManager The TransactionManager containing budgets to save
     */
    public void saveBudgets(TransactionManager transactionManager) {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        withFileLock(Paths.get(budgetFilePath), () -> {
            Path originalFile = Paths.get(budgetFilePath);
            Path tempFile = Paths.get(budgetFilePath + ".temp");
            Path backupFile = Paths.get(budgetFilePath + ".bak");

            try {
                // Create backup of existing file if it exists
                if (Files.exists(originalFile)) {
                    Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                    logger.fine("Created backup of budgets file");
                }

                // Prepare content
                List<String> lines = new ArrayList<>();
                lines.add(VERSION_PREFIX + CURRENT_FORMAT_VERSION);

                for (Map.Entry<String, Double> entry : transactionManager.getAllBudgets().entrySet()) {
                    String escapedKey = escapeSpecialChars(entry.getKey());
                    lines.add(escapedKey + PIPE_DELIMITER + entry.getValue());
                }

                // Check disk space
                checkDiskSpace(tempFile, lines);

                // Write to temp file with checksum
                writeWithChecksum(tempFile, lines);

                // Verify temp file
                if (!verifyFileIntegrity(tempFile)) {
                    throw new IOException("Verification of temp budget file failed");
                }

                // Replace original with temp file
                moveFileSafely(tempFile, originalFile);

                logger.info("Successfully saved " + transactionManager.getAllBudgets().size() + " budgets");
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving budgets", e);

                // Try to restore from backup
                try {
                    if (Files.exists(backupFile)) {
                        moveFileSafely(backupFile, originalFile);
                        logger.info("Restored budget file from backup after failed save");
                    }
                } catch (IOException restoreError) {
                    logger.log(Level.SEVERE, "Failed to restore budget backup", restoreError);
                }

                System.err.println("Error saving budgets: " + e.getMessage());
                return false;
            } finally {
                // Clean up temporary files
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error cleaning up temporary budget files", e);
                }
            }
        }, false);  // Default value if locking fails
    }

    /**
     * Loads savings goals from the savings file with error recovery.
     *
     * @param transactionManager The TransactionManager to load savings goals into
     */
    public void loadSavingsGoals(TransactionManager transactionManager) {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        withFileLock(Paths.get(savingsFilePath), () -> {
            Path mainFile = Paths.get(savingsFilePath);
            Path backupFile = Paths.get(savingsFilePath + ".bak");

            // Try main file first
            boolean mainLoaded = false;
            if (Files.exists(mainFile)) {
                try {
                    if (Files.size(mainFile) > 0 && !verifyFileIntegrity(mainFile)) {
                        logger.warning("Savings file integrity check failed");
                    } else {
                        mainLoaded = loadSavingsGoalsFromFile(mainFile, transactionManager);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading savings goals from main file", e);
                }
            }

            // Try backup if main failed
            if (!mainLoaded && Files.exists(backupFile)) {
                try {
                    if (!verifyFileIntegrity(backupFile)) {
                        logger.warning("Savings backup file integrity check failed");
                    } else {
                        boolean backupLoaded = loadSavingsGoalsFromFile(backupFile, transactionManager);
                        if (backupLoaded) {
                            // Restore main file
                            Files.copy(backupFile, mainFile, StandardCopyOption.REPLACE_EXISTING);
                            logger.info("Restored savings file from backup");
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error loading savings goals from backup file", e);
                }
            }

            if (!mainLoaded) {
                logger.info("No valid savings goals file found, skipping load.");
            }

            return true;
        }, false);  // Default value if locking fails
    }

    /**
     * Saves savings goals to the savings file with integrity protection.
     *
     * @param transactionManager The TransactionManager containing savings goals to save
     */
    public void saveSavingsGoals(TransactionManager transactionManager) {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        withFileLock(Paths.get(savingsFilePath), () -> {
            Path originalFile = Paths.get(savingsFilePath);
            Path tempFile = Paths.get(savingsFilePath + ".temp");
            Path backupFile = Paths.get(savingsFilePath + ".bak");

            try {
                // Create backup of existing file if it exists
                if (Files.exists(originalFile)) {
                    Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                    logger.fine("Created backup of savings goals file");
                }

                // Prepare content
                List<String> lines = new ArrayList<>();
                lines.add(VERSION_PREFIX + CURRENT_FORMAT_VERSION);

                for (Map.Entry<String, Double> entry : transactionManager.getAllSavingsGoals().entrySet()) {
                    String escapedKey = escapeSpecialChars(entry.getKey());
                    lines.add(escapedKey + PIPE_DELIMITER + entry.getValue());
                }

                // Check disk space
                checkDiskSpace(tempFile, lines);

                // Write to temp file with checksum
                writeWithChecksum(tempFile, lines);

                // Verify temp file
                if (!verifyFileIntegrity(tempFile)) {
                    throw new IOException("Verification of temp savings goals file failed");
                }

                // Replace original with temp file
                moveFileSafely(tempFile, originalFile);

                logger.info("Successfully saved " + transactionManager.getAllSavingsGoals().size() + " savings goals");
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving savings goals", e);

                // Try to restore from backup
                try {
                    if (Files.exists(backupFile)) {
                        moveFileSafely(backupFile, originalFile);
                        logger.info("Restored savings goals file from backup after failed save");
                    }
                } catch (IOException restoreError) {
                    logger.log(Level.SEVERE, "Failed to restore savings goals backup", restoreError);
                }

                System.err.println("Error saving savings goals: " + e.getMessage());
                return false;
            } finally {
                // Clean up temporary files
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error cleaning up temporary savings goals files", e);
                }
            }
        }, false);  // Default value if locking fails
    }

    /**
     * Exports transactions to a CSV file with integrity protection.
     *
     * @param transactionManager The TransactionManager containing transactions to export
     * @return The path to the exported file
     * @throws IOException if an I/O error occurs
     */
    public String exportToCsv(TransactionManager transactionManager) throws IOException {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        String fileName = "finbro_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        String filePath = exportDirectoryPath + File.separator + fileName;
        Path targetFile = Paths.get(filePath);
        Path tempFile = Paths.get(filePath + ".temp");

        try {
            // Prepare content for the CSV file
            List<String> lines = new ArrayList<>();

            // Add header
            lines.add("Type,Date,Amount,Description,Category,Tags");

            // Add transaction data
            List<Transaction> transactions = transactionManager.listTransactions();
            for (Transaction transaction : transactions) {
                String type = transaction instanceof Income ? "Income" : "Expense";
                String category = transaction instanceof Expense
                        ? ((Expense) transaction).getCategory().toString()
                        : "";

                String tags = String.join(";", transaction.getTags());

                // Properly escape CSV values
                String description = "\"" + transaction.getDescription().replace("\"", "\"\"") + "\"";

                String line = String.format("%s,%s,%.2f,%s,%s,%s",
                        type,
                        transaction.getDate().format(DATE_FORMATTER),
                        transaction.getAmount(),
                        description,
                        category,
                        tags);

                lines.add(line);
            }

            // Add separator before budget information
            lines.add("");
            lines.add("# Budget Information");
            lines.add("Month-Year,Budget,SavingsGoal");

            // Get all budget entries
            Map<String, Double> budgets = transactionManager.getAllBudgets();
            Map<String, Double> savingsGoals = transactionManager.getAllSavingsGoals();

            // Combine all month-year keys from both maps
            Set<String> allPeriods = new HashSet<>();
            allPeriods.addAll(budgets.keySet());
            allPeriods.addAll(savingsGoals.keySet());

            // Write budget and savings goal information
            for (String period : allPeriods) {
                double budget = budgets.getOrDefault(period, -1.0);
                double savingsGoal = savingsGoals.getOrDefault(period, -1.0);

                lines.add(String.format("%s,%.2f,%.2f", period, budget, savingsGoal));
            }

            // Check disk space
            checkDiskSpace(tempFile, lines);

            // Write the content to temp file
            Files.write(tempFile, lines, FILE_CHARSET);

            // Move temp to final
            moveFileSafely(tempFile, targetFile);

            logger.info("Successfully exported " + transactions.size() + " transactions and " +
                    allPeriods.size() + " budget/savings entries to CSV");

            return filePath;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting to CSV", e);

            // Clean up
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupError) {
                logger.log(Level.WARNING, "Failed to clean up temp export file", cleanupError);
            }

            throw e;
        }
    }

    /**
     * Exports transactions to a TXT file with integrity protection.
     *
     * @param transactionManager The TransactionManager containing transactions to export
     * @return The path to the exported file
     * @throws IOException if an I/O error occurs
     */
    public String exportToTxt(TransactionManager transactionManager) throws IOException {
        if (transactionManager == null) {
            throw new IllegalArgumentException("TransactionManager cannot be null");
        }

        String fileName = "finbro_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        String filePath = exportDirectoryPath + File.separator + fileName;
        Path targetFile = Paths.get(filePath);
        Path tempFile = Paths.get(filePath + ".temp");

        try {
            // Prepare content for the TXT file
            List<String> lines = new ArrayList<>();

            // Add header
            lines.add("FinBro Export - " + LocalDate.now().format(DATE_FORMATTER));
            lines.add("");
            lines.add("TRANSACTIONS:");
            lines.add("--------------------------------------------------------------------------------");

            // Add transaction data
            List<Transaction> transactions = transactionManager.listTransactions();
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                lines.add((i + 1) + ". " + transaction.toString());
                lines.add("    Date: " + transaction.getDate().format(DATE_FORMATTER));

                if (transaction instanceof Expense) {
                    lines.add("    Category: " + ((Expense) transaction).getCategory());
                }

                if (!transaction.getTags().isEmpty()) {
                    lines.add("    Tags: " + String.join(", ", transaction.getTags()));
                }

                lines.add("--------------------------------------------------------------------------------");
            }

            // Add summary
            lines.add("");
            lines.add("SUMMARY:");
            lines.add("--------------------------------------------------------------------------------");
            lines.add("Total Income: $" + String.format("%.2f", transactionManager.getTotalIncome()));
            lines.add("Total Expenses: $" + String.format("%.2f", transactionManager.getTotalExpenses()));
            lines.add("Current Balance: $" + String.format("%.2f", transactionManager.getBalance()));

            // Check disk space
            checkDiskSpace(tempFile, lines);

            // Write the content to temp file
            Files.write(tempFile, lines, FILE_CHARSET);

            // Move temp to final
            moveFileSafely(tempFile, targetFile);

            logger.info("Successfully exported " + transactions.size() + " transactions to TXT");

            return filePath;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting to TXT", e);

            // Clean up
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupError) {
                logger.log(Level.WARNING, "Failed to clean up temp export file", cleanupError);
            }

            throw e;
        }
    }

    /**
     * Parses a line of text into a Transaction.
     *
     * @param line The line to parse
     * @return The parsed Transaction, or null if parsing fails
     */
    private Transaction parseTransaction(String line) {
        try {
            // Split the line but respect proper escaping of pipes
            String[] parts = safeSplit(line, PIPE_DELIMITER);

            if (parts.length < 5) {
                logger.warning("Invalid data format (not enough fields): " + line);
                return null;
            }

            String type = parts[0];
            LocalDate date = LocalDate.parse(parts[1], DATE_FORMATTER);
            double amount = Double.parseDouble(parts[2]);

            // Unescape the description
            String description = unescapeSpecialChars(parts[3]);

            List<String> tags = new ArrayList<>();
            // Check if there's a 6th part (index 5) for tags
            if (parts.length > 5 && parts[5] != null && !parts[5].isEmpty()) {
                // Unescape each tag
                tags = Arrays.stream(parts[5].split(","))
                        .map(this::unescapeSpecialChars)
                        .collect(Collectors.toList());
            }

            if ("INCOME".equals(type)) {
                logger.fine("Parsed income transaction: date=" + date +
                        ", amount=" + amount + ", description=" + description +
                        ", tags=" + tags);
                return new Income(amount, description, date, tags);
            } else if ("EXPENSE".equals(type)) {
                Expense.Category category = Expense.Category.fromString(parts[4]);
                logger.fine("Parsed expense transaction: date=" + date +
                        ", amount=" + amount + ", description=" + description +
                        ", category=" + category + ", tags=" + tags);
                return new Expense(amount, description, date, category, tags);
            } else {
                logger.warning("Unknown transaction type: " + type);
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing transaction: " + line, e);
            return null;
        }
    }

    /**
     * Formats a Transaction as a string for saving.
     *
     * @param transaction The Transaction to format
     * @return The formatted string
     */
    private String formatTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        StringBuilder sb = new StringBuilder();

        if (transaction instanceof Income) {
            sb.append("INCOME");
        } else if (transaction instanceof Expense) {
            sb.append("EXPENSE");
        } else {
            throw new IllegalStateException("Unknown transaction type: " + transaction.getClass().getName());
        }

        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }

        sb.append(PIPE_DELIMITER).append(transaction.getDate().format(DATE_FORMATTER));
        sb.append(PIPE_DELIMITER).append(transaction.getAmount());

        if (transaction.getDescription() == null) {
            throw new IllegalArgumentException("Transaction description cannot be null");
        }

        // Escape special characters in description
        sb.append(PIPE_DELIMITER).append(escapeSpecialChars(transaction.getDescription()));

        if (transaction instanceof Expense) {
            Expense expense = (Expense) transaction;
            if (expense.getCategory() == null) {
                throw new IllegalArgumentException("Expense category cannot be null");
            }
            sb.append(PIPE_DELIMITER).append(expense.getCategory());
        } else {
            sb.append(PIPE_DELIMITER); // Empty category for income
        }

        sb.append(PIPE_DELIMITER);
        if (transaction.getTags() == null) {
            throw new IllegalArgumentException("Transaction tags cannot be null");
        }

        if (!transaction.getTags().isEmpty()) {
            // Escape special characters in tags
            List<String> escapedTags = transaction.getTags().stream()
                    .map(this::escapeSpecialChars)
                    .collect(Collectors.toList());
            sb.append(String.join(",", escapedTags));
        }

        return sb.toString();
    }

    /**
     * Helper method to load budgets from a file.
     *
     * @param path The file to load from
     * @param transactionManager The TransactionManager to load into
     * @return true if loading was successful, false otherwise
     */
    private boolean loadBudgetsFromFile(Path path, TransactionManager transactionManager) {
        try {
            List<String> lines = Files.readAllLines(path, FILE_CHARSET);

            // Handle version if present
            if (!lines.isEmpty() && lines.get(0).startsWith(VERSION_PREFIX)) {
                lines.remove(0);
            }

            // Remove checksum if present
            if (!lines.isEmpty() && lines.get(lines.size() - 1).startsWith(CHECKSUM_PREFIX)) {
                lines.remove(lines.size() - 1);
            }

            int successCount = 0;

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = safeSplit(line, PIPE_DELIMITER);
                    if (parts.length != 2) {
                        continue;
                    }

                    // Unescape the key
                    String unescapedKey = unescapeSpecialChars(parts[0]);
                    String[] dateParts = unescapedKey.split("-");
                    if (dateParts.length != 2) {
                        continue;
                    }

                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    double amount = Double.parseDouble(parts[1]);

                    transactionManager.setBudget(month, year, amount);
                    successCount++;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error parsing budget entry: " + line, e);
                }
            }

            logger.info("Loaded " + successCount + " budgets");
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading budgets from file", e);
            return false;
        }
    }

    /**
     * Helper method to load savings goals from a file.
     *
     * @param path The file to load from
     * @param transactionManager The TransactionManager to load into
     * @return true if loading was successful, false otherwise
     */
    private boolean loadSavingsGoalsFromFile(Path path, TransactionManager transactionManager) {
        try {
            List<String> lines = Files.readAllLines(path, FILE_CHARSET);

            // Handle version if present
            if (!lines.isEmpty() && lines.get(0).startsWith(VERSION_PREFIX)) {
                lines.remove(0);
            }

            // Remove checksum if present
            if (!lines.isEmpty() && lines.get(lines.size() - 1).startsWith(CHECKSUM_PREFIX)) {
                lines.remove(lines.size() - 1);
            }

            int successCount = 0;

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = safeSplit(line, PIPE_DELIMITER);
                    if (parts.length != 2) {
                        continue;
                    }

                    // Unescape the key
                    String unescapedKey = unescapeSpecialChars(parts[0]);
                    String[] dateParts = unescapedKey.split("-");
                    if (dateParts.length != 2) {
                        continue;
                    }

                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    double amount = Double.parseDouble(parts[1]);

                    transactionManager.setSavingsGoal(month, year, amount);
                    successCount++;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error parsing savings goal entry: " + line, e);
                }
            }

            logger.info("Loaded " + successCount + " savings goals");
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading savings goals from file", e);
            return false;
        }
    }

    /**
     * Escapes special characters in a string for storage in the pipe-delimited format.
     *
     * @param input The string to escape
     * @return The escaped string
     */
    private String escapeSpecialChars(String input) {
        if (input == null) {
            return "";
        }

        // First escape backslashes (must come first)
        String result = input.replace(BACKSLASH, ESCAPED_BACKSLASH);

        // Then escape pipe characters
        result = result.replace(PIPE_DELIMITER, ESCAPED_PIPE);

        return result;
    }

    /**
     * Unescapes special characters from a string stored in the pipe-delimited format.
     *
     * @param input The string to unescape
     * @return The unescaped string
     */
    private String unescapeSpecialChars(String input) {
        if (input == null) {
            return "";
        }

        // First unescape pipes
        String result = input.replace(ESCAPED_PIPE, PIPE_DELIMITER);

        // Then unescape backslashes
        result = result.replace(ESCAPED_BACKSLASH, BACKSLASH);

        return result;
    }

    /**
     * Safely splits a string by delimiter, respecting escaped delimiters.
     *
     * @param input The string to split
     * @param delimiter The delimiter to split by
     * @return Array of split strings
     */
    private String[] safeSplit(String input, String delimiter) {
        if (input == null) {
            return new String[0];
        }

        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaped = false;
        int maxSafeSectionSize = 10000; // Prevent excessive memory usage

        // Handle edge case where input exceeds reasonable size
        if (input.length() > 1000000) {
            logger.warning("Input string too large for safe parsing: " + input.length() + " characters");
            throw new IllegalArgumentException("Input string too large (exceeds 1MB limit)");
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Safety check to prevent StringBuilder from growing too large
            if (current.length() > maxSafeSectionSize) {
                logger.warning("Field size exceeds safe limit during parsing");
                throw new IllegalArgumentException("Field size exceeds safe limit");
            }

            if (escaped) {
                // Previous character was a backslash, add current character as-is
                // Only escape certain characters (backslash and delimiter)
                if (c == '\\' || String.valueOf(c).equals(delimiter)) {
                    current.append(c);
                } else {
                    // For invalid escape sequences, add both the backslash and the character
                    current.append('\\').append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                // Current character is a backslash, enter escaped mode
                // Handle backslash at end of string
                if (i == input.length() - 1) {
                    // Trailing backslash, add it literally
                    current.append('\\');
                } else {
                    escaped = true;
                }
            } else if (delimiter.length() == 1 && c == delimiter.charAt(0)) {
                // Simple case: single-character delimiter
                result.add(current.toString());
                current = new StringBuilder();
            } else if (delimiter.length() > 1 && i <= input.length() - delimiter.length()) {
                // Check multi-character delimiter
                boolean isDelimiter = true;
                for (int j = 0; j < delimiter.length(); j++) {
                    if (input.charAt(i + j) != delimiter.charAt(j)) {
                        isDelimiter = false;
                        break;
                    }
                }

                if (isDelimiter) {
                    result.add(current.toString());
                    current = new StringBuilder();
                    i += delimiter.length() - 1; // Skip the rest of the delimiter
                } else {
                    // Regular character
                    current.append(c);
                }
            } else {
                // Regular character
                current.append(c);
            }
        }

        // Add the last part
        result.add(current.toString());

        // Validate result isn't excessively large
        if (result.size() > 1000) {
            logger.warning("Split resulted in too many fields: " + result.size());
            throw new IllegalArgumentException("Parsing resulted in too many fields (>1000)");
        }

        return result.toArray(new String[0]);
    }

    /**
     * Calculates a checksum for a list of strings.
     *
     * @param lines The lines to calculate the checksum for
     * @return The checksum string
     */
    private String calculateChecksum(List<String> lines) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String line : lines) {
                digest.update(line.getBytes(FILE_CHARSET));
            }
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.warning("SHA-256 not available, using fallback secure checksum");

            // More robust fallback using multiple techniques
            byte[] data = new byte[0];
            try {
                // Concatenate all lines with position information
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (int i = 0; i < lines.size(); i++) {
                    String lineWithPosition = i + ":" + lines.get(i);
                    outputStream.write(lineWithPosition.getBytes(FILE_CHARSET));

                    // Add a non-printable separator to avoid manipulation
                    outputStream.write(new byte[] {0x1F, 0x1E});
                }
                data = outputStream.toByteArray();

                // Apply a simple but effective algorithm (CRC32 + custom manipulation)
                CRC32 crc = new CRC32();
                crc.update(data);
                long crcValue = crc.getValue();

                // Mix with additional factors to make it harder to generate collisions
                long timestamp = System.currentTimeMillis();
                int dataLength = data.length;

                String result = Long.toHexString(crcValue ^ (timestamp & 0xFFFF) ^ dataLength);
                result += ":" + Integer.toHexString(dataLength);

                return result;
            } catch (IOException ex) {
                // Absolute last resort if everything fails
                return UUID.randomUUID().toString();
            }
        }
    }

    /**
     * Writes a list of strings to a file with a checksum.
     *
     * @param file The file to write to
     * @param lines The lines to write
     * @throws IOException if an I/O error occurs
     */
    private void writeWithChecksum(Path file, List<String> lines) throws IOException {
        // Calculate checksum
        String checksum = calculateChecksum(lines);

        // Write all lines plus the checksum
        try (BufferedWriter writer = Files.newBufferedWriter(file, FILE_CHARSET)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }

            // Append checksum
            writer.write(CHECKSUM_PREFIX + checksum);
        }
    }

    /**
     * Verifies the integrity of a file using checksums.
     *
     * @param file The file to verify
     * @return true if the file has a valid checksum, false otherwise
     */
    private boolean verifyFileIntegrity(Path file) {
        try {
            List<String> lines = Files.readAllLines(file, FILE_CHARSET);
            if (lines.isEmpty()) {
                return true;
            }  // Empty file is OK

            // Extract checksum line
            String lastLine = lines.get(lines.size() - 1);
            if (!lastLine.startsWith(CHECKSUM_PREFIX)) {
                logger.warning("No checksum found in file: " + file);
                return false;
            }

            String storedChecksum = lastLine.substring(CHECKSUM_PREFIX.length());
            lines.remove(lines.size() - 1);  // Remove checksum line before calculating

            String calculatedChecksum = calculateChecksum(lines);
            boolean matches = storedChecksum.equals(calculatedChecksum);

            if (!matches) {
                logger.warning("Checksum mismatch: stored=" + storedChecksum +
                        ", calculated=" + calculatedChecksum);
            }

            return matches;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error verifying file integrity: " + file, e);
            return false;
        }
    }

    /**
     * Validates a transaction.
     *
     * @param transaction The transaction to validate
     * @return true if the transaction is valid, false otherwise
     */
    private boolean isValidTransaction(Transaction transaction) {
        if (transaction == null) {
            logger.warning("Transaction is null");
            return false;
        }

        if (transaction.getAmount() <= 0) {
            logger.warning("Transaction amount must be positive: " + transaction.getAmount());
            return false;
        }

        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            logger.warning("Transaction description cannot be null or empty");
            return false;
        }

        if (transaction.getDate() == null) {
            logger.warning("Transaction date cannot be null");
            return false;
        }

        if (transaction instanceof Expense && ((Expense) transaction).getCategory() == null) {
            logger.warning("Expense category cannot be null");
            return false;
        }

        if (transaction.getTags() == null) {
            logger.warning("Transaction tags cannot be null");
            return false;
        }

        return true;
    }

    /**
     * Migrates data from an older format version.
     *
     * @param version The format version
     * @param lines The lines of data
     * @return The migrated data
     */
    private List<String> migrateFromVersion(String version, List<String> lines) {
        logger.info("Migrating data from version: " + version);

        // Implement version-specific migrations here
        // For now, we just return the lines unchanged
        return lines;
    }

    /**
     * Platform-independent file move with fallback.
     *
     * @param source The source file
     * @param target The target file
     * @throws IOException if an I/O error occurs
     */
    private void moveFileSafely(Path source, Path target) throws IOException {
        try {
            // Try atomic move first
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            // Fallback to copy-then-delete
            logger.warning("Atomic move not supported, using fallback strategy");
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            // Verify the copied file
            if (!Files.exists(target) || Files.size(source) != Files.size(target)) {
                throw new IOException("File copy verification failed");
            }

            // Only delete source after successful copy
            Files.delete(source);
        }
    }

    /**
     * Checks if there is enough disk space for a write operation.
     *
     * @param target The target file
     * @param content The content to write
     * @throws IOException if there is not enough disk space
     */
    private void checkDiskSpace(Path target, List<String> content) throws IOException {
        // Estimate required space
        long requiredSpace = estimateRequiredSpace(content);

        try {
            // Get available space on the filesystem
            FileStore fileStore = Files.getFileStore(target.getParent() != null ? target.getParent() : Paths.get("."));
            long availableSpace = fileStore.getUsableSpace();

            // Check if we have enough space (3x margin for safety)
            if (availableSpace < requiredSpace * 3) {
                logger.severe("Insufficient disk space: available=" + availableSpace +
                        ", required=" + (requiredSpace * 3));
                throw new IOException("Insufficient disk space: " + availableSpace +
                        " bytes available, need " + (requiredSpace * 3));
            }
        } catch (IOException e) {
            // If we can't check space, log but continue
            logger.warning("Could not check available disk space: " + e.getMessage());
        }
    }

    /**
     * Estimates the required space for a list of strings.
     *
     * @param content The content to estimate
     * @return The estimated space in bytes
     */
    private long estimateRequiredSpace(List<String> content) {
        // Rough estimate: each character is 2 bytes in UTF-8 (worst case)
        // Plus overhead for line endings
        long estimate = 0;
        for (String line : content) {
            estimate += line.length() * 2 + 2;
        }
        return estimate;
    }

    /**
     * Executes an action with a file lock.
     *
     * @param <T> The return type
     * @param file The file to lock
     * @param action The action to execute
     * @param defaultValue The default value if locking fails
     * @return The result of the action, or the default value if locking fails
     */
    private <T> T withFileLock(Path file, Callable<T> action, T defaultValue) {
        // Create parent directory if it doesn't exist
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not create parent directory for file: " + file, e);
            return defaultValue;
        }

        // Create lock file
        Path lockFile = Paths.get(file.toString() + ".lock");

        try (FileChannel channel = FileChannel.open(lockFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {

            try (FileLock lock = channel.tryLock()) {
                if (lock == null) {
                    // Could not acquire lock
                    logger.warning("Could not acquire lock for file: " + file);
                    return defaultValue;
                }

                // Execute the action while holding the lock
                return action.call();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while using file lock", e);
            return defaultValue;
        } finally {
            // Clean up lock file
            try {
                Files.deleteIfExists(lockFile);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not delete lock file: " + lockFile, e);
            }
        }
    }
}
