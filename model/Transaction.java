package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Transaction {

    private static final double FINE_PER_DAY = 0.50;
    private static final int    LOAN_DAYS    = 14;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private int       transactionID;
    private int       memberID;
    private int       bookID;
    private int       librarianID;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null until returned
    private String    status;       // Issued | Returned | Overdue
    private double    fineAmount;

    public Transaction(int transactionID, int memberID, int bookID, int librarianID) {
        this.transactionID = transactionID;
        this.memberID      = memberID;
        this.bookID        = bookID;
        this.librarianID   = librarianID;
        this.issueDate     = LocalDate.now();
        this.dueDate       = LocalDate.now().plusDays(LOAN_DAYS);
        this.status        = "Issued";
        this.fineAmount    = 0.0;
    }

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    public double calculateFine() {
        LocalDate base = returnDate != null ? returnDate : LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(dueDate, base);
        return overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0.0;
    }

    public void closeLoan(LocalDate returnDate) {
        this.returnDate  = returnDate;
        this.fineAmount  = calculateFine();
        this.status      = "Returned";
    }

    public String getDisplayStatus() {
        if ("Returned".equals(status)) return "Returned";
        return isOverdue() ? "Overdue" : "Issued";
    }

    // Getters
    public int       getTransactionID()   { return transactionID; }
    public int       getMemberID()        { return memberID; }
    public int       getBookID()          { return bookID; }
    public int       getLibrarianID()     { return librarianID; }
    public LocalDate getIssueDate()       { return issueDate; }
    public LocalDate getDueDate()         { return dueDate; }
    public LocalDate getReturnDate()      { return returnDate; }
    public String    getStatus()          { return status; }
    public double    getFineAmount()      { return fineAmount; }

    public String getIssueDateStr()  { return issueDate.format(FMT); }
    public String getDueDateStr()    { return dueDate.format(FMT); }
    public String getReturnDateStr() { return returnDate != null ? returnDate.format(FMT) : "—"; }

    // Setters (for DataStore seed data)
    public void setDueDate(LocalDate d)  { this.dueDate = d; }
    public void setIssueDate(LocalDate d){ this.issueDate = d; }
    public void setStatus(String s)      { this.status = s; }
}