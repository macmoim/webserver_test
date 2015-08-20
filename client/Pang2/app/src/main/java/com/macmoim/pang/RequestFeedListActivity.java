package com.macmoim.pang;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.adapter.FoodRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.CommonSharedPreperences;
import com.macmoim.pang.data.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.feed_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        rv = (RecyclerView) findViewById(R.id.feed_recyclerview);

        user_id = CommonSharedPreperences.GetInstance(this).getString(CommonSharedPreperences.KEY_ID);
        user_name = CommonSharedPreperences.GetInstance(this).getString(CommonSharedPreperences.KEY_NAME);

        setupRecyclerView(rv);
        ShowList();
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new FoodRecyclerViewAdapter(this, feedItems));
    }



    protected void setLatestTimestamp(String time) {
        mLatestTimestamp = time;
    }

    protected void ShowList(){}

    protected void DeleteItem(int dbId){}

    @Override
    protected void onDestroy() {
        if(feedItems != null){
            feedItems.clear();
            feedItems = null;
        }

        if(mLatestTimestamp != null){
            mLatestTimestamp = null;
        }

        if(user_id != null){
            user_id = null;
        }

        if(rv != null){
            rv = null;
        }
        super.onDestroy();
    }
}
