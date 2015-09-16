package com.macmoim.pang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.macmoim.pang.data.LoginPreferences;

import static com.macmoim.pang.data.LoginPreferences.*;

/**
 * Created by P11872 on 2015-08-24.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent _Intent = new Intent();

        if (IsLogged()) {
            _Intent.setClass(this, MainActivity.class);
        } else {
            _Intent.setClass(this, LogInActivity.class);
        }

        startActivity(_Intent);
        finish();
    }

    private boolean IsLogged() {
        return LoginPreferences.GetInstance().getBoolean(this, USER_AUTHENTICATED);
    }
}