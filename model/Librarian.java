package model;

public class Librarian extends User {

    private int    librarianID;
    private String employeeID;

    public Librarian(int userID, String name, String username, String password,
                     int librarianID, String employeeID) {
        super(userID, name, username, password);
        this.librarianID = librarianID;
        this.employeeID  = employeeID;
    }

    @Override
    public String getRole() { return "LIBRARIAN"; }

    public int    getLibrarianID() { return librarianID; }
    public String getEmployeeID()  { return employeeID; }
}