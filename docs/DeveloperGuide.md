# FinBro Developer Guide

## Introduction

FinBro is a personal finance management application that operates through a Command Line Interface (CLI). This developer guide provides comprehensive information about the architecture, implementation, and design decisions behind FinBro to help developers understand the codebase and contribute effectively.

## Setting Up the Development Environment

### Prerequisites
- JDK 17
- Gradle 7.6.2 or higher
- IntelliJ IDEA (recommended)

### Getting Started

1. Clone the repository:
   ```
   git clone https://github.com/AY2425S2-CS2113-W13-3/tp.git
   ```

2. Import the project as a Gradle project in IntelliJ IDEA:
   - Open IntelliJ IDEA
   - Select "Import Project"
   - Navigate to the project directory and select the `build.gradle` file
   - Follow the prompts to complete the import

3. Verify the setup:
   - Run the tests: `./gradlew test`
   - Run the application: `./gradlew run`

## Architecture

FinBro follows a layered architecture pattern with clear separation of concerns:

![Architecture Diagram](docs/images/architecture.png)

### Component Overview

#### 1. Main Component (`FinBro.java`)
- Serves as the entry point of the application
- Coordinates interactions between other components
- Manages the main execution flow

#### 2. UI Component (`Ui.java`)
- Handles all user interaction through the command line
- Displays messages, prompts, and results to the user
- Captures and forwards user input to the parser

#### 3. Logic Component
- **Parser (`Parser.java`)**: Converts user input into command objects
- **Command Classes**: Implement specific functionalities using the Command pattern

#### 4. Model Component
- **Transaction Classes**: Define the core data structures
- **TransactionManager**: Manages the collection of transactions
- Implements business logic and operations on the data model

#### 5. Storage Component (`Storage.java`)
- Handles persistence of data
- Manages saving and loading of transaction data
- Supports data export in various formats

### Class Structure

```
seedu.finbro/
├── FinBro.java                 # Main class
├── logic/
│   ├── command/
│   │   ├── Command.java         # Command interface
│   │   ├── IncomeCommand.java
│   │   ├── ExpenseCommand.java
│   │   └── ...                  # Other command implementations
│   └── parser/
│       └── Parser.java          # Input parser
├── model/
│   ├── Transaction.java         # Abstract base class
│   ├── Income.java
│   ├── Expense.java
│   └── TransactionManager.java  # Business logic
├── storage/
│   └── Storage.java             # Data persistence
└── ui/
    └── Ui.java                  # User interface
```

## Sequence Diagrams

### Adding a Transaction

The following sequence diagram illustrates the process when a user adds a new transaction:

```
User -> FinBro: input command
FinBro -> Parser: parseCommand(userInput)
Parser -> IncomeCommand: new IncomeCommand(...)
Parser --> FinBro: command
FinBro -> IncomeCommand: execute()
IncomeCommand -> TransactionManager: addTransaction(income)
IncomeCommand -> Storage: saveTransactions()
IncomeCommand --> FinBro: result message
FinBro -> Ui: showMessage(result)
Ui --> User: display result
```

## Design Patterns

FinBro implements several design patterns to enhance maintainability and extensibility:

### 1. Command Pattern
All user actions are encapsulated as command objects implementing the `Command` interface. This allows for:
- Uniform handling of different commands
- Easy addition of new commands
- Support for operations like undo/redo (future enhancement)

### 2. Singleton Pattern
The `TransactionManager` is implemented as a singleton to ensure:
- Only one instance manages the transactions
- Consistent state across the application
- Centralized access to transaction data

### 3. Factory Method Pattern
The `Parser` class serves as a factory, creating appropriate command objects based on user input. Benefits include:
- Encapsulation of command creation logic
- Separation of command execution from creation
- Enhanced extensibility when adding new commands

### 4. Model-View-Controller (MVC)
The application follows an MVC-like structure:
- **Model**: `Transaction` classes and `TransactionManager`
- **View**: `Ui` class
- **Controller**: `Command` classes and `FinBro` class

## Implementation Details

### Key Features

#### 1. Transaction Management

Transactions are represented by an abstract `Transaction` class with concrete implementations `Income` and `Expense`.

**Key aspects:**
- Flexible tag system for categorization
- Predefined categories for expenses
- Date-based tracking

**Implementation:**
```java
public abstract class Transaction {
    protected final double amount;
    protected final String description;
    protected final LocalDate date;
    protected final List<String> tags;
    // ...
}

public class Income extends Transaction {
    // Income-specific implementation
}

public class Expense extends Transaction {
    private final Category category;
    // Expense-specific implementation
}
```

#### 2. Command Parsing

The `Parser` class converts user input into appropriate command objects through several phases:
1. Tokenization of input
2. Extraction of command word
3. Parameter parsing
4. Command object creation

**Implementation Example:**
```java
public Command parseCommand(String userInput) {
    String[] parts = userInput.split("\\s+", 2);
    String commandWord = parts[0].toLowerCase();
    String arguments = parts.length > 1 ? parts[1] : "";

    switch (commandWord) {
        case "income":
            return parseIncomeCommand(arguments);
        case "expense":
            return parseExpenseCommand(arguments);
        // ... other commands
    }
}
```

#### 3. Data Persistence

The `Storage` class manages saving and loading of transaction data using a custom text-based format.

**File formats:**
- **Internal storage**: Text file with fields separated by '|' delimiter
- **Export formats**: CSV and TXT

**Implementation:**
- Transactions are serialized to text format for persistence
- Data is loaded into memory at application startup
- Changes are saved to disk after each transaction modification

#### 4. Financial Summaries

The `SummaryCommand` generates financial reports with the following capabilities:
- Filtering by time period (month/year)
- Categorized expense breakdown
- Tag-based transaction analysis
- Income vs. expense comparison

## Testing

### Structure

Tests are organized following the same package structure as the main code:

```
test/java/seedu/finbro/
├── logic/
│   ├── command/
│   │   ├── IncomeCommandTest.java
│   │   └── ...
│   └── parser/
│       └── ParserTest.java
├── model/
│   ├── TransactionTest.java
│   └── TransactionManagerTest.java
└── storage/
    └── StorageTest.java
```

### Running Tests

Run all tests:
```
./gradlew test
```

Run a specific test:
```
./gradlew test --tests "seedu.finbro.model.TransactionTest"
```

### Text UI Testing

Text UI testing verifies application behavior by comparing output against expected results:

1. Run tests:
```
cd text-ui-test
./runtest.sh
```

2. The script compares the actual output against predefined expected output.

## Future Enhancements

### Planned Features

1. **Recurring Transactions**
   - Automatic addition of regular income/expenses
   - Customizable recurrence patterns

2. **Budget Management**
   - Setting spending limits by category
   - Alerts when approaching budget thresholds
   - Visual budget utilization indicators

3. **Data Visualization**
   - Text-based charts for spending patterns
   - Trend analysis for income/expenses over time
   - Category distribution visualization

4. **Multiple Accounts**
   - Support for tracking different financial accounts
   - Transfer operations between accounts
   - Consolidated and per-account reporting

5. **Investment Tracking**
   - Basic portfolio management
   - Investment performance metrics
   - Asset allocation tracking

## Appendix

### Glossary

- **CLI**: Command Line Interface
- **Transaction**: Any financial event (income or expense)
- **Tag**: User-defined label for categorizing transactions
- **Category**: Predefined classification for expenses

### References

- Java SE 17 Documentation
- JUnit 5 User Guide
- Gradle User Manual