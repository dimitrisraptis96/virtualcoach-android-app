package com.example.metafifth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPreferences {
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public AppPreferences(Context context) {
        this.appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getValue(String text) {
        return appSharedPrefs.getString(text, "");
    }

    public void setValue(String key,String text) {
        prefsEditor.putString(key, text);
        prefsEditor.commit();
    }
}