package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;

public class ShowAccountActivity extends AppCompatActivity {

    String logUser;
    String accNum;

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
    Button card;
    Button delete;

    String bank;
    String type;
    int balance;
    int limit;
    int interest;

    private BankDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);
        getWindow().setTitle("My Account");
        db = new BankDbHelper(this);

        logUser = getIntent().getStringExtra("loguser");
        accNum = getIntent().getStringExtra("accNum");
        header = findViewById(R.id.header);
        header.setText("Account number\n" + accNum);

        textBank = findViewById(R.id.textBank);
        textType = findViewById(R.id.textType);
        textBalance = findViewById(R.id.textBalance);
        textLimit = findViewById(R.id.textLimit);
        textInterest = findViewById(R.id.textInterest);
        changeBalance = findViewById(R.id.addMoney);
        changeLimit = findViewById(R.id.changeLimit);
        changeInterest = findViewById(R.id.changeInterest);
        save = findViewById(R.id.save);
        card = findViewById(R.id.cardButton);
        delete = findViewById(R.id.buttonDelete2);

        accountInfo();
        card();
        changeInfo();
        deleteAccount();
    }

    private void accountInfo() {
        for (int i = 0; i < db.getAllAccounts().size(); i++) {
            if (db.getAllAccounts().get(i).getAccNum().equals(accNum)) {
                bank = db.getAllAccounts().get(i).getBank();
                type = db.getAllAccounts().get(i).getType();
                balance = db.getAllAccounts().get(i).getBalance();
                limit = db.getAllAccounts().get(i).getLimit();
                interest = db.getAllAccounts().get(i).getInterestRate();

                textBank.setText("Bank: " + bank);
                textType.setText("Type: " + type);
                textBalance.setText("Balance: " + balance + "€");

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
                }
            }
        }
    }

    private void changeInfo() {
        final String[] bic = new String[1];
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                for (int i = 0; i < db.getAllAccounts().size(); i++) {
                    for (int j = 0; j < db.getAllBanks().size(); j++) {
                        if (db.getAllBanks().get(j).getName().equals(db.getAllAccounts().get(i).getBank())) {
                            bic[0] = db.getAllBanks().get(j).getBIC();
                        }
                    }

                    if (db.getAllAccounts().get(i).getAccNum().equals(accNum)) {
                        if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            changeBalance.setError("Nothing changed");
                            changeLimit.setError("Nothing changed");
                            changeInterest.setError("Nothing changed");

                        } else if (changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = db.getAllAccounts().get(i).getInterestRate();
                            db.updateAccount(accNum, balance, limit, interest);
                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of " + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " + balance,
                                    Toast.LENGTH_LONG).show();

                        } else if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance = db.getAllAccounts().get(i).getBalance();
                            limit = Integer.parseInt(changeLimit.getEditText().getText().toString());
                            interest = db.getAllAccounts().get(i).getLimit();
                            db.updateAccount(accNum, balance, limit, interest);
                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New limit: " + limit,
                                    Toast.LENGTH_LONG).show();

                        } else if (changeBalance.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("")) {
                            balance = db.getAllAccounts().get(i).getBalance();
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = Integer.parseInt(changeInterest.getEditText().getText().toString());
                            db.updateAccount(accNum, balance, limit, interest);
                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New interest rate: "
                                    + interest, Toast.LENGTH_LONG).show();

                        } else if (!changeBalance.getEditText().getText().toString().equals("") &&
                                !changeLimit.getEditText().getText().toString().equals("") &&
                                changeInterest.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = Integer.parseInt(changeLimit.getEditText().getText().toString());
                            interest = db.getAllAccounts().get(i).getLimit();
                            db.updateAccount(accNum, balance, limit, interest);
                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of " + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " +
                                    balance + "\nNew limit: " + limit, Toast.LENGTH_LONG).show();

                        } else if (!changeBalance.getEditText().getText().toString().equals("") &&
                                !changeInterest.getEditText().getText().toString().equals("") &&
                                changeLimit.getEditText().getText().toString().equals("")) {
                            balance += Integer.parseInt(changeBalance.getEditText().getText().toString());
                            limit = db.getAllAccounts().get(i).getLimit();
                            interest = Integer.parseInt(changeInterest.getEditText().getText().toString());
                            db.updateAccount(accNum, balance, limit, interest);
                            db.addTransaction("Cash", "Cash", "Cash",
                                    accNum, db.getAllAccounts().get(i).getBank(), bic[0],
                                    Integer.parseInt(changeBalance.getEditText().getText().toString()),
                                    currentTime.toString(), "Deposition of " + changeBalance.getEditText().getText().toString()
                                            + "€ to account " + accNum + " " + db.getAllAccounts().get(i).getBank()
                                            + " " + bic[0] + " on " + currentTime.toString());
                            changeBalance.setError(null);
                            changeLimit.setError(null);
                            changeInterest.setError(null);
                            Toast.makeText(ShowAccountActivity.this, "New balance: " +
                                    balance + "\nNew interest rate: " + interest, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    private void card() {
        if (db.getAllCards().size() == 0) {
            card.setText("No cards");
        } else {
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getAccNum().equals(accNum)) {
                    card.setText("Show information on owned cards by pressing the button.");
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowAccountActivity.this, ShowCardActivity.class);
                            intent.putExtra("loguser", logUser);
                            //intent.putExtra("accNum", accNum);
                            startActivity(intent);
                        }
                    });
                } else {
                    card.setText("No card for this account. Create a new card by pressing the button.");
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowAccountActivity.this, CreateCardActivity.class);
                            intent.putExtra("loguser", logUser);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

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
                        Toast.makeText(ShowAccountActivity.this, "Account " + accNum
                                + "deleted.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ShowAccountActivity.this, MainActivity.class);
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
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowAccountActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
