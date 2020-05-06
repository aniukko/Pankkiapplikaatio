package com.example.pankkiapplikaatio;

//Includes information of transactions
public class Transactions {
    private String fromAcc;
    private String fromBank;
    private String fromBic;
    private String toAcc;
    private String toBank;
    private String toBic;
    private int amount;
    private String time;
    private String description;

    //Empty constructor for the BankDbHelper
    public Transactions(){}

    public Transactions(String fromAcc, String fromBank, String fromBic, String toAcc,
                        String toBank, String toBic, int amount, String time, String description) {
        this.fromAcc = fromAcc;
        this.fromBank = fromBank;
        this.fromBic = fromBic;
        this.toAcc = toAcc;
        this.toBank = toBank;
        this.toBic = toBic;
        this.amount = amount;
        this.time = time;
        this.description = description;
    }

    public String getFromAcc() {
        return fromAcc;
    }

    public void setFromAcc(String fromAcc) {
        this.fromAcc = fromAcc;
    }

    public String getFromBank() {
        return fromBank;
    }

    public void setFromBank(String fromBank) {
        this.fromBank = fromBank;
    }

    public String getFromBic() {
        return fromBic;
    }

    public void setFromBic(String fromBic) {
        this.fromBic = fromBic;
    }

    public String getToAcc() {
        return toAcc;
    }

    public void setToAcc(String toAcc) {
        this.toAcc = toAcc;
    }

    public String getToBank() {
        return toBank;
    }

    public void setToBank(String toBank) {
        this.toBank = toBank;
    }

    public String getToBic() {
        return toBic;
    }

    public void setToBic(String toBic) {
        this.toBic = toBic;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
