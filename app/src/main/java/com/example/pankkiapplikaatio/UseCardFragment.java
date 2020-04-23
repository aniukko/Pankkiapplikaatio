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

public class UseCardFragment extends Fragment {

    private View view;
    private BankDbHelper db;
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
        selection();
        withdraw();

        return view;
    }

    private void switches() {
        switchWithdraw.setChecked(true);
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

    private void selection() {
        if (db.getAllCards().size() == 0) {
            Toast.makeText(getActivity(), "You have no cards",Toast.LENGTH_LONG).show();
        } else {
            List<String> cards = new ArrayList<>();
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getUsername().equals(user)) {
                    cards.add(db.getAllCards().get(i).getAccNum());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, cards);
            cardSpinner.setAdapter(adapter);
            cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenCard = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected  card for account " +
                            chosenCard, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard)) {
                    credLim = db.getAllAccounts().get(i).getLimit();
                    moneyOnCard = db.getAllAccounts().get(i).getBalance();
                    cardMoney.setText(moneyOnCard + "€");
                }
            }
            for (int i = 0; i < db.getAllCards().size(); i++) {
                if (db.getAllCards().get(i).getAccNum().equals(chosenCard)) {
                    withdrawOnCard = (int) db.getAllCards().get(i).getWithdrawLimit();
                    cardWithdraw.setText(withdrawOnCard + "€");
                    payOnCard = (int) db.getAllCards().get(i).getPayLimit();
                    cardPay.setText(payOnCard + "€");
                    areaOnCard = db.getAllCards().get(i).getArea();
                    cardArea.setText(areaOnCard + "€");
                }
            }
        }
    }

    private void withdraw() {
        final String[] bank = new String[1];
        final String[] bic = new String[1];
        final Date currentTimeWithdraw = Calendar.getInstance().getTime();
        if (switchWithdraw.isChecked()) {
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (withdrawAmount.getEditText().getText().toString().equals("")) {
                        withdrawAmount.setError("Give the amount you want to withdraw");
                    } else {
                        withdrawAmount.setError(null);
                        withdraw = Integer.parseInt(withdrawAmount.getEditText().getText().toString());
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard)) {
                                bank[0] = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        for (int i = 0; i < db.getAllBanks().size(); i++) {
                            if (db.getAllBanks().get(i).getName().equals(bank[0])) {
                                bic[0] = db.getAllBanks().get(i).getBIC();
                            }
                        }
                        if (withdraw > withdrawOnCard) {
                            Toast.makeText(getActivity(), "Can't withdraw. Amount over the withdraw limit", Toast.LENGTH_LONG).show();
                        } else if (withdraw > moneyOnCard + credLim) {
                            Toast.makeText(getActivity(), "Can't withdraw. Not enough money on the account", Toast.LENGTH_LONG).show();
                        } else if (!areaOnCard.equals(chosenArea)) {
                            if (areaOnCard.equals("Whole world")) {
                                db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0);
                                db.addTransaction(chosenCard, bank[0], bic[0], "Cash", "", "",
                                        withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank[0] +
                                                " " + bic[0] + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());

                            } else if (areaOnCard.equals("Europe")) {
                                if (chosenArea.equals("Whole world")) {
                                    db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0);
                                    db.addTransaction(chosenCard, bank[0], bic[0], "Cash", "", "",
                                            withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank[0] +
                                                    " " + bic[0] + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                                } else {
                                    Toast.makeText(getActivity(), "Can't withdraw. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Nordic countries and Estonia")) {
                                if (chosenArea.equals("Whole world") || chosenArea.equals("Europe")) {
                                    db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0);
                                    db.addTransaction(chosenCard, bank[0], bic[0], "Cash", "", "",
                                            withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank[0] +
                                                    " " + bic[0] + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                                } else {
                                    Toast.makeText(getActivity(), "Can't withdraw. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Finland")) {
                                db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0);
                                db.addTransaction(chosenCard, bank[0], bic[0], "Cash", "", "",
                                        withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank[0] +
                                                " " + bic[0] + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                            }
                        } else {
                            db.updateAccount(chosenCard, moneyOnCard - withdraw, credLim, 0);
                            db.addTransaction(chosenCard, bank[0], bic[0], "Cash", "", "",
                                    withdraw, currentTimeWithdraw.toString(), chosenCard + " " + bank[0] +
                                            " " + bic[0] + " withdrew " + withdraw + "€ on " + currentTimeWithdraw.toString());
                        }
                    }
                }
            });

        } else if (switchPay.isChecked()) {
            final String[] fromBank = new String[1];
            final String[] fromBic = new String[1];
            final String[] toBank = new String[1];
            final String[] toBic = new String[1];
            final Date currentTimePay = Calendar.getInstance().getTime();
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                        accountTo.setError(null);
                        payAmount.setError(null);
                        toAcc = accountTo.getEditText().getText().toString();
                        pay = Integer.parseInt(payAmount.getEditText().getText().toString());
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenCard)) {
                                fromBank[0] = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                toBank[0] = db.getAllAccounts().get(i).getBank();
                            } else {
                                toBank[0] = "";
                            }
                        }
                        for (int i = 0; i < db.getAllBanks().size(); i++) {
                            if (db.getAllBanks().get(i).getName().equals(fromBank[0])) {
                                fromBic[0] = db.getAllBanks().get(i).getBIC();
                            } else if (db.getAllBanks().get(i).getName().equals(toBank[0])) {
                                toBic[0] = db.getAllBanks().get(i).getBIC();
                            } else {
                                toBic[0] = "";
                            }
                        }
                        if (pay > payOnCard) {
                            Toast.makeText(getActivity(), "Can't pay. Amount over the pay limit", Toast.LENGTH_LONG).show();
                        } else if (pay > moneyOnCard + credLim) {
                            Toast.makeText(getActivity(), "Can't pay. Not enough money on the account", Toast.LENGTH_LONG).show();
                        } else if (!areaOnCard.equals(chosenArea)) {
                            if (areaOnCard.equals("Whole world")) {
                                for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                    if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                        db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                + pay, db.getAllAccounts().get(i).getLimit(), 0);
                                    }
                                }
                                db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0);
                                db.addTransaction(chosenCard, fromBank[0], fromBic[0], toAcc, toBank[0],
                                        toBic[0], pay, currentTimePay.toString(), "Payment of "
                                                + pay + "€ with a card from " + chosenCard + " " + fromBank[0] + " "
                                                + fromBic[0] + " to " + toAcc + " " + toBank[0] + " "
                                                + toBic[0] + " on " + currentTimePay.toString());
                            } else if (areaOnCard.equals("Europe")) {
                                if (chosenArea.equals("Whole world")) {
                                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                        if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                            db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                    + pay, db.getAllAccounts().get(i).getLimit(), 0);
                                        }
                                    }
                                    db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0);
                                    db.addTransaction(chosenCard, fromBank[0], fromBic[0], toAcc, toBank[0],
                                            toBic[0], pay, currentTimePay.toString(), "Payment of "
                                                    + pay + "€ with a card from " + chosenCard + " " + fromBank[0] + " "
                                                    + fromBic[0] + " to " + toAcc + " " + toBank[0] + " "
                                                    + toBic[0] + " on " + currentTimePay.toString());
                                } else {
                                    Toast.makeText(getActivity(), "Can't pay. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Nordic countries and Estonia")) {
                                if (chosenArea.equals("Whole world") || chosenArea.equals("Europe")) {
                                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                        if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                            db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                    + pay, db.getAllAccounts().get(i).getLimit(), 0);
                                        }
                                    }
                                    db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0);
                                    db.addTransaction(chosenCard, fromBank[0], fromBic[0], toAcc, toBank[0],
                                            toBic[0], pay, currentTimePay.toString(), "Payment of "
                                                    + pay + "€ with a card from " + chosenCard + " " + fromBank[0] + " "
                                                    + fromBic[0] + " to " + toAcc + " " + toBank[0] + " "
                                                    + toBic[0] + " on " + currentTimePay.toString());
                                } else {
                                    Toast.makeText(getActivity(), "Can't pay. Card isn't allowed to be used in this location", Toast.LENGTH_LONG).show();
                                }
                            } else if (areaOnCard.equals("Finland")) {
                                for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                    if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                        db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                                + pay, db.getAllAccounts().get(i).getLimit(), 0);
                                    }
                                }
                                db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0);
                                db.addTransaction(chosenCard, fromBank[0], fromBic[0], toAcc, toBank[0],
                                        toBic[0], pay, currentTimePay.toString(), "Payment of "
                                                + pay + "€ with a card from " + chosenCard + " " + fromBank[0] + " "
                                                + fromBic[0] + " to " + toAcc + " " + toBank[0] + " "
                                                + toBic[0] + " on " + currentTimePay.toString());
                            }
                        } else {
                            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                                if (db.getAllAccounts().get(i).getAccNum().equals(toAcc)) {
                                    db.updateAccount(toAcc, db.getAllAccounts().get(i).getBalance()
                                            + pay, db.getAllAccounts().get(i).getLimit(), 0);
                                }
                            }
                            db.updateAccount(chosenCard, moneyOnCard - pay, credLim, 0);
                            db.addTransaction(chosenCard, fromBank[0], fromBic[0], toAcc, toBank[0],
                                    toBic[0], pay, currentTimePay.toString(), "Payment of "
                                            + pay + "€ with a card from " + chosenCard + " " + fromBank[0] + " "
                                            + fromBic[0] + " to " + toAcc + " " + toBank[0] + " "
                                            + toBic[0] + " on " + currentTimePay.toString());
                        }
                    }
                }
            });
        }
    }
}
