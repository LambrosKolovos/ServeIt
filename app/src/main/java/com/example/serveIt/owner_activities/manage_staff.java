package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.serveIt.R;
import com.example.serveIt.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class manage_staff extends Fragment {

    private DatabaseReference ref;
    private List<User> employees;
    private EmployeeAdapter adapter;
    private RecyclerView employees_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.activity_manage_staff, container, false);

        ref = FirebaseDatabase.getInstance().getReference("Store");
        employees_list = root.findViewById(R.id.employees_list);
        employees_list.setLayoutManager(new LinearLayoutManager(getContext()));
        employees_list.setHasFixedSize(true);

        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(employees);

        employees_list.setAdapter(adapter);

        loadEmployees();

        return root;
    }


    private void loadEmployees(){

        ref.child("-M8SwpTY_7LsTRQzHtYm").child("employees")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                    }
                });

    }

}
