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

//For doing transactions: paying to others and transferring money to the same user
public class PayFragment extends Fragment {

    private View view;
    private BankDbHelper db;

    private Spinner accSpinner;
    private TextView currentMoney;
    private TextView currentType;
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

    //Getting the access to the file that writes XML-files
    private WriteXML writeXML = new WriteXML();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pay, container, false);
        getActivity().setTitle("Pay");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this.getActivity());

        accSpinner = view.findViewById(R.id.payAccSpinner);
        currentMoney = view.findViewById(R.id.currentMoney);
        currentType = view.findViewById(R.id.currentType);
        textCredLim = view.findViewById(R.id.textViewCredLim);
        currCredLim = view.findViewById(R.id.currentCredLim);
        receiver = view.findViewById(R.id.receivingAcc);
        payAmount = view.findViewById(R.id.payAmount);
        pay = view.findViewById(R.id.payButton);

        //Getting the username of a certain user and saving it to a variable
        user = getArguments().getString("loguser");

        //Adding the accounts that can be used for payments to a list and adding that list to a spinner
        if (db.getAllAccounts().size() == 0) {
            Toast.makeText(getActivity(), "You have no accounts you can use to pay",Toast.LENGTH_LONG).show();
        } else {
            final List<String> payAccounts = new ArrayList<>();
            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                if (db.getAllAccounts().get(i).getType().equals("Debit") &&
                        db.getAllAccounts().get(i).getUsername().equals(user) &&
                        db.getAllAccounts().get(i).getCanPay() == 1) {
                    payAccounts.add(db.getAllAccounts().get(i).getAccNum());
                } else if (db.getAllAccounts().get(i).getType().equals("Credit") &&
                        db.getAllAccounts().get(i).getUsername().equals(user) &&
                        db.getAllAccounts().get(i).getCanPay() == 1) {
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

                    //Setting the visibility of certain fields based on the account selected and the
                    // information that needs to be shown.
                    //Setting information for textViews to see information on the account that is
                    // selected to be used for payments
                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                        if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                db.getAllAccounts().get(i).getType().equals("Debit")) {
                            textCredLim.setVisibility(View.GONE);
                            currCredLim.setVisibility(View.GONE);
                            payMoney = db.getAllAccounts().get(i).getBalance();
                            currentMoney.setText(String.valueOf(payMoney));
                            currentType.setText(db.getAllAccounts().get(i).getType());
                            payLimit = 0;
                        } else if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc) &&
                                db.getAllAccounts().get(i).getType().equals("Credit")) {
                            textCredLim.setVisibility(View.VISIBLE);
                            currCredLim.setVisibility(View.VISIBLE);
                            payMoney = db.getAllAccounts().get(i).getBalance();
                            currentMoney.setText(String.valueOf(payMoney));
                            currentType.setText(db.getAllAccounts().get(i).getType());
                            payLimit = db.getAllAccounts().get(i).getLimit();
                            currCredLim.setText(String.valueOf(payLimit));
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

    //Does the payment. Updates the tables, writes the XML-files again and adds transactions after a button press
    private void payment() {
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

                    //Saving the current time to a variable
                    Date currentTime = Calendar.getInstance().getTime();
                    chosenReceiver = receiver.getEditText().getText().toString();
                    chosenAmount = Integer.parseInt(payAmount.getEditText().getText().toString());

                    //If there is enough money in the account (taking the credit limit into consideration),
                    // the payment will be made
                    if (payMoney - chosenAmount + payLimit >= 0) {
                        db.updateAccount(chosenAcc, payMoney - chosenAmount, payLimit, 0, 1);
                        writeXML.writeAccounts(getActivity(), db);

                        currentMoney.setText(String.valueOf(payMoney - chosenAmount));

                        String fromBank = "null";
                        String fromBic = "null";

                        //If the payment is made to an account that isn't in the database, the bank
                        // and the BIC-code of the account will be "null".
                        //Otherwise the bank and the BIC-code of the account that the payment is
                        // made to is saved and shown in the transactions
                        String toBank = "null";
                        String toBic = "null";
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenAcc)) {
                                fromBank = db.getAllAccounts().get(i).getBank();
                            }
                        }
                        for (int i = 0; i < db.getAllAccounts().size(); i++) {
                            if (db.getAllAccounts().get(i).getAccNum().equals(chosenReceiver)) {
                                db.updateAccount(chosenReceiver, db.getAllAccounts().get(i).getBalance()
                                        + chosenAmount, db.getAllAccounts().get(i).getLimit(), 0, 1);
                                writeXML.writeAccounts(getActivity(), db);

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
                        db.addTransaction(chosenAcc, fromBank, fromBic, chosenReceiver, toBank,
                            toBic, chosenAmount, currentTime.toString(), "Payment of "
                                    + chosenAmount + "€ from " + chosenAcc + " " + fromBank + " "
                                    + fromBic + " to " + chosenReceiver + " " + toBank + " "
                                    + toBic + " on " + currentTime.toString());
                        writeXML.writeTransactions(getActivity(), db);

                        Toast.makeText(getActivity(), chosenAcc + " " + fromBank + " " + fromBic
                                + " payed " + chosenAmount + "€ to " + chosenReceiver + " " + toBank
                                + " " + toBic, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Not enough money on the account " +
                                chosenAcc + " to make the payment.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
