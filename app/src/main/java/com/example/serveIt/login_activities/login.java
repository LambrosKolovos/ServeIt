package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.employee_activities.employee_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.serveIt.employee_activities.settings;

public class login extends AppCompatActivity {

    FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthState;

    EditText email, password;
    Button login_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, employee_activity.class));
            this.finish();
        }else{
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
        }

        /*
        mAuthState = new FirebaseAuth.AuthStateListener() {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseUser != null){
                    Toast.makeText(login.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), employee_activity.class));
                }
                else{
                    Toast.makeText(login.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

         */
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  mAuth.addAuthStateListener(mAuthState);
    }

    public void loginClick(View view){
        String email_text = email.getText().toString();
        String pass_text = password.getText().toString();

        if(email_text.isEmpty()){
            email.setError("Please enter email");
            email.requestFocus();
        }
        else if (pass_text.isEmpty()){
            password.setError("Please enter password");
            password.requestFocus();
        }
        else{
            mAuth.signInWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), employee_activity.class));
                    }
                }
            });
        }

    }
}
