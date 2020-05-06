package com.example.pankkiapplikaatio;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

//For creating a new account for a user
public class CreateAccountFragment extends Fragment {

    private String user;
    private View view;
    private Spinner spinnerBank;
    private Spinner spinnerType;
    private String chosenBank;
    private String chosenType;
    private TextInputLayout accNum;
    private TextInputLayout balance;
    private TextInputLayout limit;
    private TextInputLayout interest;
    private String chosenAccNum;
    private String chosenBalance;
    private String chosenLimit;
    private String chosenInterest;
    private TextView textLimit;
    private TextView textInterest;
    private Button save;
    private TextView canPayText;
    private Switch canPaySwitch;
    private int canPay;

    private Account account = new Account();
    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    private WriteXML writeXML = new WriteXML();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_account, container, false);
        getActivity().setTitle("Create account");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this.getActivity());

        accNum = view.findViewById(R.id.setAccNum);
        balance = view.findViewById(R.id.setBalance);
        limit = view.findViewById(R.id.setLimit);
        interest = view.findViewById(R.id.setInterest);

        spinnerBank = view.findViewById(R.id.spinnerBank);
        spinnerType = view.findViewById(R.id.spinnerType);

        canPayText = view.findViewById(R.id.canPay);
        canPaySwitch = view.findViewById(R.id.canPaySwitch);

        textLimit = view.findViewById(R.id.textViewLimit);
        textInterest = view.findViewById(R.id.textViewInterest);

        save = view.findViewById(R.id.save);

        //Getting the username of the logged in user from the MainActivity and saving it to a variable
        user = getArguments().getString("loguser");

        //Creating a spinner the user can select the bank they want to create an account to
        ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(this.getActivity(), android.R.layout.
                simple_spinner_dropdown_item, db.getAllBanks());
        spinnerBank.setAdapter(bankAdapter);
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenBank = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + chosenBank, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Creating a spinner for selecting the account type of the new account type
        //The spinner gets the account types from the Account.java where a list was set
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, account.subAccounts);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenType = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + chosenType, Toast.LENGTH_SHORT).show();

                //Setting the visibility of different TextViews and TextInputLayouts depending on
                //whether the user needs to fill the information for a certain account type
                switch (chosenType) {
                    case "Debit":
                        limit.setVisibility(View.GONE);
                        interest.setVisibility(View.GONE);
                        textLimit.setVisibility(View.GONE);
                        textInterest.setVisibility(View.GONE);
                        break;
                    case "Credit":
                        limit.setVisibility(View.VISIBLE);
                        interest.setVisibility(View.GONE);
                        textLimit.setVisibility(View.VISIBLE);
                        textInterest.setVisibility(View.GONE);
                        break;
                    case "Savings":
                        limit.setVisibility(View.GONE);
                        interest.setVisibility(View.VISIBLE);
                        textLimit.setVisibility(View.GONE);
                        textInterest.setVisibility(View.VISIBLE);

                        //Making an assumption that payments can't be made from a savings account
                        canPaySwitch.setChecked(false);
                        canPaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                //Making sure that when a savings account is selected, the switch for
                                // payments can't be turned on
                                if (canPaySwitch.isChecked()) {
                                    canPaySwitch.setChecked(false);
                                    Toast.makeText(getActivity(), "Can't pay from a savings account",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        createAccount();
        return view;
    }

    //Creates the account after a press of a button
    private void createAccount() {
        //Saves the choice of payments allowed (= 1) or disallowed (= 0)
        canPaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (canPaySwitch.isChecked()) {
                    canPay = 1;
                } else {
                    canPay = 0;
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adding the information of the accounts to the database and writing the XML-files.
                //Different types of accounts need different information.
                //If all the required fields aren't filled, an error message will appear and updates
                // are made and new files are written only when the required criteria are met.
                //When an account has been made, the home page (MainActivity) will load
                if (!db.getAllAccounts().isEmpty()) {
                    for (int i = 0; i < db.getAllAccounts().size(); i++) {
                        if (db.getAllAccounts().get(i).getAccNum().equals(accNum.getEditText().getText().toString())) {
                            //accNum.setError("Account number already in use");
                        } else {
                            chosenAccNum = accNum.getEditText().getText().toString();
                            chosenBalance = balance.getEditText().getText().toString();
                            chosenLimit = limit.getEditText().getText().toString();
                            chosenInterest = interest.getEditText().getText().toString();
                            if (chosenType.equals("Debit")) {
                                if (chosenAccNum.equals("") || chosenBalance.equals("")) {
                                    accNum.setError("Fill all the fields");
                                    balance.setError("Fill all the fields");
                                } else {
                                    accNum.setError(null);
                                    balance.setError(null);
                                    db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                            Integer.parseInt(chosenBalance), 0, 0, canPay);
                                    writeXML.writeAccounts(getActivity(), db);
                                    Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("loguser", user);
                                    startActivity(intent);
                                }
                            } else if (chosenType.equals("Credit")) {
                                if (chosenAccNum.equals("") || chosenBalance.equals("") || chosenLimit.equals("")) {
                                    accNum.setError("Fill all the fields");
                                    balance.setError("Fill all the fields");
                                    limit.setError("Fill all the fields");
                                } else {
                                    accNum.setError(null);
                                    balance.setError(null);
                                    limit.setError(null);
                                    db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                            Integer.parseInt(chosenBalance), Integer.parseInt(chosenLimit), 0, canPay);
                                    writeXML.writeAccounts(getActivity(), db);
                                    Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("loguser", user);
                                    startActivity(intent);
                                }
                            } else if (chosenType.equals("Savings")) {
                                if (chosenAccNum.equals("") || chosenBalance.equals("") || chosenInterest.equals("")) {
                                    accNum.setError("Fill all the fields");
                                    balance.setError("Fill all the fields");
                                    interest.setError("Fill all the fields");
                                } else {
                                    accNum.setError(null);
                                    balance.setError(null);
                                    limit.setError(null);
                                    db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                            Integer.parseInt(chosenBalance), 0, Integer.parseInt(chosenInterest), canPay);
                                    writeXML.writeAccounts(getActivity(), db);
                                    Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtra("loguser", user);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                } else if (db.getAllAccounts().isEmpty()) {
                    chosenAccNum = accNum.getEditText().getText().toString();
                    chosenBalance = balance.getEditText().getText().toString();
                    chosenLimit = limit.getEditText().getText().toString();
                    chosenInterest = interest.getEditText().getText().toString();
                    if (chosenType.equals("Debit")) {
                        if (chosenAccNum.equals("") || chosenBalance.equals("")) {
                            accNum.setError("Fill all the fields");
                            balance.setError("Fill all the fields");
                        } else {
                            accNum.setError(null);
                            balance.setError(null);
                            db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                    Integer.parseInt(chosenBalance), 0, 0, canPay);
                            writeXML.writeAccounts(getActivity(), db);
                            Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("loguser", user);
                            startActivity(intent);
                        }
                    } else if (chosenType.equals("Credit")) {
                        if (chosenAccNum.equals("") || chosenBalance.equals("") || chosenLimit.equals("")) {
                            accNum.setError("Fill all the fields");
                            balance.setError("Fill all the fields");
                            limit.setError("Fill all the fields");
                        } else {
                            accNum.setError(null);
                            balance.setError(null);
                            limit.setError(null);
                            db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                    Integer.parseInt(chosenBalance), Integer.parseInt(chosenLimit), 0, canPay);
                            writeXML.writeAccounts(getActivity(), db);
                            Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("loguser", user);
                            startActivity(intent);
                        }
                    } else if (chosenType.equals("Savings")) {
                        if (chosenAccNum.equals("") || chosenBalance.equals("") || chosenInterest.equals("")) {
                            accNum.setError("Fill all the fields");
                            balance.setError("Fill all the fields");
                            interest.setError("Fill all the fields");
                        } else {
                            accNum.setError(null);
                            balance.setError(null);
                            limit.setError(null);
                            db.addAccount(chosenAccNum, chosenBank, user, chosenType,
                                    Integer.parseInt(chosenBalance), 0, Integer.parseInt(chosenInterest), canPay);
                            writeXML.writeAccounts(getActivity(), db);
                            Toast.makeText(getActivity(), "Account " + chosenAccNum + " created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("loguser", user);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}