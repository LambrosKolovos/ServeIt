package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class active_order extends Fragment {


    private RecyclerView order_list;
    private DatabaseReference database;

    private ArrayList<Order_Item> list_items;
    private OrderAdapter orderAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_active_order, container, false);

        order_list = root.findViewById(R.id.order_list);

        database = FirebaseDatabase.getInstance().getReference("Order");
        order_list.setLayoutManager(new LinearLayoutManager(getContext()));
        list_items = new ArrayList<>();
        orderAdapter = new OrderAdapter(list_items);


        order_list.setAdapter(orderAdapter);

        Query firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").orderByChild("quantity");
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               list_items.clear();
              //  System.out.println("TRIGGERED");
              //  orderAdapter.notifyDataSetChanged();
                for(DataSnapshot order: dataSnapshot.getChildren()){
                  //  if(order_list.getChildCount() == 0) {
                       // System.out.println(order.getKey());
                        for(DataSnapshot order_items: order.getChildren()){
                            //System.out.println(order_items.getKey());

                            Order_Item items = order_items.getValue(Order_Item.class);
                            if(items != null ){
                                list_items.add(items);
                            }

                        }
                 //   }

                }

                Collections.sort(list_items, new QuantityComparator());
                order_list.setAdapter(orderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadOrders(){


    }


    public static class QuantityComparator implements Comparator<Order_Item> {
        @Override
        public int compare(Order_Item o1, Order_Item o2) {
            return o2.getQuantity() - o1.getQuantity();
        }
    }
}
