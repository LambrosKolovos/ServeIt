package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.serveIt.*;
import com.google.firebase.database.annotations.NotNull;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    TextView name;
    TextView logout;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        logout = root.findViewById(R.id.logoutView);
        name = root.findViewById(R.id.name);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Activity activity = getActivity();
                if(activity != null){
                    activity.finish();
                    startActivity(new Intent(getActivity(),login.class));
                }
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        //load user credentials
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            userID = mAuth.getCurrentUser().getUid();
            name.setText(mAuth.getCurrentUser().getEmail());
        }

    }

}
