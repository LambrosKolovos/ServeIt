package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.serveIt.User;
import com.example.serveIt.login_activities.login;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
    Bundle b;
    User currentUser;
    private Button sign_out;

    private FirebaseRecyclerAdapter<Store, ViewHolder > firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Store> options;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);
        sign_out = findViewById(R.id.sign_out);
        mAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference("Store");

        b = getIntent().getExtras();
        if( b != null ){
            currentUser = (User) b.getSerializable("userLoggedIn");
        }

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
                sign_out.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                noDataView.setVisibility(View.VISIBLE);
                firebaseRecyclerAdapter.stopListening();
                sign_out.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
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

                return new ViewHolder(view, parent.getContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull Store model) {
                viewHolder.setDetails(model, currentUser);
            }

        };

        storeView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Dialog joinDialog;
        Context context;
        FirebaseAuth mAuth;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            mView = itemView;
            this.context = context;
            joinDialog = new Dialog(context);

            mAuth = FirebaseAuth.getInstance();

        }

        public void setDetails(final Store store, final User user){


            TextView store_name = mView.findViewById(R.id.store_name);
            LinearLayout join = mView.findViewById(R.id.store);

            store_name.setText(store.getName());
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showJoinDialog(v, store.getOwnerID(), user);
                }
            });

        }
        private  void showJoinDialog(View v, final String id, final User user){
            final EditText password;
            Button join_btn;
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Store");

            joinDialog.setContentView(R.layout.join_popup);

            join_btn = joinDialog.findViewById(R.id.join);
            password = joinDialog.findViewById(R.id.pass_field);

            join_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String pass_entered = password.getText().toString();
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot x: dataSnapshot.getChildren()){
                                String storeID = x.getKey();
                                Store store = x.getValue(Store.class);
                                if(id.equals(store.getOwnerID())){
                                    if(pass_entered.equals(store.getPassword())){
                                        System.out.println("USER ENTERS THE STORE");
                                        System.out.println(storeID);


                                        //Update store's employees
                                        store.getEmployees().put(mAuth.getCurrentUser().getUid(), user);
                                        ref.child(storeID).child("employees").setValue(store.getEmployees());


                                        //Update user's workspace
                                        user.setWorkID(storeID);
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("workID")
                                                .setValue(user.getWorkID());


                                        //Load main application
                                        Intent i = new Intent(context, employee_activity.class);
                                        i.putExtra("storeID", user.getWorkID());
                                        context.startActivity(i);
                                        break;
                                    }
                                    else
                                        System.out.println("IM AFRAID THIS IS NOT CORRECT");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            joinDialog.show();
        }
    }

    public void sign_out(View view){
        finish();
        firebaseRecyclerAdapter.stopListening();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), login.class));
    }
}
