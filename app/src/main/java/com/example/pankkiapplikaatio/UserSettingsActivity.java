package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

//For viewing and editing user information
public class UserSettingsActivity extends AppCompatActivity {
    private TextView oldName;
    private TextView oldAddress;
    private TextView oldPhoneNum;

    private TextInputLayout newName;
    private TextInputLayout newAddress;
    private TextInputLayout newPhoneNum;

    private Button save;
    private Button changePasswordButton;
    private Button delete;

    private String user;
    private String admin;

    private BankDbHelper db;

    //Getting the access to the file that writes XML-files
    private WriteXML writeXML = new WriteXML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        getWindow().setTitle("User Settings");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        oldName = findViewById(R.id.oldName);
        oldAddress = findViewById(R.id.oldAddress);
        oldPhoneNum = findViewById(R.id.oldPhone);

        newName = findViewById(R.id.newName);
        newAddress = findViewById(R.id.newAddress);
        newPhoneNum = findViewById(R.id.newPhone);

        save = findViewById(R.id.save);
        changePasswordButton = findViewById(R.id.changePassword);
        delete = findViewById(R.id.buttonDelete);

        //Getting information from other activities
        user = getIntent().getStringExtra("loguser");
        admin = getIntent().getStringExtra("admin");

        setInfo();
        changePassword();
        saveNewInfo();
        deleteUser();
    }

    //Setting the information of the logged in user to the TextViews
    private void setInfo() {
        if (db.getAllUsers().size() == 0) {
            Toast.makeText(this, "No users", Toast.LENGTH_LONG).show();
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

    //Updating the information in the database and writing an XML-file for the user after pressing
    // the button if a field is filled.
    //Sets the new info for the textViews after the updates are made
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
                            db.updateUsers(user, db.getAllUsers().get(i).getPassword(),
                                    newName.getEditText().getText().toString(),
                                    newAddress.getEditText().getText().toString(),
                                    newPhoneNum.getEditText().getText().toString());
                            writeXML.writeUsers(UserSettingsActivity.this, db);
                        }
                    }
                    Toast.makeText(UserSettingsActivity.this, "New name: " +
                            newName.getEditText().getText().toString() +
                            "\nNew address: " + newAddress.getEditText().getText().toString() +
                            "\nNew phone number: " + newPhoneNum.getEditText().getText().toString(),
                            Toast.LENGTH_LONG).show();
                    oldName.setText(newName.getEditText().getText().toString());
                    oldAddress.setText(newAddress.getEditText().getText().toString());
                    oldPhoneNum.setText(newPhoneNum.getEditText().getText().toString());
                }
            }
        });
    }

    //Opens the dialog that allows changing the password
    private void changePassword() {
        final Bundle b = new Bundle();
        b.putString("loguser", user);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                changePasswordDialog.setArguments(b);
                changePasswordDialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    //If the user wants to delete the user, an alertDialog will appear asking if the user really
    // wants to go through with the deletion
    private void deleteUser() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingsActivity.this);
                builder.setTitle("Delete user");
                builder.setMessage("Are you sure you want to delete user " + user + "?");
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteUser(user);
                        writeXML.writeUsers(UserSettingsActivity.this, db);

                        Toast.makeText(UserSettingsActivity.this, "User " + user
                                + "deleted.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UserSettingsActivity.this, LogInActivity.class);
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

    //Will return to the home page (MainActivity) or the AdminActivity depending on who was the user
    // after the press of the back button
    @Override
    public void onBackPressed() {
        if (admin.equals("user")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("loguser", user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        }
    }
}
