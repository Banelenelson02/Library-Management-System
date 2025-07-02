# Library Management System

A Java-based Library Management System with MySQL database integration.

## Features
- Book management (add/view)
- Member registration
- Book lending/returning
- Reporting (borrowed books, overdue books)
## 📸 Screenshots

### Main Menu
![Main Menu](screenshots/main-menu.png)

### Adding a Book
```bash
Title: The Great Gatsby
Author: F. Scott Fitzgerald
...
```
![Add Book](screenshots/add-book.png)

### Lending a Book
![Lend Book](screenshots/lend-book.png)

### Reports View
![Reports](screenshots/reports.png)
## Setup Instructions

1. **Database Setup**:
```bash
mysql -u root -p < sql-scripts/schema.sql
