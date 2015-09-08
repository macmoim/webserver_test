package com.macmoim.pang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by P11872 on 2015-08-20.
 */
public class AppPreferences {
    static private SharedPreferences.Editor mEditor;
    static private SharedPreferences mPref;

    public static final String PUSH_AGREE_POPUP_SHOWN = "push-agree-shown"; //value is a Boolean
    public static final String PUSH_AGREE = "push-agree"; //value is a Boolean
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    private static volatile AppPreferences mAppPreference;

    private AppPreferences() {

    }

    public static AppPreferences GetInstance() {
        if (mAppPreference == null) {
            synchronized (AppPreferences.class) {
                mAppPreference = new AppPreferences();
            }
        }
        return mAppPreference;
    }

    public void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String val = preferences.getString(key, null);
        return val;
    }

    public Boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public void clear(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
    }

}
