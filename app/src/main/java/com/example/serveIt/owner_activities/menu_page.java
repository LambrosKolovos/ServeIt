package com.example.serveIt.owner_activities;

import android.app.Dialog;
import android.os.Bundle;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.serveIt.helper_classes.Category;
import com.example.serveIt.helper_classes.Food_Item;
import com.example.serveIt.R;
import com.example.serveIt.helper_classes.Store;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class menu_page extends Fragment {


    private LinkedHashMap<String, List<Food_Item>> content;
    private ArrayList<Food_Item> foodItems;
    private FloatingActionButton categoryBtn;
    private Dialog categoryDialog, foodItemDialog, deleteDialog;
    private int i = 0;
    private int clickPos1, clickPos2;
    private ExpandingList menu;

    private FirebaseDatabase database;
    private DatabaseReference ref, menuRef;
    private RelativeLayout menu_display;
    private ProgressBar load_menu;
    
    String storeID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_menu, container, false);

        getActivity().setTitle("Food Menu");

        categoryDialog = new Dialog(getContext());
        foodItemDialog = new Dialog(getContext());
        deleteDialog = new Dialog(getContext());

        foodItems = new ArrayList<>();
        categoryBtn = root.findViewById(R.id.category_btn);
        load_menu = root.findViewById(R.id.load_menu);
        menu_display = root.findViewById(R.id.menu_display);

        menu = root.findViewById(R.id.expanding_list_main);

        database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("Menu");

        loadData();

        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog(v);
            }
        });

        return root;
    }

    public void loadData() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Store");
        ref.orderByChild("ownerID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storeID = null;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    storeID = data.getKey();
                    System.out.println(storeID);
                }

                if(storeID != null){
                    final DatabaseReference ref = database.getReference("Menu");
                    ref.child(storeID).orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount() == 0){
                                load_menu.setVisibility(View.GONE);
                                menu_display.setVisibility(View.VISIBLE);
                            }

                            for(final DataSnapshot data: dataSnapshot.getChildren()){
                                if(!data.getKey().equals("ItemList")){
                                    //MenuListData.addCategory(data.getKey());
                                    //addItem(data);
                                    if(data.getKey() != null){
                                        ref.child(storeID).child(data.getKey()).orderByChild("price").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data1: dataSnapshot.getChildren()){
                                                    if(!data1.getKey().equals("name")){
                                                        Food_Item food_item = data1.getValue(Food_Item.class);
                                                        foodItems.add(food_item);
                                                    }

                                                }

                                                addItem(data.getKey(), foodItems);
                                                foodItems.clear();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        load_menu.setVisibility(View.INVISIBLE);
                                                        menu_display.setVisibility(View.VISIBLE);
                                                    }
                                                }, 100);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }


    private void addItem(final String title, ArrayList<Food_Item> subItems){
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = menu.createNewItem(R.layout.expanding_layout);

        if(item != null){

            item.setIndicatorColorRes(R.color.color);
            item.setIndicatorIconRes(R.drawable.ic_restaurant_menu_black_24dp);

            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            final TextView textView = item.findViewById(R.id.title);
            textView.setText(title);


            //We can create items in batch.
            item.createSubItems(subItems.size());
            for (int i =0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems.get(i));
            }

            item.findViewById(R.id.add_more_sub_items)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFoodItemDialog(v ,title, new OnItemCreated() {
                                @Override
                                public void itemCreated(Food_Item food_item) {
                                    View newSubItem = item.createSubItem();
                                    assert newSubItem != null;
                                    configureSubItem(item, newSubItem, food_item);
                                }
                            });
                        }
                    });


            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.removeItem(item);
                    removeCategoryFromDatabase(textView.getText().toString());
                }
            });


        }



    }

    private void configureSubItem(final ExpandingItem item, final View view, final Food_Item food_item) {
        final TextView title = view.findViewById(R.id.sub_title);
        final TextView price = view.findViewById(R.id.price);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref.orderByChild("ownerID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot data) {
                String storeID = null;
                System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                for(DataSnapshot store: data.getChildren()){
                    storeID = store.getKey();
                }

                if(storeID != null) {
                    ref = database.getReference("Store");
                    ref.child(storeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Store store = dataSnapshot.getValue(Store.class);

                            if(store != null){
                                title.setText(food_item.getName());
                                price.setText(food_item.getPrice() + store.getCurrency());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        view.findViewById(R.id.remove_sub_item)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.removeSubItem(view);
                        removeItemFromDatabase(food_item.getCategory(), food_item);
                    }
                });
    }

    private void showCategoryDialog(View v){
        Button closeBtn, addBtn;
        final EditText categoryInput;
        final TextView alert;

        categoryDialog.setContentView(R.layout.category_popup);

        categoryInput = categoryDialog.findViewById(R.id.categoryField);
        alert = categoryDialog.findViewById(R.id.warning);
        addBtn = categoryDialog.findViewById(R.id.add_category);
        closeBtn = categoryDialog.findViewById(R.id.close_btn);

        alert.setVisibility(View.INVISIBLE);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String categoryName = categoryInput.getText().toString();
                if(!categoryName.isEmpty()){
                    addItem(categoryName, new ArrayList<Food_Item>());
                    addCategoryToDatabase(categoryName);
                    categoryDialog.dismiss();
                }
                else{
                    alert.setVisibility(View.VISIBLE);
                }

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

    private void showFoodItemDialog(View v, final String category, final OnItemCreated positive){
        Button closeBtn, addBtn;
        final EditText name, price;
        final TextView alertName, alertPrice;

        foodItemDialog.setContentView(R.layout.add_item_popup);

        name = foodItemDialog.findViewById(R.id.name_field);
        price = foodItemDialog.findViewById(R.id.price_field);
        alertName = foodItemDialog.findViewById(R.id.warning_name);
        alertPrice = foodItemDialog. findViewById(R.id.warning_price);
        addBtn = foodItemDialog.findViewById(R.id.add_item);
        closeBtn = foodItemDialog.findViewById(R.id.close_btn);

        alertName.setVisibility(View.INVISIBLE);
        alertPrice.setVisibility(View.INVISIBLE);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String itemPrice = price.getText().toString();
                final String itemName = name.getText().toString();

                if(!itemPrice.isEmpty() && !itemName.isEmpty()){
                    Food_Item food = new Food_Item(itemName, itemPrice);
                    positive.itemCreated(food);
                    addItemToDatabase(category, food);
                    foodItemDialog.dismiss();
                }
                else {
                    if (itemName.isEmpty()) {
                        alertName.setText("Item name can't be empty!");
                        alertName.setVisibility(View.VISIBLE);
                    }
                    if (itemPrice.isEmpty()) {
                        alertPrice.setText("Item price can't be empty!");
                        alertPrice.setVisibility(View.VISIBLE);
                    }
                }
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

    private void addItemToDatabase(final String category, final Food_Item food_item){

        food_item.setCategory(category);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                        menuRef.child(storeID)
                                .child(category)
                                .child(food_item.getName()).setValue(food_item);

                        menuRef.child(storeID)
                                .child("ItemList")
                                .child(food_item.getName()).setValue(food_item);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


    }

    private void addCategoryToDatabase(final String name){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                                menuRef.child(storeID)
                                        .child(name)
                                        .setValue(new Category(name));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void removeItemFromDatabase(final String category, final Food_Item item){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){

                        menuRef.child(storeID)
                                .child(category)
                                .child(item.getName()).removeValue();

                        menuRef.child(storeID)
                                .child("ItemList")
                                .child(item.getName()).removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void removeCategoryFromDatabase(final String name){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    final String storeID = data.getKey();
                    if(storeID != null){
                       menuRef.child(storeID)
                                .child(name)
                                .removeValue();

                        Query query = menuRef.child(storeID).child("ItemList");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot food_items: dataSnapshot.getChildren()){
                                    Food_Item food_item = food_items.getValue(Food_Item.class);
                                    if(food_item != null && food_item.getCategory().equals(name)){
                                        String foodName= food_item.getName();

                                        System.out.println("FOOD NAME DELETE " + food_item.getName());
                                        System.out.println("FOOD PRICE DELETE " + food_item.getPrice());
                                        System.out.println("FOOD CAT DELETE " + food_item.getCategory());
                                        menuRef.child(storeID).child("ItemList").child(foodName).removeValue();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    private interface OnItemCreated {
        void itemCreated(Food_Item food_item);
    }
}