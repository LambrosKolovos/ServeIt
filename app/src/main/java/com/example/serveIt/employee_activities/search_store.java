package com.example.serveIt.employee_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.R;

public class search_store extends AppCompatActivity {

    RecyclerView storeView;
    LinearLayout noDataView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);

        noDataView = findViewById(R.id.no_data_view);
        storeView = findViewById(R.id.recycler_view);
        storeView.setLayoutManager(new LinearLayoutManager(this));

        if(storeView.getLayoutManager().getItemCount() == 0) {
            noDataView.setVisibility(View.VISIBLE);
        }
        else{
            noDataView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchStoreView = (SearchView) searchItem.getActionView();
        searchStoreView.setQueryHint("Search for stores...");
        return true;
    }

    class StoresViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public StoresViewHolder(View storeView) {
            super(storeView);
            mView = storeView;
        }

        public void setDetails(){

        }
    }

    public void goToApp(View view){
        startActivity(new Intent(getApplicationContext(), employee_activity.class));
        finish();
    }
}
