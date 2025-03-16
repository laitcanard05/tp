package seedu.finbro.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.finbro.model.Expense;
import seedu.finbro.model.Income;
import seedu.finbro.model.Transaction;
import seedu.finbro.model.TransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Storage class.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    private Path dataDir;
    private Path exportDir;
    private Path dataFile;
    private Storage storage;
    private TransactionManager transactionManager;

    @BeforeEach
    public void setUp() throws IOException {
        // Create temp subdirectories
        dataDir = tempDir.resolve("data");
        exportDir = tempDir.resolve("exports");
        Files.createDirectories(dataDir);
        Files.createDirectories(exportDir);
        
        dataFile = dataDir.resolve("finbro.txt");
        
        // Initialize storage with custom paths
        storage = new Storage(dataFile.toString(), exportDir.toString());
        
        // Create a test transaction manager with sample data
        transactionManager = new TransactionManager();
        transactionManager.addTransaction(new Income(1000,
                "Salary", LocalDate.of(2025, 3, 15), Collections.emptyList()));
        transactionManager.addTransaction(new Expense(50, "Groceries", LocalDate.of(2025, 3, 16), 
                Expense.Category.FOOD, Collections.emptyList()));
        // Verify content
        assertEquals(2, transactionManager.getTransactionCount());
    }

    @AfterEach
    public void tearDown() {
        // No need to delete files as TempDir handles cleanup
    }

    @Test
    public void saveAndLoadTransactionsSuccess() {
        // Save transactions
        storage.saveTransactions(transactionManager);
        
        // Verify file was created
        assertTrue(Files.exists(dataFile));

        // Load transactions and verify
        TransactionManager loadedManager = storage.loadTransactions();
        assertEquals(2, loadedManager.getTransactionCount());
        
        // Since TransactionManager.listTransactions() sorts by date in reverse chronological order,
        // we need to check the content without assuming a specific order
        List<Transaction> loadedTransactions = loadedManager.listTransactions();
        
        // Check that both expected transactions are in the loaded transactions
        boolean foundIncome = false;
        boolean foundExpense = false;
        
        for (Transaction t : loadedTransactions) {
            if (t.toString().equals("[Income] $1000.00 - Salary")) {
                foundIncome = true;
            } else if (t.toString().contains("[Expense]") && t.toString().contains("$50.00 - Groceries")) {
                foundExpense = true;
            }
        }
        
        assertTrue(foundIncome, "Income transaction not found");
        assertTrue(foundExpense, "Expense transaction not found");
    }

    @Test
    public void loadTransactionsEmptyFileSuccess() throws IOException {
        // Create empty file
        Files.createFile(dataFile);
        
        // Load transactions and verify
        TransactionManager loadedManager = storage.loadTransactions();
        assertEquals(0, loadedManager.getTransactionCount());
    }

    @Test
    public void loadTransactionsNonExistentFileReturnsEmptyManager() {
        // Don't create the file
        
        // Load transactions and verify
        TransactionManager loadedManager = storage.loadTransactions();
        assertEquals(0, loadedManager.getTransactionCount());
    }

    @Test
    public void exportToCsvSuccess() throws IOException {
        // Export to CSV
        String csvFilePath = storage.exportToCsv(transactionManager);
        
        // Verify file exists
        Path csvPath = Path.of(csvFilePath);
        assertTrue(Files.exists(csvPath));
        
        // Check file content
        List<String> lines = Files.readAllLines(csvPath);
        assertEquals("Type,Date,Amount,Description,Category,Tags", lines.get(0));
        assertTrue(lines.get(1).contains("Expense,2025-03-16,50.00,\"Groceries\",Food"));
        assertTrue(lines.get(2).contains("Income,2025-03-15,1000.00,\"Salary\""));
    }

    @Test
    public void exportToTxtSuccess() throws IOException {
        // Export to TXT
        String txtFilePath = storage.exportToTxt(transactionManager);
        
        // Verify file exists
        Path txtPath = Path.of(txtFilePath);
        assertTrue(Files.exists(txtPath));
        
        // Check file content
        List<String> lines = Files.readAllLines(txtPath);
        assertTrue(lines.get(0).contains("FinBro Export"));
        assertTrue(lines.contains("1. [Income] $1000.00 - Salary") || 
                lines.contains("2. [Income] $1000.00 - Salary"));
        assertTrue(lines.contains("1. [Expense][Food] $50.00 - Groceries") || 
                lines.contains("2. [Expense][Food] $50.00 - Groceries"));
        assertTrue(lines.contains("Total Income: $1000.00"));
        assertTrue(lines.contains("Total Expenses: $50.00"));
        assertTrue(lines.contains("Current Balance: $950.00"));
    }

    @Test
    public void formatTransactionHandlesTags() throws IOException {
        // Create transaction with tags
        transactionManager = new TransactionManager();
        transactionManager.addTransaction(new Income(1000, "Salary", 
                Collections.singletonList("work")));
        
        // Save and reload
        storage.saveTransactions(transactionManager);
        TransactionManager loadedManager = storage.loadTransactions();
        
        // Verify tags were saved and loaded
        List<Transaction> loadedTransactions = loadedManager.listTransactions();
        assertEquals(1, loadedTransactions.size());
        assertEquals(1, loadedTransactions.get(0).getTags().size());
        assertEquals("work", loadedTransactions.get(0).getTags().get(0));
    }

    @Test
    public void exportFilenamesUseCurrentDate() throws IOException {
        // Export files
        String csvFilePath = storage.exportToCsv(transactionManager);
        String txtFilePath = storage.exportToTxt(transactionManager);
        
        // Get current date in expected format
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Verify filenames contain current date
        assertTrue(csvFilePath.contains("finbro_export_" + dateStr + ".csv"));
        assertTrue(txtFilePath.contains("finbro_export_" + dateStr + ".txt"));
    }
}
