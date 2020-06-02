package com.example.serveIt.employee_activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class TableAdapterEmployee extends RecyclerView.Adapter<TableAdapterEmployee.ViewHolder> {

    private List<Table> tableList;
    private Dialog clearTableDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference storeRef, tableRef;
    private Context context;
    private FragmentActivity activity;

    private String storeID;

    public TableAdapterEmployee(List<Table> tableList, Context context,FragmentActivity activity, String storeID){
        this.tableList = tableList;

        firebaseAuth = FirebaseAuth.getInstance();
        storeRef = FirebaseDatabase.getInstance().getReference("Store");
        tableRef = FirebaseDatabase.getInstance().getReference("Table");

        this.context = context;
        this.storeID = storeID;
        this.activity = activity;

        clearTableDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public TableAdapterEmployee.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_layout, parent, false);
        return new TableAdapterEmployee.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull final TableAdapterEmployee.ViewHolder holder, final int position) {
        final Table table =  tableList.get(position);
        final String tableID = String.valueOf(table.getID());

        holder.tableID.setText(tableID);

        if(table.getStatus().equals("AVAILABLE"))
            holder.tableView.setCardBackgroundColor(Color.parseColor("#f0f0f0"));
        else if(table.getStatus().equals("ORDER_TAKEN"))
            holder.tableView.setCardBackgroundColor(Color.parseColor("#ffe959"));
        else
            holder.tableView.setCardBackgroundColor(Color.parseColor("#008080"));

        holder.tableView
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showClearDialog(v, table);
                        return true;
                    }
                });

        holder.tableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(table.getStatus().equals("AVAILABLE")){
                    //Open new fragment
                    openNewOrder(table.getID());
                    //Update table's status in DB;
                    updateTableStatus(table.getID());
                }
                else if(table.getStatus().equals("ORDER_TAKEN")){
                    Toast.makeText(context, "Order already taken!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context, "Order is delivered!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void openNewOrder(int ID){
        if(activity != null){
            BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_nav);
            bottomNavigationView.setSelectedItemId(R.id.new_order);
        }

        Bundle a = new Bundle();
        a.putSerializable("storeID", storeID);
        a.putInt("tableID", ID);

        Fragment nextFrag= new new_order();
        nextFrag.setArguments(a);

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag)
                .addToBackStack(null)
                .commit();
    }

    public void updateTableStatus(int id){

        tableRef.child(storeID)
                .orderByChild("id")
                .equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String tableID = null;
                        for(DataSnapshot data: dataSnapshot.getChildren())
                            tableID = data.getKey();

                        if (tableID != null) {
                            tableRef.child(storeID)
                                    .child(tableID)
                                    .child("status")
                                    .setValue("ORDER_TAKEN");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void showClearDialog(View v, final Table table){
        Button clear_btn, cancel_btn;

        clearTableDialog.setContentView(R.layout.clear_table_popup);

        clear_btn = clearTableDialog.findViewById(R.id.clear_btn);
        cancel_btn = clearTableDialog.findViewById(R.id.close_btn);

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tableRef.child(storeID)
                        .orderByChild("id")
                        .equalTo(table.getID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String tableID = null;
                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                    tableID = data.getKey();
                                }

                                if(tableID != null){
                                    tableRef.child(storeID)
                                            .child(tableID)
                                            .child("status")
                                            .setValue("AVAILABLE");

                                }

                                clearTableDialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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

    @Override
    public int getItemCount() {
        return tableList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private CardView tableView;
        private TextView tableID;
        private Context context;

        public ViewHolder(final View view, Context context){
            super(view);

            this.context = context;
            mView = view;

            tableView = view.findViewById(R.id.table_view);
            tableID = view.findViewById(R.id.table_id);


        }
    }
}
