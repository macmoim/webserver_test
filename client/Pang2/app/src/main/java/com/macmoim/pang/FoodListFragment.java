/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.macmoim.pang;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.adapter.FoodRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FoodListFragment extends Fragment {
    private static final String TAG = "FoodListFragment";

    private String URL = "http://localhost:8080/web_test/thumbImageList";
    private List<FoodItem> feedItems;
    RecyclerView rv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    String mLatestTimestamp;

    private static String REQ_TAG = "FOOD-REQ";

    public static FoodListFragment getInstance(int position) {

        //Construct the fragment
        FoodListFragment myFragment = new FoodListFragment();

        //New bundle instance
        Bundle args = new Bundle();

        //Passing in the Integer position of the fragment into the argument
        args.putInt("position", position);

        //Setting the argument of the fragment to be the position
        myFragment.setArguments(args);

        REQ_TAG += position;

        //Return the fragment
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.fragment_cheese_list, container, false);


        rv = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.recyclerview);
        setupRecyclerView(rv);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];
                if ("Popular".equals(category)) {
                    refreshListWithClearingArray();
                } else {
                    refreshListByTimeStamp();
                }
            }


        });

        showList();

        return mSwipeRefreshLayout;
    }

    private void showList() {
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL + "/" + ctg;


        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response, false);
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
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }

    private void refreshListByTimeStamp() {
        onStartRefresh();
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL + "/" + ctg + "/" + mLatestTimestamp;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response, false);
                }
                onFinishRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                onFinishRefresh();
            }
        });
//	}
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }

    private void refreshListWithClearingArray() {
        onStartRefresh();
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL + "/" + ctg;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response, true);
                }
                onFinishRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                onFinishRefresh();
            }
        });
//	}
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new FoodRecyclerViewAdapter(getActivity(), feedItems));
    }

    private void onStartRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void onFinishRefresh() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (feedItems != null && feedItems.size() > 0) {
            setLatestTimestamp(feedItems.get(0).getTimeStamp());
        }
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    private void parseJsonFeed(JSONObject response, boolean toClearArray) {
        if (rv == null) {
            return;
        }
        if (toClearArray) {
            feedItems.clear();
        }
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLatestTimestamp(String time) {
        mLatestTimestamp = time;
    }

    @Override
    public void onDestroyView() {
//        AppController.getInstance().cancelPendingRequests(REQ_TAG);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(null);
            mSwipeRefreshLayout.removeAllViews();
        }
        if (rv != null) {
            rv.removeAllViews();
            rv.setLayoutManager(null);
            rv.setAdapter(null);
            rv = null;
        }
        super.onDestroyView();
    }
}
