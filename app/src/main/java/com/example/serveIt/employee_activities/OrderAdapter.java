package com.example.serveIt.employee_activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.Food_Item;
import com.example.serveIt.Order_Item;
import com.example.serveIt.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

   private ArrayList<Order_Item> order_items;

    public OrderAdapter(ArrayList<Order_Item> order_items){
        this.order_items = order_items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Order_Item order_item = order_items.get(position);

      // if(!order_item.getItem().getName().equals("$last$")){
           holder.item_name.setText(order_item.getItem().getName());
           holder.item_quantity.setText(order_item.getQuantity() + "x");
      // }

    }

    @Override
    public int getItemCount() {
        return order_items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        Context context;

        TextView item_name, item_quantity;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.itemView = itemView;
            this.context = context;


            Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);

            item_name = itemView.findViewById(R.id.item_name);
            item_quantity = itemView.findViewById(R.id.item_quantity);

            item_name.clearAnimation();
            item_quantity.clearAnimation();

            item_name.startAnimation(a);
            item_quantity.startAnimation(a);
        }

    }
}
