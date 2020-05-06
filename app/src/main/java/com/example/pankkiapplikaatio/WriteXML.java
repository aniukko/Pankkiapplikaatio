package com.example.pankkiapplikaatio;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

//For writing XML files
public class WriteXML {

    //Creates the file by taking a string that is the name of the file
    private static void writeToFile(Context context, String fileName, String str) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(str.getBytes(), 0, str.length());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Writes an XML-file for banks
    public void writeBanks(Context context, BankDbHelper db) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "banks");
            for (Bank bank : db.getAllBanks()) {
                serializer.startTag("", "bank");
                serializer.attribute("", "name", bank.getName());
                serializer.startTag("", "bic");
                serializer.text(bank.getBIC());
                serializer.endTag("", "bic");
                serializer.endTag("", "bank");
            }
            serializer.endTag("", "banks");
            serializer.endDocument();
            String result = writer.toString();
            //Deletes the file if it already exists and creates it again
            context.deleteFile("banks.xml");
            writeToFile(context, "banks.xml", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Writes an XML-file for users
    public void writeUsers(Context context, BankDbHelper db) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "users");
            for (Users user : db.getAllUsers()) {
                serializer.startTag("", "user");
                serializer.attribute("", "username", user.getUsername());
                serializer.startTag("", "password");
                serializer.text(user.getPassword());
                serializer.endTag("", "password");
                serializer.startTag("", "name");
                serializer.text(user.getName());
                serializer.endTag("", "name");
                serializer.startTag("", "address");
                serializer.text(user.getAddress());
                serializer.endTag("", "address");
                serializer.startTag("", "phone_number");
                serializer.text(user.getPhoneNum());
                serializer.endTag("", "phone_number");
                serializer.endTag("", "user");
            }
            serializer.endTag("", "users");
            serializer.endDocument();
            String result = writer.toString();
            //Deletes the file if it already exists and creates it again
            context.deleteFile("users.xml");
            writeToFile(context, "users.xml", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Writes an XML-file for accounts
    public void writeAccounts(Context context, BankDbHelper db) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "accounts");
            for (Account account : db.getAllAccounts()) {
                serializer.startTag("", "account");
                serializer.attribute("", "bank", account.getBank());
                serializer.startTag("", "username");
                serializer.text(account.getUsername());
                serializer.endTag("", "username");
                serializer.startTag("", "account_number");
                serializer.text(account.getAccNum());
                serializer.endTag("", "account_number");
                serializer.startTag("", "type");
                serializer.text(account.getType());
                serializer.endTag("", "type");
                serializer.startTag("", "balance");
                serializer.text(String.valueOf(account.getBalance()));
                serializer.endTag("", "balance");
                serializer.startTag("", "credit_limit");
                serializer.text(String.valueOf(account.getLimit()));
                serializer.endTag("", "credit_limit");
                serializer.startTag("", "interest_rate");
                serializer.text(String.valueOf(account.getInterestRate()));
                serializer.endTag("", "interest_rate");
                serializer.startTag("", "allow_payments");
                serializer.text(String.valueOf(account.getCanPay()));
                serializer.endTag("", "allow_payments");
                serializer.endTag("", "account");
            }
            serializer.endTag("", "accounts");
            serializer.endDocument();
            String result = writer.toString();
            //Deletes the file if it already exists and creates it again
            context.deleteFile("accounts.xml");
            writeToFile(context, "accounts.xml", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Writes an XML-file for cards
    public void writeCards(Context context, BankDbHelper db) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "cards");
            for (Card card : db.getAllCards()) {
                serializer.startTag("", "card");
                serializer.attribute("", "username", card.getUsername());
                serializer.startTag("", "account_number");
                serializer.text(card.getAccNum());
                serializer.endTag("", "account_number");
                serializer.startTag("", "withdraw_limit");
                serializer.text(String.valueOf(card.getWithdrawLimit()));
                serializer.endTag("", "withdraw_limit");
                serializer.startTag("", "pay_limit");
                serializer.text(String.valueOf(card.getPayLimit()));
                serializer.endTag("", "pay_limit");
                serializer.startTag("", "area");
                serializer.text(card.getArea());
                serializer.endTag("", "area");
                serializer.endTag("", "card");
            }
            serializer.endTag("", "cards");
            serializer.endDocument();
            String result = writer.toString();
            //Deletes the file if it already exists and creates it again
            context.deleteFile("cards.xml");
            writeToFile(context, "cards.xml", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Writes an XML-file for transactions
    public void writeTransactions(Context context, BankDbHelper db) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "transactions");
            for (Transactions transaction : db.getAllTransactions()) {
                serializer.startTag("", "transaction");
                serializer.attribute("", "account_from", transaction.getFromAcc());
                serializer.startTag("", "bank_from");
                serializer.text(transaction.getFromBank());
                serializer.endTag("", "bank_from");
                serializer.startTag("", "bic_from");
                serializer.text(transaction.getFromBic());
                serializer.endTag("", "bic_from");
                serializer.startTag("", "account_to");
                serializer.text(transaction.getToAcc());
                serializer.endTag("", "account_to");
                serializer.startTag("", "bank_to");
                serializer.text(transaction.getToBank());
                serializer.endTag("", "bank_to");
                serializer.startTag("", "bic_to");
                serializer.text(transaction.getToBic());
                serializer.endTag("", "bic_to");
                serializer.startTag("", "amount");
                serializer.text(String.valueOf(transaction.getAmount()));
                serializer.endTag("", "amount");
                serializer.startTag("", "time_of_transaction");
                serializer.text(transaction.getTime());
                serializer.endTag("", "time_of_transaction");
                serializer.startTag("", "description");
                serializer.text(transaction.getDescription());
                serializer.endTag("", "description");
                serializer.endTag("", "transaction");
            }
            serializer.endTag("", "transactions");
            serializer.endDocument();
            String result = writer.toString();
            //Deletes the file if it already exists and creates it again
            context.deleteFile("transactions.xml");
            writeToFile(context, "transactions.xml", result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
