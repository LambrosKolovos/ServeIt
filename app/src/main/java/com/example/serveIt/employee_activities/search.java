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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class search extends AppCompatActivity {

    private EditText search_bar;
    private RecyclerView resultList;
    private DatabaseReference database;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Food_Item> options;
    public Order currentOrder = new Order();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        currentOrder = (Order) i.getSerializableExtra("sampleOrder");

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
        Bundle b = new Bundle();
        b.putSerializable("currentOrder", currentOrder);
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                selectedFragment = new new_order();
                selectedFragment.setArguments(b);
                break;
        }
        setContentView(R.layout.activity_employee_activity);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUserSearch(null);
        firebaseRecyclerAdapter.startListening();
    }


    private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery;
        if(searchText == null)
             firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").child("ItemList").orderByChild("name");
        else
             firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").child("ItemList").orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

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

           item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Order_Item orderItem = new Order_Item(item, 1, "");
                    currentOrder.addItem(orderItem);
                    Toast.makeText(ctx, "Added: " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}

