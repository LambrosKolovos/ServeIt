package com.example.serveIt.owner_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    TextView name;
    TextView logout, stats;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings_owner, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        logout = root.findViewById(R.id.logoutView);
        name = root.findViewById(R.id.name);
        stats = root.findViewById(R.id.stats);

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment stats= new stats_page();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, stats, "statsFragment")
                        .commit();
            }
        });

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
