package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    EditText email, password;
    Button login_btn;
    TextView register_view, alert;

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
        register_view = findViewById(R.id.register_message);
        alert = findViewById(R.id.inputCheck);

        register_view.setPaintFlags(register_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register_view.setText("Don't have an account? Sign up here!");

        register_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), sign_up.class));
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            DatabaseReference user = ref.child("Users").child(currentUser.getUid()).child("isOwner");
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isOwner = dataSnapshot.getValue(Boolean.class);

                    if(isOwner != null){
                        if(isOwner){
                            finish();
                            startActivity(new Intent(getApplicationContext(), owner_activity.class));
                        }
                        else{
                            finish();
                            startActivity(new Intent(getApplicationContext(), employee_activity.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        alert.setVisibility(View.INVISIBLE);
    }

    public void loginClick(View view){
        String email_text = email.getText().toString();
        String pass_text = password.getText().toString();

        if(email_text.isEmpty()){
            alert.setVisibility(View.VISIBLE);
            alert.setText("Email can't be empty!");
            email.requestFocus();
        }
        else if (pass_text.isEmpty()){
            alert.setVisibility(View.VISIBLE);
            alert.setText("Password can't be empty!");
            password.requestFocus();
        }
        else{
            mAuth.signInWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        alert.setVisibility(View.VISIBLE);
                        alert.setText("Wrong credentials!");
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
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), owner_activity.class));
                                    }
                                    else{
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), employee_activity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }

    }
}
