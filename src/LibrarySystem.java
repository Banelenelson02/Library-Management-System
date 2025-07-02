import java.util.Scanner;
import java.time.LocalDate;

public class LibrarySystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static DatabaseManager dbManager;

    public static void main(String[] args) {
        dbManager = new DatabaseManager();
        if (!dbManager.connect()) {
            System.out.println("Failed to connect to database. Exiting...");
            return;
        }

        mainMenu();
        dbManager.close();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n==== Library Management System ====");
            System.out.println("1. Add Book");
            System.out.println("2. Register Member");
            System.out.println("3. Lend Book");
            System.out.println("4. Return Book");
            System.out.println("5. View Reports");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 6);
            switch (choice) {
                case 1: addBook(); break;
                case 2: registerMember(); break;
                case 3: lendBook(); break;
                case 4: returnBook(); break;
                case 5: viewReports(); break;
                case 6: return;
            }
        }
    }

    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        
        System.out.print("Publication Year: ");
        int year = getIntInput(1000, LocalDate.now().getYear());
        
        System.out.print("Quantity Available: ");
        int quantity = getIntInput(1, 100);

        Book book = new Book(title, author, isbn, year, quantity);
        dbManager.addBook(book);
        System.out.println("Book added successfully!");
    }

    private static void registerMember() {
        System.out.println("\n--- Register New Member ---");
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        Member member = new Member(name, email, phone);
        dbManager.registerMember(member);
        System.out.println("Member registered successfully! ID: " + member.getId());
    }

    private static void lendBook() {
        System.out.println("\n--- Lend Book ---");
        System.out.print("Member ID: ");
        int memberId = getIntInput(1, 9999);
        
        System.out.print("Book ID: ");
        int bookId = getIntInput(1, 9999);
        
        System.out.print("Days to return (1-30): ");
        int days = getIntInput(1, 30);

        if (dbManager.lendBook(bookId, memberId, days)) {
            System.out.println("Book lent successfully!");
        } else {
            System.out.println("Failed to lend book. Check availability or IDs.");
        }
    }

    private static void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.print("Borrow Record ID: ");
        int recordId = getIntInput(1, 9999);

        if (dbManager.returnBook(recordId)) {
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Failed to return book. Invalid record ID.");
        }
    }

    private static void viewReports() {
        while (true) {
            System.out.println("\n--- Reports Menu ---");
            System.out.println("1. List All Books");
            System.out.println("2. List Borrowed Books");
            System.out.println("3. List Overdue Books");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 4);
            switch (choice) {
                case 1: dbManager.listAllBooks(); break;
                case 2: dbManager.listBorrowedBooks(); break;
                case 3: dbManager.listOverdueBooks(); break;
                case 4: return;
            }
        }
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) return input;
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}