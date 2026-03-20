package storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import java.time.LocalDate;

/**
 * Singleton in-memory data store.
 * ObservableLists allow JavaFX TableViews to auto-refresh on data changes.
 */
public class DataStore {

    private static DataStore instance;

    private final ObservableList<Book>        books        = FXCollections.observableArrayList();
    private final ObservableList<Member>      members      = FXCollections.observableArrayList();
    private final ObservableList<Librarian>   librarians   = FXCollections.observableArrayList();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final ObservableList<Fine>        fines        = FXCollections.observableArrayList();

    private int bookCounter        = 6;
    private int memberCounter      = 4;
    private int transactionCounter = 3;
    private int fineCounter        = 1;

    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ── Seed Data ─────────────────────────────────────────────────────────
    private void seedData() {
        // Librarians (login: admin / admin123)
        librarians.add(new Librarian(1, "Admin User", "admin", "admin123", 1, "EMP001"));

        // Books
        books.add(new Book(1, "978-0134685991", "Effective Java",      "Joshua Bloch",    "Addison-Wesley", "Programming",     3));
        books.add(new Book(2, "978-0132350884", "Clean Code",          "Robert C. Martin","Prentice Hall",  "Programming",     2));
        books.add(new Book(3, "978-0201633610", "Design Patterns",     "Gang of Four",    "Addison-Wesley", "Software Engineering", 2));
        books.add(new Book(4, "978-1491910771", "Learning Java",       "Patrick Niemeyer","O'Reilly",       "Programming",     4));
        books.add(new Book(5, "978-0134494166", "Clean Architecture",  "Robert C. Martin","Prentice Hall",  "Software Engineering", 1));

        // Members
        members.add(new Member(2, "Alice Johnson",  "alice@email.com", "pass123", 1, "555-0101", "10 High Street"));
        members.add(new Member(3, "Bob Williams",   "bob@email.com",   "pass456", 2, "555-0102", "22 Oak Avenue"));
        members.add(new Member(4, "Carol Martinez", "carol@email.com", "pass789", 3, "555-0103", "5 Elm Road"));

        // Sample transactions
        // Alice has "Design Patterns" (book 3) – still active
        Transaction t1 = new Transaction(1, 1, 3, 1);
        t1.setIssueDate(LocalDate.now().minusDays(5));
        t1.setDueDate(LocalDate.now().plusDays(9));
        books.get(2).decrementQuantity(); // Design Patterns qty: 2→1
        transactions.add(t1);

        // Bob has "Clean Code" (book 2) – OVERDUE
        Transaction t2 = new Transaction(2, 2, 2, 1);
        t2.setIssueDate(LocalDate.now().minusDays(20));
        t2.setDueDate(LocalDate.now().minusDays(6));
        books.get(1).decrementQuantity(); // Clean Code qty: 2→1
        transactions.add(t2);
    }

    // ── ID generators ─────────────────────────────────────────────────────
    public int nextBookId()        { return bookCounter++; }
    public int nextMemberId()      { return memberCounter++; }
    public int nextTransactionId() { return transactionCounter++; }
    public int nextFineId()        { return fineCounter++; }

    // ── Accessors ─────────────────────────────────────────────────────────
    public ObservableList<Book>        getBooks()        { return books; }
    public ObservableList<Member>      getMembers()      { return members; }
    public ObservableList<Librarian>   getLibrarians()   { return librarians; }
    public ObservableList<Transaction> getTransactions() { return transactions; }
    public ObservableList<Fine>        getFines()        { return fines; }
}