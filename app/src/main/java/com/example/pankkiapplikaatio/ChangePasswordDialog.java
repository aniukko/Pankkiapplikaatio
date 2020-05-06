package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

//A dialog that appears when a user wants to change its password
public class ChangePasswordDialog extends AppCompatDialogFragment {

    //Creates a pattern that has to be matched when creating a new password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +                     //at least 1 digit
                    "(?=.*[a-z])" +                     //at least 1 lower case letter
                    "(?=.*[A-Z])" +                     //at least 1 upper case letter
                    "(?=.*[@#$%^&+=.,:;!/?<>])" +       //at least 1 special character
                    "(?=\\S+$)" +                       //no white spaces
                    ".{12,}" +                          //at least 12 characters
                    "$");
    private String user;
    private TextInputLayout oldPassword;
    private TextInputLayout newPassword;
    private TextInputLayout newPasswordAgain;
    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    private WriteXML writeXML = new WriteXML();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_change_password, null);

        //Saving the username to a variable
        user = getArguments().getString("loguser");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(getActivity());

        oldPassword = view.findViewById(R.id.oldPassword);
        newPassword = view.findViewById(R.id.newPassword);
        newPasswordAgain = view.findViewById(R.id.newPasswordAgain);

        //Builds the dialog and only updates the database and writes a new file if the required criteria are met
        builder.setView(view)
                .setTitle("Change password")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < db.getAllUsers().size(); i++) {
                            if (oldPassword.getEditText().getText().toString().equals("") ||
                                    newPassword.getEditText().getText().toString().equals("") ||
                                    newPasswordAgain.getEditText().getText().toString().equals("")) {
                                oldPassword.setError("Fill all the fields");
                                newPassword.setError("Fill all the fields");
                                newPasswordAgain.setError("Fill all the fields");
                                Toast.makeText(getActivity(), "Password change failed. Fill all the fields", Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            } else if (!db.getAllUsers().get(i).getPassword().equals(oldPassword.getEditText().getText().toString())) {
                                oldPassword.setError("Old password not correct");
                                newPassword.setError(null);
                                newPasswordAgain.setError(null);
                                Toast.makeText(getActivity(), "Password change failed. Old password incorrect", Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            } else if (!PASSWORD_PATTERN.matcher(newPassword.getEditText().getText().toString()).matches()) {
                                oldPassword.setError(null);
                                newPassword.setError("Password too weak");
                                newPasswordAgain.setError(null);
                                Toast.makeText(getActivity(), "Password change failed. New password too weak", Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            } else if (!newPassword.getEditText().getText().toString().equals(newPasswordAgain.getEditText().getText().toString())) {
                                oldPassword.setError(null);
                                newPassword.setError("New passwords don't match");
                                newPasswordAgain.setError("New passwords don't match");
                                Toast.makeText(getActivity(), "Password change failed. New passwords don't match", Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            } else {
                                oldPassword.setError(null);
                                newPassword.setError(null);
                                newPasswordAgain.setError(null);
                                db.updateUsers(user, newPassword.getEditText().getText().toString(), db.getAllUsers().get(i).getName(),
                                        db.getAllUsers().get(i).getAddress(), db.getAllUsers().get(i).getPhoneNum());
                                writeXML.writeUsers(getActivity(), db);
                                Toast.makeText(getActivity(), "Password changed for " + user, Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });

        return builder.create();
    }
}
