package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.serveIt.employee_activities.Adapters.OrderAdapter;
import com.example.serveIt.helper_classes.Order_Item;
import com.example.serveIt.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/*
* handles order view
* */
public class active_order extends Fragment {


    private RecyclerView order_list;
    private DatabaseReference database, tableRef;

    private TextView tableId, ready_text;
    private ArrayList<Order_Item> list_items;
    private List<String> orderIDs;
    private OrderAdapter orderAdapter;
    private Button prevBtn,nextBtn,readyBtn, deleteBtn;
    private int orderNumber;
    private long orders;
    Bundle b;
    String storeID;

    private Dialog delete_all;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_active_order, container, false);

        requireActivity().setTitle("Orders");

        tableRef = FirebaseDatabase.getInstance().getReference("Table");
        order_list = root.findViewById(R.id.order_list);
        tableId = root.findViewById(R.id.table_id);
        nextBtn = root.findViewById(R.id.next_btn);
        prevBtn = root.findViewById(R.id.prev_btn);
        readyBtn = root.findViewById(R.id.readyBtn);
        deleteBtn = root.findViewById(R.id.deleteBtn);
        delete_all = new Dialog(requireContext());



        ready_text = root.findViewById(R.id.ready_text);
        ready_text.setVisibility(View.INVISIBLE);

        b = getArguments();
        if(b.getSerializable("storeID") != null){
            storeID = (String) b.getSerializable("storeID");
        }

        database = FirebaseDatabase.getInstance().getReference("Order");
        order_list.setLayoutManager(new LinearLayoutManager(getContext()));
        list_items = new ArrayList<>();
        orderIDs = new ArrayList<>();
        orderAdapter = new OrderAdapter(list_items, requireContext());
        orderNumber = 0;

        tableId.setPaintFlags(tableId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        order_list.setAdapter(orderAdapter);

        /*reads data from db*/
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(final List<String> list,final long number) {
                System.out.println(list.toString());

                orders = number;

                //check if list size is bigger than 0
                if(list.size() > 0) {

                    orderNumber = list.size() - 1;

                    final Query firebaseSearchQuery = database.child(storeID).child(list.get(list.size() - 1)).child("orderItems");
                    firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list_items.clear();

                            for (DataSnapshot order_items : dataSnapshot.getChildren()) {
                                //System.out.println(order_items.getKey());
                                Order_Item items = order_items.getValue(Order_Item.class);//retrieves the order item
                                if (items != null) {
                                    list_items.add(items);//add them to list
                                }

                            }

                            //   Collections.sort(list_items, new QuantityComparator());
                            order_list.setAdapter(orderAdapter);//bind them to an adapter to show them on the recycler view
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //delete order button
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(orders > 0){
                                String orderID = list.get(orderNumber);
                                list.remove(orderNumber);
                                orders--;
                                database.child(storeID).child(orderID).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Order deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    });

                    //handles the delete all when you long click delete button
                    deleteBtn.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(orders > 0){
                                showDeleteAllDialog(v, storeID);
                            }

                            return true;
                        }
                    });

                    tableID(orderNumber);
                    checkReady(orderNumber);
                }
                else
                    ready_text.setVisibility(View.INVISIBLE);

            }
        });

        //previous order button
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderIDs.size() > 0 ){
                    order_list.removeAllViews();
                    list_items.clear();
                    orderAdapter = new OrderAdapter(list_items, requireContext());

                    if(orderNumber > 0 )
                        orderNumber--;
                    else
                        orderNumber = orderIDs.size() - 1;

                    Query firebaseSearchQuery = database.child(storeID).child(orderIDs.get(orderNumber)).child("orderItems");
                    firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot order_items: dataSnapshot.getChildren()){
                                //System.out.println(order_items.getKey());
                                Order_Item items = order_items.getValue(Order_Item.class);
                                if(items != null ){
                                    list_items.add(items);
                                }

                            }
                            // Collections.sort(list_items, new QuantityComparator());
                            order_list.setAdapter(orderAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    tableID(orderNumber);
                    checkReady(orderNumber);
                }
            }
        });

        //next order button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderIDs.size() > 0 ){
                    order_list.removeAllViews();
                    list_items.clear();
                    orderAdapter = new OrderAdapter(list_items, requireContext());


                    if(orderNumber == orderIDs.size() - 1)
                        orderNumber = 0;
                    else
                        orderNumber++;

                    Query firebaseSearchQuery = database.child(storeID).child(orderIDs.get(orderNumber)).child("orderItems");
                    firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot order_items: dataSnapshot.getChildren()){
                                //System.out.println(order_items.getKey());
                                Order_Item items = order_items.getValue(Order_Item.class);
                                if(items != null ){
                                    list_items.add(items);
                                }

                            }
                            //  Collections.sort(list_items, new QuantityComparator());
                            order_list.setAdapter(orderAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    tableID(orderNumber);
                    checkReady(orderNumber);

                }
            }
        });

        /*ready button that save the status of the order to order is ready in the db
        and also changes the table current status
         */
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderIDs.size() > 0 ){
                    database.child(storeID).child(orderIDs.get(orderNumber)).child("ready").setValue(true);
                    database.child(storeID).child(orderIDs.get(orderNumber)).child("table").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String tableID = dataSnapshot.getValue(String.class);
                            System.out.println("TABLE ID FOR ORDER: " + tableID);
                            if(tableID != null){
                                tableRef.child(storeID).orderByChild("id").equalTo(Integer.parseInt(tableID)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String table = null;
                                        for(DataSnapshot data: dataSnapshot.getChildren()){
                                            table = data.getKey();
                                        }

                                        if(table != null){
                                            tableRef.child(storeID).child(table).child("status").setValue("ORDER_DELIVERED");
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

            }
        });


        return root;
    }

    /*
    shows the tableID from the current order
     */
    private void tableID(int orderNumber) {
        database.child(storeID).child(orderIDs.get(orderNumber)).child("table")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot tableID) {
                        if(tableID.getValue(String.class) != null)
                            tableId.setText("TABLE" + " " + tableID.getValue(String.class));
                        else
                            tableId.setText("TABLE");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    /*
    reads the order IDs from db and them to an arraylist
     */
    private void readData(final FirebaseCallback firebaseCallback){

        final Query firebaseSearchQuery = database.child(storeID);
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_items.clear();
                //  System.out.println("TRIGGERED");
                //  orderAdapter.notifyDataSetChanged();
                for(DataSnapshot order: dataSnapshot.getChildren()){
                    //  if(order_list.getChildCount() == 0) {
                    //System.out.println(order.getKey());
                    orderIDs.add(order.getKey());

                }

                firebaseCallback.onCallback(orderIDs, dataSnapshot.getChildrenCount());

                for(Order_Item x: list_items){
                    System.out.println(x.getItem().getName());
                }

                order_list.setAdapter(orderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
    check if order is ready and shows the textview
    */
    private void checkReady(int orderNumber){
        if(orderIDs.size() > 0 ){
            database.child(storeID).child(orderIDs.get(orderNumber)).child("ready").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot readyValue) {
                    if(readyValue.getValue(Boolean.class)!= null){
                        boolean value = readyValue.getValue(Boolean.class);
                        if (value)
                            ready_text.setVisibility(View.VISIBLE);
                        else
                            ready_text.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }

    /*
    dialog to delete all orders
     */
    private void showDeleteAllDialog(View v, final String storeID){
        Button del_btn;

        delete_all.setContentView(R.layout.delete_all_dialog);

        del_btn = delete_all.findViewById(R.id.del_btn);

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_items.clear();
                orderAdapter.notifyDataSetChanged();
                database.child(storeID).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "All orders deleted", Toast.LENGTH_SHORT).show();
                                delete_all.dismiss();
                            }
                        });
            }
        });

        delete_all.show();
    }

    public static class QuantityComparator implements Comparator<Order_Item> {
        @Override
        public int compare(Order_Item o1, Order_Item o2) {
            return o2.getQuantity() - o1.getQuantity();
        }
    }


    //callback to user retrieved data out of the on data change
    private interface FirebaseCallback{

        void onCallback(List<String> list, long number);
    }
}
