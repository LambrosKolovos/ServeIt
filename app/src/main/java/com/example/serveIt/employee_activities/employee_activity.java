package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.example.serveIt.User;
import com.example.serveIt.login_activities.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class employee_activity extends AppCompatActivity {

    Bundle b;
    String storeID;
    private DatabaseReference ref, refUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener kicked, storeDelete;
    private Dialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_activity);

        SharedPref sharedPref = new SharedPref(this);
        ref = FirebaseDatabase.getInstance().getReference("Store");
        refUser = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        logoutDialog = new Dialog(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        b = getIntent().getExtras();
        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);


        if( b.getBoolean("trigger")){
            storeID = (String) b.getSerializable("storeID");
            bottomNav.setSelectedItemId(R.id.settings);
            Fragment selectedFragment = new settings();
            selectedFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            checkWorkID();
            return;
        }
        if( b.getSerializable("currentOrder") != null){
            storeID = (String) b.getSerializable("storeID");
            bottomNav.setSelectedItemId(R.id.new_order);
            Fragment selectedFragment = new new_order();
            selectedFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }
        else{
            storeID = (String) b.getSerializable("storeID");
            bottomNav.setSelectedItemId(R.id.store);
            Fragment selectedFragment = new store_layout();
            selectedFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }

        checkWorkID();
    }

    private void checkWorkID(){

         storeDelete = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(storeID)){
                    Toast.makeText(employee_activity.this, "Store with id " + storeID + " deleted", Toast.LENGTH_SHORT).show();
                    // storeID = " ";
                    refUser.child(user.getUid()).child("workID").setValue(" ")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        refUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);

                                                if(user != null){
                                                    //  user.setWorkID(" ");
                                                    finish();
                                                    Intent i = new Intent(getApplicationContext(), search_store.class);
                                                    i.putExtra("userLoggedIn", user);
                                                    startActivity(i);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addValueEventListener(storeDelete);

        kicked = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot storeSnap) {
                if(storeSnap.hasChild(storeID)){
                    ref.child(storeID).child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userNode) {
                                    if(!dataSnapshot.hasChild(user.getUid()) && userNode.hasChild(user.getUid())){
                                        Toast.makeText(employee_activity.this, "You are kicked from store", Toast.LENGTH_SHORT).show();
                                        //    storeID = " ";
                                        refUser.child(user.getUid()).child("workID").setValue(" ")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            refUser.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    User user = dataSnapshot.getValue(User.class);

                                                                    if(user != null){
                                                                        // user.setWorkID(" ");
                                                                        finish();
                                                                        Intent i = new Intent(getApplicationContext(), search_store.class);
                                                                        i.putExtra("userLoggedIn", user);
                                                                        startActivity(i);
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // throw databaseError.toException();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addValueEventListener(kicked);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(kicked != null)
            ref.removeEventListener(kicked);

        if(storeDelete != null)
            ref.removeEventListener(storeDelete);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.store:
                            selectedFragment = new store_layout();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.new_order:
                            selectedFragment = new new_order();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.active_orders:
                            selectedFragment = new active_order();
                            selectedFragment.setArguments(b);
                            break;
                        case R.id.settings:
                            selectedFragment = new settings();
                            selectedFragment.setArguments(b);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    public void showLogoutDialog(){
        Button logoutBtn, cancelBtn;
        logoutDialog.setContentView(R.layout.logout_dialog);

        logoutBtn = logoutDialog.findViewById(R.id.log_btn);
        cancelBtn = logoutDialog.findViewById(R.id.cancel_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent i = new  Intent(getApplicationContext(), login.class);
                    startActivity(i);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });


        logoutDialog.show();
    }
}