package com.example.pankkiapplikaatio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

//The main activity controls the programme and the navigation drawer.
//Opens different fragments and activities
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    String logUser;

    TextView header;
    TextView noAccs;

    ListView listView;

    private BankDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setTitle("Home");

        //Getting the access to the BankDbHelper
        db = new BankDbHelper(this);

        header = findViewById(R.id.header);
        noAccs = findViewById(R.id.noAccs);
        listView = findViewById(R.id.listView);

        //Sets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creates the side menu/drawer for the app
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //The username of the user that has logged in is saved here
        //The username is brought from the LogInActivity
        logUser = getIntent().getStringExtra("loguser");

        createHome();
    }

    //Adds all the accounts the user has to a ListView
    public void createHome() {
        String bic = "";
        ArrayList<String> myAccounts = new ArrayList<>();
        if (db.getAllAccounts().size() == 0) {
            noAccs.setVisibility(View.VISIBLE);
        } else {
            //Info from two different tables needs to combined to show wanted info
            //Info will first be added to a list and then the list will be added to a listview
            for (int i = 0; i < db.getAllAccounts().size(); i++) {
                for (int j = 0; j < db.getAllBanks().size(); j++) {
                    if (db.getAllBanks().get(j).getName().equals(db.getAllAccounts().get(i).getBank())) {
                        bic = db.getAllBanks().get(j).getBIC();
                    }
                }
                if (db.getAllAccounts().get(i).getUsername().equals(logUser)) {
                    myAccounts.add("Bank: " + db.getAllAccounts().get(i).getBank() + " " + bic + "\n" +
                            "Account number: " + db.getAllAccounts().get(i).getAccNum() + "\n" +
                            "Account type: " + db.getAllAccounts().get(i).getType() + "\n" +
                            "Balance: " + db.getAllAccounts().get(i).getBalance());
                    noAccs.setVisibility(View.GONE);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, myAccounts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String acc = listView.getItemAtPosition(position).toString();
                //Parsing the selected account number and saving it to a variable
                String[] accNumb = acc.split("\n");
                String[] accNumbNumb = accNumb[1].split(" ");
                //Opening the ShowAccountActivity for the selected account
                Intent intent = new Intent(getApplicationContext(), ShowAccountActivity.class);
                intent.putExtra("loguser", logUser);
                intent.putExtra("accNum", accNumbNumb[2]);
                intent.putExtra("admin", "user");
                startActivity(intent);
            }
        });
    }

    //Opens different activities and fragments depending on the navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        header.setVisibility(View.GONE);
        noAccs.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        //Creating a bundle that can be used to sending the username of the logged in user to different fragments
        Bundle b = new Bundle();
        b.putString("loguser", logUser);

        //Opening new activities or fragments and sending information to them depending on what has been selected
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intentHome = new Intent(this, MainActivity.class);
                intentHome.putExtra("loguser", logUser);
                startActivity(intentHome);
                break;
            case R.id.nav_create_account:
                CreateAccountFragment newAccFragment = new CreateAccountFragment();
                newAccFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        newAccFragment).commit();
                break;
            case R.id.nav_pay:
                PayFragment payFragment = new PayFragment();
                payFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        payFragment).commit();
                break;
            case R.id.nav_use_card:
                UseCardFragment useCardFragment = new UseCardFragment();
                useCardFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        useCardFragment).commit();
                break;
            case R.id.nav_my_card:
                Intent intentMyCard = new Intent(this, ShowCardActivity.class);
                intentMyCard.putExtra("loguser", logUser);
                intentMyCard.putExtra("admin", "user");
                startActivity(intentMyCard);
                break;
            case R.id.nav_card:
                Intent intentCard = new Intent(this, CreateCardActivity.class);
                intentCard.putExtra("loguser", logUser);
                intentCard.putExtra("admin", "user");
                startActivity(intentCard);
                break;
            case R.id.nav_transactions:
                TransactionsFragment transFragment = new TransactionsFragment();
                transFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        transFragment).commit();
                break;
            case R.id.nav_user_settings:
                Intent intentUser = new Intent(this, UserSettingsActivity.class);
                intentUser.putExtra("loguser", logUser);
                intentUser.putExtra("admin", "user");
                startActivity(intentUser);
                break;
            case R.id.nav_logout:
                //If the user wants to log out, an alert dialog will appear where the user needs to
                // confirm that they want to log out
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Log out");
                builder.setMessage("Are you sure you want to log out?");
                builder.setCancelable(true);
                builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
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
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Closes the drawer with the press of the back button
    //If the drawer is closed, a press of the back button will open an alert dialog where the user
    // is asked whether they want to log out
   @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Log Out");
            builder.setMessage("Are you sure you want to log out of the app?");
            builder.setCancelable(true);
            builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
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
    }
}
