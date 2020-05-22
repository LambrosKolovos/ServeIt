package com.example.serveIt.owner_activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.serveIt.Food_Item;
import com.example.serveIt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class menu_page extends Fragment {


    private LinkedHashMap<String, List<Food_Item>> content;
    private List<String> menuTitle;
    private FloatingActionButton categoryBtn;
    private Dialog categoryDialog, foodItemDialog, deleteDialog;
    private int i = 0;
    private int clickPos1, clickPos2;
    private ExpandableListView menu;
    private ListAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    
    String storeID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_menu, container, false);

        categoryDialog = new Dialog(getContext());
        foodItemDialog = new Dialog(getContext());
        deleteDialog = new Dialog(getContext());

        content = MenuListData.getData();
        categoryBtn = root.findViewById(R.id.category_btn);

        menu = root.findViewById(R.id.expandableListView);
        menuTitle = new ArrayList<>(content.keySet());
        adapter = new ListAdapter(getContext(), menuTitle, content);


        menu.setAdapter(adapter);

        loadData();

        menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, final int childPosition, long id) {

                if(i%2 != 0)
                    clickPos1 = childPosition;
                else
                    clickPos2 = childPosition;

                i++;
                final String category = menuTitle.get(groupPosition);
                final Food_Item itemClicked = content.get(menuTitle.get(groupPosition)).get(childPosition);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 2 && clickPos1 == clickPos2 && !(childPosition == content.get(category).size() - 1)){
                            System.out.println("YOU CLICKED THE SAME ITEM REMOVING!!");
                            MenuListData.removeItem(itemClicked, category);
                            refreshScreen();
                        }
                        i = 0;
                    }
                }, 300);

                //Handle last child
                if (childPosition == content.get(category).size() - 1) {
                    showFoodItemDialog(v, category);
                }
                return false;
            }
        });


        menu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = menu.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    showDeleteDialog(view, menuTitle.get(groupPosition));
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
                            for(final DataSnapshot data: dataSnapshot.getChildren()){
                                if(adapter.isEmpty() ){
                                    MenuListData.addCategory(data.getKey());
                                    if(data.getKey() != null){
                                        ref.child(storeID).child(data.getKey()).orderByChild("price").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data1: dataSnapshot.getChildren()){
                                                    if(!data1.getKey().equals("name")){
                                                        Food_Item food_item = data1.getValue(Food_Item.class);
                                                        MenuListData.addItem(food_item, data.getKey());
                                                        refreshScreen();
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    refreshScreen();
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

    private void showFoodItemDialog(View v, final String category){
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
                    MenuListData.addItem(food, category);
                    refreshScreen();
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
                final String category = categoryInput.getText().toString();

                if(inputIsCorrect(category)){
                    MenuListData.addCategory(category);
                    refreshScreen();
                    categoryDialog.dismiss();
                }
                else {
                    alert.setText("Category name can't be empty!");
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

    private void showDeleteDialog(View v, final String category){
        Button closeBtn, addBtn;
        final TextView deleteMessage;

        deleteDialog.setContentView(R.layout.delete_popup);

        deleteMessage = deleteDialog.findViewById(R.id.message);
        addBtn = deleteDialog.findViewById(R.id.del_btn);
        closeBtn = deleteDialog.findViewById(R.id.close_btn);

        deleteMessage.setText("Are you sure you want to delete " + category + "?");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuListData.removeCategory(category);
                refreshScreen();
                deleteDialog.dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();;
            }
        });
        deleteDialog.show();
    }

    private boolean inputIsCorrect(String x){
        return !x.isEmpty();
    }
    private void refreshScreen(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}