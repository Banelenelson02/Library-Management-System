📚 Library Management System
A Java CLI application for managing a library’s books, members, and borrowing operations — backed by MySQL.
Features
	•	✅ Add, view, search, update, and delete books
	•	✅ Register, view, search, and remove members
	•	✅ Borrow and return books with automatic availability tracking
	•	✅ 14-day due date assigned on borrow
	•	✅ Borrow history report
	•	✅ Overdue books report
Tech Stack
	•	Language: Java 17+
	•	Database: MySQL
	•	Connector: MySQL JDBC Driver (mysql-connector-j)
Project Structure
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
