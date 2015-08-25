package com.macmoim.pang.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.macmoim.pang.LogInActivity;
import com.macmoim.pang.login.SocialProfile;

/**
 * Created by P11872 on 2015-08-20.
 */
public class LoginPreferences {
    static private SharedPreferences.Editor mEditor;
    static private SharedPreferences mPref;

    public static final String USER_AUTHENTICATED = "user_authenticated"; //value is a Boolean
    public static final String USER_SOCIAL = "user_social"; //value is a String and means user is logged with Social.FACEBOOK or Social.GOOGLE
    public static final String PROFILE_ID = "profile_id";  //value is a String
    public static final String PROFILE_NAME = "profile_name";  //value is a String
    public static final String PROFILE_EMAIL = "profile_email";
    public static final String PROFILE_IMAGE = "profile_image";  //value is a String
    public static final String PROFILE_COVER = "profile_cover"; //value is a String

    private static volatile LoginPreferences mLoginPreferences;

    private LoginPreferences(){

    }

    public static LoginPreferences GetInstance() {
        if (mLoginPreferences == null) {
            synchronized (LoginPreferences.class) {
                mLoginPreferences = new LoginPreferences();
            }
        }
        return mLoginPreferences;
    }

    public void putProfile(Context context, SocialProfile profile){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(USER_AUTHENTICATED, true);
        editor.putString(USER_SOCIAL, profile.getNetwork());
        editor.putString(PROFILE_ID, profile.getId());
        editor.putString(PROFILE_NAME,  profile.getName());
        editor.putString(PROFILE_EMAIL, profile.getEmail());
        editor.putString(PROFILE_IMAGE, profile.getImage());
        editor.putString(PROFILE_COVER, profile.getCover());
        editor.apply();
    }

    public String getString(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String val = preferences.getString(key, null);
        return val;
    }

    public Boolean getBoolean(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key,false);
    }

    public boolean CheckLogin(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(LoginPreferences.USER_AUTHENTICATED, false);
    }

    public void CheckLogOut(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTHENTICATED, false);
    }
    public void clear(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .clear()
                .apply();
    }

}
