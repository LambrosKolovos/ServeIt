package com.example.serveIt.owner_activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.serveIt.Category;
import com.example.serveIt.R;
import com.example.serveIt.SharedPref;
import com.example.serveIt.contact_us;
import com.example.serveIt.login_activities.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference userRef, storeRef, tableRef, ordersRef, menuRef;
    TextView name;
    TableRow logout, delete_acc, change_pass, contact, help;
    String userID;
    Switch darkMode;

    private Dialog deleteDialog, passDialog;

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

        if(sharedPref.loadNightMode())
            darkMode.setChecked(true);
        else
            darkMode.setChecked(false);

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent i = new Intent(getContext(), owner_activity.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(true);
                    i.putExtras(b);
                    getActivity().finish();
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getContext(), owner_activity.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(false);
                    i.putExtras(b);
                    getActivity().finish();
                    startActivity(i);
                }
            }
        });

        user = mAuth.getCurrentUser();
        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(v);
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePassDialog(v);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((getContext()), help_owner.class));
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), contact_us.class));
            }
        });

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

    private void showChangePassDialog(View v) {
        final EditText oldpass, newPass;
        Button change, cancel;

        passDialog.setContentView(R.layout.pass_dialog);

        oldpass = passDialog.findViewById(R.id.old_pass);
        newPass = passDialog.findViewById(R.id.new_pass);
        change = passDialog.findViewById(R.id.change);
        cancel = passDialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passDialog.dismiss();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String old_text = oldpass.getText().toString();
                    final String new_text = newPass.getText().toString();
                    final FirebaseUser user = mAuth.getCurrentUser();

                    if(user != null){
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),old_text);

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(new_text).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                                passDialog.dismiss();
                                            } else {
                                                Toast.makeText(getContext(), "Password didn't change", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getContext(), "Password didn't change", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });

        passDialog.show();
    }

}
