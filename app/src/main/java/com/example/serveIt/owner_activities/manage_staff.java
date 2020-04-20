package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.employee_activities.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class manage_staff extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        BottomNavigationView bottomNav = findViewById(R.id.owner_nav);
        bottomNav.setSelectedItemId(R.id.staff);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.layout:
                        startActivity(new Intent(getApplicationContext(), build_layout.class));
                        overridePendingTransition(0, 0);
                    case R.id.menu:
                        startActivity(new Intent(getApplicationContext(), menu.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.staff:
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(getApplicationContext(), stats.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}
