# Loh Jay Hon, Giggs - Project Portfolio Page

## Overview
Our team developed a personal finance tracker application that helps users manage their income and expenses through a command-line interface. The application allows users to track financial transactions, generate reports, and export data for further analysis.

## Summary of Contributions

### Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=notgiggs&breakdown=true) <!-- Replace with actual link when available -->

### Enhancements Implemented

1. **Budget Feature Implementation (`setbudget` Command)**
    * Implemented full support for setting monthly budgets by month/year and amount.
    * Created `SetBudgetCommand` to execute logic, handled validation for months, years, and positive values.
    * Extended the parser and `Ui` to prompt and validate user inputs for budget setting.
    * Updated `TransactionManager` and `Storage` to store and retrieve budget information persistently.

2. **Enhanced Transaction Listing (`list` Command)**
    * Enabled support for optional `limit` and/or `start date` in `ListCommand`.
    * Implemented logic to filter transactions from a date or limit their count.
    * Added robust parsing and validation in the parser, including `readStartDate()` and `readLimit()` methods.
    * Designed friendly CLI interactions and informative logger messages to guide the user.

3. **Range-Based Transaction Deletion (`delete` Command)**
    * Designed and implemented support for both single index and index range deletion (e.g., `2` or `2-5`).
    * Created `readIndexRange()` in `Ui.java` to process and validate inputs.
    * Implemented reverse-index deletion in `TransactionManager` to avoid index shifting issues.
    * Ensured appropriate error handling for invalid, zero, or negative indexes.

4. **Budget Persistence System**
    * Developed the logic in `Storage.java` to save and load monthly budgets from `budgets.txt`.
    * Used a simple `yyyy-MM|amount` format for easy readability and debugging.
    * Ensured fault-tolerant file parsing with proper error logging.

5. **Parser Enhancements**
    * Extended the parser to support multiple new commands (`list`, `delete`, `setbudget`).
    * Added full validation for number formats, ranges, empty inputs, and incorrect date formats.
    * Refactored and modularized the parser methods (`parseDeleteCommand`, `parseListCommand`, `parseSetBudgetCommand`, etc.) for testability and clarity.
    * Logged parsing operations in detail for easier debugging.

---

### Contributions to the User Guide

* Authored the following sections:
    * `list` command
    * `delete` command
    * `setbudget` command
* Added usage examples, optional parameter handling, and validation behavior.
* Helped shape the guide formatting for consistency and readability.

---

### Contributions to the Developer Guide

* Wrote and integrated the following sections:
    * Parser logic for `list`, `delete`, `setbudget`
    * Execution flow of `ListCommand` and budget storage handling
* Added and updated the following diagrams:
    * `Parser`, `TransactionManager`, `Storage` and `Exceptions` Class Diagrams
    * `ListCommand` and `SearchCommand` Sequence Diagram

---

### Contributions to Team-Based Tasks

* Maintained and organized `Parser.java`, ensuring all new commands were properly integrated.
* Coordinated common logger usage and parser error handling strategies across the team.
* Contributed to test script setup and CI enhancements (e.g., `runtest.sh`, `runtest.bat` compatibility).
* Reviewed teammates’ parser and command PRs for consistency with project style and standards.

---

### Review/Mentoring Contributions

