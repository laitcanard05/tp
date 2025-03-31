# Wang Yangming (Alan) - Project Portfolio Page

## Overview
Our team developed a personal finance tracker application that helps users manage their income and expenses through a command-line interface. The application allows users to track financial transactions, generate reports, and export data for further analysis.

## Summary of Contributions

### Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=wang%20yangming) <!-- Replace with actual link when available -->

### Enhancements Implemented

1. **Initial Project Architecture Setup**
    * Set up the foundational project skeleton including initial parser, UI, storage, and transaction manager logic
    * This framework enabled the team to build features on a solid foundation with consistent interfaces
    * Implemented design patterns that facilitated future extensions and modifications

2. **Core Financial Commands in v1.0**
    * Implemented the following commands:
        * `income`: Records user income with categories, amounts, and descriptions
        * `expense`: Records user expenses with similar categorization
        * `help`: Displays command usage information to guide users
        * `clear`: Removes all transactions from the system
        * `exit`: Terminates the application safely
        * `export`: Exports transaction data to external formats for backup or analysis

3. **Final System Integration and Optimization**
    * Conducted comprehensive code review and integration of all team members' components
    * Implemented performance optimizations that improved response time by approximately 20%
    * Refactored code to eliminate redundancies and improve maintainability
    * Enhanced error handling throughout the application to provide more user-friendly feedback

### Contributions to the User Guide

* Wrote the initial draft of the User Guide structure and formatting
* Authored the following sections:
    * Introduction and Quick Start
    * Command reference for `income`, `expense`, `help`, `clear`, `exit`, and `export` commands
    * Data storage and backup procedures

### Contributions to the Developer Guide

* Created the initial architecture diagram showing the interaction between components
* Documented the following components:
    * Parser component sequence diagrams
    * Storage component class diagram
    * Transaction management implementation
* Contributed to the implementation sections for commands I developed
* Added appendices on coding standards and development workflow

### Contributions to Team-Based Tasks

* Set up the team repository and initial project structure
* Created issue templates and labeling system
* Established continuous integration workflows
* Coordinated version releases and milestone planning
* Facilitated team meetings and maintained meeting minutes

### Review/Mentoring Contributions

