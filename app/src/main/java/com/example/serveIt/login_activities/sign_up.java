package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.*;
import com.example.serveIt.employee_activities.search_store;
import com.example.serveIt.owner_activities.owner_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {

    RelativeLayout login_container;
    Button signUp_btn;
    EditText name, email, password;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    Switch owner_switch;
    EditText store_name;
    TextView login_view, alert;

    private boolean isOwner;
    private LinearLayout layout;
    private EditText store_pass;
    private ProgressBar loading_bar;


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
        owner_switch = findViewById(R.id.owner_switch);
        store_name = findViewById(R.id.store_name);
        alert = findViewById(R.id.inputCheck);
        login_view = findViewById(R.id.login_message);
        layout = findViewById(R.id.layout);
        store_pass = findViewById(R.id.store_pass);
        loading_bar = findViewById(R.id.loading_bar);

        layout.setVisibility(View.GONE);
        alert.setVisibility(View.GONE);
        loading_bar.setVisibility(View.GONE);

        login_view.setPaintFlags(login_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login_view.setText("Already a member? Login here!");
        login_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), login.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });


        owner_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layout.setVisibility(View.VISIBLE);
                    isOwner = true;
                }
                else{
                    layout.setVisibility(View.GONE);
                    isOwner = false;
                }
            }
        });


        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name_text = name.getText().toString();
                final String email_text = email.getText().toString();
                final String pass_text = password.getText().toString();
                final String store_text = store_name.getText().toString();
                final String store_code = store_pass.getText().toString();

                if(name_text.isEmpty()){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter full name.");
                    name.requestFocus();
                }
                else if(email_text.isEmpty()){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter your email.");
                    email.requestFocus();
                }
                else if (pass_text.length() < 6){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter a valid password.");
                    password.requestFocus();
                }
                else if( isOwner && store_text.isEmpty()){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter business name.");
                    store_name.requestFocus();
                }
                else if( isOwner && store_code.length() < 6){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter a valid business password.");
                }
                else{
                    alert.setVisibility(View.GONE);
                    loading_bar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final User user = new User(
                                        email_text,
                                        name_text,
                                        isOwner
                                );

                                if(!user.getIsOwner())
                                    user.setWorkID(" ");

                                database.getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            if(user.getIsOwner()){
                                                Store store = new Store(store_text,mAuth.getCurrentUser().getUid(), store_code);
                                                database.getReference("Store")
                                                        .push()
                                                        .setValue(store);
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), owner_activity.class));
                                            }
                                            else{
                                                Intent intent = new Intent(getApplicationContext(), search_store.class);
                                                intent.putExtra("userLoggedIn", user);
                                                startActivity(intent);
                                                finish();
                                            }

                                        }
                                        else{
                                            Toast.makeText(sign_up.this, "Sign up unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                alert.setVisibility(View.VISIBLE);
                                loading_bar.setVisibility(View.GONE);
                                alert.setText("Email invalid format");
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public void finish() {
        super.finish();

    }
}
