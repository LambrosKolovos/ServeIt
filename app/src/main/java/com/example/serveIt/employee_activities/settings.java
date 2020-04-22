package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.settings);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), store_layout.class));
                        overridePendingTransition(0,0);
                    case R.id.new_order:
                        startActivity(new Intent(getApplicationContext(), new_order.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.active_orders:
                        startActivity(new Intent(getApplicationContext(), active_order.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        return true;
                }
                return false;
            }
        });
    }
}
