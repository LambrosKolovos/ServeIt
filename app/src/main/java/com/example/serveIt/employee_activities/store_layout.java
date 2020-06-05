package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.serveIt.R;
import com.example.serveIt.employee_activities.Adapters.TableAdapterEmployee;
import com.example.serveIt.helper_classes.Store;
import com.example.serveIt.helper_classes.Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class store_layout extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference ref, storeRef;
    private RecyclerView table_view;
    private TableRow currentRow;
    private Dialog clearTableDialog;
    Bundle b;
    String storeID;

    private List<Table> tableList;
    private TableAdapterEmployee tableAdapter;
    private TextView restaurantName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_store_layout, container, false);


        requireActivity().setTitle("Store layout");

        currentRow= new TableRow(getContext());
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Table");
        storeRef = database.getReference("Store");
        table_view = root.findViewById(R.id.table_view);
        restaurantName = root.findViewById(R.id.restaurant_name);


        table_view.setLayoutManager(new GridLayoutManager(requireContext(), calculateNoOfColumns(requireContext(),130)));
        tableList = new ArrayList<>();

        clearTableDialog = new Dialog(requireContext());

        b = getArguments();
        if(b!=null){
            storeID =(String) b.getSerializable("storeID");
            System.out.println(storeID);
        }

        tableAdapter = new TableAdapterEmployee(tableList, requireContext(), requireActivity(), storeID);

        table_view.setAdapter(tableAdapter);


        showRestName();
        readFromDB();

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(clearTableDialog != null)
            clearTableDialog.dismiss();
    }

    private void readFromDB(){
        ref.child(storeID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tableList.clear();
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Table table = data.getValue(Table.class);
                            tableList.add(table);
                        }

                        table_view.setAdapter(tableAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showRestName(){
        storeRef.child(storeID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Store store = dataSnapshot.getValue(Store.class);

                        if(store != null)
                            restaurantName.setText(store.getName());
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
}
