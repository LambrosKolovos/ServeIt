package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.*;
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
import com.example.serveIt.*;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private DatabaseReference ref, refStore;

    ScrollView setting_page;
    TextView name;
    TableRow change_pass, leave, delete_acc, contact, help, logout;
    Dialog alertDialog, passDialog;
    Switch darkModeSwitch;
    String userID;
    String storeID;
    Bundle b;
    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings, container, false);

        requireActivity().setTitle("Settings");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        refStore = database.getReference("Store");

        sharedPref = new SharedPref(getContext());
        b = getArguments();
        if ( b.getSerializable("storeID") != null){
            storeID = (String) b.getSerializable("storeID");
        }

        alertDialog = new Dialog(getContext());
        passDialog = new Dialog(getContext());

        setting_page = root.findViewById(R.id.setting_page);
        name = root.findViewById(R.id.name_view);
        change_pass = root.findViewById(R.id.change_password_row);
        leave = root.findViewById(R.id.leave_row);
        delete_acc = root.findViewById(R.id.delete_row);
        contact = root.findViewById(R.id.contact_row);
        help = root.findViewById(R.id.help_row);
        logout = root.findViewById(R.id.logout_row);
        darkModeSwitch = root.findViewById(R.id.dark_switch);

        setting_page.setVisibility(View.INVISIBLE);

        if(sharedPref.loadNightMode())
            darkModeSwitch.setChecked(true);
        else
            darkModeSwitch.setChecked(false);


        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent i = new Intent(getContext(), employee_activity.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(true);
                    i.putExtras(b);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(getContext(), employee_activity.class);
                    b.putBoolean("trigger", true);
                    sharedPref.setNightMode(false);
                    i.putExtras(b);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    startActivity(i);
                }
            }
        });

        DatabaseReference username = ref.child(mAuth.getUid()).child("full_name");
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name_str = dataSnapshot.getValue(String.class);
                name.setText(name_str);
                setting_page.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addListeners();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(alertDialog != null)
            alertDialog.dismiss();
    }

    private void addListeners(){
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showChangePassDialog(v);
                startActivity(new Intent(getContext(), change_password.class));
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(v, false);
            }
        });

        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(v, true);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), contact_us.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), help_employee.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Activity activity = getActivity();
                if(activity != null){
                    activity.finish();
                    Intent i = new  Intent(getActivity(),login.class);
                    i.putExtra("darkPref", b.getBoolean("drakPref"));
                    startActivity(i);
                }
            }
        });

    }


    @SuppressLint("SetTextI18n")
    private void showAlert(View v, final boolean delete){
        Button no_btn, yes_btn;
        final TextView msg_view;

        alertDialog.setContentView(R.layout.alert_popup);
        msg_view = alertDialog.findViewById(R.id.message);
        yes_btn = alertDialog.findViewById(R.id.del_btn);
        no_btn = alertDialog.findViewById(R.id.close_btn);
        msg_view.setTextSize(18);

        if(delete){
            msg_view.setText("If you choose to delete your ServeIt account, " +
                    "keep in mind that you won't be able to sign in for ServeIt " +
                    "with the same credentials in the future.");
        }
        else{
            yes_btn.setText("LEAVE");
            msg_view.setText("Exit store?");
        }


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delete){
                    //HANDLE DELETE ACC
                    deleteUser();
                }
                else{
                    //HANDLE LEAVE STORE
                    leaveStore();
                }
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void deleteUser(){
        final FirebaseUser user = mAuth.getCurrentUser();

        ref.child(user.getUid()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            refStore.child(storeID).child("employees").child(user.getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                                            Activity activity = getActivity();
                                                            if(activity != null){
                                                                activity.finish();
                                                                startActivity(new Intent(getContext(), login.class));
                                                            }
                                                        }
                                                        else{
                                                            Toast.makeText(getContext(), task.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(getContext(), "User cannot deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getContext(), "User cannot leave store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void leaveStore(){
        final DatabaseReference userRef = ref.child(mAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println("Removing user: " + user.getFull_name());
                System.out.println("From store: " + user.getWorkID());

                user.setWorkID(" ");
                userRef.child("workID").setValue(" ");

                getActivity().finish();

                Intent i = new Intent(getContext(), search_store.class);
                i.putExtra("userLoggedIn", user);
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}