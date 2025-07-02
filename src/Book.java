public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private int quantityAvailable;

    public Book(String title, String author, String isbn, int publicationYear, int quantityAvailable) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.quantityAvailable = quantityAvailable;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getPublicationYear() { return publicationYear; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { 
        this.quantityAvailable = quantityAvailable; 
    }
}