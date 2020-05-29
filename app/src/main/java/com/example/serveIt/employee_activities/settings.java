package com.example.serveIt.employee_activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.login_activities.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.google.firebase.database.annotations.NotNull;

public class settings extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthState;
    private FirebaseDatabase database;
    private DatabaseReference ref, refStore;

    TextView name;
    TableRow change_pass, leave, delete_acc, contact, help, logout;
    Dialog alertDialog, passDialog;
    String userID;
    String storeID;
    Bundle b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        refStore = database.getReference("Store");

        b = getArguments();
        if ( b.getSerializable("storeID") != null){
            storeID = (String) b.getSerializable("storeID");
        }

        alertDialog = new Dialog(getContext());
        passDialog = new Dialog(getContext());

        name = root.findViewById(R.id.name_view);
        change_pass = root.findViewById(R.id.change_password_row);
        leave = root.findViewById(R.id.leave_row);
        delete_acc = root.findViewById(R.id.delete_row);
        contact = root.findViewById(R.id.contact_row);
        help = root.findViewById(R.id.help_row);
        logout = root.findViewById(R.id.logout_row);

        addListeners();

        return root;
    }

    private void addListeners(){
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePassDialog(v);
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
                    startActivity(new Intent(getActivity(),login.class));
                }
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

        final String old_text = oldpass.getText().toString();
        final String new_text = newPass.getText().toString();

        if(old_text.length() < 6){

        }
        else if(new_text.length() < 6){

        }
        else{
            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        }


        passDialog.show();
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
            msg_view.setText("If you choose to delete ServeIt from your account, " +
                    "keep in mind that you won't be able to sign up for ServeIt " +
                    "with the same username in the future.");
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
                    final FirebaseUser user = mAuth.getCurrentUser();
                    refStore.child(storeID).child("employees").child(user.getUid()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        ref.child(user.getUid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(getContext(), login.class));
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
                else{
                    //HANDLE LEAVE STORE
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
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}