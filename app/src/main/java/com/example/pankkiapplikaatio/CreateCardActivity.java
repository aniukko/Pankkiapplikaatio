package com.example.pankkiapplikaatio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

//For creating a new card for any account a user owns
public class CreateCardActivity extends AppCompatActivity {

    String logUser;
    Spinner accSpinner;
    TextInputLayout withdrawLimit;
    TextInputLayout payLimit;
    Switch wholeWorld;
    Switch europe;
    Switch nordic;
    Switch finland;
    Button save;
    String chosenAcc;
    String chosenWithdrawLimit;
    String chosenPayLimit;
    String chosenArea;
    TextView withdrawText;
    TextView payText;
    TextView checkText;
    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    WriteXML writeXML = new WriteXML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
        getWindow().setTitle("New card");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        //Getting the username of the logged in user from the MainActivity and saving it to a variable
        logUser = getIntent().getStringExtra("loguser");

        accSpinner = findViewById(R.id.accSpinner);
        withdrawLimit = findViewById(R.id.withdrawLimit);
        payLimit = findViewById(R.id.payLimit);
        wholeWorld = findViewById(R.id.switchWholeWorld);
        europe = findViewById(R.id.switchEurope);
        nordic = findViewById(R.id.switchNordic);
        finland = findViewById(R.id.switchFinland);
        save = findViewById(R.id.saveButtonCard);
        withdrawText = findViewById(R.id.withdrawTextView);
        payText = findViewById(R.id.payTextView);
        checkText = findViewById(R.id.textCheck);

