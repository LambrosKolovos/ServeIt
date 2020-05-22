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

import com.example.serveIt.Food_Item;
import com.example.serveIt.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class search extends AppCompatActivity {

    private EditText search_bar;
    private Button search_button;
    private RecyclerView resultList;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        database = FirebaseDatabase.getInstance().getReference("Menu");

        search_bar = findViewById(R.id.search_bar);
        resultList = findViewById(R.id.recycler_view);
        search_button = findViewById(R.id.search_button);

        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = search_bar.getText().toString();
                firebaseUserSearch(string);
            }
        });

    }

    private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery = database.child("-M7sKK7wobW-3QAIbUvj").orderByChild("price").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Food_Item> options =
                new FirebaseRecyclerOptions.Builder<Food_Item>()
                        .setQuery(firebaseSearchQuery, Food_Item.class)
                        .build();


        FirebaseRecyclerAdapter<Food_Item, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Food_Item, UsersViewHolder>(options) {
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

    }
}

// View Holder Class

class UsersViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public UsersViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

    }

    public void setDetails(Context ctx, String name, String price){

        TextView user_name = (TextView) mView.findViewById(R.id.food_name);
        TextView user_status = (TextView) mView.findViewById(R.id.food_price);


        user_name.setText(name);
        user_status.setText(price);

    }




}
