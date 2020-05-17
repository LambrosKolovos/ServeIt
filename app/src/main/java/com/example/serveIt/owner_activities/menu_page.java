package com.example.serveIt.owner_activities;

import android.os.Bundle;

import com.example.serveIt.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class menu_page extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_menu, container, false);

        ExpandableListView menu = root.findViewById(R.id.expandableListView);
        HashMap<String, List<String>> content = MenuListData.getData();
        List<String> menuTitle = new ArrayList<String>(content.keySet());
        ExpandableListAdapter adapter = new ListAdapter(getContext(), menuTitle, content);

        menu.setAdapter(adapter);

        return root;
    }
}