package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.Food_Item;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        database = FirebaseDatabase.getInstance().getReference("Menu");

        search_bar = findViewById(R.id.search_bar);
        resultList = findViewById(R.id.recycler_view);

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


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food_Item, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_list, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int position, @NonNull Food_Item model) {
                viewHolder.setDetails(getApplicationContext(), model.getName(), model.getPrice());
            }

        };

        resultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}

// View Holder Class

class UsersViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public UsersViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

    }

    public void setDetails(final Context ctx, String name, String price){

        TextView user_name = (TextView) mView.findViewById(R.id.food_name);
        TextView user_status = (TextView) mView.findViewById(R.id.food_price);


        user_name.setText(name);
        user_status.setText(price);

        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "CLICKED", Toast.LENGTH_SHORT).show();
            }
        });


    }




}
