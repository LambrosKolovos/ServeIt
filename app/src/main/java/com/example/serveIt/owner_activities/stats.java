package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.employee_activities.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class stats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        BottomNavigationView bottomNav = findViewById(R.id.owner_nav);
        bottomNav.setSelectedItemId(R.id.stats);

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
                    case R.id.staff:
                        startActivity(new Intent(getApplicationContext(), manage_staff.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.stats:
                        return true;
                }
                return false;
            }
        });

    }
}
