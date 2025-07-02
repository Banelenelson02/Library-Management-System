import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
private static final String USER = "root"; 
private static final String PASS = "_Nelos5827086816"; // my password

    public boolean connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            return true;
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, publication_year, quantity_available) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setInt(4, book.getPublicationYear());
            stmt.setInt(5, book.getQuantityAvailable());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) book.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public void registerMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) member.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error registering member: " + e.getMessage());
        }
    }

    public boolean lendBook(int bookId, int memberId, int days) {
        try {
            if (!isBookAvailable(bookId)) return false;
            
            String sql = "INSERT INTO borrowing_records (book_id, member_id, borrow_date, due_date) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY))";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, bookId);
                stmt.setInt(2, memberId);
                stmt.setInt(3, days);
                stmt.executeUpdate();
            }
            
            updateBookQuantity(bookId, -1);
            return true;
        } catch (SQLException e) {
            System.err.println("Error lending book: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(int recordId) {
        try {
            int bookId = getBookIdFromRecord(recordId);
            if (bookId == -1) return false;
            
            String sql = "UPDATE borrowing_records SET return_date = CURDATE() WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, recordId);
                stmt.executeUpdate();
            }
            
            updateBookQuantity(bookId, 1);
            return true;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    public void listAllBooks() {
        String sql = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAll Books:");
            System.out.printf("%-5s %-30s %-20s %-15s %-6s %s\n", 
                "ID", "Title", "Author", "ISBN", "Year", "Qty");
            while (rs.next()) {
                System.out.printf("%-5d %-30s %-20s %-15s %-6d %d\n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("publication_year"),
                    rs.getInt("quantity_available"));
            }
        } catch (SQLException e) {
            System.err.println("Error listing books: " + e.getMessage());
        }
    }

    public void listBorrowedBooks() {
        String sql = "SELECT r.id, b.title, m.name, r.borrow_date, r.due_date " +
                     "FROM borrowing_records r " +
                     "JOIN books b ON r.book_id = b.id " +
                     "JOIN members m ON r.member_id = m.id " +
                     "WHERE r.return_date IS NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nBorrowed Books:");
            System.out.printf("%-10s %-30s %-20s %-12s %s\n", 
                "Record ID", "Title", "Member", "Borrow Date", "Due Date");
            while (rs.next()) {
                System.out.printf("%-10d %-30s %-20s %-12s %s\n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getDate("borrow_date"),
                    rs.getDate("due_date"));
            }
        } catch (SQLException e) {
            System.err.println("Error listing borrowed books: " + e.getMessage());
        }
    }

    public void listOverdueBooks() {
        String sql = "SELECT r.id, b.title, m.name, r.due_date " +
                     "FROM borrowing_records r " +
                     "JOIN books b ON r.book_id = b.id " +
                     "JOIN members m ON r.member_id = m.id " +
                     "WHERE r.return_date IS NULL AND r.due_date < CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nOverdue Books:");
            System.out.printf("%-10s %-30s %-20s %s\n", 
                "Record ID", "Title", "Member", "Due Date");
            while (rs.next()) {
                System.out.printf("%-10d %-30s %-20s %s\n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getDate("due_date"));
            }
        } catch (SQLException e) {
            System.err.println("Error listing overdue books: " + e.getMessage());
        }
    }

    private boolean isBookAvailable(int bookId) throws SQLException {
        String sql = "SELECT quantity_available FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("quantity_available") > 0;
            }
        }
    }

    private void updateBookQuantity(int bookId, int change) throws SQLException {
        String sql = "UPDATE books SET quantity_available = quantity_available + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, change);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    private int getBookIdFromRecord(int recordId) throws SQLException {
        String sql = "SELECT book_id FROM borrowing_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("book_id") : -1;
            }
        }
    }
	
}