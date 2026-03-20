package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Member extends User {

    private int       memberID;
    private String    phone;
    private String    address;
    private LocalDate registrationDate;
    private int       maxBooksAllowed;

    public Member(int userID, String name, String email, String password,
                  int memberID, String phone, String address) {
        super(userID, name, email, password);
        this.memberID         = memberID;
        this.phone            = phone;
        this.address          = address;
        this.registrationDate = LocalDate.now();
        this.maxBooksAllowed  = 5;
    }

    @Override
    public String getRole() { return "MEMBER"; }

    // Getters
    public int       getMemberID()        { return memberID; }
    public String    getPhone()           { return phone; }
    public String    getAddress()         { return address; }
    public LocalDate getRegistrationDate(){ return registrationDate; }
    public int       getMaxBooksAllowed() { return maxBooksAllowed; }

    public String getRegistrationDateFormatted() {
        return registrationDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    // Setters
    public void setPhone(String phone)     { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setRegistrationDate(LocalDate d) { this.registrationDate = d; }
}