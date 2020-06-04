package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.Food_Item;
import com.example.serveIt.Order;
import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class search_food extends AppCompatActivity {

    private EditText search_bar;
    private RecyclerView resultList;
    private DatabaseReference database;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Food_Item> options;
    public Order currentOrder = new Order();
    Bundle b;
    private String storeID;
    private int tableID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPref sharedPref = new SharedPref(this);

        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);
        b = getIntent().getExtras();

        if( b.getSerializable("storeID") != null)
            storeID = (String) b.getSerializable("storeID");

        if( b.getSerializable("sampleOrder") != null)
            currentOrder = (Order) b.getSerializable("sampleOrder");

        if( b.getInt("tableID") != 0 ){
            tableID = b.getInt("tableID");
        }

        setContentView(R.layout.activity_search);
        database = FirebaseDatabase.getInstance().getReference("Menu");

        search_bar = findViewById(R.id.search_bar);
        resultList = findViewById(R.id.recycler_view);

        search_bar.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_bar, InputMethodManager.SHOW_IMPLICIT);

        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    firebaseUserSearch(s.toString());
                }
                else{
                    firebaseUserSearch(null);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), employee_activity.class);
        Bundle b = new Bundle();
        b.putSerializable("currentOrder", currentOrder);
        b.putSerializable("storeID", storeID);
        b.putInt("tableID", tableID);
        intent.putExtras(b);

        finish();
        startActivity(intent);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUserSearch(null);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery;
        if(searchText == null)
             firebaseSearchQuery = database.child(storeID).child("ItemList").orderByChild("category");
        else
             firebaseSearchQuery = database.child(storeID).child("ItemList").orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        options =
                new FirebaseRecyclerOptions.Builder<Food_Item>()
                        .setQuery(firebaseSearchQuery, Food_Item.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food_Item, ItemsViewHolder>(options) {
            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_list, parent, false);

                return new ItemsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder viewHolder, int position, @NonNull Food_Item model) {
                viewHolder.setDetails(getApplicationContext(), model);
            }

        };

        resultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    class ItemsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ItemsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(final Context ctx, final Food_Item item){

            final TextView item_name = mView.findViewById(R.id.food_name);
            TextView item_add = mView.findViewById(R.id.food_price);
            LinearLayout item_layout = mView.findViewById(R.id.item_layout);

            item_name.setText(item.getName());
            item_add.setText("+");

            final Order_Item order_item = new Order_Item(item, 1 , "");;

            item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!currentOrder.containsItem(item))
                        currentOrder.addItem(order_item);
                    else{
                        currentOrder.getItem(item).incQuanity();
                    }
                    Toast.makeText(ctx, "Added: " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), employee_activity.class);
        Bundle b = new Bundle();
        b.putSerializable("currentOrder", currentOrder);
        b.putSerializable("storeID", storeID);
        intent.putExtras(b);

        finish();
        startActivity(intent);
    }
}

