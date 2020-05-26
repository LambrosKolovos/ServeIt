package com.example.serveIt.owner_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.Table;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class build_layout extends Fragment {

    FloatingActionButton add_table;
    TableLayout table_view;
    private int i=0 ,id = 0;
    private ArrayList<Button> tablelist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_build_layout, container, false);

        add_table = root.findViewById(R.id.table_btn);
        table_view = root.findViewById(R.id.table_view);
        final TableRow[] currentRow = {new TableRow(getContext())};
        tablelist = new ArrayList<>();

        //Convert px to dp
        int padding = 10;
        final float scale = getResources().getDisplayMetrics().density;
        final int padd_bottom = (int) (padding * scale + 0.5f);

        currentRow[0].setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);

        add_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTableView(id, currentRow[0]);
                id++;
                if (id % 3  == 0) {
                    currentRow[0] = new TableRow(getContext());
                    currentRow[0].setPadding(padd_bottom, padd_bottom, padd_bottom, padd_bottom);
                }
            }
        });
        return root;
    }

    public void addTableView(final int id, final TableRow row){
        //Convert px to dp
        int padding = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int  x = (int) (padding * scale + 0.5f);

        final Button table = new Button(getContext());

        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 2){
                            tablelist.remove(table);
                            row.removeView(table);
                            Toast.makeText(getContext(), "Removing: " + (id+1), Toast.LENGTH_SHORT).show();
                        }
                        i = 0;
                    }
                }, 300);
            }
        });

        table.setText(String.valueOf(id+1));
        table.setBackgroundResource(R.drawable.table_available);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1
        );
        params.setMargins(x, 0, x, x);
        table.setLayoutParams(params);

        if( id % 3 == 0){
            table_view.addView(row);
            row.addView(table);

        }
        else{
            row.addView(table);
        }

        tablelist.add(table);
    }

}
