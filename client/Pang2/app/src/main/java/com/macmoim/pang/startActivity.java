package com.macmoim.pang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.macmoim.pang.data.LoginPreferences;

import static com.macmoim.pang.data.LoginPreferences.*;

/**
 * Created by P11872 on 2015-08-24.
 */

public class startActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();

        if (isLogged()){
            intent.setClass(this, MainActivity.class);
        }else{
            intent.setClass(this, LogInActivity.class);
        }

        startActivity(intent);
    }

    private boolean isLogged(){
        return LoginPreferences.GetInstance().getBoolean(this, USER_AUTHENTICATED);
    }
}