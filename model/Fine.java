package model;

public class Fine {

    private int     fineID;
    private int     transactionID;
    private int     daysOverdue;
    private double  amount;
    private boolean paid;

    public Fine(int fineID, int transactionID, int daysOverdue, double amount) {
        this.fineID        = fineID;
        this.transactionID = transactionID;
        this.daysOverdue   = daysOverdue;
        this.amount        = amount;
        this.paid          = false;
    }

    public void markPaid() { this.paid = true; }

    public int    getFineID()        { return fineID; }
    public int    getTransactionID() { return transactionID; }
    public int    getDaysOverdue()   { return daysOverdue; }
    public double getAmount()        { return amount; }
    public boolean isPaid()          { return paid; }
    public String getPaidStatus()    { return paid ? "Paid" : "Unpaid"; }
}