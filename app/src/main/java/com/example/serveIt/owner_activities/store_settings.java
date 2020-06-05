package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;
import com.example.serveIt.helper_classes.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
    Updates owner's store settings such as name , enter code, currency.
 */
public class store_settings extends AppCompatActivity {

    private EditText store_name, store_password;
    private Button save_changes;
    private DatabaseReference storeRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Store Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPref sharedPref = new SharedPref(this);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storeRef = FirebaseDatabase.getInstance().getReference("Store");

        if(sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_store_settings);

        store_name = findViewById(R.id.store_name);
        store_password = findViewById(R.id.store_password);
        save_changes = findViewById(R.id.save_changes);

        final Spinner dropdown = findViewById(R.id.currency);
        String[] items = new String[]{"Euros (€)", "US Dollars ($)"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.contact_spinner_item, items);
        dropdown.setAdapter(adapter);


        storeRef.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        Store store = null;
                        System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot x: data.getChildren()){
                            store = x.getValue(Store.class);
                        }

                        if(store != null){
                            store_name.setText(store.getName());
                            store_password.setText(store.getPassword());

                            String findValue = "€";

                            if(store.getCurrency().equals("€"))
                                findValue = "Euros (€)";
                            else
                                findValue = "US Dollars ($)";

                            int spinnerPosition = adapter.getPosition(findValue);
                            dropdown.setSelection(spinnerPosition);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = store_name.getText().toString();
                final String pass = store_password.getText().toString();
                final String selectedCurrency = dropdown.getSelectedItem().toString();
                final String curr;
                if(selectedCurrency.equals("Euros (€)")){
                    curr = "€";
                }
                else{
                    curr = "$";
                }

                if(!name.isEmpty() && pass.length() >= 6){
                    storeRef.orderByChild("ownerID").equalTo(user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot data) {
                                    String storeID = null;
                                    System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                                    for(DataSnapshot store: data.getChildren()){
                                        storeID = store.getKey();
                                    }

                                    if(storeID != null) {
                                        storeRef.child(storeID).child("name").setValue(name);
                                        storeRef.child(storeID).child("password").setValue(pass);
                                        storeRef.child(storeID).child("currency").setValue(curr);
                                        Toast.makeText(store_settings.this, "Store settings updated", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else{
                    Toast.makeText(store_settings.this, "Store name or password is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
