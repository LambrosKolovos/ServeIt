package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.employee_activities.employee_activity;
import com.example.serveIt.owner_activities.build_layout;
import com.example.serveIt.owner_activities.owner_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.serveIt.employee_activities.settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthState;

    EditText email, password;
    Button login_btn;
    ProgressBar progressBar;

    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.loading);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);


            DatabaseReference user = ref.child("Users").child(currentUser.getUid()).child("isOwner");
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isOwner = dataSnapshot.getValue(Boolean.class);

                    if(isOwner != null){
                        if(isOwner){
                            progressBar.setVisibility(View.GONE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), owner_activity.class));
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), employee_activity.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
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
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    else{

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        DatabaseReference user = ref.child("Users").child(currentUser.getUid()).child("isOwner");

                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Boolean isOwner = dataSnapshot.getValue(Boolean.class);

                                if(isOwner != null){
                                    if(isOwner){
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), owner_activity.class));
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), employee_activity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }
}