        switches();
        chooseAcc();
    }

    //Saves the chosen area to a variable and makes sure that more than one switches can't be on at the same time.
    private void switches() {
        finland.setChecked(true);
        chosenArea = "Finland";
        wholeWorld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    europe.setChecked(false);
                    nordic.setChecked(false);
                    finland.setChecked(false);
                    chosenArea = "Whole world";
                }
            }
        });
        europe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wholeWorld.setChecked(false);
                    nordic.setChecked(false);
                    finland.setChecked(false);
                    chosenArea = "Europe";

                }
            }
        });
        nordic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wholeWorld.setChecked(false);
                    europe.setChecked(false);
                    finland.setChecked(false);
                    chosenArea = "Nordic countries and Estonia";
                }
            }
        });
        finland.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wholeWorld.setChecked(false);
                    europe.setChecked(false);
                    nordic.setChecked(false);
                    chosenArea = "Finland";
                }
            }
        });
    }

    //Chooses the account that you want to create the card to
    private void chooseAcc() {
        //If there are no accounts in the database, you will be sent back to home (MainActivity).
        //Otherwise all the accounts of the logged in user will be added to a list and the list will
        // be added to a spinner
        if (db.getAllAccounts().size() == 0) {
            Toast.makeText(CreateCardActivity.this, "You don't have any accounts. Create an account first.",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CreateCardActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            List<String> accList = new ArrayList<>();
            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                if (db.getAllAccounts().get(i).getUsername().equals(logUser)) {
                    accList.add(db.getAllAccounts().get(i).getAccNum());
                }
            }

            ArrayAdapter<String> accAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, accList);
            accSpinner.setAdapter(accAdapter);
            accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenAcc = parent.getItemAtPosition(position).toString();
                    Toast.makeText(CreateCardActivity.this, "Selected: " + chosenAcc, Toast.LENGTH_SHORT).show();

                    //Sets the visibility of different fields and therefore allows or denys creation
                    // of cards based on whether a card can be created to that account or not.
                    //An error message will tell why creating a card for a certain account isn't
                    // allowed, if necessary
                    if (!db.getAllCards().isEmpty()) {
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            for (int j = 0; j < db.getAllCards().size(); j++) {
                                if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                        db.getAllCards().get(j).getAccNum().equals(chosenAcc)) {
                                    withdrawLimit.setVisibility(View.GONE);
                                    payLimit.setVisibility(View.GONE);
                                    wholeWorld.setVisibility(View.GONE);
                                    europe.setVisibility(View.GONE);
                                    nordic.setVisibility(View.GONE);
                                    finland.setVisibility(View.GONE);
                                    save.setVisibility(View.GONE);
                                    withdrawText.setVisibility(View.GONE);
                                    payText.setVisibility(View.GONE);
                                    checkText.setVisibility(View.GONE);

                                    Toast.makeText(CreateCardActivity.this, "Card already exists for this account",
                                            Toast.LENGTH_LONG).show();
                                } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                        db.getAllAccounts().get(i).getType().equals("Savings")) {

                                    withdrawLimit.setVisibility(View.GONE);
                                    payLimit.setVisibility(View.GONE);
                                    wholeWorld.setVisibility(View.GONE);
                                    europe.setVisibility(View.GONE);
                                    nordic.setVisibility(View.GONE);
                                    finland.setVisibility(View.GONE);
                                    save.setVisibility(View.GONE);
                                    withdrawText.setVisibility(View.GONE);
                                    payText.setVisibility(View.GONE);
                                    checkText.setVisibility(View.GONE);

                                    Toast.makeText(CreateCardActivity.this, "Can't create a card to a savings account",
                                            Toast.LENGTH_LONG).show();
                                } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) && db.getAllAccounts().get(i).getCanPay() == 0) {
                                    withdrawLimit.setVisibility(View.GONE);
                                    payLimit.setVisibility(View.GONE);
                                    wholeWorld.setVisibility(View.GONE);
                                    europe.setVisibility(View.GONE);
                                    nordic.setVisibility(View.GONE);
                                    finland.setVisibility(View.GONE);
                                    save.setVisibility(View.GONE);
                                    withdrawText.setVisibility(View.GONE);
                                    payText.setVisibility(View.GONE);
                                    checkText.setVisibility(View.GONE);

                                    Toast.makeText(CreateCardActivity.this, "Can't create a card to an account that doesn't allow payments",
                                            Toast.LENGTH_LONG).show();
                                } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                        !db.getAllAccounts().get(i).getType().equals("Savings") &&
                                        !db.getAllCards().get(j).getAccNum().equals(chosenAcc) &&
                                        db.getAllAccounts().get(i).getCanPay() == 1) {
                                    withdrawLimit.setVisibility(View.VISIBLE);
                                    payLimit.setVisibility(View.VISIBLE);
                                    wholeWorld.setVisibility(View.VISIBLE);
                                    europe.setVisibility(View.VISIBLE);
                                    nordic.setVisibility(View.VISIBLE);
                                    finland.setVisibility(View.VISIBLE);
                                    save.setVisibility(View.VISIBLE);
                                    withdrawText.setVisibility(View.VISIBLE);
                                    payText.setVisibility(View.VISIBLE);
                                    checkText.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                    db.getAllAccounts().get(i).getType().equals("Savings")) {

                                withdrawLimit.setVisibility(View.GONE);
                                payLimit.setVisibility(View.GONE);
                                wholeWorld.setVisibility(View.GONE);
                                europe.setVisibility(View.GONE);
                                nordic.setVisibility(View.GONE);
                                finland.setVisibility(View.GONE);
                                save.setVisibility(View.GONE);
                                withdrawText.setVisibility(View.GONE);
                                payText.setVisibility(View.GONE);
                                checkText.setVisibility(View.GONE);

                                Toast.makeText(CreateCardActivity.this, "Can't create a card to a savings account",
                                        Toast.LENGTH_LONG).show();
                            } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) && db.getAllAccounts().get(i).getCanPay() == 0) {
                                withdrawLimit.setVisibility(View.GONE);
                                payLimit.setVisibility(View.GONE);
                                wholeWorld.setVisibility(View.GONE);
                                europe.setVisibility(View.GONE);
                                nordic.setVisibility(View.GONE);
                                finland.setVisibility(View.GONE);
                                save.setVisibility(View.GONE);
                                withdrawText.setVisibility(View.GONE);
                                payText.setVisibility(View.GONE);
                                checkText.setVisibility(View.GONE);

                                Toast.makeText(CreateCardActivity.this, "Can't create a card to an account that doesn't allow payments",
                                        Toast.LENGTH_LONG).show();
                            } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                    !db.getAllAccounts().get(i).getType().equals("Savings") &&
                                    db.getAllAccounts().get(i).getCanPay() == 1) {
                                withdrawLimit.setVisibility(View.VISIBLE);
                                payLimit.setVisibility(View.VISIBLE);
                                wholeWorld.setVisibility(View.VISIBLE);
                                europe.setVisibility(View.VISIBLE);
                                nordic.setVisibility(View.VISIBLE);
                                finland.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                withdrawText.setVisibility(View.VISIBLE);
                                payText.setVisibility(View.VISIBLE);
                                checkText.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            selections();
        }
    }

    //Creates a card by adding it to the database and writing the XML-file after a press of a button.
    //Will give error messages if all the required fields aren't filled.
    //After a card has been created, the home page (MainActivity) will be opened
    private void selections() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withdrawLimit.getEditText().getText().toString().equals("") &&
                        payLimit.getEditText().getText().toString().equals("")) {
                    withdrawLimit.setError("Set a withdraw limit");
                    payLimit.setError("Set a pay limit");
                } else if (withdrawLimit.getEditText().getText().toString().equals("") ) {
                    withdrawLimit.setError("Set a withdraw limit");
                    payLimit.setError(null);
                } else if (payLimit.getEditText().getText().toString().equals("")) {
                    payLimit.setError("Set a pay limit");
                    withdrawLimit.setError(null);
                } else {
                    withdrawLimit.setError(null);
                    payLimit.setError(null);
                    chosenWithdrawLimit = withdrawLimit.getEditText().getText().toString();
                    chosenPayLimit = payLimit.getEditText().getText().toString();
                    db.addCard(logUser, chosenAcc, Integer.parseInt(chosenWithdrawLimit),
                            Integer.parseInt(chosenPayLimit), chosenArea);
                    Toast.makeText(CreateCardActivity.this, "New card for the account "
                            + chosenAcc + " created.", Toast.LENGTH_LONG).show();
                    writeXML.writeCards(CreateCardActivity.this, db);

                    Intent intent = new Intent(CreateCardActivity.this, MainActivity.class);
                    intent.putExtra("loguser", logUser);
                    startActivity(intent);
                }
            }
        });
    }

    //When the back button is pressed, the home page (MainActivity) will be opened
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("loguser", logUser);
        startActivity(intent);
    }
}
