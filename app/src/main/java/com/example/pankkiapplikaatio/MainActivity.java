package com.example.pankkiapplikaatio;

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

//The main activity controls the programme
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

        db = new BankDbHelper(this);
        header = findViewById(R.id.header);
        noAccs = findViewById(R.id.noAccs);
        listView = findViewById(R.id.listView);
        noAccs.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating the side menu/drawer for the app
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

    public void createHome() {
        //Adding all the accounts the user has to a ListView
        String bic = "";
        ArrayList<String> myAccounts = new ArrayList<>();
        if (db.getAllAccounts().size() == 0) {
            noAccs.setVisibility(View.VISIBLE);
        } else {

            for (int i = 0; i < db.getAllAccounts().size(); i++) {

                for (int j = 0; j < db.getAllBanks().size(); j++) {
                    if (db.getAllBanks().get(j).getName().equals(db.getAllAccounts().get(i).getBank())) {
                        bic = db.getAllBanks().get(j).getBIC();
                    }
                }
                if (db.getAllAccounts().get(i).getUsername().equals(logUser)) {
                    myAccounts.add(db.getAllAccounts().get(i).getAccNum() + "\n" +
                            db.getAllAccounts().get(i).getType() + "\n" +
                            db.getAllAccounts().get(i).getBalance() + "\n" +
                            db.getAllAccounts().get(i).getBank() + " " + bic);
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
                String[] accNumb = acc.split("\n");
                Intent intent = new Intent(getApplicationContext(), ShowAccountActivity.class);
                intent.putExtra("loguser", logUser);
                intent.putExtra("accNum", accNumb[0]);
                startActivity(intent);
            }
        });
    }

    //Opens different activities and fragments depending on the navigation item selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        listView.setVisibility(View.GONE);
        header.setVisibility(View.GONE);

        //Creating a bundle that can be used to sending the username of the logged in user to different fragments
        Bundle b = new Bundle();
        b.putString("loguser", logUser);

        //Opening new activities or fragments and sending information to them depending on the item selected
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
            case R.id.nav_card:
                Intent intentCard = new Intent(this, CreateCardActivity.class);
                intentCard.putExtra("loguser", logUser);
                startActivity(intentCard);
                break;

            case R.id.nav_transactions:
                TransactionsFragment transFragment = new TransactionsFragment();
                transFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        transFragment).commit();
                break;
            case R.id.nav_user_settings:
                UserSettingsFragment usFragment = new UserSettingsFragment();
                usFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        usFragment).commit();
                break;
            case R.id.nav_logout:
                Intent intent8 = new Intent(this, LogInActivity.class);
                startActivity(intent8);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        listView.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
        createHome();

    }

    //Closes the drawer with the press of the back button
   @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
