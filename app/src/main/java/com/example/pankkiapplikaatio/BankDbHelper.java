package com.example.pankkiapplikaatio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.pankkiapplikaatio.BankContract.AccountTable;
import com.example.pankkiapplikaatio.BankContract.BankTable;
import com.example.pankkiapplikaatio.BankContract.CardTable;
import com.example.pankkiapplikaatio.BankContract.TransactionTable;
import com.example.pankkiapplikaatio.BankContract.UserTable;

import java.util.ArrayList;
import java.util.List;

//Importing the BankContract file, so that the lines about getting information from the tables are shorter

//For handling the database
public class BankDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BankDb.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db = this.getWritableDatabase();

    public BankDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates SQL tables when the app is run the first time (on creation)
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_BANKS_TABLE = "CREATE TABLE " +
                BankTable.TABLE_NAME + "(" +
                BankTable.COLUMN_NAME + " TEXT PRIMARY KEY NOT NULL, " +
                BankTable.COLUMN_BIC + " TEXT NOT NULL" +
                ")";
        db.execSQL(SQL_CREATE_BANKS_TABLE);
        fillBanksTable();

        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " +
                UserTable.TABLE_NAME + "(" +
                UserTable.COLUMN_USERNAME + " TEXT PRIMARY KEY NOT NULL, " +
                UserTable.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                UserTable.COLUMN_NAME + " TEXT, " +
                UserTable.COLUMN_ADDRESS + " TEXT, " +
                UserTable.COLUMN_PHONE + " TEXT" +
                ")";
        db.execSQL(SQL_CREATE_USERS_TABLE);

        final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " +
                AccountTable.TABLE_NAME + "(" +
                AccountTable.COLUMN_ACCOUNT_NUMBER + " TEXT PRIMARY KEY NOT NULL, " +
                AccountTable.COLUMN_BANK + " TEXT, " +
                AccountTable.COLUMN_USERNAME + " TEXT NOT NULL, " +
                AccountTable.COLUMN_TYPE + " TEXT, " +
                AccountTable.COLUMN_BALANCE + " INTEGER, " +
                AccountTable.COLUMN_LIMIT + " INTEGER, " +
                AccountTable.COLUMN_INTEREST + " INTEGER, " +
                "FOREIGN KEY (" + AccountTable.COLUMN_USERNAME + ") REFERENCES " +
                UserTable.TABLE_NAME + " (" + UserTable.COLUMN_USERNAME + "), " +
                "FOREIGN KEY (" + AccountTable.COLUMN_BANK + ") REFERENCES " +
                BankTable.TABLE_NAME + " (" + BankTable.COLUMN_NAME + ")" +
                ")";
        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);

        final String SQL_CREATE_CARD_TABLE = "CREATE TABLE " +
                CardTable.TABLE_NAME + "(" +
                CardTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardTable.COLUMN_USERNAME + " TEXT, " +
                CardTable.COLUMN_ACCOUNT_NUMBER + " TEXT NOT NULL, " +
                CardTable.COLUMN_WITHDRAW_LIMIT + " INTEGER, " +
                CardTable.COLUMN_PAY_LIMIT + " INTEGER, " +
                CardTable.COLUMN_AREA + " TEXT, " +
                "FOREIGN KEY (" + CardTable.COLUMN_ACCOUNT_NUMBER + ") REFERENCES " +
                AccountTable.TABLE_NAME + " (" + AccountTable.COLUMN_ACCOUNT_NUMBER + ")" +
                ")";
        db.execSQL(SQL_CREATE_CARD_TABLE);

        final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " +
                TransactionTable.TABLE_NAME + "(" +
                TransactionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TransactionTable.COLUMN_FROM_ACC + " TEXT, " +
                TransactionTable.COLUMN_FROM_BANK + " TEXT, " +
                TransactionTable.COLUMN_FROM_BIC + " TEXT, " +
                TransactionTable.COLUMN_TO_ACC + " TEXT, " +
                TransactionTable.COLUMN_TO_BANK + " TEXT, " +
                TransactionTable.COLUMN_TO_BIC + " TEXT, " +
                TransactionTable.COLUMN_AMOUNT + " INTEGER, " +
                TransactionTable.COLUMN_TIME + " TEXT, " +
                TransactionTable.COLUMN_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    //For editing the tables without deleting the app from the emulator
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BankTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AccountTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CardTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillBanksTable() {
        Bank bankList[] = {new Bank("Nordea", "NDEAFIHH"),
                new Bank("Osuuspankki", "OKOYFIHH"),
                new Bank("S-Pankki", "SBANFIHH"),
                new Bank("Danske Bank", "DABAFIHH")
        };

        Bank b1 = bankList[0];
        addBank(b1);
        Bank b2 = bankList[1];
        addBank(b2);
        Bank b3 = bankList[2];
        addBank(b3);
        Bank b4 = bankList[3];
        addBank(b4);
    }

    private void addBank(Bank bank) {
        ContentValues cv = new ContentValues();
        cv.put(BankTable.COLUMN_NAME, bank.getName());
        cv.put(BankTable.COLUMN_BIC, bank.getBIC());
        db.insert(BankTable.TABLE_NAME, null, cv);
    }

    public void addUsers(String username, String password, String name, String address, String phoneNumber) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.COLUMN_USERNAME, username);
        cv.put(UserTable.COLUMN_PASSWORD, password);
        cv.put(UserTable.COLUMN_NAME, name);
        cv.put(UserTable.COLUMN_ADDRESS, address);
        cv.put(UserTable.COLUMN_PHONE, phoneNumber);
        db.insert(UserTable.TABLE_NAME, null, cv);
    }

    public void addAccount(String accNum, String bank, String username, String type, int balance, int limit, int interest) {
        ContentValues cv = new ContentValues();
        cv.put(AccountTable.COLUMN_ACCOUNT_NUMBER, accNum);
        cv.put(AccountTable.COLUMN_BANK, bank);
        cv.put(AccountTable.COLUMN_USERNAME, username);
        cv.put(AccountTable.COLUMN_TYPE, type);
        cv.put(AccountTable.COLUMN_BALANCE, balance);
        cv.put(AccountTable.COLUMN_LIMIT, limit);
        cv.put(AccountTable.COLUMN_INTEREST, interest);
        db.insert(AccountTable.TABLE_NAME, null, cv);
    }

    public void addCard(String user, String accNum, int withdraw, int pay, String area) {
        ContentValues cv = new ContentValues();
        cv.put(CardTable.COLUMN_USERNAME, user);
        cv.put(CardTable.COLUMN_ACCOUNT_NUMBER, accNum);
        cv.put(CardTable.COLUMN_WITHDRAW_LIMIT, withdraw);
        cv.put(CardTable.COLUMN_PAY_LIMIT, pay);
        cv.put(CardTable.COLUMN_AREA, area);
        db.insert(CardTable.TABLE_NAME, null, cv);
    }

    public void addTransaction(String fromAcc, String fromBank, String fromBic, String toAcc,
                               String toBank, String toBic, int amount, String time, String description) {
        ContentValues cv = new ContentValues();
        cv.put(TransactionTable.COLUMN_FROM_ACC, fromAcc);
        cv.put(TransactionTable.COLUMN_FROM_BANK, fromBank);
        cv.put(TransactionTable.COLUMN_FROM_BIC, fromBic);
        cv.put(TransactionTable.COLUMN_TO_ACC, toAcc);
        cv.put(TransactionTable.COLUMN_TO_BANK, toBank);
        cv.put(TransactionTable.COLUMN_TO_BIC, toBic);
        cv.put(TransactionTable.COLUMN_AMOUNT, amount);
        cv.put(TransactionTable.COLUMN_TIME, time);
        cv.put(TransactionTable.COLUMN_DESCRIPTION, description);
        db.insert(TransactionTable.TABLE_NAME, null, cv);
    }

    public void updateUsers(String user, String name, String address, String phoneNumber) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("address", address);
        cv.put("phone_number", phoneNumber);
        db.update(UserTable.TABLE_NAME, cv, "username = ?", new String[]{user});
    }

    public void updateAccount(String accNum, int balance, int limit, int interest) {
        ContentValues cv = new ContentValues();
        cv.put("balance", balance);
        cv.put("credit_limit", limit);
        cv.put("interest_rate", interest);
        db.update(AccountTable.TABLE_NAME, cv, "account_number = ?", new String[]{accNum});
    }

    public void updateCard(String accNum, int withdrawLimit, int payLimit, String area) {
        ContentValues cv = new ContentValues();
        cv.put("withdraw_limit", withdrawLimit);
        cv.put("pay_limit", payLimit);
        cv.put("area", area);
        db.update(CardTable.TABLE_NAME, cv, "account_number = ?", new String[]{accNum});
    }

    public void deleteUser(String username) {
        db.delete(UserTable.TABLE_NAME, "username = ?", new String[]{username});
    }

    public void deleteAccount(String accNum) {
        db.delete(AccountTable.TABLE_NAME, "account_number = ?", new String[]{accNum});
    }

    public void deleteCard(String accNum) {
        db.delete(CardTable.TABLE_NAME, "account_number = ?", new String[]{accNum});
    }

    //For getting information from the database, adding it to a list and returning the list
    public List<Bank> getAllBanks() {
        List<Bank> bankList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + BankTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Bank bank = new Bank();
                bank.setName(c.getString(c.getColumnIndex(BankTable.COLUMN_NAME)));
                bank.setBIC(c.getString(c.getColumnIndex(BankTable.COLUMN_BIC)));
                bankList.add(bank);
            } while (c.moveToNext());
        }
        c.close();
        return bankList;
    }

    //For getting information from the database, adding it to a list and returning the list
    public List<Users> getAllUsers() {
        List<Users> userList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + UserTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Users users = new Users();
                users.setUsername(c.getString(c.getColumnIndex(UserTable.COLUMN_USERNAME)));
                users.setPassword(c.getString(c.getColumnIndex(UserTable.COLUMN_PASSWORD)));
                users.setName(c.getString(c.getColumnIndex(UserTable.COLUMN_NAME)));
                users.setAddress(c.getString(c.getColumnIndex(UserTable.COLUMN_ADDRESS)));
                users.setPhoneNum(c.getString(c.getColumnIndex(UserTable.COLUMN_PHONE)));
                userList.add(users);
            } while (c.moveToNext());
        }
        c.close();
        return userList;
    }

    //For getting information from the database, adding it to a list and returning the list
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + AccountTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Account account = new Account();
                account.setBank(c.getString(c.getColumnIndex(AccountTable.COLUMN_BANK)));
                account.setUsername(c.getString(c.getColumnIndex(AccountTable.COLUMN_USERNAME)));
                account.setAccNum(c.getString(c.getColumnIndex(AccountTable.COLUMN_ACCOUNT_NUMBER)));
                account.setType(c.getString(c.getColumnIndex(AccountTable.COLUMN_TYPE)));
                account.setBalance(c.getInt(c.getColumnIndex(AccountTable.COLUMN_BALANCE)));
                account.setLimit(c.getInt(c.getColumnIndex(AccountTable.COLUMN_LIMIT)));
                account.setInterestRate(c.getInt(c.getColumnIndex(AccountTable.COLUMN_INTEREST)));
                accountList.add(account);
            } while (c.moveToNext());
        }
        c.close();
        return accountList;
    }

    //For getting information from the database, adding it to a list and returning the list
    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CardTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Card card = new Card();
                card.setUsername(c.getString(c.getColumnIndex(CardTable.COLUMN_USERNAME)));
                card.setAccNum(c.getString(c.getColumnIndex(CardTable.COLUMN_ACCOUNT_NUMBER)));
                card.setPayLimit(c.getInt(c.getColumnIndex(CardTable.COLUMN_PAY_LIMIT)));
                card.setWithdrawLimit(c.getInt(c.getColumnIndex(CardTable.COLUMN_WITHDRAW_LIMIT)));
                card.setArea(c.getString(c.getColumnIndex(CardTable.COLUMN_AREA)));
                cardList.add(card);
            } while (c.moveToNext());
        }
        c.close();
        return cardList;
    }

    public List<Transactions> getAllTransactions() {
        List<Transactions> transactionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TransactionTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Transactions transactions = new Transactions();
                transactions.setFromAcc(c.getString(c.getColumnIndex(TransactionTable.COLUMN_FROM_ACC)));
                transactions.setFromBank(c.getString(c.getColumnIndex(TransactionTable.COLUMN_FROM_BANK)));
                transactions.setFromBic(c.getString(c.getColumnIndex(TransactionTable.COLUMN_FROM_BIC)));
                transactions.setToAcc(c.getString(c.getColumnIndex(TransactionTable.COLUMN_TO_ACC)));
                transactions.setToBank(c.getString(c.getColumnIndex(TransactionTable.COLUMN_TO_BANK)));
                transactions.setToBic(c.getString(c.getColumnIndex(TransactionTable.COLUMN_TO_BIC)));
                transactions.setAmount(c.getInt(c.getColumnIndex(TransactionTable.COLUMN_AMOUNT)));
                transactions.setTime(c.getString(c.getColumnIndex(TransactionTable.COLUMN_TIME)));
                transactions.setDescription(c.getString(c.getColumnIndex(TransactionTable.COLUMN_DESCRIPTION)));
                transactionList.add(transactions);
            } while (c.moveToNext());
        }
        c.close();
        return transactionList;
    }
}
