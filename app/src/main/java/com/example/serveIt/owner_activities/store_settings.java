package com.example.serveIt.owner_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.serveIt.R;
import com.example.serveIt.SharedPref;

public class store_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Store Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPref sharedPref = new SharedPref(this);

        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_store_settings);


        Spinner dropdown = findViewById(R.id.currency);
        String[] items = new String[]{"Euros (€)", "US Dollars ($)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.contact_spinner_item, items);
        dropdown.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
