package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.Food_Item;
import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.example.serveIt.Store;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class search_store extends AppCompatActivity {

    RecyclerView storeView;
    LinearLayout noDataView;
    DatabaseReference ref;

    private FirebaseRecyclerAdapter<Store, ViewHolder > firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Store> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);

        ref = FirebaseDatabase.getInstance().getReference("Store");

        noDataView = findViewById(R.id.no_data_view);
        storeView = findViewById(R.id.recycler_view);
        storeView.setLayoutManager(new LinearLayoutManager(this));
        storeView.setHasFixedSize(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchStoreView = (SearchView) searchItem.getActionView();
        searchStoreView.setQueryHint("Search for stores...");

        searchStoreView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty() || newText.equals(" ")){
                    storeSearch(" ");
                    noDataView.setVisibility(View.VISIBLE);
                }
                else{
                    noDataView.setVisibility(View.GONE);
                    storeSearch(newText);
                }
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                storeSearch(" ");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                noDataView.setVisibility(View.VISIBLE);
                return true;
            }
        });


        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        storeSearch(" ");
        firebaseRecyclerAdapter.startListening();
    }

    private void storeSearch(String searchText){
        Query firebaseSearchQuery;

        if(searchText == null)
            firebaseSearchQuery = ref.orderByChild("name");
        else
            firebaseSearchQuery = ref.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

         options =
                new FirebaseRecyclerOptions.Builder<Store>()
                        .setQuery(firebaseSearchQuery, Store.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Store, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.store_list, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull Store model) {
                viewHolder.setDetails(getApplicationContext(), model);
            }

        };

        storeView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDetails(Context context, Store store){

            TextView store_name = mView.findViewById(R.id.store_name);

            store_name.setText(store.getName());

        }

    }

    private void showJoinDialog(){

    }
    public void goToApp(View view){
        startActivity(new Intent(getApplicationContext(), employee_activity.class));
    }
}
