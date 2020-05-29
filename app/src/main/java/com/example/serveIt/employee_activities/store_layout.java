package com.example.serveIt.employee_activities;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.example.serveIt.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class store_layout extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private TableLayout table_view;
    private TableRow currentRow;
    Bundle b;
    String storeID;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_store_layout, container, false);

        currentRow= new TableRow(getContext());
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Table");
        table_view = root.findViewById(R.id.table_view);

        b = getArguments();
        if(b!=null){
            storeID =(String) b.getSerializable("storeID");
            System.out.println(storeID);
        }
      //  readFromDB();
        addViews();

        return root;
    }

    private void readFromDB(){
        ref.child(storeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Table table = data.getValue(Table.class);
                    addTableView(table.getID(), currentRow);
                    if (table.getID() % 3  == 0) {
                        currentRow = new TableRow(getContext());
                        currentRow.setPadding(10, 10, 10, 10);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addViews(){
        for(int j=1 ; j<12; j++){
            if (j % 3 == 0) {
                currentRow = new TableRow(getContext());
                currentRow.setPadding(10, 10, 10, 10);
            }
            addTableView(j, currentRow);

        }
    }


    public void addTableView(final int id, final TableRow row){
        //Convert px to dp
        int padding = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int  x = (int) (padding * scale + 0.5f);

        final Button tableIcon = new Button(getContext());

        tableIcon.setText(String.valueOf(id));
        //Check of table status - this needs to change
        tableIcon.setBackgroundResource(R.drawable.table_available);

        tableIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open new fragment
                openNewOrder(id);

                //Update table's status in DB;

                //This sould go
                tableIcon.setBackgroundResource(R.drawable.order_taken);
            }
        });


        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        params.setMargins(x, 0, x, x);
        tableIcon.setLayoutParams(params);

        if( id % 3 == 0){
            table_view.addView(row);
            row.addView(tableIcon);

        }
        else{
            row.addView(tableIcon);
        }
    }

    public void openNewOrder(int ID){
        Bundle a = new Bundle();
        a.putSerializable("storeID", storeID);
        a.putInt("tableID", ID);

        Fragment nextFrag= new new_order();
        nextFrag.setArguments(a);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag)
                .addToBackStack(null)
                .commit();
    }

}
