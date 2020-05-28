package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.serveIt.R;
import com.example.serveIt.User;
import com.example.serveIt.employee_activities.employee_activity;
import com.example.serveIt.employee_activities.search_store;
import com.example.serveIt.employee_activities.store_layout;
import com.example.serveIt.owner_activities.build_layout;
import com.example.serveIt.owner_activities.owner_activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home_screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        Button login = findViewById(R.id.login);
        Button register_btn = findViewById(R.id.register_btn);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startLogin();
                }
            }, 2500);

        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startApp(currentUser);
                }
            }, 2500);
        }


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),sign_up.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });


    }

    private void startLogin(){
        startActivity(new Intent(getApplicationContext(), login.class));
    }

    private void startApp(FirebaseUser currentUser){
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
                    if(user.getWorkID() == null){
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
