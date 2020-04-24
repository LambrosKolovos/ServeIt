package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.home_screen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class new_order extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        final SearchView searchMenu = findViewById(R.id.searchMenu);
        searchMenu.onActionViewExpanded();

        searchMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.new_order);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), store_layout.class));
                        overridePendingTransition(0,0);
                    case R.id.new_order:
                        return true;
                    case R.id.active_orders:
                        startActivity(new Intent(getApplicationContext(), active_order.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), settings.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), home_screen.class));
    }

    public void loadItemNotes(View view) {
        startActivity(new Intent(getApplicationContext(), item_notes.class));
    }
}
