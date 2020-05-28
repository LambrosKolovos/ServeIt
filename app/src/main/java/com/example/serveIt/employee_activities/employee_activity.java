package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class employee_activity extends AppCompatActivity {

    Bundle b;
    String storeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_activity);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        b = getIntent().getExtras();
        if( b.getSerializable("currentOrder") != null){
            storeID = (String) b.getSerializable("storeID");
            bottomNav.setSelectedItemId(R.id.new_order);
            Fragment selectedFragment = new new_order();
            selectedFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }
        else{
            storeID = (String) b.getSerializable("storeID");
            bottomNav.setSelectedItemId(R.id.store);
            Fragment selectedFragment = new store_layout();
            selectedFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.store:
                            selectedFragment = new store_layout();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.new_order:
                            selectedFragment = new new_order();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.active_orders:
                            selectedFragment = new active_order();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.settings:
                            selectedFragment = new settings();
                            selectedFragment.setArguments(b);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}
