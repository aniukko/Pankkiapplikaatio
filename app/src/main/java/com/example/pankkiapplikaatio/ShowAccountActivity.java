package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;

//For showing information on the accounts and editing them
public class ShowAccountActivity extends AppCompatActivity {

    String logUser;
    String accNum;
    String admin;

    TextView header;
    TextView textBank;
    TextView textType;
    TextView textBalance;
    TextView textLimit;
    TextView textInterest;
    TextInputLayout changeBalance;
    TextInputLayout changeLimit;
    TextInputLayout changeInterest;
    Button save;

    Button delete;
    Switch changeCanPay;

    String bank;
    String type;
    int balance;
    int limit;
    int interest;
    int canPay;

    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    WriteXML writeXML = new WriteXML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);
        getWindow().setTitle("My Account");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        //Getting information from other activities
        logUser = getIntent().getStringExtra("loguser");
        admin = getIntent().getStringExtra("admin");
        accNum = getIntent().getStringExtra("accNum");
        header = findViewById(R.id.header2);
        header.setText("Chosen account:\n" + accNum);

        textBank = findViewById(R.id.textBank2);
        textType = findViewById(R.id.textType2);
        textBalance = findViewById(R.id.textBalance2);
        textLimit = findViewById(R.id.textLimit2);
        textInterest = findViewById(R.id.textInterest2);
        changeBalance = findViewById(R.id.addMoney2);
        changeLimit = findViewById(R.id.changeLimit);
        changeInterest = findViewById(R.id.changeInterest);
        save = findViewById(R.id.save2);

        delete = findViewById(R.id.buttonDelete2);
        changeCanPay = findViewById(R.id.changeCanPay);

        accountInfo();
        changeInfo();
        deleteAccount();
    }

    //Showing information on the account that has been selected from the previous activity
    private void accountInfo() {
        for (int i = 0; i < db.getAllAccounts().size(); i++) {
            if (db.getAllAccounts().get(i).getAccNum().equals(accNum)) {
                bank = db.getAllAccounts().get(i).getBank();
                type = db.getAllAccounts().get(i).getType();
                balance = db.getAllAccounts().get(i).getBalance();
                limit = db.getAllAccounts().get(i).getLimit();
                interest = db.getAllAccounts().get(i).getInterestRate();
                canPay = db.getAllAccounts().get(i).getCanPay();

                textBank.setText("Bank:\n" + bank);
                textType.setText("Type:\n" + type);
                textBalance.setText("Balance: " + balance + "€");

                //Setting the switch "on" or "off" depending on whether payments are allowed
                if (canPay == 1) {
                    changeCanPay.setChecked(true);
                } else if (canPay == 0) {
                    changeCanPay.setChecked(false);
                }

                //Making fields that don't matter for that account type invisible
                if (type.equals("Debit")) {
                    textLimit.setVisibility(View.GONE);
                    textInterest.setVisibility(View.GONE);
                    changeLimit.setVisibility(View.GONE);
                    changeInterest.setVisibility(View.GONE);
                } else if (type.equals("Credit")) {
                    textLimit.setText("Limit: " + limit + "€");
                    textInterest.setVisibility(View.GONE);
                    changeInterest.setVisibility(View.GONE);
                } else if (type.equals("Savings")) {
                    textInterest.setText("Interest rate: " + interest + "%");
                    textLimit.setVisibility(View.GONE);
                    changeLimit.setVisibility(View.GONE);
                    changeCanPay.setVisibility(View.GONE);
                }
            }
        }
    }

    //Changing the information of the account after a button press
    private void changeInfo() {
        //Checking if the switch for payments is "on" or "off"
        changeCanPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (changeCanPay.isChecked()) {
                    canPay = 1;
                } else {
                    canPay = 0;
                }
            }
        });

        final String[] bic = new String[1];
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the current time
                Date currentTime = Calendar.getInstance().getTime();
                for (int i = 0; i < db.getAllAccounts().size(); i++) {
                    for (int j = 0; j < db.getAllBanks().size(); j++) {
                        //Getting the BIC-code of the bank that the account is in
                        if (db.getAllBanks().get(j).getName().equals(db.getAllAccounts().get(i).getBank())) {
                            bic[0] = db.getAllBanks().get(j).getBIC();
                        }
                    }

                    //Checking which fields are filled and updating the tables, writing new
                    // XML-files and creating new transactions based on the fields filled.
                    //TextViews containing info of the account will also be updated
                    if (db.getAllAccounts().get(i).getAccNum().equals(accNum)) {
                        if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            Toast.makeText(ShowAccountActivity.this, "Ability to pay from this account set to "
                                    + canPay, Toast.LENGTH_LONG).show();

                        } else if (changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = db.getAllAccounts().get(i).getInterestRate();
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of "
                                            + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            writeXML.writeTransactions(ShowAccountActivity.this, db);

                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " +
                                            balance + "\n Ability to pay set to " + canPay, Toast.LENGTH_LONG).show();

                        } else if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance = db.getAllAccounts().get(i).getBalance();
                            limit = Integer.parseInt(changeLimit.getEditText().getText().toString());
                            interest = db.getAllAccounts().get(i).getLimit();
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New limit: " +
                                    limit + "\n Ability to pay set to " + canPay, Toast.LENGTH_LONG).show();

                        } else if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("")) {
                            balance = db.getAllAccounts().get(i).getBalance();
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = Integer.parseInt(changeInterest.getEditText().getText().toString());
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New interest rate: "
                                    + interest + "\n Ability to pay set to " + canPay, Toast.LENGTH_LONG).show();

                        } else if (!changeBalance.getEditText().getText().toString().equals("") &&
                                !changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = Integer.parseInt(changeLimit.getEditText().getText().toString());
                            interest = db.getAllAccounts().get(i).getLimit();
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of "
                                            + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            writeXML.writeTransactions(ShowAccountActivity.this, db);

                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " +
                                    balance + "\nNew limit: " + limit + "\n Ability to pay set to "
                                    + canPay, Toast.LENGTH_LONG).show();

                        } else if (!changeBalance.getEditText().getText().toString().equals("") &&
                                !changeInterest.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = Integer.parseInt(changeInterest.getEditText().getText().toString());
                            db.updateAccount(accNum, balance, limit, interest, canPay);
                            writeXML.writeAccounts(ShowAccountActivity.this, db);

                            textBalance.setText("Balance: " + balance + "€");
                            textLimit.setText("Limit: " + limit + "€");
                            textInterest.setText("Interest rate: " + interest + "%");

                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of "
                                            + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            writeXML.writeTransactions(ShowAccountActivity.this, db);

                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " +
                                    balance + "\nNew interest rate: " + interest +
                                    "\n Ability to pay set to " + canPay, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    //If the user wants to delete the account, an alertDialog will appear asking if the user really
    // wants to go through with the deletion
    private void deleteAccount() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowAccountActivity.this);
                builder.setTitle("Delete account");
                builder.setMessage("Are you sure you want to delete account " + accNum + "?");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAccount(accNum);
                        writeXML.writeAccounts(ShowAccountActivity.this, db);

                        Toast.makeText(ShowAccountActivity.this, "Account " + accNum
                                + "deleted.", Toast.LENGTH_LONG).show();

                        //Checks whether the user was a normal user or an admin and returns to the
                        // MainActivity or the AdminActivity accordingly
                        if (admin.equals("user")) {
                            Intent intent = new Intent(ShowAccountActivity.this, MainActivity.class);
                            intent.putExtra("loguser", logUser);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ShowAccountActivity.this, AdminActivity.class);
                            startActivity(intent);
                        }
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
        });
    }

    //Will return to the home page (MainActivity) or the AdminActivity depending on who was the user
    // after the press of the back button
    @Override
    public void onBackPressed() {
        if (admin.equals("user")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("loguser", logUser);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
    }
}
