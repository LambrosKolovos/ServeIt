package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serveIt.R;
import com.example.serveIt.Store;
import com.example.serveIt.Table;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class build_layout extends Fragment {

    FloatingActionButton add_table;
    private int i=0 , tableID = 0;

    private FirebaseDatabase database;
    private DatabaseReference table_ref, store_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<Table> tablelist;
    private TableAdapter tableAdapter;

    private RecyclerView table_view;
    private TextView restaurant_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_build_layout, container, false);

        getActivity().setTitle("Table layout");

        add_table = root.findViewById(R.id.table_btn);
        table_view = root.findViewById(R.id.table_view);
        restaurant_name = root.findViewById(R.id.restaurant_name);

        tablelist = new ArrayList<>();
        table_view.setLayoutManager(new GridLayoutManager(requireContext(), calculateNoOfColumns(requireContext(),130)));
        tableAdapter = new TableAdapter(tablelist,requireContext());

        table_view.setAdapter(tableAdapter);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        table_ref = database.getReference("Table");
        store_ref = database.getReference("Store");

        user = mAuth.getCurrentUser();

        showRestName();


        readFromDB(new FirebaseCallback() {
            @Override
            public void onCallback(final int id) {
                tableID = id;
                if(id > 0)
                    tableID = tablelist.get(id - 1).getID();
                else
                    tableID = 0;

                add_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!tablelist.isEmpty())
                        tableID = tablelist.get(tableAdapter.getItemCount() - 1).getID();
                    else
                        tableID = 0;

                    tableID++;

                    tableAdapter.add(tableID);
                    table_view.setAdapter(tableAdapter);
                }
            });
        }
        });


        return root;
    }

    private void readFromDB(final FirebaseCallback firebaseCallback){
        store_ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        String storeID = null;

                        System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot store: data.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            table_ref.child(storeID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                            int size_tables = 0;

                                            for(DataSnapshot tables: dataSnapshot.getChildren()){
                                                Table table = tables.getValue(Table.class);
                                                tablelist.add(table);
                                                size_tables++;
                                            }

                                            firebaseCallback.onCallback(size_tables);

                                            tableAdapter.notifyDataSetChanged();
                                          //  table_view.setAdapter(tableAdapter);
                                          //  table_view.setAdapter(tableAdapter);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showRestName(){

        store_ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        String storeID = null;

                        System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot store: data.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null) {
                            store_ref.child(storeID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Store store = dataSnapshot.getValue(Store.class);

                                            if(store != null)
                                                restaurant_name.setText(store.getName());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }


    private interface FirebaseCallback{
        void onCallback(int id);
    }



}