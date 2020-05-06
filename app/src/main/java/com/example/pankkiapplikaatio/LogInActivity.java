package com.example.pankkiapplikaatio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;


//For logging in to the app
//This is set to be the launcher activity in the AndroidManifest.xml
public class LogInActivity extends AppCompatActivity {

    TextInputLayout user;
    TextInputLayout password;

    private BankDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        //Getting the access to the file that writes XML-files
        WriteXML writeXML = new WriteXML();

        //Writing the XML-file for the banks.
        //Will be done whenever the app is opened, or the LogInActivity is loaded
        writeXML.writeBanks(LogInActivity.this, db);

        user = findViewById(R.id.username);
        password = findViewById(R.id.password);

        //Creating a new user or logging in with an existing one depending on the button pressed
        View.OnClickListener listener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == findViewById(R.id.signUp)) {
                    openCreateUser();
                }
                else if (v == findViewById(R.id.login)) {
                    openMainActivity();
                }
            }
        });
        Button signUp = findViewById(R.id.signUp);
        Button logIn = findViewById(R.id.login);
        signUp.setOnClickListener(listener);
        logIn.setOnClickListener(listener);
    }

    //Opens a new activity where a new account can be created
    private void openCreateUser() {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    //Logs into the app with an admin account or with a created account that has been saved to the database
    private void openMainActivity() {
        Bundle b = new Bundle();
        //Saving the username of the logged in user to a variable
        b.putString("loguser", user.getEditText().getText().toString());

        if (user.getEditText().getText().toString().equals("") &&
                password.getEditText().getText().toString().equals("")) {
            user.setError("Give a username");
            password.setError("Give a password");
        } else if (user.getEditText().getText().toString().equals("")) {
            user.setError("Give a username");
            password.setError(null);
        } else if (password.getEditText().getText().toString().equals("")) {
            password.setError("Give a password");
            user.setError(null);
        } else {
            user.setError(null);
            password.setError(null);
            if (db.getAllUsers().isEmpty()) {
                //Checks if the user wants to log in as an admin
                if (user.getEditText().getText().toString().equals("Admin") &&
                        password.getEditText().getText().toString().equals("12345")) {
                    Intent intent = new Intent(this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    user.setError("Username or password incorrect");
                    password.setError("Username or password incorrect");
                }
            } else {
                for (int i = 0; i < db.getAllUsers().size(); i++) {
                    //Checks if the user wants to log in as an admin
                    if (user.getEditText().getText().toString().equals("Admin") &&
                            password.getEditText().getText().toString().equals("12345")) {
                        Intent intent = new Intent(this, AdminActivity.class);
                        startActivity(intent);

                    //If the user has given a correct username and password combination, a dialog will
                    // appear, where the user needs to give a random code, simulating the security code for real banks
                    } else if (user.getEditText().getText().toString().equals(db.getAllUsers().get(i).getUsername()) &&
                            password.getEditText().getText().toString().equals(db.getAllUsers().get(i).getPassword())) {
                        user.setError(null);
                        password.setError(null);
                        SecurityCodeDialog securityCodeDialog = new SecurityCodeDialog();
                        securityCodeDialog.setArguments(b);
                        securityCodeDialog.show(getSupportFragmentManager(), "dialog");
                    } /*else {
                        user.setError("Username or password incorrect");
                        password.setError("Username or password incorrect");
                    }*/
                }
            }
        }
    }
}