package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

//For viewing and editing the cards owned by the user
public class ShowCardActivity extends AppCompatActivity {

    String logUser;
    String admin;
    String cardAcc;
    TextView header;
    Spinner cardSpinner;
    TextView textWithdraw;
    TextView textPay;
    TextView textArea;
    TextInputLayout changeWithdraw;
    TextInputLayout changePay;
    TextView textViewArea;
    Switch world;
    Switch europe;
    Switch nordic;
    Switch finland;
    Button save;
    Button delete;
    String chosenAcc;
    String withdraw;
    String pay;
    String area;
    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    WriteXML writeXML = new WriteXML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);
        getWindow().setTitle("My Cards");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        //Getting information from other activities
        logUser = getIntent().getStringExtra("loguser");
        admin = getIntent().getStringExtra("admin");
        cardAcc = getIntent().getStringExtra("accNum");
        header = findViewById(R.id.header);

        if (admin.equals("user")) {
            header.setText("Select a card from " + logUser + "'s cards");
        } else {
            header.setText("Card for account " + cardAcc);
        }
        cardSpinner = findViewById(R.id.cardSpinner);
        textWithdraw = findViewById(R.id.textWithdraw);
        textPay = findViewById(R.id.textPay);
        textArea = findViewById(R.id.textArea);
        changeWithdraw = findViewById(R.id.changeWithdraw);
        changePay = findViewById(R.id.changePay);
        textViewArea = findViewById(R.id.textViewArea);
        world = findViewById(R.id.switchWorld);
        europe = findViewById(R.id.switchEurope2);
        nordic = findViewById(R.id.switchNordic2);
        finland = findViewById(R.id.switchFinland2);
        save = findViewById(R.id.saveButton2);
        delete = findViewById(R.id.buttonDelete3);

        textWithdraw.setVisibility(View.GONE);
        textPay.setVisibility(View.GONE);
        textArea.setVisibility(View.GONE);
        changeWithdraw.setVisibility(View.GONE);
        changePay.setVisibility(View.GONE);
        textViewArea.setVisibility(View.GONE);
        world.setVisibility(View.GONE);
        europe.setVisibility(View.GONE);
        nordic.setVisibility(View.GONE);
        finland.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        //Saving the selcted area to a variable and making sure that multiple areas can't be chosen at once
        finland.setChecked(true);
        area = "Finland";
        world.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    europe.setChecked(false);
                    nordic.setChecked(false);
                    finland.setChecked(false);
                    area = "Whole world";
                }
            }
        });
        europe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    world.setChecked(false);
                    nordic.setChecked(false);
                    finland.setChecked(false);
                    area = "Europe";

                }
            }
        });
        nordic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    world.setChecked(false);
                    europe.setChecked(false);
                    finland.setChecked(false);
                    area = "Nordic countries and Estonia";
                }
            }
        });
        finland.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    world.setChecked(false);
                    europe.setChecked(false);
                    nordic.setChecked(false);
                    area = "Finland";
                }
            }
        });

        if (admin.equals("user")) {
            chooseCard();
            changeInfo();
            deleteCard();
        } else {
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getAccNum().equals(cardAcc)) {
                    withdraw = String.valueOf(db.getAllCards().get(i).getWithdrawLimit());
                    pay = String.valueOf(db.getAllCards().get(i).getPayLimit());
                    area = db.getAllCards().get(i).getArea();

                    cardSpinner.setVisibility(View.GONE);

                    textWithdraw.setVisibility(View.VISIBLE);
                    textPay.setVisibility(View.VISIBLE);
                    textArea.setVisibility(View.VISIBLE);
                    changeWithdraw.setVisibility(View.VISIBLE);
                    changePay.setVisibility(View.VISIBLE);
                    textViewArea.setVisibility(View.VISIBLE);
                    world.setVisibility(View.VISIBLE);
                    europe.setVisibility(View.VISIBLE);
                    nordic.setVisibility(View.VISIBLE);
                    finland.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);

                    textWithdraw.setText("Withdraw limit:\n" + withdraw);
                    textPay.setText("Pay limit:\n" + pay);
                    textArea.setText("Area:\n" + area);
                }
            }
            changeInfo();
            deleteCard();
        }
    }

    private void chooseCard() {
        //If a normal user has logged in, the user can choose which one of his cards they want to choose.
        //The cards of the user will be added to a list and the list will be added to a spinner
        if (db.getAllCards().size() == 0) {
            Toast.makeText(this, "You have no cards you can use to pay", Toast.LENGTH_LONG).show();
        } else {
            List<String> cardList = new ArrayList<>();
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getUsername().equals(logUser)) {
                    cardList.add("Card for account " + db.getAllCards().get(i).getAccNum());
                }
            }
            if (cardList.isEmpty()) {
                Toast.makeText(this, "You have no cards you can use to pay", Toast.LENGTH_LONG).show();
            } else {
                ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, cardList);
                cardSpinner.setAdapter(cardAdapter);
                cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        chosenAcc = parent.getItemAtPosition(position).toString();

                        //When a card is selected, fields will be set visible and the info on the card will be shown
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc)) {

                                textWithdraw.setVisibility(View.VISIBLE);
                                textPay.setVisibility(View.VISIBLE);
                                textArea.setVisibility(View.VISIBLE);
                                changeWithdraw.setVisibility(View.VISIBLE);
                                changePay.setVisibility(View.VISIBLE);
                                textViewArea.setVisibility(View.VISIBLE);
                                world.setVisibility(View.VISIBLE);
                                europe.setVisibility(View.VISIBLE);
                                nordic.setVisibility(View.VISIBLE);
                                finland.setVisibility(View.VISIBLE);
                                save.setVisibility(View.VISIBLE);
                                Toast.makeText(ShowCardActivity.this, "Card for account "
                                        + chosenAcc + " selected", Toast.LENGTH_LONG).show();
                                for (int j = 0; j < db.getAllCards().size(); j++) {
                                    if (db.getAllCards().get(j).getAccNum().equals(chosenAcc)) {
                                        withdraw = String.valueOf(db.getAllCards().get(j).getWithdrawLimit());
                                        pay = String.valueOf(db.getAllCards().get(j).getPayLimit());

                                        textWithdraw.setText("Withdraw limit:\n" + withdraw);
                                        textPay.setText("Pay limit:\n" + pay);
                                        textArea.setText("Area:\n" + db.getAllCards().get(j).getArea());
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }
    }

    //Changing the info of the cards after a press of a button depending on the info given.
    //The textViews containing information on the cards will be updated.
    //Database table will be updated and XML-files will be created
    private void changeInfo() {
        if (admin.equals("user")) {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (changeWithdraw.getEditText().getText().toString().equals("") &&
                            changePay.getEditText().getText().toString().equals("")) {
                        db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Area changed to " + area,
                                Toast.LENGTH_LONG).show();

                    } else if (changeWithdraw.getEditText().getText().toString().equals("")) {
                        pay = changePay.getEditText().getText().toString();
                        db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textPay.setText("Pay limit:\n" + pay);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Pay limit changed to " +
                                pay + " and area changed to " + area, Toast.LENGTH_LONG).show();

                    } else if (changePay.getEditText().getText().toString().equals("")) {
                        withdraw = changeWithdraw.getEditText().getText().toString();
                        db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textWithdraw.setText("Withdraw limit:\n" + withdraw);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Withdraw limit changed to "
                                + withdraw + " and area changed to " + area, Toast.LENGTH_LONG).show();
                    } else {
                        withdraw = changeWithdraw.getEditText().getText().toString();
                        pay = changePay.getEditText().getText().toString();

                        db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textWithdraw.setText("Withdraw limit:\n" + withdraw);
                        textPay.setText("Pay limit:\n" + pay);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Withdraw limit changed to " +
                                withdraw + ", pay limit changed to " + pay + " and area changed to " +
                                area, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (changeWithdraw.getEditText().getText().toString().equals("") &&
                            changePay.getEditText().getText().toString().equals("")) {
                        db.updateCard(cardAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Area changed to " + area,
                                Toast.LENGTH_LONG).show();

                    } else if (changeWithdraw.getEditText().getText().toString().equals("")) {
                        pay = changePay.getEditText().getText().toString();
                        db.updateCard(cardAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textPay.setText("Pay limit:\n" + pay);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Pay limit changed to " +
                                pay + " and area changed to " + area, Toast.LENGTH_LONG).show();

                    } else if (changePay.getEditText().getText().toString().equals("")) {
                        withdraw = changeWithdraw.getEditText().getText().toString();
                        db.updateCard(cardAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textWithdraw.setText("Withdraw limit:\n" + withdraw);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Withdraw limit changed to "
                                + withdraw + " and area changed to " + area, Toast.LENGTH_LONG).show();
                    } else {
                        withdraw = changeWithdraw.getEditText().getText().toString();
                        pay = changePay.getEditText().getText().toString();

                        db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                        writeXML.writeCards(ShowCardActivity.this, db);

                        textWithdraw.setText("Withdraw limit:\n" + withdraw);
                        textPay.setText("Pay limit:\n" + pay);
                        textArea.setText("Area:\n" + area);

                        Toast.makeText(ShowCardActivity.this, "Withdraw limit changed to " +
                                withdraw + ", pay limit changed to " + pay + " and area changed to " +
                                area, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //If the user wants to delete the card, an alertDialog will appear asking if the user really
    // wants to go through with the deletion
    public void deleteCard() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowCardActivity.this);
                builder.setTitle("Delete card");
                if (admin.equals("user")) {
                    builder.setMessage("Are you sure you want to delete the card for the account " + chosenAcc + "?");
                } else {
                    builder.setMessage("Are you sure you want to delete the card for the account " + cardAcc + "?");
                }
                builder.setCancelable(true);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Checks whether the user was a normal user or an admin, uses the correct account
                        // number and returns to the MainActivity or the AdminActivity accordingly
                        if (admin.equals("user")) {
                            db.deleteCard(chosenAcc);
                            writeXML.writeCards(ShowCardActivity.this, db);

                            Toast.makeText(ShowCardActivity.this, "Card for the account "
                                    + chosenAcc + " deleted.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ShowCardActivity.this, MainActivity.class);
                            intent.putExtra("loguser", logUser);
                            startActivity(intent);
                        } else {
                            db.deleteCard(cardAcc);
                            writeXML.writeCards(ShowCardActivity.this, db);
                            Toast.makeText(ShowCardActivity.this, "Card for the account "
                                    + cardAcc + " deleted.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ShowCardActivity.this, AdminActivity.class);
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
