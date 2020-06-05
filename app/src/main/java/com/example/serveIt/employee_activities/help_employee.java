package com.example.serveIt.employee_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;

/*
activity that help the user to understand the app's functionality
 */
public class help_employee extends AppCompatActivity {

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

        setContentView(R.layout.activity_help_employee);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
