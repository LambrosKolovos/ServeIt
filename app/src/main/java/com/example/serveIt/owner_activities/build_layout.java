package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class build_layout extends Fragment {

    FloatingActionButton add_table;
    TableLayout table_view;
    private int i=0 , tableID = 0;
    private ArrayList<Table> tablelist;
    private TableRow currentRow;

    private FirebaseDatabase database;
    private DatabaseReference table_ref, store_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private int table_number;

    private float scale;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_build_layout, container, false);

        add_table = root.findViewById(R.id.table_btn);
        table_view = root.findViewById(R.id.table_view);
        mAuth = FirebaseAuth.getInstance();

        currentRow = new TableRow(getContext());
        tablelist = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        table_ref = database.getReference("Table");
        store_ref = database.getReference("Store");

        user = mAuth.getCurrentUser();

        table_number = 0;

        //Convert px to dp
        int padding = 10;
        scale = getResources().getDisplayMetrics().density;
        final int padd_bottom = (int) (padding * scale + 0.5f);

        currentRow.setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);

        loadFromDB(new FirebaseCallback() {
            @Override
            public void onCallback(final int id, final Context context) {
                tableID = id;

                add_table.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTableView(tableID, currentRow, context);
                        tableID++;
                        if (tableID % 3  == 0) {
                            currentRow = new TableRow(context);
                            currentRow.setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);
                        }
                    }
                });
            }
        }, getContext());


        return root;
    }

    public void loadFromDB(final FirebaseCallback firebaseCallback, final Context context){
        store_ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String storeID = null;
                        System.out.println("THE NUMBER IS: " + dataSnapshot.getChildrenCount());
                        for(DataSnapshot store: dataSnapshot.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            table_ref.child(storeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
                                        table_number++;
                                        addTableView(table_number - 1, currentRow, context);
                                        if (table_number % 3  == 0) {
                                            currentRow = new TableRow(context);
                                            currentRow.setPadding(10, 10, 10, 10);
                                        }
                                    }

                                    firebaseCallback.onCallback(table_number, context);

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

    public void addTableView(final int id, final TableRow row, final Context context){
        //Convert px to dp
        int padding = 10;
        scale = context.getResources().getDisplayMetrics().density;
        int  x = (int) (padding * scale + 0.5f);

        final Button tableView = new Button(context);
        final Table table = new Table(id+1, "AVAILABLE");

        tableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 2){
                            tablelist.remove(table);
                            row.removeView(tableView);
                            deleteTableDB(String.valueOf(id));
                            Toast.makeText(context, "Removing: " + (id+1), Toast.LENGTH_SHORT).show();
                        }
                        i = 0;
                    }
                }, 300);
            }
        });

        tableView.setText(String.valueOf(id+1));
        tableView.setBackgroundResource(R.drawable.table_available);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        params.setMargins(x, 0, x, x);
        tableView.setLayoutParams(params);

        if( id % 3 == 0){
            table_view.addView(row);
            row.addView(tableView);

        }
        else{
            row.addView(tableView);
        }

        tablelist.add(table);
        addTableToDB();
    }

    private void addTableToDB(){
        store_ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String storeID = null;
                        System.out.println("THE NUMBER IS: " + dataSnapshot.getChildrenCount());
                        for(DataSnapshot store: dataSnapshot.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            table_ref.child(storeID)
                                    .setValue(tablelist);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void deleteTableDB(final String id){

        store_ref.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String storeID = null;
                        System.out.println("THE NUMBER IS: " + dataSnapshot.getChildrenCount());
                        for(DataSnapshot store: dataSnapshot.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            table_ref.child(storeID)
                                    .child(id)
                                    .removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private interface FirebaseCallback{
        void onCallback(int id, Context context);
    }
}
