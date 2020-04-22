package com.example.serveIt.login_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.serveIt.R;
import com.example.serveIt.employee_activities.store_layout;
import com.example.serveIt.owner_activities.build_layout;

public class home_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button loginBtn = findViewById(R.id.login_btn);
        Button login_ownerBtn = findViewById(R.id.owner_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), store_layout.class));
            }
        });

        login_ownerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), build_layout.class));
            }
        });

    }
}
