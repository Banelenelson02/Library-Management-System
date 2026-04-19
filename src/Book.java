package src;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int quantity;
    private int availableQuantity;

    public Book(int id, String isbn, String title, String author, String genre, int quantity, int availableQuantity) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    public Book(String isbn, String title, String author, String genre, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.availableQuantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getQuantity() { return quantity; }
    public int getAvailableQuantity() { return availableQuantity; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    @Override
    public String toString() {
        return String.format("| %-4d | %-15s | %-30s | %-20s | %-15s | %-8d | %-9d |",
                id, isbn, title, author, genre, quantity, availableQuantity);
    }
}