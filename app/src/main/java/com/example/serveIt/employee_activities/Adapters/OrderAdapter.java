package com.example.serveIt.employee_activities.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.helper_classes.Order_Item;
import com.example.serveIt.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

   private ArrayList<Order_Item> order_items;
   private Dialog notesDialog;
   private Context context;

    public OrderAdapter(ArrayList<Order_Item> order_items, Context context){
        this.order_items = order_items;
        this.context = context;

        notesDialog = new Dialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       final Order_Item order_item = order_items.get(position);

       holder.item_name.setText(order_item.getItem().getName());
       holder.item_quantity.setText(order_item.getQuantity() + "x");

       if(!order_item.getNotes().isEmpty()) {
           holder.notes.setVisibility(View.VISIBLE);
           holder.item_layout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   showNotesDialog(v, order_item.getNotes());
               }
               });
           }
    }

    private void showNotesDialog(View v, String notes_text) {
        TextView notes;
        Button close;

        notesDialog.setContentView(R.layout.notes_popup);

        notes = notesDialog.findViewById(R.id.notes);
        close = notesDialog.findViewById(R.id.close_btn);

        notes.setText(notes_text);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog.dismiss();
            }
        });

        notesDialog.show();
    }

    @Override
    public int getItemCount() {
        return order_items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        Context context;

        TextView item_name, item_quantity;
        LinearLayout item_layout;
        ImageView notes;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.itemView = itemView;
            this.context = context;


            Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);

            item_layout = itemView.findViewById(R.id.item_layout);
            item_name = itemView.findViewById(R.id.item_name);
            item_quantity = itemView.findViewById(R.id.item_quantity);
            notes = itemView.findViewById(R.id.notes);

            item_name.clearAnimation();
            item_quantity.clearAnimation();
            notes.clearAnimation();

            item_name.startAnimation(a);
            item_quantity.startAnimation(a);
            notes.startAnimation(a);
        }

    }
}
