package com.example.serveIt.helper_classes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharePref;

    public SharedPref(Context context) {
        mySharePref = context.getSharedPreferences("serveIt", Context.MODE_PRIVATE);
    }

    public void setNightMode(Boolean state) {
        SharedPreferences.Editor editor = mySharePref.edit();
        editor.putBoolean("nightmode", state);
        editor.commit();
    }

    public Boolean loadNightMode() {
        Boolean state = mySharePref.getBoolean("nightmode", false);
        return state;
    }

}
