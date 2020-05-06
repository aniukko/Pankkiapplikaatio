package com.example.pankkiapplikaatio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//For doing transactions by using a card
public class UseCardFragment extends Fragment {

    private View view;
    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    WriteXML writeXML = new WriteXML();
    private String user;

    private Spinner cardSpinner;
    private TextView cardMoney;
    private TextView cardWithdraw;
    private TextView cardPay;
    private TextView cardArea;
    private Switch switchWithdraw;
    private Switch switchPay;
    private Switch world;
    private Switch europe;
    private Switch nordic;
    private Switch finland;
    private TextView withdrawText;
    private TextInputLayout withdrawAmount;
    private TextView payText;
    private TextInputLayout accountTo;
    private TextInputLayout payAmount;
    private Button save;

    private String chosenCard;
    private String chosenArea;
    private int moneyOnCard;
    private int credLim;
    private int withdrawOnCard;
    private int payOnCard;
    private String areaOnCard;
    private int withdraw;
    private String toAcc;
    private int pay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_use_card, container, false);
        getActivity().setTitle("Pay with a card");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this.getActivity());

        //Getting the username of the logged in user from the MainActivity and saving it
        user = getArguments().getString("loguser");

        cardSpinner = view.findViewById(R.id.spinnerCard);
        cardMoney = view.findViewById(R.id.cardMoney);
        cardWithdraw = view.findViewById(R.id.cardWithdraw);
        cardPay = view.findViewById(R.id.cardPay);
        cardArea = view.findViewById(R.id.cardArea);
        switchWithdraw = view.findViewById(R.id.switchWithdraw);
        switchPay = view.findViewById(R.id.switchPay);
        world = view.findViewById(R.id.worldSwitch);
        europe = view.findViewById(R.id.europeSwitch);
        nordic = view.findViewById(R.id.nordicSwitch);
        finland = view.findViewById(R.id.finlandSwitch);
        withdrawAmount = view.findViewById(R.id.withdrawAmount);
        withdrawText = view.findViewById(R.id.withdrawText);
        payText = view.findViewById(R.id.payText);
        accountTo = view.findViewById(R.id.toAcc);
        payAmount = view.findViewById(R.id.cardPayAmount);
        save = view.findViewById(R.id.useCardButton);

        switches();
        if (db.getAllCards().size() == 0) {
            Toast.makeText(getActivity(), "You have no cards",Toast.LENGTH_LONG).show();
        } else {
            selection();
            withdrawOrPay();
        }
        return view;
    }

    //Saves the switch selections to variables
    private void switches() {
        payText.setVisibility(View.GONE);
        accountTo.setVisibility(View.GONE);
        payAmount.setVisibility(View.GONE);
        withdrawText.setVisibility(View.GONE);
        withdrawAmount.setVisibility(View.GONE);

        //Makes sure that "pay" and "withdraw" options won't be on at the same time.
        //Sets the visibility of the fields so that only required fields for that selection are shown
        switchWithdraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPay.setChecked(false);
                    payText.setVisibility(View.GONE);
                    accountTo.setVisibility(View.GONE);
                    payAmount.setVisibility(View.GONE);
                    withdrawText.setVisibility(View.VISIBLE);
                    withdrawAmount.setVisibility(View.VISIBLE);
                    save.setText("Withdraw");
                }
            }
        });
        switchPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchWithdraw.setChecked(false);
                    withdrawText.setVisibility(View.GONE);
                    withdrawAmount.setVisibility(View.GONE);
                    payText.setVisibility(View.VISIBLE);
                    accountTo.setVisibility(View.VISIBLE);
                    payAmount.setVisibility(View.VISIBLE);
                    save.setText("Pay");
                }
            }
        });


        finland.setChecked(true);
        chosenArea = "Finland";
        //Makes sure that multiple areas aren't selected at the same time
        world.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                    world.setChecked(false);
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
                    world.setChecked(false);
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
                    world.setChecked(false);
                    europe.setChecked(false);
                    nordic.setChecked(false);
                    chosenArea = "Finland";
                }
            }
        });
    }

    //Making the selection of which card is used
    private void selection() {
        //Adding all the users cards to a list and adding that list to a spinner
        List<String> cards = new ArrayList<>();
        for (int i = 0; i < db.getAllCards().size(); i++) {
            if (db.getAllCards().get(i).getUsername().equals(user)) {
                cards.add(db.getAllCards().get(i).getAccNum());
            }
        }
        if (cards.isEmpty()) {
            Toast.makeText(getActivity(), "You have no cards",Toast.LENGTH_LONG).show();
            save.setVisibility(View.GONE);

        } else {
            save.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, cards);
            cardSpinner.setAdapter(adapter);
            cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenCard = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected  card for account " +
                            chosenCard, Toast.LENGTH_SHORT).show();

                    //Checking if payments are still allowed for the card and setting info into
                    // textView to show information on the chosen card
                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                        if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard) &&
                                db.getAllAccounts().get(i).getCanPay() == 0) {
                            Toast.makeText(parent.getContext(), "Payments aren't allowed for this account anymore" +
                                    chosenCard, Toast.LENGTH_LONG).show();
                            save.setVisibility(View.GONE);
                        } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard) &&
                                db.getAllAccounts().get(i).getCanPay() == 1) {
                            save.setVisibility(View.VISIBLE);
                            credLim = db.getAllAccounts().get(i).getLimit();
                            moneyOnCard = db.getAllAccounts().get(i).getBalance();
                            cardMoney.setText(String.valueOf(moneyOnCard) + "€");
                            for (int j = 0; j < db.getAllCards().size(); j++) {
                                if (db.getAllCards().get(j).getAccNum().equals(chosenCard)) {
                                    withdrawOnCard = db.getAllCards().get(j).getWithdrawLimit();
                                    cardWithdraw.setText(String.valueOf(withdrawOnCard) + "€");
                                    payOnCard = db.getAllCards().get(j).getPayLimit();
                                    cardPay.setText(String.valueOf(payOnCard) + "€");
                                    areaOnCard = db.getAllCards().get(j).getArea();
                                    cardArea.setText(areaOnCard);
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

    //Withdrawing or doing a payment after pressing a button
    private void withdrawOrPay() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the current times for a withdrawal or a payment
                Date currentTimeWithdraw = Calendar.getInstance().getTime();
                Date currentTimePay = Calendar.getInstance().getTime();

                //Checking which switches are switched on and what fields are filled.
                //With that information accounts are updated, XML-files are written and transactions are created.
                //TextViews that have info on the cards will also be updated when the withdrawal or payment is done
                if (switchWithdraw.isChecked()) {
                    if (withdrawAmount.getEditText().getText().toString().equals("")) {
                        withdrawAmount.setError("Give the amount you want to withdraw");
                    } else {
                        withdrawAmount.setError(null);
                        withdraw = Integer.parseInt(withdrawAmount.getEditText().getText().toString());

                        //Saving the bank and the BIC-code to variables from different tables to add
                        // them to the transaction
                        String bank = "";
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard)) {
                                bank = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        String bic = "";
                        for (int i = 0; i < db.getAllBanks().size(); i++) {
                            if (db.getAllBanks().get(i).getName().equals(bank)) {
                                bic = db.getAllBanks().get(i).getBIC();
                            }
                        }
                        //Checking if the account has enough money with the credit limit taken into consideration
                        if (withdraw > withdrawOnCard) {
                            Toast.makeText(getActivity(), "Can't withdraw. Amount over the withdraw limit",
                                    Toast.LENGTH_LONG).show();
                        } else if (withdraw > moneyOnCard + credLim) {
                            Toast.makeText(getActivity(), "Can't withdraw. Not enough money on the account",
                                    Toast.LENGTH_LONG).show();

                        //Checking where the card is used (which area switch is turned on) and
                        // updating and creating info accordingly
                        } else if (!areaOnCard.equals(chosenArea)) {
                            if (areaOnCard.equals("Whole world")) {
                                db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0, 1);
                                writeXML.writeAccounts(getActivity(), db);

                                db.addTransaction(chosenCard, bank, bic, "Cash", "", "",
                                        withdraw, currentTimeWithdraw.toString(), chosenCard + " "
                                                + bank + " " + bic + " withdrew " + withdraw + "€ on "
                                                + currentTimeWithdraw.toString());
                                writeXML.writeTransactions(getActivity(), db);

                                cardMoney.setText(String.valueOf(moneyOnCard));
                                Toast.makeText(getActivity(), chosenCard + " withdrew " + withdraw + "€", Toast.LENGTH_LONG).show();

                            } else if (areaOnCard.equals("Europe")) {
                                if (chosenArea.equals("Whole world")) {
                                    Toast.makeText(getActivity(), "Can't withdraw. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                                } else {
                                    db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0, 1);
                                    writeXML.writeAccounts(getActivity(), db);

                                    db.addTransaction(chosenCard, bank, bic, "Cash", "", "",
                                            withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank +
                                                    " " + bic + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                                    writeXML.writeTransactions(getActivity(), db);

                                    moneyOnCard -= withdraw;
                                    cardMoney.setText(String.valueOf(moneyOnCard));
                                    Toast.makeText(getActivity(), chosenCard + " withdrew " + withdraw + "€", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Nordic countries and Estonia")) {
                                if (chosenArea.equals("Whole world") || chosenArea.equals("Europe")) {
                                    Toast.makeText(getActivity(), "Can't withdraw. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();

                                } else if (chosenArea.equals("Finland")){
                                    db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0, 1);
                                    writeXML.writeAccounts(getActivity(), db);

                                    db.addTransaction(chosenCard, bank, bic, "Cash", "", "",
                                            withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank +
                                                    " " + bic + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                                    writeXML.writeTransactions(getActivity(), db);

                                    moneyOnCard -= withdraw;
                                    cardMoney.setText(String.valueOf(moneyOnCard));
                                    Toast.makeText(getActivity(), chosenCard + " withdrew " + withdraw + "€", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Finland")) {
                                Toast.makeText(getActivity(), "Can't withdraw. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0, 1);
                            writeXML.writeAccounts(getActivity(), db);

                            db.addTransaction(chosenCard, bank, bic, "Cash", "", "",
                                    withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank +
                                            " " + bic + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                            writeXML.writeTransactions(getActivity(), db);

                            moneyOnCard -= withdraw;
                            cardMoney.setText(String.valueOf(moneyOnCard));
                            Toast.makeText(getActivity(), chosenCard + " withdrew " + withdraw + "€", Toast.LENGTH_LONG).show();

                        }
                    }
                } else if (switchPay.isChecked()) {
                    if (accountTo.getEditText().getText().toString().equals("")  &&
                            payAmount.getEditText().getText().toString().equals("")) {
                        accountTo.setError("Give the account you want to pay to");
                        payAmount.setError("Give the amount you want to pay");
                    } else if (accountTo.getEditText().getText().toString().equals("")) {
                        accountTo.setError("Give the account you want to pay to");
                        payAmount.setError(null);
                    } else if (payAmount.getEditText().getText().toString().equals("")) {
                        payAmount.setError("Give the amount you want to pay");
                        accountTo.setError(null);
                    } else {
                        String fromBank = "";
                        String fromBic = "";

                        //If the payment is made to an account that isn't in the database, the bank
                        // and the BIC-code of the account will be "null".
                        //Otherwise the bank and the BIC-code of the account that the payment is
                        // made to is saved and shown in the transactions
                        String toBank = "null";
                        String toBic = "null";
                        accountTo.setError(null);
                        payAmount.setError(null);
                        toAcc = accountTo.getEditText().getText().toString();
                        pay = Integer.parseInt(payAmount.getEditText().getText().toString());

                        //Saving the bank and the BIC-code to variables from different tables to add
                        // them to the transaction
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard)) {
                                fromBank = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                toBank = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        for (int i = 0; i < db.getAllBanks().size(); i++) {
                            if (db.getAllBanks().get(i).getName().equals(fromBank)) {
                                fromBic = db.getAllBanks().get(i).getBIC();
                            }
                        }
                        for (int i = 0; i < db.getAllBanks().size(); i++) {
                            if (db.getAllBanks().get(i).getName().equals(toBank)) {
                                toBic = db.getAllBanks().get(i).getBIC();
                            }
                        }
                        //Checking if the account has enough money with the credit limit taken into consideration
                        if (pay > payOnCard) {
                            Toast.makeText(getActivity(), "Can't pay. Amount over the pay limit", Toast.LENGTH_LONG).show();
                        } else if (pay > moneyOnCard + credLim) {
                            Toast.makeText(getActivity(), "Can't pay. Not enough money on the account", Toast.LENGTH_LONG).show();

                        //Checking where the card is used (which area switch is turned on) and
                        // updating and creating info accordingly.
                        } else if (!areaOnCard.equals(chosenArea)) {
                            if (areaOnCard.equals("Whole world")) {
                                for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                    if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                        db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                + pay, db.getAllAccounts().get(i).getLimit(), 0, db.getAllAccounts().get(i).getCanPay());
                                        writeXML.writeAccounts(getActivity(), db);
                                    }
                                }
                                db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0, 1);
                                writeXML.writeAccounts(getActivity(), db);

                                db.addTransaction(chosenCard, fromBank, fromBic, toAcc, toBank,
                                        toBic, pay, currentTimePay.toString(), "Payment of "
                                                + pay + "€ with a card from " + chosenCard + " " + fromBank + " "
                                                + fromBic + " to " + toAcc + " " + toBank + " "
                                                + toBic + " on " + currentTimePay.toString());
                                writeXML.writeTransactions(getActivity(), db);

                                moneyOnCard -= pay;
                                cardMoney.setText(String.valueOf(moneyOnCard));
                                Toast.makeText(getActivity(), chosenCard + " " + fromBank + " " + fromBic +
                                        " payed " + pay + "€ to " + toAcc + " " + toBank + " " + toBic, Toast.LENGTH_LONG).show();

                            } else if (areaOnCard.equals("Europe")) {
                                if (chosenArea.equals("Whole world")) {
                                    Toast.makeText(getActivity(), "Can't pay. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();

                                } else {
                                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                        if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                            db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                    + pay, db.getAllAccounts().get(i).getLimit(), 0, db.getAllAccounts().get(i).getCanPay());
                                            writeXML.writeAccounts(getActivity(), db);

                                        }
                                    }
                                    db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0, 1);
                                    writeXML.writeAccounts(getActivity(), db);

                                    db.addTransaction(chosenCard, fromBank, fromBic, toAcc, toBank,
                                            toBic, pay, currentTimePay.toString(), "Payment of "
                                                    + pay + "€ with a card from " + chosenCard + " " + fromBank + " "
                                                    + fromBic + " to " + toAcc + " " + toBank + " "
                                                    + toBic + " on " + currentTimePay.toString());
                                    writeXML.writeTransactions(getActivity(), db);

                                    moneyOnCard -= pay;
                                    cardMoney.setText(String.valueOf(moneyOnCard));
                                    Toast.makeText(getActivity(), chosenCard + " " + fromBank + " " + fromBic +
                                            " payed " + pay + "€ to " + toAcc + " " + toBank + " " + toBic, Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Nordic countries and Estonia")) {
                                if (chosenArea.equals("Whole world") || chosenArea.equals("Europe")) {
                                    Toast.makeText(getActivity(), "Can't pay. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();

                                } else if (chosenArea.equals("Finland")){
                                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                        if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                            db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                    + pay, db.getAllAccounts().get(i).getLimit(), 0, db.getAllAccounts().get(i).getCanPay());
                                            writeXML.writeAccounts(getActivity(), db);
                                        }
                                    }
                                    db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0, 1);
                                    writeXML.writeAccounts(getActivity(), db);

                                    db.addTransaction(chosenCard, fromBank, fromBic, toAcc, toBank,
                                            toBic, pay, currentTimePay.toString(), "Payment of "
                                                    + pay + "€ with a card from " + chosenCard + " " + fromBank + " "
                                                    + fromBic + " to " + toAcc + " " + toBank + " "
                                                    + toBic + " on " + currentTimePay.toString());
                                    writeXML.writeTransactions(getActivity(), db);

                                    moneyOnCard -= pay;
                                    cardMoney.setText(String.valueOf(moneyOnCard));
                                    Toast.makeText(getActivity(), chosenCard + " " + fromBank + " " + fromBic +
                                            " payed " + pay + "€ to " + toAcc + " " + toBank + " " + toBic, Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Finland")) {
                                Toast.makeText(getActivity(), chosenCard + " payed " + pay + "€ to " + toAcc, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                    db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                            + pay, db.getAllAccounts().get(i).getLimit(), 0, db.getAllAccounts().get(i).getCanPay());
                                    writeXML.writeAccounts(getActivity(), db);

                                }
                            }
                            db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0, 1);
                            writeXML.writeAccounts(getActivity(), db);

                            db.addTransaction(chosenCard, fromBank, fromBic, toAcc, toBank,
                                    toBic, pay, currentTimePay.toString(), "Payment of "
                                            + pay + "€ with a card from " + chosenCard + " " + fromBank + " "
                                            + fromBic + " to " + toAcc + " " + toBank + " "
                                            + toBic + " on " + currentTimePay.toString());
                            writeXML.writeTransactions(getActivity(), db);

                            moneyOnCard -= pay;
                            cardMoney.setText(String.valueOf(moneyOnCard));
                            Toast.makeText(getActivity(), chosenCard + " " + fromBank + " " + fromBic +
                                    " payed " + pay + "€ to " + toAcc + " " + toBank + " " + toBic, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
}
