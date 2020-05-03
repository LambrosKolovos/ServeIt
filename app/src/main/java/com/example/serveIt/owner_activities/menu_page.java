package com.example.serveIt.owner_activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.serveIt.R;
import com.example.serveIt.owner_activities.ui.menu.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

public class menu_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.layout:
                        startActivity(new Intent(getApplicationContext(), build_layout.class));
                        overridePendingTransition(0, 0);
                    case R.id.menu:
                        return true;
                    case R.id.staff:
                        startActivity(new Intent(getApplicationContext(), manage_staff.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(getApplicationContext(), stats_page.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}