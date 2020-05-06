package com.example.pankkiapplikaatio;

//Includes information for the accounts
public class Account {
    public String[] subAccounts = {"Debit", "Credit", "Savings"};
    private String bank;
    private String username;
    private String accNum;
    private String type;
    private int balance;
    private int limit;
    private int interestRate;
    private int canPay;

    //Empty constructor for the BankDbHelper
    public Account() {}

    public Account(String ba, String u, String an, String t, int b, int l, int i, int cp) {
        bank = ba;
        username = u;
        accNum = an;
        type = t;
        balance = b;
        limit = l;
        interestRate = i;
        canPay = cp;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public int getCanPay() {
        return canPay;
    }

    public void setCanPay(int canPay) {
        this.canPay = canPay;
    }
}