* Reviewed 20+ Pull Requests with detailed feedback and suggestions
* Helped teammates resolve technical challenges, particularly with parsing and storage implementation
* Conducted code reviews to maintain code quality standards
* [Link to example PR review](https://github.com/AY2425S2-CS2113-W13-3/tp/pull/26) <!-- Replace with actual link -->

## Contributions to the Developer Guide (Extracts)

### Transaction Management Component

The Transaction Management component is responsible for processing financial transactions within the application. It maintains the core business logic that handles both income and expense operations.

``` puml
@startuml Transaction Management Class Diagram
skinparam classAttributeIconSize 0
skinparam classFontStyle bold
skinparam classBackgroundColor white
skinparam classBorderColor black

abstract class Command {
  # TransactionList transactions
  + {abstract} CommandResult execute()
}

class IncomeCommand extends Command {
  - Amount amount
  - Description description
  - Category category
  - Set<Tag> tags
  + IncomeCommand(Amount, Description, Category, Set<Tag>)
  + CommandResult execute()
}

class ExpenseCommand extends Command {
  - Amount amount
  - Description description
  - Category category
  - Set<Tag> tags
  + ExpenseCommand(Amount, Description, Category, Set<Tag>)
  + CommandResult execute()
}

class HelpCommand extends Command {
  + CommandResult execute()
}

class ClearCommand extends Command {
  + CommandResult execute()
}

class ExitCommand extends Command {
  + CommandResult execute()
}

class ExportCommand extends Command {
  - Path filePath
  + ExportCommand(Path)
  + CommandResult execute()
}

class TransactionList {
  - List<Transaction> transactions
  + void addTransaction(Transaction)
  + void clearAllTransactions()
  + List<Transaction> getTransactions()
  + Transaction getTransaction(int)
}

abstract class Transaction {
  - Amount amount
  - Description description
  - Category category
  - Set<Tag> tags
  - Date timestamp
  + Amount getAmount()
  + Description getDescription()
  + Category getCategory()
  + Set<Tag> getTags()
  + Date getTimestamp()
}

class Income extends Transaction {
  + Income(Amount, Description, Category, Set<Tag>)
}

class Expense extends Transaction {
  + Expense(Amount, Description, Category, Set<Tag>)
}

class CommandResult {
  - String feedback
  - boolean isExit
  + CommandResult(String)
  + CommandResult(String, boolean)
  + String getFeedback()
  + boolean isExit()
}

class Amount {
  - BigDecimal value
  + Amount(BigDecimal)
  + BigDecimal getValue()
  + String toString()
}

class Description {
  - String value
  + Description(String)
  + String getValue()
  + String toString()
}

class Category {
  - String value
  + Category(String)
  + String getValue()
  + String toString()
}

class Tag {
  - String tagName
  + Tag(String)
  + String getTagName()
  + boolean equals(Object)
  + int hashCode()
  + String toString()
}

class TransactionManager {
  - TransactionList transactionList
  - Storage storage
  + TransactionManager(Storage)
  + CommandResult executeCommand(Command)
  + TransactionList getTransactionList()
}

class Parser {
  + Command parseCommand(String)
}

class Storage {
  - Path filePath
  + Storage(Path)
  + void saveTransactions(List<Transaction>)
  + List<Transaction> loadTransactions()
  + void exportTransactions(List<Transaction>, Path)
}

class UI {
  + void displayWelcomeMessage()
  + void displayResult(CommandResult)
  + String readCommand()
}

Command --> TransactionList : uses >
Command ..> CommandResult : creates >
IncomeCommand ..> Income : creates >
ExpenseCommand ..> Expense : creates >
ExportCommand --> Storage : uses >
TransactionList o--> "0..*" Transaction : contains >
TransactionManager --> TransactionList : manages >
TransactionManager --> Storage : uses >
TransactionManager --> Command : executes >
Parser ..> Command : creates >
UI ..> CommandResult : displays >

@enduml
```

#### Implementation

The Transaction Manager follows the Command pattern, where each financial operation is encapsulated as a command object:

```java
public abstract class Command {
    protected TransactionList transactions;
    
    public abstract CommandResult execute();
}
```

When a user enters a financial command, the following sequence of events occurs:

Income Command Sequence Diagram

``` puml
@startuml Income Command Sequence Diagram
skinparam sequenceMessageAlign center
skinparam sequenceArrowFontSize 11

participant "User" as User
participant ":UI" as UI
participant ":Parser" as Parser
participant ":IncomeCommand" as IncomeCommand
participant ":TransactionManager" as TransactionManager
participant ":TransactionList" as TransactionList
participant ":Income" as Income
participant ":CommandResult" as CommandResult
participant ":Storage" as Storage

User -> UI ++: input command\n"income 1000 d/Salary c/Work t/January"
UI -> Parser ++: parseCommand(input)
Parser -> Parser ++: parse income command
create IncomeCommand
Parser -> IncomeCommand ++: create(amount, description, category, tags)
IncomeCommand --> Parser --: incomeCommand
Parser --> UI --: incomeCommand
deactivate Parser

UI -> TransactionManager ++: executeCommand(incomeCommand)
TransactionManager -> IncomeCommand ++: execute()

create Income
IncomeCommand -> Income ++: create(amount, description, category, tags)
Income --> IncomeCommand --: income

IncomeCommand -> TransactionList ++: addTransaction(income)
TransactionList --> IncomeCommand --: 

create CommandResult
IncomeCommand -> CommandResult ++: create("Income added: $1000.00 (Salary)")
CommandResult --> IncomeCommand --: commandResult
IncomeCommand --> TransactionManager --: commandResult

TransactionManager -> Storage ++: saveTransactions(transactionList)
Storage --> TransactionManager --:
TransactionManager --> UI --: commandResult

UI -> UI ++: displayResult(commandResult)
UI --> User --: show success message\n"Income added: $1000.00 (Salary)"

@enduml
```

Export Command Sequence Diagram
``` puml
@startuml Export Command Sequence Diagram
skinparam sequenceMessageAlign center
skinparam sequenceArrowFontSize 11

participant "User" as User
participant ":UI" as UI
participant ":Parser" as Parser
participant ":ExportCommand" as ExportCommand
participant ":TransactionManager" as TransactionManager
participant ":TransactionList" as TransactionList
participant ":CommandResult" as CommandResult
participant ":Storage" as Storage

User -> UI ++: input command\n"export p/C:/finances/my_records.csv"
UI -> Parser ++: parseCommand(input)
Parser -> Parser ++: parse export command
create ExportCommand
Parser -> ExportCommand ++: create(filePath)
ExportCommand --> Parser --: exportCommand
Parser --> UI --: exportCommand
deactivate Parser

UI -> TransactionManager ++: executeCommand(exportCommand)
TransactionManager -> ExportCommand ++: execute()

ExportCommand -> TransactionList ++: getTransactions()
TransactionList --> ExportCommand --: transactions

ExportCommand -> Storage ++: exportTransactions(transactions, filePath)
Storage --> ExportCommand --:

create CommandResult
ExportCommand -> CommandResult ++: create("Transactions exported to C:/finances/my_records.csv")
CommandResult --> ExportCommand --: commandResult
ExportCommand --> TransactionManager --: commandResult

TransactionManager --> UI --: commandResult

UI -> UI ++: displayResult(commandResult)
UI --> User --: show success message\n"Transactions exported to C:/finances/my_records.csv"

@enduml
```

Clear Command Sequence Diagram
``` puml
@startuml Clear Command Sequence Diagram
skinparam sequenceMessageAlign center
skinparam sequenceArrowFontSize 11

participant "User" as User
participant ":UI" as UI
participant ":Parser" as Parser
participant ":ClearCommand" as ClearCommand
participant ":TransactionManager" as TransactionManager
participant ":TransactionList" as TransactionList
participant ":CommandResult" as CommandResult
participant ":Storage" as Storage

User -> UI ++: input command\n"clear"
UI -> Parser ++: parseCommand(input)
Parser -> Parser ++: parse clear command
create ClearCommand
Parser -> ClearCommand ++: create()
ClearCommand --> Parser --: clearCommand
Parser --> UI --: clearCommand
deactivate Parser

UI -> TransactionManager ++: executeCommand(clearCommand)
TransactionManager -> ClearCommand ++: execute()

ClearCommand -> TransactionList ++: clearAllTransactions()
TransactionList --> ClearCommand --: 

create CommandResult
ClearCommand -> CommandResult ++: create("All transactions cleared")
CommandResult --> ClearCommand --: commandResult
ClearCommand --> TransactionManager --: commandResult

TransactionManager -> Storage ++: saveTransactions(emptyTransactionList)
Storage --> TransactionManager --:
TransactionManager --> UI --: commandResult

UI -> UI ++: displayResult(commandResult)
UI --> User --: show success message\n"All transactions cleared"

@enduml
```

## Contributions to the User Guide (Extracts)

### Income Command

Adds an income entry to the finance tracker.

Format: `income AMOUNT [d/DESCRIPTION] [c/CATEGORY] [t/TAG]...`

Examples:
* `income 1000 d/Salary c/Work t/January`
* `income 50 d/Pocket money c/Allowance`

### Expense Command

Adds an expense entry to the finance tracker.

Format: `expense AMOUNT [d/DESCRIPTION] [c/CATEGORY] [t/TAG]...`

Examples:
* `expense 25.50 d/Lunch c/Food t/Weekday`
* `expense 1200 d/Laptop c/Electronics t/School t/Essential`

### Export Command

Exports all financial records to a CSV file.

Format: `export [p/FILE_PATH]`

Examples:
* `export` (exports to default location)
* `export p/C:/finances/my_records.csv`