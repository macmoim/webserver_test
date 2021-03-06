package com.macmoim.pang;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.layout.SimpleDividerItemDecoration;
import com.macmoim.pang.layout.swipe.util.Attributes;
import com.macmoim.pang.adapter.SwipeFoodRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by P11872 on 2015-08-16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class LikeActivity extends RequestFeedListActivity implements SwipeFoodRecyclerViewAdapter.Listener {
    private String URL = Util.SERVER_ROOT + "/like";

    private static final String VOLLEY_REQ_TAG_LIKE = "get-mylike";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SwipeFoodRecyclerViewAdapter) rv.getAdapter()).setListener(this);

    }

    @Override
    public String GetToolBarTitle() {
        return getResources().getString(R.string.my_like);
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SwipeFoodRecyclerViewAdapter(LikeActivity.this, feedItems));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        ((SwipeFoodRecyclerViewAdapter) rv.getAdapter()).setMode(Attributes.Mode.Single);
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
    }

    @Override
    protected void ShowList() {
        String url = URL + "/" + user_id;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {

                try {
                    Objects.requireNonNull(response, "response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    parseJsonFeed(response);
                    if (feedItems != null && feedItems.size() > 0) {
                        setLatestTimestamp(feedItems.get(0).getTimeStamp());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
            }
        });
//	}
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_LIKE);
    }

    protected void parseJsonFeed(JSONObject response) {
        if (rv == null) {
            return;
        }
        try {
            if ("success".equals(response.getString("ret_val"))) {
                if (feedItems != null && feedItems.size() > 0) {
                    feedItems.clear();
                }
                JSONArray feedArray = response.getJSONArray("like_info");

                int length = feedArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    FoodItem item = new FoodItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("title"));
                    item.setUserId(feedObj.getString("user_id"));
                    item.setUserName(feedObj.getString("user_name"));

                    // Image might be null sometimes
                    String image = feedObj.isNull("img_path") ? null : feedObj
                            .getString("img_path");
                    item.setImge(image);
                    item.setTimeStamp(feedObj.getString("date"));


                    Log.d(TAG, "parseJsonFeed dbname " + feedObj
                            .getString("img_path"));
                    feedItems.add(0, item);

                }

                // notify data changes to list adapater
                rv.getAdapter().notifyDataSetChanged();
            } else {
                Log.e(TAG, "return fail : " + response.getString("ret_detail"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_LIKE);
        super.onDestroy();
    }

    @Override
    public void onDeleteButtonClick(int position) {

    }

    @Override
    public void onEditButtonClick(int position) {

    }

    @Override
    public void onLikeButtonClick(int position) {
        {

            try {
                Objects.requireNonNull(feedItems, "feedItems is null");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            FoodItem item = feedItems.get(position);
            String url = URL;
            int method = Request.Method.PUT;

            Map<String, String> obj = new HashMap<String, String>();
            int post_id = item.getId();
            String like_user_id = LoginPreferences.GetInstance().getString(getApplicationContext(), LoginPreferences.PROFILE_ID);
            obj.put("user_id", like_user_id);
            obj.put("like", "0");
            obj.put("post_id", String.valueOf(post_id));
            obj.put("post_user_id", item.getUserId());

            url += "/" + post_id + "/" + like_user_id + "/" + "0";

            Request jsonReq = new CustomRequest(method,
                    url, obj, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        try {
                            if (response.has("id")) {
                                int like_id = response.getInt("id");
                                Log.d(TAG, "like add db id " + like_id);
                            } else {
                                Log.d(TAG, "like update " + response.get("ret"));
                            }
                            ShowList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_LIKE);
        }
    }
}
