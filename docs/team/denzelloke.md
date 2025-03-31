# LOKE MUN KONG DENZEL - Project Portfoilio Page

## Project: FinBro

---
## Overview

FinBro is a Command Line Interface (CLI)-based personal finance tracker aimed at users who prefer typing to clicking. 
It allows users to add, list, search, and analyze their income and expenses, providing a simple yet powerful tool for financial management.

FinBro supports both traditional command parsing and an interactive mode, giving users a guided experience when adding transactions. 
It also emphasizes defensive programming to ensure data integrity through robust validation and clear error feedback.

---

## Summary of Contributions

### Code Contributed
- [View on CS2113 Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=&breakdown=true&sort=groupTitle&sortWithin=title&since=2024-12-01&timeframe=commit&moduleCode=CS2113&group=AY2425S2-CS2113-W13-3&auth=[yourGitHubUsername])

### Enhancements Implemented

- **Interactive CLI Input Mode**:
    - Implemented interactive prompt flows for `income`, `expense`, and `search` commands (`Parser.java`, `Ui.java`).
    - Supports guided step-by-step data entry, improving usability for non-technical users.


- **Defensive Input Validation**:
    - Designed custom exceptions: `EmptyInputException`, `NegativeNumberException`, and `DecimalPointException`.
    - Added extensive input checks and recovery flows in `Ui.java` for `readDouble`, `readIndex`, `readString`.


- **Duplicate Transaction Detection**:
    - Implemented logic to detect duplicate `Income` and `Expense` transactions via `TransactionManager#getTransactionDuplicates`.
    - Warns users of duplicates and requests confirmation before proceeding.


- **Indexed Category Selection**:
    - Enhanced `expense` command with indexed selection of categories using `parseCategory()`.


- **Search Feature**:
    - Implemented `SearchCommand` to search transactions by description keyword(s).
    - Designed interactive input flow for search queries.

---
### Contributions to the User Guide (UG)

- Added sections and examples for:
    - `income`, `expense`, and `search` commands (interactive mode).
    - Duplicate detection behavior and error handling.
    - Category index selection flow.


- Suggested and implemented a new “Input Rules” and “FAQ” enhancements.

---
### Contributions to the Developer Guide (DG)

- Added UML class and sequence diagrams for `IncomeCommand`, `SearchCommand`, and parser-interactive flows.
- Documented behavior of `Ui.java` input handling and command parsing logic.

---
### Contributions to Team-based Tasks

- Helped enforce consistent CLI prompts and logging across commands.
- Coordinated integration testing for interactive mode and input validation.

---

