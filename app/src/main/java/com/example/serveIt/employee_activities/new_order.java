package com.example.serveIt.employee_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.home_screen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;


public class new_order extends Fragment {

    private TableLayout orderLayout;
    private TextView priceView;
    private SearchView searchMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_new_order, container, false);
        searchMenu = root.findViewById(R.id.searchMenu);
        orderLayout = root.findViewById(R.id.order_display);
        priceView = root.findViewById(R.id.totalPrice);

        searchMenu.onActionViewExpanded();

        searchMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        makerOrder();

        return root;
    }

    public void loadItemNotes(View view) {
        startActivity(new Intent(getActivity(), item_notes.class));
    }

    public void makerOrder() {
        int price = 0;
        double foodcost = 6.00;
        for(int i=1; i<5; i++){
            price += i * foodcost;
            TableRow item_row = build_row("Burger", Integer.toString(i), Double.toString(foodcost*i) + "$");
            orderLayout.addView(item_row);
        }

        String totalPrice = "Total: " + price + "$";
        priceView.setText(totalPrice);
    }


    public TableRow build_row(String item_name, String quantity, String price){

        //Converts pixel to dp
        int padding_in_dp = 10;
        final float scale = getResources().getDisplayMetrics().density;
        int padd_bottom = (int) (padding_in_dp * scale + 0.5f);

        TableRow row = new TableRow(getContext());

        TextView item_view = build_view(item_name, 6, false, true, row);
        TextView quantity_view = build_view(quantity, 3,true, false, row);
        TextView price_view = build_view(price,3,true, false, row);
        TextView delete_view = build_view("x",1, true, true, row);

        row.addView(item_view);
        row.addView(quantity_view);
        row.addView(price_view);
        row.addView(delete_view);

        row.setPadding(0,0,0,padd_bottom);

        return row;
    }

    public TextView build_view(String name, int weight, boolean center, boolean clickable, final TableRow row){
        TextView view = new TextView(getContext());
        view.setText(name);

        if(clickable){
            view.setClickable(true);
            if(name.equals("x")){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderLayout.removeView(row);
                    }
                });
            }
            else{
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadItemNotes(v);
                    }
                });
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && center) {
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        view.setTextColor(Color.parseColor("#2196F3"));
        view.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));


        return  view;
    }

}
