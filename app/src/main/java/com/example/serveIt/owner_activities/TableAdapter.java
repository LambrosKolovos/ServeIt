package com.example.serveIt.owner_activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<Table> tableList;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference storeRef, tableRef;
    private Context context;

    public TableAdapter(List<Table> tableList, Context context){
        this.tableList = tableList;

        firebaseAuth = FirebaseAuth.getInstance();
        storeRef = FirebaseDatabase.getInstance().getReference("Store");
        tableRef = FirebaseDatabase.getInstance().getReference("Table");

        this.context = context;
    }

    @NonNull
    @Override
    public TableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_layout, parent, false);
        return new TableAdapter.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull final TableAdapter.ViewHolder holder, final int position) {
        final Table table =  tableList.get(position);
        final String tableID = String.valueOf(table.getID());

        holder.tableID.setText(tableID);

        holder.tableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                tableList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(position, tableList.size());

                Toast.makeText(context, "Removing table:  " + tableID, Toast.LENGTH_SHORT).show();

                storeRef.orderByChild("ownerID").equalTo(firebaseAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot data) {
                                String storeID = null;
                                System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                                for(DataSnapshot store: data.getChildren()){
                                    storeID = store.getKey();
                                }

                                if(storeID != null){
                                    final String finalStoreID = storeID;
                                    tableRef.child(storeID).
                                            orderByChild("id")
                                            .equalTo(table.getID())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String tableID = null;
                                                    for(DataSnapshot table: dataSnapshot.getChildren()){
                                                        tableID = table.getKey();
                                                    }

                                                    if(tableID != null){
                                                        tableRef.child(finalStoreID)
                                                                .child(tableID)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(tableList.size() > 0 ){

                                                                            //notifyItemRangeChanged(holder.getAdapterPosition(), tableList.size());
                                                                        }

                                                                    }
                                                                });
                                                    }
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
        });


    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public void add(int tableID){
        Table table = new Table(tableID, "AVAILABLE");
        tableList.add(table);
        addTableToDB();
        notifyItemInserted(tableList.size());
    }

    private void addTableToDB(){
        storeRef.orderByChild("ownerID").equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        String storeID = null;
                        System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot store: data.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            tableRef.child(storeID)
                                    .setValue(tableList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
