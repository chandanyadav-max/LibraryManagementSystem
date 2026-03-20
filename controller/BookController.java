package controller;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import model.Book;
import storage.DataStore;

public class BookController {

    private static BookController instance;
    private final DataStore store = DataStore.getInstance();

    private BookController() {}

    public static BookController getInstance() {
        if (instance == null) instance = new BookController();
        return instance;
    }

    public ObservableList<Book> getAllBooks() {
        return store.getBooks();
    }

    public void addBook(Book book) {
        store.getBooks().add(book);
    }

    public void deleteBook(Book book) {
        store.getBooks().remove(book);
    }

    // Update is done directly on the object; the ObservableList notifies the table
    // but we call refresh() on the TableView since plain POJOs don't fire change events.
    public void updateBook(Book book, String isbn, String title, String author,
                           String publisher, String category, int quantity) {
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setQuantity(quantity);
    }

    public FilteredList<Book> getAvailableBooks() {
        return new FilteredList<>(store.getBooks(), b -> b.getQuantity() > 0);
    }

    public int nextId() {
        return store.nextBookId();
    }

    public long countTotal()     { return store.getBooks().size(); }
    public long countAvailable() { return store.getBooks().stream().filter(b -> b.getQuantity() > 0).count(); }
}