package com.example.pankkiapplikaatio;

//Includes information of banks
public class Bank {
    private String name;
    private String BIC;

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

    @Override
    public String toString() {
        return name;
    }
}
