package com.example.pankkiapplikaatio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//For doing transactions: paying and transferring money
public class PayFragment extends Fragment {

    private View view;
    private BankDbHelper db;

    private Spinner accSpinner;
    private TextView currentMoney;
    private TextView textCredLim;
    private TextView currCredLim;
    private TextInputLayout receiver;
    private TextInputLayout payAmount;
    private Button pay;

    private String user;

    private String chosenAcc;
    private int payMoney;
    private int payLimit;

    private String chosenReceiver;
    private int chosenAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pay, container, false);
        getActivity().setTitle("Pay");
        db = new BankDbHelper(this.getActivity());

        accSpinner = view.findViewById(R.id.payAccSpinner);
        currentMoney = view.findViewById(R.id.currentMoney);
        textCredLim = view.findViewById(R.id.textViewCredLim);
        currCredLim = view.findViewById(R.id.currentCredLim);
        receiver = view.findViewById(R.id.receivingAcc);
        payAmount = view.findViewById(R.id.payAmount);
        pay = view.findViewById(R.id.payButton);

        //Getting the username of the logged in user from the MainActivity and saving it
        user = getArguments().getString("loguser");

        if (db.getAllAccounts().size() == 0) {
            Toast.makeText(getActivity(), "You have no accounts you can use to pay",Toast.LENGTH_LONG).show();
        } else {
            final List<String> payAccounts = new ArrayList<>();
            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                if (db.getAllAccounts().get(i).getType().equals("Debit") && db.getAllAccounts().get(i).getUsername().equals(user)) {
                    payAccounts.add(db.getAllAccounts().get(i).getAccNum());
                } else if (db.getAllAccounts().get(i).getType().equals("Credit") && db.getAllAccounts().get(i).getUsername().equals(user)) {
                    payAccounts.add(db.getAllAccounts().get(i).getAccNum());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, payAccounts);
            accSpinner.setAdapter(adapter);
            accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenAcc = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected " + chosenAcc, Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                        if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                db.getAllAccounts().get(i).getType().equals("Debit")) {
                            textCredLim.setVisibility(View.GONE);
                            currCredLim.setVisibility(View.GONE);
                            payMoney = db.getAllAccounts().get(i).getBalance();
                            currentMoney.setText(String.valueOf(payMoney));
                            payLimit = 0;
                        } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                db.getAllAccounts().get(i).getType().equals("Credit")) {
                            textCredLim.setVisibility(View.VISIBLE);
                            currCredLim.setVisibility(View.VISIBLE);
                            payMoney = db.getAllAccounts().get(i).getBalance();
                            currentMoney.setText(payMoney);
                            payLimit = db.getAllAccounts().get(i).getLimit();
                            currCredLim.setText(payLimit);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            payment();
        }
        return view;
    }

    private void payment() {
        final String[] fromBank = new String[1];
        final String[] fromBic = new String[1];
        final String[] toBank = new String[1];
        final String[] toBic = new String[1];

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver.getEditText().getText().toString().equals("") &&
                        payAmount.getEditText().getText().toString().equals("")) {
                    receiver.setError("Give a receiver");
                    payAmount.setError("Give an amount to be payed");
                } else if (receiver.getEditText().getText().toString().equals("") ) {
                    receiver.setError("Give a receiver");
                    payAmount.setError(null);
                } else if (payAmount.getEditText().getText().toString().equals("")) {
                    payAmount.setError("Give an amount to be payed");
                    receiver.setError(null);
                } else {
                    receiver.setError(null);
                    payAmount.setError(null);
                    Date currentTime = Calendar.getInstance().getTime();
                    chosenReceiver = receiver.getEditText().getText().toString();
                    chosenAmount = Integer.parseInt(payAmount.getEditText().getText().toString());
                    if (payMoney - chosenAmount + payLimit >= 0) {
                        db.updateAccount(chosenAcc, payMoney - chosenAmount, payLimit, 0);

                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc)) {
                                fromBank[0] = db.getAllAccounts().get(i).getBank();
                            }
                        }

                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenReceiver)) {
                                db.updateAccount(chosenReceiver, db.getAllAccounts().get(i).getBalance()
                                        + chosenAmount, db.getAllAccounts().get(i).getLimit(), 0);
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

                        db.addTransaction(chosenAcc, fromBank[0], fromBic[0], chosenReceiver, toBank[0],
                                toBic[0], chosenAmount, currentTime.toString(), "Payment of "
                                        + chosenAmount + "€ from " + chosenAcc + " " + fromBank[0] + " "
                                        + fromBic[0] + " to " + chosenReceiver + " " + toBank[0] + " "
                                        + toBic[0] + " on " + currentTime.toString());


                        Toast.makeText(getActivity(), chosenAcc + " " + fromBank[0] + " " + fromBic[0]
                                + " payed " + chosenAmount + "€ to " + chosenReceiver + " " + toBank[0]
                                + " " + toBic[0], Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Not enough money on the account " +
                                chosenAcc + " to make the payment.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
