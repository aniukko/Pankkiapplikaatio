package com.example.pankkiapplikaatio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

//For creating a new user
public class CreateUserActivity extends AppCompatActivity {

    //For creating requirements for the password
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +                     //at least 1 digit
                    "(?=.*[a-z])" +                     //at least 1 lower case letter
                    "(?=.*[A-Z])" +                     //at least 1 upper case letter
                    "(?=.*[@#$%^&+=.,:;!/?<>])" +       //at least 1 special character
                    "(?=\\S+$)" +                       //no white spaces
                    ".{12,}" +                          //at least 12 characters
                    "$");

    TextInputLayout username;
    TextInputLayout password;
    TextInputLayout name;
    TextInputLayout address;
    TextInputLayout phoneNum;
    Button save;

    private BankDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        db = new BankDbHelper(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phoneNum = findViewById(R.id.phoneNum);
        save = findViewById(R.id.save);

        createUser();
    }

    //Creates a new user, adds it to the database and returns to the log in screen
    private void createUser() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUsername();
                validatePassword();
                if (validateUsername() && validatePassword()) {
                    db.addUsers(username.getEditText().getText().toString(), password.getEditText().getText().toString(),
                            name.getEditText().getText().toString(), address.getEditText().getText().toString(),
                            phoneNum.getEditText().getText().toString());
                    Toast.makeText(CreateUserActivity.this, "User " +
                            username.getEditText().getText().toString() + " created", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateUserActivity.this, LogInActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //Checks if the new username is valid
    //If not it gives an error message
    private boolean validateUsername() {
        String input = username.getEditText().getText().toString();
        for (int i = 0; i < db.getAllUsers().size(); i++) {
            if (input.isEmpty()) {
                username.setError("Field can't be empty");
                return false;
            } else if (input.length() > 20) {
                username.setError("Username too long");
                return false;
            } else if (input.equals(db.getAllUsers().get(i).getUsername())) {
                username.setError("Username already in use");
                return false;
            } else {
                username.setError(null);
                return true;
            }
        }
        return true;
    }

    //Checks if the new password is valid
    //If not it gives an error message
    private boolean validatePassword() {
        String input = password.getEditText().getText().toString();
        if (input.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
            //Checks if the password matches with the requirements for the password
        } else if (!PASSWORD_PATTERN.matcher(input).matches()) {
            password.setError("Password too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    //Returns to the log in screen when the back button is pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateUserActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}
