package com.promact.akansh.samplefirebaserestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by Akansh on 15-09-2017.
 */

public class SaveSharedPrefs {
    private static final String PREF_NAME = "name";

    static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    static void setPrefs(Context context, String name) {
        Editor editor = getSharedPrefs(context).edit();

        editor.putString(PREF_NAME, name);
        editor.apply();
    }

    static String getCount(Context context) {
        return getSharedPrefs(context).getString("count", "");
    }

    static String getName(Context context) {
        return getSharedPrefs(context).getString(PREF_NAME, "");
    }
}
