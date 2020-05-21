package com.example.serveIt.owner_activities;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.serveIt.Category;
import com.example.serveIt.Food_Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MenuListData {

    private static LinkedHashMap<String, List<Food_Item>> categories = new LinkedHashMap<>();

    public static LinkedHashMap<String, List<Food_Item>> getData() {
        return categories;
    }

    public static void addCategory(final String name){
        List<Food_Item> newCategory = new ArrayList<>();
        Food_Item addNew = new Food_Item("Add new", "+", true);
        newCategory.add(addNew);
        categories.put(name, newCategory);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Store").orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                        FirebaseDatabase.getInstance().getReference("Menu")
                                .child(storeID)
                                .child(name)
                                .setValue(new Category(new ArrayList<Food_Item>(),name));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    public static void removeCategory(final String name){
            categories.remove(name);


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Store").orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                        FirebaseDatabase.getInstance().getReference("Menu")
                                .child(storeID)
                                .child(name)
                                .removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    public static void addItem(Food_Item item, final String category){
        for (LinkedHashMap.Entry<String, List<Food_Item>> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            List<Food_Item> categoryList = entry.getValue();

            if(categoryName.equals(category))
                categoryList.add(0, item);
        }

        final Food_Item food_item = item;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Store").orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                        FirebaseDatabase.getInstance().getReference("Menu")
                                .child(storeID)
                                .child(category)
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

    public static void removeItem(final Food_Item item, final String category){
        for (LinkedHashMap.Entry<String, List<Food_Item>> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            List<Food_Item> categoryList = entry.getValue();

            if(categoryName.equals(category))
                categoryList.remove(item);
        }



        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Store").orderByChild("ownerID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String storeID = data.getKey();
                    if(storeID != null){
                        FirebaseDatabase.getInstance().getReference("Menu")
                                .child(storeID)
                                .child(category)
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



}
