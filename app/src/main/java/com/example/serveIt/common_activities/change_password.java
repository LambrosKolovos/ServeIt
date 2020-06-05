package com.example.serveIt.common_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.serveIt.R;
import com.example.serveIt.helper_classes.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class change_password extends AppCompatActivity {

    EditText oldPass, newPass, confPass;
    Button changeBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref sharedPref = new SharedPref(this);

        setTitle("Change password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (sharedPref.loadNightMode())
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        oldPass = findViewById(R.id.old_password_field);
        newPass = findViewById(R.id.new_password_field);
        confPass = findViewById(R.id.confirm_pass_field);
        changeBtn = findViewById(R.id.change_pass);
        user = mAuth.getCurrentUser();

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPassword = oldPass.getText().toString();
                final String newPassword = newPass.getText().toString();
                final String confPassword = confPass.getText().toString();

                if(inputIsCorrect(oldPassword, newPassword, confPassword)){
                    if(passwordMatch(newPassword, confPassword))
                        if(user != null){
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Password didn't change", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Old password not correct", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                        Toast.makeText(getApplicationContext(), "New password does not match!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Password can't be less than 6 characters!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean inputIsCorrect(String s1, String s2, String s3){
        return s1.length() > 5 && s2.length() > 5 && s3.length() > 5;
    }

    public boolean passwordMatch(String s1, String s2){
        return s1.equals(s2);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
