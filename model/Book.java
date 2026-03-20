package model;

public class Book {

    private int    bookID;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private int    quantity;
    private String status;   // Available | Issued | Reserved

    public Book(int bookID, String isbn, String title, String author,
                String publisher, String category, int quantity) {
        this.bookID    = bookID;
        this.isbn      = isbn;
        this.title     = title;
        this.author    = author;
        this.publisher = publisher;
        this.category  = category;
        this.quantity  = quantity;
        this.status    = quantity > 0 ? "Available" : "Issued";
    }

    public boolean checkAvailability() {
        return quantity > 0;
    }

    // Called by TransactionController on issue/return
    public void decrementQuantity() {
        if (quantity > 0) quantity--;
        if (quantity == 0) status = "Issued";
    }

    public void incrementQuantity() {
        quantity++;
        status = "Available";
    }

    // Getters
    public int    getBookID()    { return bookID; }
    public String getIsbn()      { return isbn; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public String getPublisher() { return publisher; }
    public String getCategory()  { return category; }
    public int    getQuantity()  { return quantity; }
    public String getStatus()    { return status; }

    // Setters
    public void setIsbn(String isbn)           { this.isbn = isbn; }
    public void setTitle(String title)         { this.title = title; }
    public void setAuthor(String author)       { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setCategory(String category)   { this.category = category; }
    public void setQuantity(int quantity)      { this.quantity = quantity; updateStatus(); }
    public void setStatus(String status)       { this.status = status; }

    private void updateStatus() {
        if (quantity == 0) status = "Issued";
        else status = "Available";
    }

    @Override
    public String toString() { return title + " — " + author; }
}