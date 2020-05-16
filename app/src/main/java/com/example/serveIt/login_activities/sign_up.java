package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.*;
import com.example.serveIt.employee_activities.employee_activity;
import com.example.serveIt.employee_activities.settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {

    RelativeLayout login_container;
    Button signUp_btn;
    EditText name, email, password;
    ProgressBar pb;
    Spinner user_menu;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    private boolean isOwner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        login_container = findViewById(R.id.login_container);
        signUp_btn = findViewById(R.id.sign_up);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        user_menu = findViewById(R.id.menu);

        pb = findViewById(R.id.progressbar);
        pb.setVisibility(View.GONE);

        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name_text = name.getText().toString();
                final String email_text = email.getText().toString();
                final String pass_text = password.getText().toString();
                final String isOwner_text = user_menu.getSelectedItem().toString();

                if(isOwner_text.equals("Owner"))
                    isOwner = true;
                else
                    isOwner = false;

                if(name_text.isEmpty()){
                    name.setError("Please enter full name");
                    name.requestFocus();
                }
                else if(email_text.isEmpty()){
                    email.setError("Please enter email");
                    email.requestFocus();
                }
                else if (pass_text.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else{
                    pb.setVisibility(View.VISIBLE);
                    login_container.setBackgroundColor(Color.parseColor("#dddddd"));
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mAuth.createUserWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User user = new User(
                                        email_text,
                                        name_text,
                                        isOwner
                                );

                                database.getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            pb.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            login_container.setBackgroundColor(Color.WHITE);
                                            Toast.makeText(sign_up.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), employee_activity.class));
                                        }
                                        else{
                                            Toast.makeText(sign_up.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(sign_up.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                login_container.setBackgroundColor(Color.WHITE);
                            }
                        }
                    });
                }

            }
        });
    }

}
