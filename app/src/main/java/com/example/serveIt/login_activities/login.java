package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.example.serveIt.User;
import com.example.serveIt.employee_activities.employee_activity;
import com.example.serveIt.employee_activities.search_store;
import com.example.serveIt.owner_activities.owner_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ProgressBar loading_bar;
    SharedPref sharedPref;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        sharedPref = new SharedPref(this);

        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        register_view = findViewById(R.id.register_message);
        alert = findViewById(R.id.inputCheck);
        loading_bar = findViewById(R.id.loading_bar);

        register_view.setPaintFlags(register_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register_view.setText("Don't have an account? Sign up here!");

        register_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplication(), sign_up.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        alert.setVisibility(View.GONE);
        loading_bar.setVisibility(View.GONE);
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
            loading_bar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        loading_bar.setVisibility(View.GONE);
                        alert.setVisibility(View.VISIBLE);
                        alert.setText("Wrong credentials!");
                    }
                    else{
                        alert.setVisibility(View.GONE);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        DatabaseReference user = ref.child("Users").child(currentUser.getUid());
                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                boolean isOwner = user.getIsOwner();

                                if(isOwner){
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), owner_activity.class));
                                }
                                else{
                                    if(user.getWorkID().equals(" ")){
                                        Intent i = new Intent(getApplicationContext(), search_store.class);
                                        i.putExtra("userLoggedIn", user);
                                        startActivity(i);
                                    }
                                    else{
                                        Intent i = new Intent(getApplicationContext(), employee_activity.class);
                                        i.putExtra("storeID", user.getWorkID());
                                        startActivity(i);
                                    }
                                    finish();
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
