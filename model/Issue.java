package model;

import java.sql.Timestamp;

public class Issue {
    private int issueId;
    private String bookTitle;
    private String memberName;
    private Timestamp issueDate;
    private Timestamp returnDate;

    public Issue(int issueId, String bookTitle, String memberName, Timestamp issueDate, Timestamp returnDate) {
        this.issueId = issueId;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    // getters and setters
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public Timestamp getIssueDate() { return issueDate; }
    public void setIssueDate(Timestamp issueDate) { this.issueDate = issueDate; }
    public Timestamp getReturnDate() { return returnDate; }
    public void setReturnDate(Timestamp returnDate) { this.returnDate = returnDate; }
}