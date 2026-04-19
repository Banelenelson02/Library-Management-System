package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String DB_USER = "root";       // Change to your MySQL username
    private static final String DB_PASSWORD = "password"; // Change to your MySQL password

    private Connection connection;

    // ─── CONNECT ────────────────────────────────────────────────────────────────

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Connected to MySQL database.");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    // ─── INITIALISE TABLES ──────────────────────────────────────────────────────

    public void initializeTables() {
        String createBooksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    isbn VARCHAR(20) UNIQUE NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    genre VARCHAR(50),
                    quantity INT DEFAULT 1,
                    available_quantity INT DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createMembersTable = """
                CREATE TABLE IF NOT EXISTS members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    phone VARCHAR(20),
                    membership_date DATE DEFAULT (CURRENT_DATE),
                    borrowed_count INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createBorrowsTable = """
                CREATE TABLE IF NOT EXISTS borrows (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    member_id INT NOT NULL,
                    book_id INT NOT NULL,
                    borrow_date DATE DEFAULT (CURRENT_DATE),
                    due_date DATE,
                    return_date DATE,
                    status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
                    FOREIGN KEY (member_id) REFERENCES members(id),
                    FOREIGN KEY (book_id) REFERENCES books(id)
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createBooksTable);
            stmt.execute(createMembersTable);
            stmt.execute(createBorrowsTable);
            System.out.println("✅ Tables initialised successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    // ─── BOOK OPERATIONS ────────────────────────────────────────────────────────

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, genre, quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getQuantity());
            pstmt.setInt(6, book.getAvailableQuantity());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error adding book: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("quantity"),
                        rs.getInt("available_quantity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching books: " + e.getMessage());
        }
        return books;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("quantity"),
                        rs.getInt("available_quantity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching books: " + e.getMessage());
        }
        return books;
    }

    public boolean updateBook(int id, String title, String author, String genre, int quantity) {
        String sql = "UPDATE books SET title=?, author=?, genre=?, quantity=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, genre);
            pstmt.setInt(4, quantity);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting book: " + e.getMessage());
            return false;
        }
    }

    // ─── MEMBER OPERATIONS ──────────────────────────────────────────────────────

    public boolean addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhone());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error adding member: " + e.getMessage());
            return false;
        }
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("membership_date"),
                        rs.getInt("borrowed_count")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching members: " + e.getMessage());
        }
        return members;
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("membership_date"),
                        rs.getInt("borrowed_count")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching members: " + e.getMessage());
        }
        return members;
    }

    public boolean deleteMember(int id) {
        String sql = "DELETE FROM members WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting member: " + e.getMessage());
            return false;
        }
    }

    // ─── BORROW / RETURN OPERATIONS ─────────────────────────────────────────────

    public boolean borrowBook(int memberId, int bookId) {
        // Check availability
        String checkSql = "SELECT available_quantity FROM books WHERE id=?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("available_quantity") > 0) {
                // Insert borrow record (due in 14 days)
                String borrowSql = """
                        INSERT INTO borrows (member_id, book_id, borrow_date, due_date)
                        VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY))
                        """;
                try (PreparedStatement borrowStmt = connection.prepareStatement(borrowSql)) {
                    borrowStmt.setInt(1, memberId);
                    borrowStmt.setInt(2, bookId);
                    borrowStmt.executeUpdate();
                }

                // Decrement available_quantity
                String updateBook = "UPDATE books SET available_quantity = available_quantity - 1 WHERE id=?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateBook)) {
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                }

                // Increment borrowed_count
                String updateMember = "UPDATE members SET borrowed_count = borrowed_count + 1 WHERE id=?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateMember)) {
                    updateStmt.setInt(1, memberId);
                    updateStmt.executeUpdate();
                }
                return true;
            } else {
                System.out.println("⚠️  Book is not available for borrowing.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error borrowing book: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(int memberId, int bookId) {
        String findBorrow = """
                SELECT id FROM borrows
                WHERE member_id=? AND book_id=? AND status='borrowed'
                ORDER BY borrow_date DESC LIMIT 1
                """;
        try (PreparedStatement findStmt = connection.prepareStatement(findBorrow)) {
            findStmt.setInt(1, memberId);
            findStmt.setInt(2, bookId);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                int borrowId = rs.getInt("id");

                // Update borrow record
                String updateBorrow = "UPDATE borrows SET return_date=CURRENT_DATE, status='returned' WHERE id=?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateBorrow)) {
                    updateStmt.setInt(1, borrowId);
                    updateStmt.executeUpdate();
                }

                // Increment available_quantity
                String updateBook = "UPDATE books SET available_quantity = available_quantity + 1 WHERE id=?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateBook)) {
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                }

                // Decrement borrowed_count
                String updateMember = "UPDATE members SET borrowed_count = borrowed_count - 1 WHERE id=?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateMember)) {
                    updateStmt.setInt(1, memberId);
                    updateStmt.executeUpdate();
                }
                return true;
            } else {
                System.out.println("⚠️  No active borrow record found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error returning book: " + e.getMessage());
            return false;
        }
    }

    public void viewBorrowHistory() {
        String sql = """
                SELECT b.id, m.name AS member, bk.title AS book, bk.author,
                       b.borrow_date, b.due_date, b.return_date, b.status
                FROM borrows b
                JOIN members m ON b.member_id = m.id
                JOIN books bk ON b.book_id = bk.id
                ORDER BY b.borrow_date DESC
                """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n" + "─".repeat(110));
            System.out.printf("| %-4s | %-20s | %-25s | %-12s | %-12s | %-12s | %-10s |%n",
                    "ID", "Member", "Book", "Borrowed", "Due", "Returned", "Status");
            System.out.println("─".repeat(110));
            while (rs.next()) {
                System.out.printf("| %-4d | %-20s | %-25s | %-12s | %-12s | %-12s | %-10s |%n",
                        rs.getInt("id"),
                        rs.getString("member"),
                        rs.getString("book"),
                        rs.getString("borrow_date"),
                        rs.getString("due_date"),
                        rs.getString("return_date") != null ? rs.getString("return_date") : "—",
                        rs.getString("status"));
            }
            System.out.println("─".repeat(110));
        } catch (SQLException e) {
            System.out.println("❌ Error fetching borrow history: " + e.getMessage());
        }
    }

    public void viewOverdueBooks() {
        String sql = """
                SELECT m.name AS member, m.email, bk.title AS book,
                       b.borrow_date, b.due_date,
                       DATEDIFF(CURRENT_DATE, b.due_date) AS days_overdue
                FROM borrows b
                JOIN members m ON b.member_id = m.id
                JOIN books bk ON b.book_id = bk.id
                WHERE b.status = 'borrowed' AND b.due_date < CURRENT_DATE
                ORDER BY days_overdue DESC
                """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n" + "─".repeat(95));
            System.out.printf("| %-20s | %-25s | %-25s | %-12s | %-12s |%n",
                    "Member", "Email", "Book", "Due Date", "Days Overdue");
            System.out.println("─".repeat(95));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("| %-20s | %-25s | %-25s | %-12s | %-12d |%n",
                        rs.getString("member"),
                        rs.getString("email"),
                        rs.getString("book"),
                        rs.getString("due_date"),
                        rs.getInt("days_overdue"));
            }
            if (!found) System.out.println("| No overdue books. All is well!                                                                |");
            System.out.println("─".repeat(95));
        } catch (SQLException e) {
            System.out.println("❌ Error fetching overdue books: " + e.getMessage());
        }
    }
}