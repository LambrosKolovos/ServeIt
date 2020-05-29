package com.example.serveIt.owner_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.serveIt.Category;
import com.example.serveIt.R;
import com.example.serveIt.login_activities.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private DatabaseReference userRef, storeRef, tableRef, ordersRef, menuRef;
    TextView name;
    TableRow logout, delete_acc;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings_owner, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        storeRef = database.getReference("Store");
        tableRef = database.getReference("Table");
        menuRef = database.getReference("Menu");
        ordersRef = database.getReference("Orders");

        logout = root.findViewById(R.id.logout_row);
        delete_acc = root.findViewById(R.id.delete_acc);
        name = root.findViewById(R.id.name);

        final FirebaseUser user = mAuth.getCurrentUser();
        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeRef.orderByChild("ownerID").equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot data) {
                                String storeID = null;
                                System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                                for(DataSnapshot store: data.getChildren()){
                                    storeID = store.getKey();
                                }

                                if(storeID != null){
                                    menuRef.child(storeID).removeValue();
                                    ordersRef.child(storeID).removeValue();
                                    tableRef.child(storeID).removeValue();
                                    storeRef.child(storeID).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        userRef.child(user.getUid())
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if( task.isSuccessful()){
                                                                            user.delete()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                                                                                                Activity activity = getActivity();
                                                                                                if(activity != null){
                                                                                                    activity.finish();
                                                                                                    startActivity(new Intent(getContext(), login.class));
                                                                                                }
                                                                                            }
                                                                                            else{
                                                                                                Toast.makeText(getContext(), "User cannot be deleted", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                        else{
                                                                            Toast.makeText(getContext(), "User's prefs cannot be" +
                                                                                    "deleted", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    else{
                                                        Toast.makeText(getContext(), "Store cannot deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw  databaseError.toException();
                            }
                        });
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

}
