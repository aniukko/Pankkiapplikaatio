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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);
        getWindow().setTitle("My Cards");
        db = new BankDbHelper(this);

        logUser = getIntent().getStringExtra("loguser");
        header = findViewById(R.id.header);
        header.setText("Select a card from " + logUser + "'s cards");

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

        chooseCard();
        cardInfo();
        changeInfo();
        deleteCard();
    }

    private void chooseCard() {
        if (db.getAllCards().size() == 0) {
            Toast.makeText(this, "You have no accounts you can use to pay", Toast.LENGTH_LONG).show();
        } else {
            List<String> cardList = new ArrayList<>();
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getUsername().equals(logUser)) {
                    cardList.add(db.getAllCards().get(i).getAccNum());
                }
            }
            ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, cardList);
            cardSpinner.setAdapter(cardAdapter);
            cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenAcc = parent.getItemAtPosition(position).toString();
                    Toast.makeText(ShowCardActivity.this, "Card for account " + chosenAcc
                            + " selected", Toast.LENGTH_LONG).show();

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
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void cardInfo() {
        for (int i = 0; i < db.getAllCards().size(); i++) {
            if (db.getAllCards().get(i).getAccNum().equals(chosenAcc)) {
                withdraw = String.valueOf(db.getAllCards().get(i).getWithdrawLimit());
                pay = String.valueOf(db.getAllCards().get(i).getPayLimit());
                area = db.getAllCards().get(i).getArea();

                textWithdraw.setText("Withdraw limit:\n" + withdraw);
                textPay.setText("Pay limit:\n" + pay);
                textArea.setText("Withdraw limit:\n" + area);
            }
        }
    }

    private void changeInfo() {
        finland.setChecked(true);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeWithdraw.getEditText().getText().toString().equals("") &&
                        changePay.getEditText().getText().toString().equals("")) {
                    db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                    Toast.makeText(ShowCardActivity.this, "Area changed to " + area,
                            Toast.LENGTH_LONG).show();

                } else if (changeWithdraw.getEditText().getText().toString().equals("")) {
                    pay = changePay.getEditText().getText().toString();
                    db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                    Toast.makeText(ShowCardActivity.this, "Pay limit changed to " +
                            pay + " and area changed to " + area, Toast.LENGTH_LONG).show();

                } else if (changePay.getEditText().getText().toString().equals("")){
                    withdraw = changeWithdraw.getEditText().getText().toString();
                    db.updateCard(chosenAcc, Integer.parseInt(withdraw), Integer.parseInt(pay), area);
                    Toast.makeText(ShowCardActivity.this, "Withdraw limit changed to "
                            + withdraw + " and area changed to " + area, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteCard() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowCardActivity.this);
                builder.setTitle("Delete card");
                builder.setMessage("Are you sure you want to delete the card for the account " + chosenAcc + "?");
                builder.setCancelable(true);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteCard(chosenAcc);
                        Toast.makeText(ShowCardActivity.this, "Card for the account "
                                + chosenAcc + "deleted.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ShowCardActivity.this, MainActivity.class);
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
        Intent intent = new Intent(ShowCardActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
