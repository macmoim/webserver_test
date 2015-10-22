package com.macmoim.pang;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.macmoim.pang.adapter.SwipeFoodRecyclerViewAdapter;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.layout.SimpleDividerItemDecoration;

import java.util.ArrayList;

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

    public String GetToolBarTitle() {
        return getIntent().getStringExtra("other-user-name") + getString(R.string.other_profile);
    }

    @Override
    protected void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SwipeFoodRecyclerViewAdapter(this, feedItems));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }
}
