package com.example.serveIt.owner_activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.Order_Item;
import com.example.serveIt.R;
import com.example.serveIt.User;
import com.example.serveIt.employee_activities.OrderAdapter;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private List<User> employees;

    public EmployeeAdapter(List<User> employees){
        this.employees = employees;
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employees_list, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {
        User employee = employees.get(position);

        holder.employee_name.setText(employee.getFull_name());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private TextView employee_name;
        private ImageView delete_ico;

        public ViewHolder(View view, Context context){
            super(view);

            this.context = context;

            employee_name = view.findViewById(R.id.employee_name);
            delete_ico = view.findViewById(R.id.delete_ico);

            delete_ico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }
}
