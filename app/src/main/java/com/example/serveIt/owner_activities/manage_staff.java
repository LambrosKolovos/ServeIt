package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.User;
import com.example.serveIt.owner_activities.Adapters.EmployeeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/*
    This fragment allows owner to manage their employees.
 */
public class manage_staff extends Fragment {

    private DatabaseReference ref;
    private List<User> employees;
    private EmployeeAdapter adapter;
    private RecyclerView employees_list;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.activity_manage_staff, container, false);

        getActivity().setTitle("Manage Staff");

        ref = FirebaseDatabase.getInstance().getReference("Store");
        employees_list = root.findViewById(R.id.employees_list);
        employees_list.setLayoutManager(new LinearLayoutManager(getContext()));
        employees_list.setHasFixedSize(true);

        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(employees);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        employees_list.setAdapter(adapter);

        loadEmployees();

        return root;
    }


    /*
        Reads employee for the current store data from database and displays them on screen
        using a recycler view.
     */
    private void loadEmployees(){

        ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        String storeID = null;
                       // System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot store: data.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            ref.child(storeID).child("employees")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            employees.clear();
                                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                                User employee = data.getValue(User.class);

                                                if(employee != null){
                                                    employees.add(employee);
                                                }

                                            }
                                            employees_list.setAdapter(adapter);
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
                        throw databaseError.toException();
                    }
                });



    }

}
