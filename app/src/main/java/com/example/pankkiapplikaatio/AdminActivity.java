package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//For logging in as an admin
public class AdminActivity extends AppCompatActivity {

    ListView userList;
    ListView accountList;
    ListView cardList;
    ListView transactionList;
    private BankDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getWindow().setTitle("Admin");

        //Getting the access to the file that handles the database
        db = new BankDbHelper(this);

        userList = findViewById(R.id.listUsers);
        accountList = findViewById(R.id.listAccounts);
        cardList = findViewById(R.id.listCards);
        transactionList = findViewById(R.id.listTransactions);

        usersList();
        accountsList();
        cardsList();
        transactionsList();
    }

    //Adds all the users in the database to a list and sets the list to a listview
    //Depending on the user selected, opens the UserSettingsActivity for that user
    private void usersList() {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < db.getAllUsers().size(); i++) {
            users.add(db.getAllUsers().get(i).getUsername());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminActivity.this,
                android.R.layout.simple_list_item_1, users);
        userList.setAdapter(adapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user = userList.getItemAtPosition(position).toString();
                Intent intentUser = new Intent(AdminActivity.this, UserSettingsActivity.class);
                intentUser.putExtra("loguser", user);

                //Letting the other activity know that an admin wants to access the activity
                intentUser.putExtra("admin", "admin");
                startActivity(intentUser);
            }
        });
    }

    //Adds all the accounts in the database to a list and sets the list to a listview
    //Depending on the account selected, opens the ShowAccountActivity for that account
    private void accountsList() {
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < db.getAllAccounts().size(); i++) {
            accounts.add(db.getAllAccounts().get(i).getAccNum());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminActivity.this,
                android.R.layout.simple_list_item_1, accounts);
        accountList.setAdapter(adapter);
        accountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String acc = accountList.getItemAtPosition(position).toString();
                Intent intent = new Intent(getApplicationContext(), ShowAccountActivity.class);
                intent.putExtra("accNum", acc);

                //Letting the other activity know that an admin wants to access the activity
                intent.putExtra("admin", "admin");
                startActivity(intent);
            }
        });
    }

    //Adds all the cards in the database to a list and sets the list to a listview
    //Depending on the card selected, opens the ShowCardActivity for that card
    private void cardsList() {
        List<String> cards = new ArrayList<>();
        for (int i = 0; i < db.getAllCards().size(); i++) {
            cards.add("Card for account " + db.getAllCards().get(i).getAccNum());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminActivity.this,
                android.R.layout.simple_list_item_1, cards);
        cardList.setAdapter(adapter);
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cardAcc = cardList.getItemAtPosition(position).toString();
                String[] cardAccNum = cardAcc.split(" ");
                Intent intent = new Intent(getApplicationContext(), ShowCardActivity.class);
                intent.putExtra("accNum", cardAccNum[3]);

                //Letting the other activity know that an admin wants to access the activity
                intent.putExtra("admin", "admin");
                startActivity(intent);
            }
        });
    }

    //Adds all the transactions in the database to a list and sets the list to a listview
    //Admin can see all the transactions and their descriptions
    private void transactionsList() {
        List<String> transactions = new ArrayList<>();
        for (int i = 0; i < db.getAllTransactions().size(); i++) {
            transactions.add(db.getAllTransactions().get(i).getDescription());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminActivity.this,
                android.R.layout.simple_list_item_1, transactions);
        transactionList.setAdapter(adapter);
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    //If the back button is pressed, an alertdialog will appear asking if the user wants to log out
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out of the admin user?");
        builder.setCancelable(true);
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdminActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
