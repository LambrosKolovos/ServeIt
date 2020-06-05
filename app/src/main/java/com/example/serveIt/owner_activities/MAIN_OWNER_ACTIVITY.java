package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;
import com.example.serveIt.login_activities.login;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MAIN_OWNER_ACTIVITY extends AppCompatActivity {

    Bundle b;
    private  Dialog logoutDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        b = getIntent().getExtras();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        logoutDialog = new Dialog(this);

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


    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    public void showLogoutDialog(){
        Button logoutBtn, cancelBtn;
        logoutDialog.setContentView(R.layout.logout_dialog);

        logoutBtn = logoutDialog.findViewById(R.id.log_btn);
        cancelBtn = logoutDialog.findViewById(R.id.cancel_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent i = new  Intent(getApplicationContext(), login.class);
                startActivity(i);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });


        logoutDialog.show();
    }

}
