# Lim Jun Fun - Project Portfolio Page

## Overview
Our team developed FinBro, a personal finance tracker application designed for command-line interaction. It allows users to manage their income and expenses, search and filter transactions, and gain financial insights with minimal friction.

I have focused on implementing the some key functionalities for the application and implementing some JUnit test cases to ensure the application is working as intended.

---

### Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=ljunfun&sort=groupTitle%20dsc&sortWithin=title&since=2025-02-21&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other) 

---

### Enhancements Implemented

1. **Core Financial Commands in v1.0**
    * Implemented the following commands:
        * `view` / `balance`: Generates a report of expenses and income for the user to keep track
        * `edit`: Allows the user to update any entry he/she has made to the application
    * Enabled guided input mode for `edit` command.

2. **Enhancement of existing classes** 
    * Enchanced `Ui.java` with functions such as `readAmount`, `readDescription`, `readDate`, `readCategory` to enable smoother user input for the application and allows the user to input information as an option.
    * Reconfigured `LoggingConfig.java` to adjust the priority levels of logging, to not spam the user with all the logging messages that are unnecessary
    * Created `updateTransaction` and `getTransaction` for ease of use of EditCommand to amend anything within the transaction list.
3. **Implementation of JUnit Tests**
    * Implemented the JUnit tests for `EditCommand`, `BalanceCommand`, `ClearCommand`, `ClearCommand`,`LoggingConfig`, `SetBudgetCommandTest`, `SetSavingsGoalCommand`, `TrackBudgetCommand`, `TrackSavingsGoalCommand`, updated `Ui` Tests and     `Parser` Tests


---

### Contributions to the User Guide

- Documented:
  - Interactive usage of `view` / `balance` and `edit`
  - Provided examples for such use cases

---

### Contributions to the Developer Guide

- Added:
  - Sequence diagrams for `edit` and `view` / `balance` commands and their execution
  - Descriptions of the code for both `edit` and `view` / `balance`

---

### Contributions to Team-Based Tasks

- Supported testing and debugging of team features involving user interaction and input
- Participated in milestone discussions and documentation reviews

---

### Review/Mentoring Contributions

- Reviewed PRs related to Testing, function enhancements to `balance` and parsing changes
- Provided feedback on improving error messaging and validating corner cases
- Helped teammate troubleshoot various errors and bugs
