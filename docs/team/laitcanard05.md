# Ng Ya Xuen - Project Portfolio Page

## Overview
Our team developed FinBro, a personal finance tracker application designed for command-line interaction. It allows users to manage their income and expenses, search and filter transactions, and gain financial insights with minimal friction.

I focused on implementing and testing the command parsing logic, ensuring that key financial features such as financial summaries, budgeting, and savings goal tracking functioned correctly and efficiently.

---

### Summary of Contributions
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=laitcanard05&breakdown=true)

---

### Enhancements Implemented

1. **Implementation of Key Financial Commands**
    * Developed the command parsing logic for:
        * `summary`: Generates a financial summary, displaying income, expenses, and net balance.
        * `filter`: Filters transactions based on date range and/or category.'
        * `trackbudget`: Enables users to track monthly budgets.
        * `setsavings`: Allows users to set monthly savings goals.
        * `tracksavings`: Allows users to monitor monthly savings goals.
    * Integrated user-friendly guided input prompts for seamless interaction.

2. **Enhancement of Existing Classes**
    * Improved `Ui.java` with structured input handling functions such as `readMonthYear` and `readDates` to enhance usability.
    * Optimized `Parser.java` to support a more user-interactive command parsing experience.
    * Implemented additional helper methods in `TransactionManager.java` for budget and savings calculations.

3. **Implementation of JUnit Tests**
    * Wrote comprehensive JUnit tests for:
        * `SummaryCommand`
        * `FilterCommand`

---

### Contributions to the User Guide

- Documented:
    - Interactive usage of `summary`, `filter`, `trackbudget`, `setsavings` and `tracksavings` commands.
    - Provided examples for better user understanding.

---

### Contributions to the Developer Guide

- Added:
    - Sequence diagrams for `summary`, `filter`, `trackbudget`,`setsavings` and `tracksavings` commands.
    - Detailed descriptions explaining the execution flow of financial tracking features.

---

### Contributions to Team-Based Tasks

- Assisted in testing and debugging features related to financial calculations and user interaction.
- Collaborated on refining error handling and validation logic across different commands.
- Participated in milestone discussions and documentation reviews.

---

### Review/Mentoring Contributions

- Reviewed PRs related to budgeting features and user input parsing.
- Assisted teammates in troubleshooting command parsing errors and debugging logic-related issues.

