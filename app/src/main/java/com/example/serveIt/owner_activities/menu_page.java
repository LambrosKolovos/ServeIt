package com.example.serveIt.owner_activities;

import android.app.Dialog;
import android.os.Bundle;

import com.example.serveIt.Food_Item;
import com.example.serveIt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class menu_page extends Fragment {


    private LinkedHashMap<String, List<Food_Item>> content;
    private FloatingActionButton categoryBtn;
    private Dialog categoryDialog, foodItemDialog;
    private int i = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_menu, container, false);

        categoryDialog = new Dialog(getContext());
        foodItemDialog = new Dialog(getContext());
        content = MenuListData.getData();
        categoryBtn = root.findViewById(R.id.category_btn);

        ExpandableListView menu = root.findViewById(R.id.expandableListView);
        final List<String> menuTitle = new ArrayList<String>(content.keySet());
        ExpandableListAdapter adapter = new ListAdapter(getContext(), menuTitle, content);

        menu.setAdapter(adapter);
        menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, final int childPosition, long id) {


                i++;
                final String category = menuTitle.get(groupPosition);
                final Food_Item itemClicked = content.get(menuTitle.get(groupPosition)).get(childPosition);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 2){
                            MenuListData.removeItem(itemClicked, category);
                            refreshScreen();
                        }
                        i = 0;
                    }
                }, 300);

                if (childPosition == content.get(category).size() - 1) {
                    showFoodItemDialog(v, category);
                }
                return false;
            }
        });


        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog(v);
            }
        });

        return root;
    }

    public void showFoodItemDialog(View v, final String category){
        Button closeBtn, addBtn;
        final EditText name, price;

        foodItemDialog.setContentView(R.layout.add_item_popup);

        name = foodItemDialog.findViewById(R.id.name_field);
        price = foodItemDialog.findViewById(R.id.price_field);
        addBtn = foodItemDialog.findViewById(R.id.add_item);
        closeBtn = foodItemDialog.findViewById(R.id.close_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final double itemPrice = Double.parseDouble(price.getText().toString());
                final String itemName = name.getText().toString();

                Food_Item food = new Food_Item(itemName, itemPrice);
                MenuListData.addItem(food, category);

                refreshScreen();
                foodItemDialog.dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItemDialog.dismiss();;
            }
        });
        foodItemDialog.show();
    }

    private void showCategoryDialog(View v){
        Button closeBtn, addBtn;
        final EditText categoryInput;

        categoryDialog.setContentView(R.layout.category_popup);

        categoryInput = categoryDialog.findViewById(R.id.categoryField);
        addBtn = categoryDialog.findViewById(R.id.add_category);
        closeBtn = categoryDialog.findViewById(R.id.close_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuListData.addCategory(categoryInput.getText().toString());
                refreshScreen();
                categoryDialog.dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.dismiss();;
            }
        });

        categoryDialog.show();
    }


    private void refreshScreen(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}