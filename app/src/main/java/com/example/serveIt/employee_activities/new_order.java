package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.Order;
import com.example.serveIt.OrderDatabase;
import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class new_order extends Fragment{

    private TableLayout orderLayout;
    private TextView priceView, tableID_view;
    private LinearLayout searchMenu;
    private FloatingActionButton verifyFab;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference orderRef;
    private DatabaseReference tableRef;
    private Bundle b;

    private Dialog verificationDialog;
    private Order order;
    private String storeID;
    private int tableID;

    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_new_order, container, false);
        searchMenu = root.findViewById(R.id.searchMenu);
        orderLayout = root.findViewById(R.id.order_display);
        priceView = root.findViewById(R.id.totalPrice);
        tableID_view = root.findViewById(R.id.table_id_view);

        tableRef = FirebaseDatabase.getInstance().getReference("Table");
        sharedPref = new SharedPref(getContext());

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Menu");
        orderRef = database.getReference("Order");

        verifyFab = root.findViewById(R.id.sendOrder);
        verificationDialog = new Dialog(getContext());

        verifyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVerification(v);
            }
        });

        b = getArguments();
        if(b.getSerializable("storeID") != null){
                storeID = (String) b.getSerializable("storeID");
        }

        if(b.getInt("tableID") != 0){
            tableID = b.getInt("tableID");
            tableID_view.setText("Table: " +  tableID);
        }
        else{
            tableID_view.setText("Table: ");
        }

        if(b.getSerializable("currentOrder") != null )
            order = (Order) b.getSerializable("currentOrder");
        else{
            order = new Order();
        }


        searchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), search_food.class);
                Bundle args = new Bundle();
                args.putSerializable("sampleOrder", order);
                args.putSerializable("storeID", storeID);
                args.putInt("tableID", tableID);
                intent.putExtras(args);
                startActivity(intent);
            }
        });


        makeOrder(order);
        return root;
    }

    private void showVerification(View v){
        Button addBtn, closeBtn;
        final EditText tableField;

        verificationDialog.setContentView(R.layout.verify_order);

        addBtn = verificationDialog.findViewById(R.id.send_btn);
        closeBtn = verificationDialog.findViewById(R.id.close_btn);
        tableField = verificationDialog.findViewById(R.id.tableField);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderDatabase orderDatabase = new OrderDatabase(order.getOrdered(), tableField.getText().toString());

                if(order.getOrdered().size() != 0){
                        orderRef.child(storeID)
                                .push()
                                .setValue(orderDatabase)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            updateTableStatus(tableID);
                                            Toast.makeText(getContext(), "Order successfully sent!", Toast.LENGTH_SHORT).show();
                                            clearOrderView();
                                        }
                                        else{
                                            Toast.makeText(getContext(), "Order can't be sent!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                        verificationDialog.dismiss();
                    }
                    else
                        Toast.makeText(getContext(), "Can't send empty order", Toast.LENGTH_SHORT).show();

            }

        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationDialog.dismiss();
            }
        });

        verificationDialog.show();
    }

    public void clearOrderView(){
        order.removeAll();
        orderLayout.removeAllViews();
        refreshPriceView();
        orderLayout.addView(build_header());
    }

    public void loadItemNotes(View view, Order_Item item) {
        Intent i = new Intent(getActivity(), item_notes.class);
        b.putSerializable("item", item);
        i.putExtras(b);
        startActivity(i);
    }

    public void makeOrder(Order order) {
        for(Order_Item x : order.getOrdered()){
            TableRow item_row = build_row(x.getItem().getName(), String.valueOf(x.getQuantity()),  (x.getPrice()*x.getQuantity()) + "€");
            orderLayout.addView(item_row);
        }

       refreshPriceView();
    }


    public TableRow build_row(String item_name, String quantity, String price){

        //Converts pixel to dp
        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padd_bottom = (int) (padding_in_dp * scale + 0.5f);

        TableRow row = new TableRow(getContext());

        TextView item_view = build_view(item_name, 6, false, true, row);
        TextView quantity_view = build_view(quantity, 3,true, false, row);
        TextView price_view = build_view(price,3,true, false, row);
        TextView delete_view = build_view("x",1, true, true, row);

        row.addView(item_view);
        row.addView(quantity_view);
        row.addView(price_view);
        row.addView(delete_view);

        row.setPadding(0,0,0,padd_bottom);

        return row;
    }

    public TableRow build_header(){
        int padding_in_dp = 25;
        final float scale = getResources().getDisplayMetrics().density;
        int padd_bottom = (int) (padding_in_dp * scale + 0.5f);

        TableRow header = new TableRow(getContext());

        TextView item = new TextView(getContext());
        TextView quantity = new TextView(getContext());
        TextView price = new TextView(getContext());

        item.setText("Item");
        item.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6));

        quantity.setText("Quantity");
        quantity.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));

        price.setText("Price");
        price.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));

        header.addView(item);
        header.addView(quantity);
        header.addView(price);

        header.setPadding(0,0,0, padd_bottom);
        return header;

    }

    public TextView build_view(String name, int weight, boolean center, boolean clickable, final TableRow row){
        TextView view = new TextView(getContext());
        view.setText(name);

        if(clickable){
            view.setClickable(true);
            if(name.equals("x")){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((TextView) row.getChildAt(0)).getText().toString();
                        order.removeItem(text);
                        refreshPriceView();
                        orderLayout.removeView(row);
                    }
                });
            }
            else{
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String text = ((TextView) row.getChildAt(0)).getText().toString();
                        Order_Item item = order.findItem(text);
                        loadItemNotes(v, item);
                    }
                });
            }
        }

        if(name.equals("x")){
            if(sharedPref.loadNightMode())
                view.setTextColor(Color.parseColor("#EEEEEE"));
            else
                view.setTextColor(Color.parseColor("#252525"));
        }
        else
            view.setTextColor(Color.parseColor("#eb620e"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && center) {
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        view.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));


        return  view;
    }

    private void refreshPriceView() {
        priceView.setText("Total: " + order.getTotal_price() + "€");
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
}
