package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

//For editing user information
public class UserSettingsFragment extends Fragment {

    private View view;

    private TextView oldName;
    private TextView oldAddress;
    private TextView oldPhoneNum;

    private TextInputLayout newName;
    private TextInputLayout newAddress;
    private TextInputLayout newPhoneNum;

    private Button save;
    private Button delete;

    private String user;

    private BankDbHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_settings, container, false);
        getActivity().setTitle("User Settings");

        db = new BankDbHelper(this.getActivity());

        oldName = view.findViewById(R.id.oldName);
        oldAddress = view.findViewById(R.id.oldAddress);
        oldPhoneNum = view.findViewById(R.id.oldPhone);

        newName = view.findViewById(R.id.newName);
        newAddress = view.findViewById(R.id.newAddress);
        newPhoneNum = view.findViewById(R.id.newPhone);

        save = view.findViewById(R.id.save);
        delete = view.findViewById(R.id.buttonDelete);

        //Getting the username of the logged in user from the MainActivity and saving it
        user = getArguments().getString("loguser");

        setInfo();
        saveNewInfo();
        deleteUser();

        return view;
    }

    //Setting the information of the logged in user to the TextViews
    private void setInfo() {
        if (db.getAllUsers().size() == 0) {
            Toast.makeText(getActivity(), "No users", Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < db.getAllUsers().size(); i++) {
                if (db.getAllUsers().get(i).getUsername().equals(user)) {
                    oldName.setText(db.getAllUsers().get(i).getName());
                    oldAddress.setText(db.getAllUsers().get(i).getAddress());
                    oldPhoneNum.setText(db.getAllUsers().get(i).getPhoneNum());
                }
            }
        }
    }

    //Saving the new information into the database for the user after pressing the button
    //Sets the new info for the user if a TextInputLayout field is filled
    private void saveNewInfo(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newName.getEditText().getText().toString().equals("") &&
                        newAddress.getEditText().getText().toString().equals("") &&
                        newPhoneNum.getEditText().getText().toString().equals("")) {
                    newName.setError("Fill the field to change information");
                    newAddress.setError("Fill the field to change information");
                    newPhoneNum.setError("Fill the field to change information");
                }
                else {
                    newName.setError(null);
                    newAddress.setError(null);
                    newPhoneNum.setError(null);
                    for (int i = 0; i < db.getAllUsers().size(); i++) {
                        if (db.getAllUsers().get(i).getUsername().equals(user)) {
                            db.updateUsers(user, newName.getEditText().getText().toString(),
                                    newAddress.getEditText().getText().toString(),
                                    newPhoneNum.getEditText().getText().toString());
                        }
                    }
                }
            }
        });
    }

    private void deleteUser() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete user");
                builder.setMessage("Are you sure you want to delete user " + user + "?");
                builder.setCancelable(true);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteUser(user);
                        Toast.makeText(getActivity(), "User " + user
                                + "deleted.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), LogInActivity.class);
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
}
