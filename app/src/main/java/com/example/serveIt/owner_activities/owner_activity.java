package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.example.serveIt.employee_activities.store_layout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class owner_activity extends AppCompatActivity {

    Bundle b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        b = getIntent().getExtras();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        SharedPref sharedPref = new SharedPref(this);
        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        if(b != null) {
            if (b.getBoolean("trigger")) {
                bottomNav.setSelectedItemId(R.id.settings);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new settings()).commit();
            }
        }
        else {
            bottomNav.setSelectedItemId(R.id.layout);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new build_layout()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                  @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.layout:
                            selectedFragment = new build_layout();
                            break;
                        case R.id.menu:
                            selectedFragment = new menu_page();
                            break;
                        case R.id.staff:
                            selectedFragment = new manage_staff();
                            break;
                        case R.id.settings:
                            selectedFragment = new settings();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };



}
