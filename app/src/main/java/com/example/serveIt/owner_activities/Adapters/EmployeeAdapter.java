package com.example.serveIt.owner_activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private List<User> employees;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public EmployeeAdapter(List<User> employees){
        this.employees = employees;

        ref = FirebaseDatabase.getInstance().getReference("Store");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employees_list, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull final EmployeeAdapter.ViewHolder holder, int position) {
        final User employee = employees.get(position);

        holder.employee_name.setText(employee.getFull_name());

        holder.date.setText(employee.getDate());

        holder.delete_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("tapped");
                ref.orderByChild("ownerID").equalTo(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot data) {
                                String storeID = null;
                                for(DataSnapshot store: data.getChildren()){
                                    storeID = store.getKey();
                                }

                                if(storeID != null){
                                    final String finalStoreID = storeID;
                                    ref.child(storeID)
                                            .child("employees")
                                            .orderByChild("email")
                                            .equalTo(employee.getEmail())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String employeeID = null;
                                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                                        employeeID = data.getKey();
                                                    }

                                                    ref.child(finalStoreID).child("employees")
                                                            .child(employeeID)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(holder.context, "Employee " + employee.getFull_name()
                                                                                + " kicked", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(holder.context, "Employee cannot be deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
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
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private TextView employee_name, date;
        private ImageView delete_ico;



        public ViewHolder(View view, Context context){
            super(view);

            this.context = context;

            employee_name = view.findViewById(R.id.employee_name);
            delete_ico = view.findViewById(R.id.delete_ico);
            date = view.findViewById(R.id.date);

        }

    }
}
