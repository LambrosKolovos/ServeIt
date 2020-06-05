package com.example.serveIt.owner_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;

/*
    This is the help page for owner. A quick tutorial for owners
    to set up their store
 */
public class help_owner extends AppCompatActivity {
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = new SharedPref(this);

        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_help_owner);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
