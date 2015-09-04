package com.macmoim.pang;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by P11872 on 2015-08-16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class OtherUserPostActivity extends MyPostActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setUserId() {
        user_id = getIntent().getStringExtra("other-user-id");
        user_name = getIntent().getStringExtra("other-user-name");
    }
}
