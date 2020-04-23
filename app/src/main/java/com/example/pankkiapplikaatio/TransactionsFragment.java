package com.example.pankkiapplikaatio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

//For viewing information of transactions done
public class TransactionsFragment extends Fragment {

    View view;
    String user;
    Spinner accSpinner;
    ListView transList;
    String chosenAcc;
    private BankDbHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transactions, container, false);
        getActivity().setTitle("Transactions");
        db = new BankDbHelper(this.getActivity());

        //Getting the username of the logged in user from the MainActivity and saving it
        user = getArguments().getString("loguser");

        accSpinner = view.findViewById(R.id.spinnerAcc);
        transList = view.findViewById(R.id.transList);

        if (db.getAllAccounts().size() == 0) {
            Toast.makeText(getActivity(), "You have no accounts",Toast.LENGTH_LONG).show();
        } else {
            List<String> accounts = new ArrayList<>();
            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                if (db.getAllAccounts().get(i).getUsername().equals(user)) {
                    accounts.add(db.getAllAccounts().get(i).getAccNum());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, accounts);
            accSpinner.setAdapter(adapter);
            accSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenAcc = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected " + chosenAcc, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (db.getAllTransactions().size() == 0) {
                Toast.makeText(getActivity(), "No transactions for this account",Toast.LENGTH_LONG).show();
            } else {
                List<String> myAccounts = new ArrayList<>();
                for (int i = 0; i < db.getAllTransactions().size(); i++) {
                    if (db.getAllTransactions().get(i).getFromAcc().equals(chosenAcc)) {
                        myAccounts.add(db.getAllTransactions().get(i).getDescription());
                    } else if (db.getAllTransactions().get(i).getToAcc().equals(chosenAcc)) {
                        myAccounts.add(db.getAllTransactions().get(i).getDescription());
                    }
                }
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, myAccounts);
                transList.setAdapter(listAdapter);
                transList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //String chosenTrans = transList.getItemAtPosition(position).toString();
                    }
                });
            }
        }
        return view;
    }
}
