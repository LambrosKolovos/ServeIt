package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.*;
import com.example.serveIt.employee_activities.employee_activity;
import com.example.serveIt.employee_activities.settings;
import com.example.serveIt.owner_activities.build_layout;
import com.example.serveIt.owner_activities.owner_activity;
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
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    Switch owner_switch;
    EditText store_name;
    TextView login_view, alert;

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
        owner_switch = findViewById(R.id.owner_switch);
        store_name = findViewById(R.id.store_name);
        alert = findViewById(R.id.inputCheck);
        login_view = findViewById(R.id.login_message);


        alert.setVisibility(View.INVISIBLE);

        login_view.setPaintFlags(login_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login_view.setText("Already a member? Login here!");
        login_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });


        owner_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    store_name.setVisibility(View.VISIBLE);
                    Animation slide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_open);
                    slide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            store_name.setHint("Business name");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    store_name.startAnimation(slide);
                    isOwner = true;
                }
                else{
                    store_name.setHint("");
                    Animation slide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_close);
                    slide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            store_name.setText("");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    store_name.startAnimation(slide);
                    store_name.setVisibility(View.GONE);
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
                else if (pass_text.isEmpty()){
                    alert.setVisibility(View.VISIBLE);
                    alert.setText("Please enter a password.");
                    password.requestFocus();
                }
                else if( isOwner && store_text.isEmpty()){
                    alert.setVisibility(View.INVISIBLE);
                    alert.setText("Please enter business name.");
                    store_name.requestFocus();
                }
                else{
                    login_container.setBackgroundColor(Color.parseColor("#dddddd"));
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mAuth.createUserWithEmailAndPassword(email_text,pass_text).addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final User user = new User(
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
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            login_container.setBackgroundColor(Color.WHITE);

                                            if(user.getIsOwner()){
                                                Store store = new Store(store_text,mAuth.getCurrentUser().getUid());
                                                database.getReference("Store")
                                                        .push()
                                                        .setValue(store);
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), owner_activity.class));
                                            }
                                            else{
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), employee_activity.class));
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
                                alert.setText("Enter a valid email!");

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
