# FinBro User Guide

![FinBro Logo](img/FinBro.png)

## Introduction

Welcome to FinBro, your personal finance management companion. FinBro is a desktop application optimized for use via a Command Line Interface (CLI). If you prefer typing over clicking, FinBro will help you manage your finances more efficiently than traditional GUI applications.

## Getting Started

### Prerequisites
* Java 17 installed on your computer

### Installation
1. Download the latest FinBro.jar file from the [releases page](https://github.com/yourusername/finbro/releases)
2. Run the jar file using: `java -jar FinBro.jar`
3. Ensure you are in the correct directory when running the command

## Command Format Conventions

The following conventions are used in this guide:

* Words in `UPPER_CASE` are parameters to be supplied by you
* Items in square brackets `[...]` are optional
* Items with `...` after them can be used multiple times including zero times
* Parameters can be provided in any order

## Core Commands

### Transaction Management

#### Recording Income: `income`

Records money received into your account.

**Format:** `income AMOUNT d/DESCRIPTION [t/TAG]...`

**Parameters:**
* `AMOUNT`: A positive number with up to 2 decimal places
* `d/DESCRIPTION`: A brief description of the income source
* `t/TAG`: Optional tags to categorize your income (can have multiple)

**Examples:**
```
income 3000 d/Monthly salary t/work
income 500.50 d/Freelance project t/work t/design
```

#### Recording Expenses: `expense`

Records money spent from your account.

**Format:** `expense AMOUNT d/DESCRIPTION [c/CATEGORY] [t/TAG]...`

**Parameters:**
* `AMOUNT`: A positive number with up to 2 decimal places
* `d/DESCRIPTION`: A brief description of the expense
* `c/CATEGORY`: One of: Food, Transport, Shopping, Bills, Entertainment, Others (defaults to Others if not specified)
* `t/TAG`: Optional tags for further categorization (can have multiple)

**Examples:**
```
expense 25.50 d/Lunch c/Food t/work
expense 75 d/New shoes c/Shopping
expense 10.80 d/Bus fare c/Transport
```

#### Viewing Transactions: `list`

Displays your transaction history.

**Format:** `list [n/NUMBER] [d/DATE]`

**Parameters:**
* `n/NUMBER`: Limits the display to the specified number of most recent transactions
* `d/DATE`: Shows only transactions from the specified date (format: YYYY-MM-DD)

**Examples:**
```
list
list n/10
list d/2025-02-18
```

#### Removing Transactions: `delete`

Removes a transaction from your records.

**Format:** `delete INDEX`

**Parameters:**
* `INDEX`: The position number of the transaction as shown in the list

**Example:**
```
delete 1
```

### Finding and Filtering

#### Searching Transactions: `search`

Finds transactions containing specific keywords.

**Format:** `search KEYWORD [MORE_KEYWORDS]...`

**Notes:**
* Search is case-insensitive
* Only the description field is searched
* Matches any transaction that contains at least one keyword

**Examples:**
```
search grocery
search lunch dinner
```

#### Filtering by Date: `filter`

Shows transactions within a specific time period.

**Format:** `filter d/DATE [to/DATE]`

**Parameters:**
* `d/DATE`: Start date in YYYY-MM-DD format
* `to/DATE`: Optional end date in YYYY-MM-DD format (defaults to current date if omitted)

**Examples:**
```
filter d/2025-02-01 to/2025-02-28
filter d/2025-02-01
```

### Financial Insights

#### Checking Balance: `balance`

Shows your current account balance.

**Format:** `balance`

#### Generating Summary: `summary`

Provides an overview of your financial activity for a specific period.

**Format:** `summary [m/MONTH] [y/YEAR]`

**Parameters:**
* `m/MONTH`: Month number (1-12)
* `y/YEAR`: Year in 4-digit format

**Examples:**
```
summary
summary m/2 y/2025
```

### Data Management

#### Exporting Data: `export`

Exports your financial records to a file.

**Format:** `export [f/FORMAT]`

**Parameters:**
* `f/FORMAT`: File format, either "csv" or "txt" (defaults to CSV)

**Examples:**
```
export
export f/txt
```

#### Clearing All Data: `clear`

Deletes all your financial records.

**Format:** `clear`

**Notes:**
* This action cannot be undone
* You will be asked to confirm before proceeding

#### Exiting the Program: `exit`

Closes the application.

**Format:** `exit`

#### Getting Help: `help`

Displays the list of available commands.

**Format:** `help`

## Command Summary

| Action | Format | Example |
|--------|--------|---------|
| Add Income | `income AMOUNT d/DESCRIPTION [t/TAG]...` | `income 3000 d/Monthly salary t/work` |
| Add Expense | `expense AMOUNT d/DESCRIPTION [c/CATEGORY] [t/TAG]...` | `expense 25.50 d/Lunch c/Food t/work` |
| List Transactions | `list [n/NUMBER] [d/DATE]` | `list n/10` |
| Delete Transaction | `delete INDEX` | `delete 1` |
| Search | `search KEYWORD [MORE_KEYWORDS]...` | `search grocery food` |
| Filter | `filter d/DATE [to/DATE]` | `filter d/2025-02-01 to/2025-02-28` |
| View Balance | `balance` | `balance` |
| View Summary | `summary [m/MONTH] [y/YEAR]` | `summary m/2 y/2025` |
| Export Data | `export [f/FORMAT]` | `export f/csv` |
| Clear Data | `clear` | `clear` |
| Exit | `exit` | `exit` |
| Help | `help` | `help` |

## FAQ

**Q: How do I transfer my data to another computer?**

A: You can copy the data folder that contains the FinBro.txt file to the same directory as the FinBro.jar on the new computer.

**Q: Can I use decimal points in the amount?**

A: Yes, you can use up to 2 decimal places for transaction amounts.

**Q: What is the difference between tags and categories?**

A: Categories are predefined classifications for expenses (Food, Transport, etc.), while tags are custom labels that you can apply to both income and expenses for more personalized tracking.

**Q: How do I back up my financial data?**

A: Use the `export` command to create a CSV or TXT file containing all your transactions. Store this exported file in a safe location.