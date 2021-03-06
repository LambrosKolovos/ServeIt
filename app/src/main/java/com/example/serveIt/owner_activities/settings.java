package com.example.serveIt.owner_activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;
import com.example.serveIt.common_activities.change_password;
import com.example.serveIt.common_activities.contact_us;
import com.example.serveIt.login_activities.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
    User's settings fragment
 */
public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference userRef, storeRef, tableRef, ordersRef, menuRef;
    TextView name;
    TableRow logout, delete_acc, change_pass, contact, help, storeSettings;
    String userID;
    Switch darkMode;

    private Dialog deleteDialog, passDialog;
    private ScrollView settings_display;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings_owner, container, false);

        getActivity().setTitle("Settings");

        final SharedPref sharedPref = new SharedPref(getContext());
        final Bundle b = new Bundle();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        storeRef = database.getReference("Store");
        tableRef = database.getReference("Table");
        menuRef = database.getReference("Menu");
        ordersRef = database.getReference("Orders");

        deleteDialog = new Dialog(requireContext());
        passDialog = new Dialog(requireContext());

        logout = root.findViewById(R.id.logout_row);
        delete_acc = root.findViewById(R.id.delete_acc);
        name = root.findViewById(R.id.name_view);
        contact = root.findViewById(R.id.contact_row);
        change_pass = root.findViewById(R.id.change_password_row);
        darkMode = root.findViewById(R.id.darkModeSwitch);
        help = root.findViewById(R.id.help_row);
        storeSettings = root.findViewById(R.id.store_settings_row);
        settings_display = root.findViewById(R.id.settings_display);

        settings_display.setVisibility(View.INVISIBLE);

        if(sharedPref.loadNightMode())
            darkMode.setChecked(true);
        else
            darkMode.setChecked(false);

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent i = new Intent(getContext(), MAIN_OWNER_ACTIVITY.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(true);
                    i.putExtras(b);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getContext(), MAIN_OWNER_ACTIVITY.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(false);
                    i.putExtras(b);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    startActivity(i);
                }
            }
        });

        user = mAuth.getCurrentUser();

        // Allow's user to delete his account
        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(v);
            }
        });

        // Allow's user to change his password
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), change_password.class));
            }
        });

        // Allow's owner to change store settings
        storeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), store_settings.class));
            }
        });

        // Redirects to help page
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((getContext()), help_owner.class));
            }
        });

        // Redirects to contact page
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), contact_us.class));
            }
        });

        // Logs user out of the account
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Activity activity = getActivity();
                if(activity != null){
                    activity.finish();
                    startActivity(new Intent(getActivity(),login.class));
                }
            }
        });

        showName();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(deleteDialog != null)
            deleteDialog.dismiss();

        if(passDialog != null)
            passDialog.dismiss();
    }

    @SuppressLint("SetTextI18n")
    private void showDeleteDialog(View v){
        Button no_btn, yes_btn;
        final TextView msg_view;

        deleteDialog.setContentView(R.layout.alert_popup);
        msg_view = deleteDialog.findViewById(R.id.message);
        yes_btn = deleteDialog.findViewById(R.id.del_btn);
        no_btn = deleteDialog.findViewById(R.id.close_btn);
        msg_view.setTextSize(18);

        msg_view.setText("If you choose to delete your ServeIt account, " +
                    "keep in mind that you won't be able to sign in for ServeIt " +
                    "with the same credentials in the future.");

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //HANDLE DELETE ACC
                    deleteAccount();

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void showName(){
        DatabaseReference username = userRef.child(mAuth.getUid()).child("full_name");
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name_str = dataSnapshot.getValue(String.class);
                name.setText(name_str);
                settings_display.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*
        Deletes user account from database
     */
    private void deleteAccount(){
        storeRef.orderByChild("ownerID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        String storeID = null;
                        System.out.println("THE NUMBER IS: " + data.getChildrenCount());
                        for(DataSnapshot store: data.getChildren()){
                            storeID = store.getKey();
                        }

                        if(storeID != null){
                            menuRef.child(storeID).removeValue();
                            ordersRef.child(storeID).removeValue();
                            tableRef.child(storeID).removeValue();
                            storeRef.child(storeID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                userRef.child(user.getUid())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if( task.isSuccessful()){
                                                                    user.delete()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                                                                                        Activity activity = getActivity();
                                                                                        if(activity != null){
                                                                                            activity.finish();
                                                                                            startActivity(new Intent(getContext(), login.class));
                                                                                        }
                                                                                    }
                                                                                    else{
                                                                                        Toast.makeText(getContext(), "User cannot be deleted", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                                else{
                                                                    Toast.makeText(getContext(), "User's prefs cannot be" +
                                                                            "deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                Toast.makeText(getContext(), "Store cannot deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw  databaseError.toException();
                    }
                });
    }

}
