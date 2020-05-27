package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class build_layout extends Fragment {

    FloatingActionButton add_table;
    TableLayout table_view;
    private int i=0 , id = 0;
    private ArrayList<Table> tablelist;
    private TableRow currentRow;

    private FirebaseDatabase database;
    private DatabaseReference table_ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_build_layout, container, false);

        add_table = root.findViewById(R.id.table_btn);
        table_view = root.findViewById(R.id.table_view);

        currentRow = new TableRow(getContext());
        tablelist = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        table_ref = database.getReference("Table");

        // loadFromDB();

        //Convert px to dp
        int padding = 10;
        final float scale = getResources().getDisplayMetrics().density;
        final int padd_bottom = (int) (padding * scale + 0.5f);

        currentRow.setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);

        add_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTableView(id, currentRow);
                id++;
                if (id % 3  == 0) {
                    currentRow = new TableRow(getContext());
                    currentRow.setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);
                }
            }
        });
        return root;
    }

    public void loadFromDB(){
        table_ref.child("-M7sKK7wobW-3QAIbUvj").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Table table = data.getValue(Table.class);
                    addTableView(table.getID(), currentRow);
                    if (table.getID() % 3  == 0) {
                        currentRow = new TableRow(getContext());
                        currentRow.setPadding(10, 10, 10, 10);
                    }
                    id = table.getID();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addTableView(final int id, final TableRow row){
        //Convert px to dp
        int padding = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int  x = (int) (padding * scale + 0.5f);

        final Button tableView = new Button(getContext());
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
                            deleteTableDB(String.valueOf(id+1));
                            Toast.makeText(getContext(), "Removing: " + (id+1), Toast.LENGTH_SHORT).show();
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

        table_ref.child("-M7sKK7wobW-3QAIbUvj")
                .setValue(tablelist);
    }

    private void deleteTableDB(String id){
        table_ref.child("-M7sKK7wobW-3QAIbUvj")
                .child(id)
                .removeValue();
    }

}
