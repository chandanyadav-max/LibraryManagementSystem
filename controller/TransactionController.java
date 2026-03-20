package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Book;
import model.Member;
import model.Transaction;
import storage.DataStore;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class TransactionController {

    private static TransactionController instance;
    private final DataStore store = DataStore.getInstance();

    private TransactionController() {}

    public static TransactionController getInstance() {
        if (instance == null) instance = new TransactionController();
        return instance;
    }

    /**
     * Issues a book to a member. Returns null if book unavailable.
     */
    public Transaction issueBook(Member member, Book book, int librarianID) {
        if (!book.checkAvailability()) return null;

        int id = store.nextTransactionId();
        Transaction t = new Transaction(id, member.getMemberID(), book.getBookID(), librarianID);
        book.decrementQuantity();
        store.getTransactions().add(t);
        return t;
    }

    /**
     * Returns a book. Calculates fine if overdue.
     */
    public double returnBook(Transaction transaction) {
        transaction.closeLoan(LocalDate.now());

        // Restore book quantity
        Book book = findBook(transaction.getBookID());
        if (book != null) book.incrementQuantity();

        double fine = transaction.getFineAmount();
        if (fine > 0) {
            // Record fine
            int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS
                    .between(transaction.getDueDate(), LocalDate.now());
            model.Fine f = new model.Fine(
                    store.nextFineId(), transaction.getTransactionID(), daysOverdue, fine);
            store.getFines().add(f);
        }
        return fine;
    }

    public ObservableList<Transaction> getAll() {
        return store.getTransactions();
    }

    public ObservableList<Transaction> getActiveTransactions() {
        return FXCollections.observableArrayList(
                store.getTransactions().stream()
                        .filter(t -> !"Returned".equals(t.getStatus()))
                        .collect(Collectors.toList()));
    }

    public ObservableList<Transaction> getActiveForMember(int memberID) {
        return FXCollections.observableArrayList(
                store.getTransactions().stream()
                        .filter(t -> t.getMemberID() == memberID && !"Returned".equals(t.getStatus()))
                        .collect(Collectors.toList()));
    }

    public long countActive()  { return store.getTransactions().stream().filter(t -> !"Returned".equals(t.getStatus())).count(); }
    public long countOverdue() { return store.getTransactions().stream().filter(Transaction::isOverdue).count(); }

    public Book findBook(int bookID) {
        return store.getBooks().stream()
                .filter(b -> b.getBookID() == bookID).findFirst().orElse(null);
    }

    public Member findMember(int memberID) {
        return store.getMembers().stream()
                .filter(m -> m.getMemberID() == memberID).findFirst().orElse(null);
    }
}