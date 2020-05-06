package com.example.pankkiapplikaatio;

//Includes information of banks
public class Bank {
    private String name;
    private String BIC;

    //Empty constructor for the BankDbHelper
    public Bank() {}

    public Bank(String n, String b) {
        name = n;
        BIC = b;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBIC() {
        return BIC;
    }

    public void setBIC(String BIC) {
        this.BIC = BIC;
    }

    //When adding the object to a spinner this method makes sure that you see only the
    // name of the Bank-object in the spinner
    @Override
    public String toString() {
        return name;
    }
}
