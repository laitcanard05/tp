# FinBro User Guide

![FinBro Logo](img/FinBro.png)

## Table of Contents
* [Introduction](#introduction)

* [Quick Start](#quick-start)

* [Command Format Conventions](#command-format-conventions)

* [Features](#features)
    * [Transaction Management](#features---transaction-management)
        * [Recording Income](#1-recording-income-income-transaction-management)
        * [Recording Expenses](#2-recording-expenses-expense-transaction-management)
        * [Viewing Transactions](#3-viewing-transactions-list-transaction-management)
        * [Removing Transactions](#4-removing-transactions-delete-transaction-management)
      
    * [Finding and Filtering](#features---finding-and-filtering)
        * [Searching Transactions](#5-searching-transactions-search-finding-and-filtering)
        * [Filtering by Date](#6-filtering-by-date-filter-finding-and-filtering)
      
    * [Financial Insights](#features---financial-insights)
        * [Checking Balance](#7-checking-balance-balance-financial-insights)
        * [Generating Summary](#8-generating-summary-summary-financial-insights)
      
    * [Data Management](#features---data-management)
        * [Exporting Data](#9-exporting-data-export-data-management)
        * [Clearing All Data](#10-clearing-all-data-clear-data-management)
      
* [Command Summary](#command-summary)

* [FAQ](#faq)

## Introduction

Welcome to FinBro, your personal finance management companion. 
FinBro is a desktop application optimized for use via a Command Line Interface (CLI). 
If you prefer typing to clicking, FinBro will help you manage your finances more efficiently than traditional GUI applications.

## Quick Start

1. Ensure you have Java 17 or above installed on your computer.
2. Download the latest `FinBro.jar` from [here](https://github.com/AY2425S2-CS2113-W13-3/tp/releases).
3. Copy the file to the folder you want to use as the home folder for FinBro.
4. Open a command prompt/terminal and navigate to the folder where you placed the jar file.
5. Run the application using: `java -jar FinBro.jar`
6. Type a command in the command box and press Enter to execute it. 
For example, typing `help` and pressing Enter will display a summary of available commands.

## Command Format Conventions

The following conventions are used in this guide:

* Words in `UPPER_CASE` are parameters to be supplied by you
* Items in square brackets `[...]` are optional
* Items with `...` after them can be used multiple times including zero times
* Parameters can be provided in any order

# Features

---
## Features - Transaction Management
### 1. Recording Income: `income` [Transaction Management]

Records money received into your account.

**Format:** `income`, `AMOUNT`, `DESCRIPTION`. `[TAGS]`

**Parameters:**
* `AMOUNT`: A positive number with up to 2 decimal places
* `DESCRIPTION`: A brief description of the income source
* `TAGS`: Optional tags to categorize your income (can have 0-3)

**Examples:**
```
Enter Command Word:
> income
 
Enter Amount:
> 0.01

Enter Description:
> Monthly salary

Enter up to 3 tags (separated by space or comma), or press Enter to skip:
> Internship

```

**Enhanced Duplicate Detection**

FinBro automatically checks for **duplicate transactions** based on the same amount and description.

If duplicates are detected:
1. You will receive a **warning**
2. You’ll be asked to confirm whether you still want to proceed

This helps prevent accidental repeated entries.

---

### 2. Recording Expenses: `expense` [Transaction Management]

Records money spent from your account.

**Format:** `expense`, `AMOUNT`, `DESCRIPTION`, `CATEGORY`, `[TAGS]`

**Parameters:**
* `AMOUNT`: A positive number with up to 2 decimal places
* `DESCRIPTION`: A brief description of the expense
* `CATEGORY`: One of: Food, Transport, Shopping, Bills, Entertainment, Others (defaults to Others if not specified)
* `TAG`: Optional tags for further categorization (can have 0-3 Tags)

**Example:**
```
Enter Command Word:
> expense
 
Enter Amount:
> 26.80

Enter Description:
> Chat subscription

Please select a category by entering its corresponding index
0 - OTHERS
1 - FOOD
2 - TRANSPORT
3 - SHOPPING
4 - BILLS
5 - ENTERTAINMENT
> 4

Enter up to 3 tags (separated by space or comma), or press Enter to skip:
>

```
---
### 3. Viewing Transactions: `list` [Transaction Management]

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

**Enhanced Duplicate Detection**

FinBro automatically checks for **duplicate transactions** based on the same amount and description.

If duplicates are detected:
1. You will receive a **warning**
2. You’ll be asked to confirm whether you still want to proceed

This helps prevent accidental repeated entries.

---

### 4. Editing Transactions: `edit` [Transaction Management]

Modifies an existing transaction's details.

**Format:** `edit INDEX`

**Parameters:**
* `INDEX`: The position number of the transaction to edit as shown in the list

**Notes:**
* You can leave any field blank to keep its original value
* For each field, you'll be prompted to enter a new value or press Enter to skip

**Example:**

---

### 5. Removing Transactions: `delete` [Transaction Management]

Removes a transaction from your records.

**Format:** `delete INDEX`

**Parameters:**
* `INDEX`: The position number of the transaction as shown in the list

**Example:**
```
delete 1
```

---
## Features - Finding and Filtering
### 6. Searching Transactions: `search` [Finding and Filtering]

Finds transactions containing specific keywords.

**Format:** `search` `KEYWORD`

**Notes:**
* Search is case-insensitive
* Only the description field is searched
* Only transactions containing entire keyword will be shown

**Example:**
```
> search
Enter keyword or string to search:
> grocery lunch
```

---

### 7. Filtering by Date: `filter` [Finding and Filtering]

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

---
## Features - Financial Insights
### 8. Checking Balance: `balance` [Financial Insights]

Shows your current account balance.

**Format:** `balance`


---

### 9. Generating Summary: `summary`  [Financial Insights]

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

---
## Features - Data Management
### 10. Exporting Data: `export` [Data Management]

Exports your financial records to a file.

**Format:** `export [f/FORMAT]`

**Parameters:**
* `f/FORMAT`: File format, either "csv" or "txt" (defaults to CSV)

**Examples:**
```
export
export f/txt
```

---

### 11. Clearing All Data: `clear`  [Data Management]

Deletes all your financial records.

**Format:** `clear`

**Notes:**
* This action cannot be undone
* You will be asked to confirm before proceeding


---

### 12. Exiting the Program: `exit`

Closes the application.

**Format:** `exit`


---

### 13. Getting Help: `help`

Displays the list of available commands.

**Format:** `help`

---

## Command Summary

| Action             | Command Word                 | Following Fields                                | Example                                    |
|--------------------|------------------------------|-------------------------------------------------|--------------------------------------------|
| Add Income         | `income`                     | `AMOUNT`, `DESCRIPTION`, `[TAGS]`               | `income`, `3000`, `Monthly salary`, `work` |
| Add Expense        | `expense`                    | `AMOUNT`, `DESCRIPTION`, `CATEGORY`, `[TAGS]`   | `expense`, `25.50`, `Lunch`, `1`, `work`   |
| List Transactions  | `list`                       | `[NUMBER]`, `[DATE]`                            | `list`, `10`                               |
Edit Transaction | `edit` | `[AMOUNT]`, `[DESCRIPTION]`, `[DATE]`, `[CATEGORY]`, `[TAGS]` | `edit`, `2` |
| Delete Transaction | `delete`                     | `INDEX`                                         | `delete`, `1`                              |
| Search             | `search`                     | `KEYWORD`                                       | `search`, `grocery food`                   |
| Filter             | `filter`                     | `STARTDATE`, `[ENDDATE]`                        | `filter`, `2025-02-01`, `2025-02-28`       |
| View Balance       | `balance`                    | -                                               | `balance`                                  |
| View Summary       | `summary`                    | `[MONTH] [YEAR]`                                | `summary`, `2`, `2025`                     |
| Export Data        | `export`                     | `[FORMAT]`                                      | `export`, `csv`                            |
| Clear Data         | `clear`                      | -                                               | `clear`                                    |
| Exit               | `exit`                       | -                                               | `exit`                                     |
| Help               | `help`                       | -                                               | `help`                                     |

## FAQ

**Q: How do I transfer my data to another computer?**

A: You can copy the data folder that contains the FinBro.txt file to the same directory as the FinBro.jar on the new computer.

**Q: Can I use decimal points in the amount?**

A: Yes, you can use up to 2 decimal places for transaction amounts.

**Q: What is the difference between tags and categories?**

A: Categories are predefined classifications for expenses (Food, Transport, etc.), while tags are custom labels that you can apply to both income and expenses for more personalized tracking.

**Q: How do I back up my financial data?**

A: Use the `export` command to create a CSV or TXT file containing all your transactions. Store this exported file in a safe location.