* Reviewed over 10 PRs with thorough suggestions, especially those related to parser structure, storage handling, and UI prompts.
* Helped teammates debug logging configuration issues and Windows file lock problems.
* Example review:
    * [PR #101 - Quick Updates](https://github.com/AY2425S2-CS2113-W13-3/tp/pull/101)

---

### Contributions Beyond the Project Team

* Investigated and helped resolve a Windows-specific file lock issue caused by Java’s `FileHandler`.
* Shared tips on how to resolve text-ui-test issues since data was not being appropriately cleared.

### Set Budget Class Diagram

``` puml
@startuml SetBudgetCommand Class Diagram
skinparam classAttributeIconSize 0
skinparam classFontStyle bold
skinparam classBackgroundColor white
skinparam classBorderColor black

interface Command {
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class SetBudgetCommand {
  - double budget
  - int month
  - int year
  + SetBudgetCommand(double, int, int)
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class TransactionManager {
  + void setBudget(int, int, double)
}

class Storage {
  + void saveBudgets(TransactionManager)
}

class Ui

Command <|.. SetBudgetCommand
SetBudgetCommand --> TransactionManager : uses >
SetBudgetCommand --> Storage : uses >
SetBudgetCommand --> Ui : uses >

@enduml
```

### List Command Class Diagram

``` puml
@startuml ListCommand Class Diagram
skinparam classAttributeIconSize 0
skinparam classFontStyle bold
skinparam classBackgroundColor white
skinparam classBorderColor black

interface Command {
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class ListCommand {
  - Integer limit
  - LocalDate date
  + ListCommand(Integer, LocalDate)
  + ListCommand()
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class Transaction {
  + LocalDate getDate()
}

class TransactionManager {
  + List<Transaction> listTransactions()
  + List<Transaction> listTransactions(Integer)
  + List<Transaction> listTransactionsFromDate(LocalDate)
}

class Storage

class Ui

Command <|.. ListCommand
ListCommand --> TransactionManager : uses >
ListCommand --> Storage : uses >
ListCommand --> Ui : uses >
ListCommand --> "0..*" Transaction : lists >

@enduml
```

### Delete Command Class Diagram

``` puml
@startuml DeleteCommand Class Diagram
skinparam classAttributeIconSize 0
skinparam classFontStyle bold
skinparam classBackgroundColor white
skinparam classBorderColor black

interface Command {
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class DeleteCommand {
  - int startIndex
  - int endIndex
  + DeleteCommand(int, int)
  + String execute(TransactionManager, Ui, Storage)
  + boolean isExit()
}

class TransactionManager {
  + int getTransactionCount()
  + List<Transaction> listTransactions()
  + void deleteTransaction(int)
}

class Storage {
  + void saveTransactions(TransactionManager)
}

class Ui

class Transaction {
  + String toString()
}

Command <|.. DeleteCommand
DeleteCommand --> TransactionManager : uses >
DeleteCommand --> Storage : uses >
DeleteCommand --> Ui : uses >
DeleteCommand --> "0..*" Transaction : lists >

@enduml
```

## Contributions to the User Guide (Extracts)

### SetBudget Command

Sets a monthly budget for a specified month and year.

Format: `setbudget`

The app will prompt for:
- Month (1-12) — optional (defaults to current month)
- Year — optional (defaults to current year)
- Budget amount — required (must be a positive number)

Example Interaction:
```
> setbudget  
Please enter the month (1-12):  
3  
Please enter the year:  
2025  
Enter your budget:  
1500  
```

Expected Output:
```
Budget of 1500.0 has been set for 3/2025
```

---

### List Command

Lists transactions filtered optionally by a start date and/or a limit.

Format: `list`

The app will prompt for:
- Start date — optional (format: YYYY-MM-DD)
- Number of transactions to show — optional (leave blank to show all)

Example Interaction:
```
> list  
Please enter the start date in the format yyyy-mm-dd:  
2025-03-01  
Enter number of transactions to list:  
5  
```

Expected Output:
```
Here are your transactions:
1. [Income] $1000.00 - Salary (Date created: 2025-03-03)
2. [Expense] $200.00 - Groceries (Date created: 2025-03-04)
...
```

---

### Delete Command

Deletes one or more transactions by index or index range.

Format: `delete`

The app will prompt:
- A single index (e.g., `3`) or a range (e.g., `2-4`)

Example Interaction:
```
> delete  
Enter a number or range to delete:  
2-4  
```

Expected Output:
```
Deleted transactions:
- [Income] $500.00 - Bonus
- [Expense] $50.00 - Lunch
- [Expense] $20.00 - Bus Fare
```


