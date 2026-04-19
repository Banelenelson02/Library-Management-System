package src;

import java.util.List;
import java.util.Scanner;

public class LibrarySystem {

    private static final DatabaseManager db = new DatabaseManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      📚 LIBRARY MANAGEMENT SYSTEM    ║");
        System.out.println("╚══════════════════════════════════════╝");

        if (!db.connect()) {
            System.out.println("Could not connect to database. Exiting.");
            return;
        }

        db.initializeTables();
        mainMenu();

        db.disconnect();
        scanner.close();
    }

    // ─── MAIN MENU ──────────────────────────────────────────────────────────────

    private static void mainMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║        MAIN MENU         ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Book Management      ║");
            System.out.println("║  2. Member Management    ║");
            System.out.println("║  3. Borrow / Return      ║");
            System.out.println("║  4. Reports              ║");
            System.out.println("║  0. Exit                 ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> bookMenu();
                case "2" -> memberMenu();
                case "3" -> borrowMenu();
                case "4" -> reportsMenu();
                case "0" -> {
                    System.out.println("\n👋 Goodbye!");
                    return;
                }
                default -> System.out.println("⚠️  Invalid option. Try again.");
            }
        }
    }

    // ─── BOOK MENU ──────────────────────────────────────────────────────────────

    private static void bookMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║      BOOK MANAGEMENT     ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Add Book             ║");
            System.out.println("║  2. View All Books       ║");
            System.out.println("║  3. Search Books         ║");
            System.out.println("║  4. Update Book          ║");
            System.out.println("║  5. Delete Book          ║");
            System.out.println("║  0. Back                 ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> viewAllBooks();
                case "3" -> searchBooks();
                case "4" -> updateBook();
                case "5" -> deleteBook();
                case "0" -> { return; }
                default -> System.out.println("⚠️  Invalid option.");
            }
        }
    }

    private static void addBook() {
        System.out.println("\n── Add New Book ──");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        Book book = new Book(isbn, title, author, genre, quantity);
        if (db.addBook(book)) {
            System.out.println("✅ Book added successfully!");
        }
    }

    private static void viewAllBooks() {
        List<Book> books = db.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("📭 No books in the library.");
            return;
        }
        System.out.println("\n" + "─".repeat(115));
        System.out.printf("| %-4s | %-15s | %-30s | %-20s | %-15s | %-8s | %-9s |%n",
                "ID", "ISBN", "Title", "Author", "Genre", "Qty", "Available");
        System.out.println("─".repeat(115));
        books.forEach(System.out::println);
        System.out.println("─".repeat(115));
        System.out.println("Total: " + books.size() + " book(s)");
    }

    private static void searchBooks() {
        System.out.print("\nSearch by title / author / ISBN: ");
        String keyword = scanner.nextLine().trim();
        List<Book> books = db.searchBooks(keyword);
        if (books.isEmpty()) {
            System.out.println("🔍 No books found matching '" + keyword + "'.");
            return;
        }
        System.out.println("\n" + "─".repeat(115));
        System.out.printf("| %-4s | %-15s | %-30s | %-20s | %-15s | %-8s | %-9s |%n",
                "ID", "ISBN", "Title", "Author", "Genre", "Qty", "Available");
        System.out.println("─".repeat(115));
        books.forEach(System.out::println);
        System.out.println("─".repeat(115));
    }

    private static void updateBook() {
        System.out.print("\nEnter Book ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("New Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("New Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("New Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("New Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        if (db.updateBook(id, title, author, genre, quantity)) {
            System.out.println("✅ Book updated successfully!");
        } else {
            System.out.println("❌ Book not found.");
        }
    }

    private static void deleteBook() {
        System.out.print("\nEnter Book ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Are you sure? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            if (db.deleteBook(id)) {
                System.out.println("✅ Book deleted.");
            } else {
                System.out.println("❌ Book not found.");
            }
        }
    }

    // ─── MEMBER MENU ────────────────────────────────────────────────────────────

    private static void memberMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║    MEMBER MANAGEMENT     ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Register Member      ║");
            System.out.println("║  2. View All Members     ║");
            System.out.println("║  3. Search Members       ║");
            System.out.println("║  4. Remove Member        ║");
            System.out.println("║  0. Back                 ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addMember();
                case "2" -> viewAllMembers();
                case "3" -> searchMembers();
                case "4" -> deleteMember();
                case "0" -> { return; }
                default -> System.out.println("⚠️  Invalid option.");
            }
        }
    }

    private static void addMember() {
        System.out.println("\n── Register New Member ──");
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        Member member = new Member(name, email, phone);
        if (db.addMember(member)) {
            System.out.println("✅ Member registered successfully!");
        }
    }

    private static void viewAllMembers() {
        List<Member> members = db.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("📭 No members registered.");
            return;
        }
        System.out.println("\n" + "─".repeat(100));
        System.out.printf("| %-4s | %-20s | %-25s | %-15s | %-12s | %-8s |%n",
                "ID", "Name", "Email", "Phone", "Joined", "Borrowed");
        System.out.println("─".repeat(100));
        members.forEach(System.out::println);
        System.out.println("─".repeat(100));
        System.out.println("Total: " + members.size() + " member(s)");
    }

    private static void searchMembers() {
        System.out.print("\nSearch by name / email: ");
        String keyword = scanner.nextLine().trim();
        List<Member> members = db.searchMembers(keyword);
        if (members.isEmpty()) {
            System.out.println("🔍 No members found matching '" + keyword + "'.");
            return;
        }
        System.out.println("\n" + "─".repeat(100));
        System.out.printf("| %-4s | %-20s | %-25s | %-15s | %-12s | %-8s |%n",
                "ID", "Name", "Email", "Phone", "Joined", "Borrowed");
        System.out.println("─".repeat(100));
        members.forEach(System.out::println);
        System.out.println("─".repeat(100));
    }

    private static void deleteMember() {
        System.out.print("\nEnter Member ID to remove: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Are you sure? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            if (db.deleteMember(id)) {
                System.out.println("✅ Member removed.");
            } else {
                System.out.println("❌ Member not found.");
            }
        }
    }

    // ─── BORROW MENU ────────────────────────────────────────────────────────────

    private static void borrowMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║     BORROW / RETURN      ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Borrow a Book        ║");
            System.out.println("║  2. Return a Book        ║");
            System.out.println("║  0. Back                 ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> borrowBook();
                case "2" -> returnBook();
                case "0" -> { return; }
                default -> System.out.println("⚠️  Invalid option.");
            }
        }
    }

    private static void borrowBook() {
        System.out.println("\n── Borrow a Book ──");
        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());

        if (db.borrowBook(memberId, bookId)) {
            System.out.println("✅ Book borrowed! Due date: 14 days from today.");
        }
    }

    private static void returnBook() {
        System.out.println("\n── Return a Book ──");
        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());

        if (db.returnBook(memberId, bookId)) {
            System.out.println("✅ Book returned successfully!");
        }
    }

    // ─── REPORTS MENU ───────────────────────────────────────────────────────────

    private static void reportsMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║        REPORTS           ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Borrow History       ║");
            System.out.println("║  2. Overdue Books        ║");
            System.out.println("║  0. Back                 ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> db.viewBorrowHistory();
                case "2" -> db.viewOverdueBooks();
                case "0" -> { return; }
                default -> System.out.println("⚠️  Invalid option.");
            }
        }
    }
}