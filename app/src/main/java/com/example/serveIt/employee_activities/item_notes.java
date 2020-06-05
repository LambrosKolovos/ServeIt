package com.example.serveIt.employee_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.serveIt.helper_classes.Order;
import com.example.serveIt.helper_classes.Order_Item;
import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;

public class item_notes extends AppCompatActivity {
    Bundle b;
    Order_Item item;
    Order currentOrder;
    String storeID;
    int tableID;
    EditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref sharedPref = new SharedPref(this);
        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_item_notes);

        notes = findViewById(R.id.notes_text);

        b = getIntent().getExtras();

        if( b != null){
            item = (Order_Item) b.getSerializable("item");
        }

        if( b.getSerializable("storeID") != null)
            storeID = (String) b.getSerializable("storeID");

        if( b.getSerializable("currentOrder") != null)
            currentOrder = (Order) b.getSerializable("currentOrder");

        if( b.getInt("tableID") != 0 ){
            tableID = b.getInt("tableID");
        }

        currentOrder.removeItem(item.getItem().getName());

        if(!item.getNotes().isEmpty())
            notes.setText(item.getNotes());

        final Button incBtn = findViewById(R.id.incBtn);
        final Button decBtn = findViewById(R.id.decBtn);
        final Button addBtn = findViewById(R.id.addBtn);
        final TextView quantityView = findViewById(R.id.quantity);
        quantityView.setText(String.valueOf(item.getQuantity()));

        incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.incQuantity();
                quantityView.setText(String.valueOf(item.getQuantity()));
            }
        });
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getQuantity() > 1) {
                    item.decQuantity();
                    quantityView.setText(String.valueOf(item.getQuantity()));
                }
                else{
                   item.setQuantity(1);
                   quantityView.setText("1");
                }
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!notes.getText().toString().isEmpty()){
                    item.setNotes(notes.getText().toString());
                }
                currentOrder.addItem(item);
                b.putSerializable("currentOrder", currentOrder);
                b.putSerializable("storeID", storeID);
                b.putInt("tableID", tableID);
                Intent i = new Intent(getApplicationContext(), MAIN_EMPLOYEE_ACTIVITY.class);
                i.putExtras(b);
                finish(); //to be changed (hardcoded)
                startActivity(i);

            }
        });
    }
}

