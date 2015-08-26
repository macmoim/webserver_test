package com.macmoim.pang;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by P11872 on 2015-08-16.
 */
public class SearchActivity extends RequestFeedListActivity {
    private static final String TAG = "SearchActivity";

    private String URL = "http://localhost:8080/web_test/post/search";

    private EditText mSearchEdit;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.search_activity);


        mSearchEdit = (EditText) findViewById(R.id.search_edit);
        ((Button) findViewById(R.id.search_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.openKeyBoard(getApplicationContext());
    }

    @Override
    protected void ShowList() {
        if (mSearchEdit == null || mSearchEdit.getText() == null) {
            return;
        }

        showDialog();

        String keyword = mSearchEdit.getText().toString();
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = URL + "/" + keyword;


        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response);
                }
                removeDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                removeDialog();
            }
        });
//	}
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    protected void DeleteItem(int dbId) {
    }

    protected void parseJsonFeed(JSONObject response) {
        if (rv == null) {
            return;
        }
        Util.closeKeyBoard(getApplicationContext());
        try {
            if ("success".equals(response.getString("ret_val"))) {
                if (feedItems != null && feedItems.size() > 0) {
                    feedItems.clear();
                }
                JSONArray feedArray = response.getJSONArray("post_info");

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
            } else {
                Log.e(TAG, "return fail : " + response.getString("ret_detail"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            mDialog = new ProgressDialog(this);
        }

        mDialog.show();

    }

    private void removeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.closeKeyBoard(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        mSearchEdit = null;
        removeDialog();
        super.onDestroy();
    }
}
