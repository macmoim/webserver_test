package com.macmoim.pang.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by P11872 on 2015-08-20.
 */
public class CommonSharedPreperences {
    static private SharedPreferences.Editor mEditor;
    static private SharedPreferences mPref;
    private Activity mActivity;


    // if you would like to register string key, then added to it
    public static String KEY_ID = "id";
    public static String KEY_NAME = "name";


    private static volatile CommonSharedPreperences mCommonSharedPreperences;

    private CommonSharedPreperences(){

    }

    public static CommonSharedPreperences GetInstance(Activity activity) {
        if (mCommonSharedPreperences == null) {
            synchronized (CommonSharedPreperences.class) {
                mCommonSharedPreperences = new CommonSharedPreperences();
                mPref = activity.getSharedPreferences("pref", Context.MODE_PRIVATE);
                mEditor = mPref.edit();
            }
        }
        return mCommonSharedPreperences;
    }

    public void putString(String key, String value){
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String getString(String key){
        return (mPref != null) ? mPref.getString(key,null) : null;
    }

    public void onDestory(){
        mActivity = null;
        mEditor = null;
        mPref = null;
    }

}
