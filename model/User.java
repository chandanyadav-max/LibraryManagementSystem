package model;

public abstract class User {

    private int    userID;
    private String name;
    private String email;
    private String password;

    public User(int userID, String name, String email, String password) {
        this.userID   = userID;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // Abstract – subclasses define their role
    public abstract String getRole();

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void updateProfile(String name, String email) {
        this.name  = name;
        this.email = email;
    }

    // Getters
    public int    getUserID()   { return userID; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() { return name + " (" + getRole() + ")"; }
}