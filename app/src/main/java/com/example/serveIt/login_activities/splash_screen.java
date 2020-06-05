package com.example.serveIt.login_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.User;
import com.example.serveIt.employee_activities.MAIN_EMPLOYEE_ACTIVITY;
import com.example.serveIt.employee_activities.search_store;
import com.example.serveIt.owner_activities.MAIN_OWNER_ACTIVITY;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splash_screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startHome();
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




    }

    private void startHome(){
        startActivity(new Intent(getApplicationContext(), home.class));
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
                    startActivity(new Intent(getApplicationContext(), MAIN_OWNER_ACTIVITY.class));
                }
                else{
                    if(user.getWorkID().equals(" ")){
                        Intent i = new Intent(getApplicationContext(), search_store.class);
                        i.putExtra("userLoggedIn", user);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(getApplicationContext(), MAIN_EMPLOYEE_ACTIVITY.class);
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
