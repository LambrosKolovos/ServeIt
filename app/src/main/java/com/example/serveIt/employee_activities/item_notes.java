package com.example.serveIt.employee_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.serveIt.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class item_notes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_notes);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.new_order);


    }
}
