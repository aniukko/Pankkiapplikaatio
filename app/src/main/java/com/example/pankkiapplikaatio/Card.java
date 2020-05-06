package com.example.pankkiapplikaatio;

//Includes information of cards
public class Card {
    private String username;
    private String accNum;
    private int withdrawLimit;
    private int payLimit;
    private String area;

    //Empty constructor for the BankDbHelper
    public Card() {}

    public Card(String un, String an, int wl, int pl, String a) {
        username = un;
        accNum = an;
        withdrawLimit = wl;
        payLimit = pl;
        area = a;
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

    public int getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(int withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public int getPayLimit() {
        return payLimit;
    }

    public void setPayLimit(int payLimit) {
        this.payLimit = payLimit;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
