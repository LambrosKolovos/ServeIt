package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
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
    private Dialog clearTableDialog;
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
        clearTableDialog = new Dialog(getContext());

        b = getArguments();
        if(b!=null){
            storeID =(String) b.getSerializable("storeID");
            System.out.println(storeID);
        }
        readFromDB(getContext());

        return root;
    }

    private void readFromDB(final Context ctx){
        ref.child(storeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Table table = data.getValue(Table.class);
                    addTableView(ctx, table, currentRow);
                    if (table.getID() % 3  == 0) {
                        currentRow = new TableRow(ctx);
                        currentRow.setPadding(10, 10, 10, 10);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void addTableView(final Context ctx, final Table table, final TableRow row){
        //Convert px to dp
        int padding = 10;
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int  x = (int) (padding * scale + 0.5f);

        final Button tableIcon = new Button(ctx);

        tableIcon.setText(String.valueOf(table.getID()));
        //Check of table status - this needs to change

        if(table.getStatus().equals("AVAILABLE"))
            tableIcon.setBackgroundResource(R.drawable.table_available);
        else if(table.getStatus().equals("ORDER_TAKEN"))
            tableIcon.setBackgroundResource(R.drawable.order_taken);
        else
            tableIcon.setBackgroundResource(R.drawable.order_delivered);

        tableIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open new fragment
                openNewOrder(table.getID());

                //Update table's status in DB;
                updateTableStatus(table.getID());
            }
        });

        tableIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showClearDialog(v, table);
                return true;
            }
        });


        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        params.setMargins(x, 0, x, x);
        tableIcon.setLayoutParams(params);

        if( table.getID() % 3 == 0){
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

    public void updateTableStatus(int id){
        int tableID = id-1;
        ref.child(storeID)
                .child(String.valueOf(tableID))
                .child("status")
                .setValue("ORDER_TAKEN");
    }

    public void showClearDialog(View v, final Table table){
        Button clear_btn, cancel_btn;

        clearTableDialog.setContentView(R.layout.clear_table_popup);

        clear_btn = clearTableDialog.findViewById(R.id.clear_btn);
        cancel_btn = clearTableDialog.findViewById(R.id.close_btn);

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(storeID)
                        .child(String.valueOf(table.getID()-1))
                        .child("status")
                        .setValue("AVAILABLE");
                clearTableDialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTableDialog.dismiss();
            }
        });

        clearTableDialog.show();
    }
}
