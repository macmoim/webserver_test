package com.macmoim.pang;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.macmoim.pang.Layout.SimpleDividerItemDecoration;
import com.macmoim.pang.adapter.FoodRecyclerViewAdapter;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.data.LoginPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by P11872 on 2015-08-13.
 */
class RequestFeedListActivity extends AppCompatActivity {

    protected static final String TAG = "RequestFeedListActivity";
    protected String category = null;
    protected List<FoodItem> feedItems;
    protected String mLatestTimestamp;
    protected RecyclerView rv;
    protected String user_id;
    protected String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.requestfeed);

        initViews();

        setupRecyclerView(rv);
        ShowList();
    }

    public void onCreate(Bundle savedInstanceState, int LayoutId) {
        super.onCreate(savedInstanceState);

        setContentView(LayoutId);

        initViews();

        setupRecyclerView(rv);
    }

    private void initViews() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.feed_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        rv = (RecyclerView) findViewById(R.id.feed_recyclerview);

        setUserId();
    }

    protected void setUserId() {
        user_id = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);
        user_name = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_NAME);
    }


    protected void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new FoodRecyclerViewAdapter(this, feedItems));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }


    protected void setLatestTimestamp(String time) {
        mLatestTimestamp = time;
    }

    protected void ShowList() {
    }

    protected void DeleteItem(int dbId) {
    }

    @Override
    protected void onDestroy() {
        if (feedItems != null) {
            feedItems.clear();
            feedItems = null;
        }

        if (mLatestTimestamp != null) {
            mLatestTimestamp = null;
        }

        if (user_id != null) {
            user_id = null;
        }

        if (rv != null) {
            rv = null;
        }
        super.onDestroy();
    }
}
