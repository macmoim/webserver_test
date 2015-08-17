package com.macmoim.pang;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by P11872 on 2015-08-16.
 */
public class LikeActivity extends RequestFeedListActivity {

    private String URL = "http://localhost:8080/web_test/getMyLikesImageList.php";
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user_id = (String) getIntent().getExtras().get("user_id");
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void ShowList() {
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("user_id", user_id);


        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                URL, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response);
                    if (feedItems != null && feedItems.size() > 0) {
                        setLatestTimestamp(feedItems.get(0).getTimeStamp());
                    }
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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    protected void parseJsonFeed(JSONObject response) {
        if (rv == null) {
            return;
        }
        try {
            JSONArray feedArray = response.getJSONArray("like_info");

            int length = feedArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FoodItem item = new FoodItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("title"));
                item.setUserId(feedObj.getString("user_id"));

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
