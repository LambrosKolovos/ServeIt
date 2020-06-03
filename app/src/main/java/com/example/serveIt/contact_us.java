package com.example.serveIt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class contact_us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPref sharedPref = new SharedPref(this);
        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_contact_us);

        Spinner dropdown = findViewById(R.id.subject_pick);
        String[] items = new String[]{"-SELECT A SUBJECT-", "Report an issue", "Give feedback"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.contact_spinner_item, items);
        dropdown.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return true;
    }
}
