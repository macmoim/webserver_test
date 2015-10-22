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

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.macmoim.pang.adapter.FoodLargeRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.custom.swiperefresh.CustomSwipeHeadView;
import com.macmoim.pang.custom.swiperefresh.CustomSwipeRefreshLayout;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.layout.SimpleDividerItemDecoration;
import com.macmoim.pang.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodListFragment extends Fragment {
    private final String TAG = getClass().getName();

    private String URL = Util.SERVER_ROOT + "/thumbImageList";
    private List<FoodItem> feedItems;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private String mLatestTimestamp;

    private static String REQ_TAG = "FOOD-REQ";

    public static FoodListFragment GetInstance(int position) {
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
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) inflater.inflate(R.layout.fragment_feed_list, container, false);
        // Set a custom HeadView. use default HeadView if not provided
        mSwipeRefreshLayout.SetCustomHeadview(new CustomSwipeHeadView(getActivity(), R.layout.custom_swiperefresh_head_layout));
        // set onRefresh listener
        mSwipeRefreshLayout.SetOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void OnRefresh() {
                InitiateRefresh();
            }
        });

        mRecyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.feed_item_recycler_view);
        SetupRecyclerView(mRecyclerView);
        return mSwipeRefreshLayout;
    }

    @Override
    public void onDestroyView() {
//        AppController.getInstance().cancelPendingRequests(REQ_TAG);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.SetOnRefreshListener(null);
            mSwipeRefreshLayout.removeAllViews();
        }
        if (mRecyclerView != null) {
            mRecyclerView.removeAllViews();
            mRecyclerView.setLayoutManager(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowList();
    }

    private void ShowList() {
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL + "/" + ctg;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    ParseJsonFeed(response, true);
                    if (feedItems != null && feedItems.size() > 0) {
                        SetLatestTimestamp(feedItems.get(0).getTimeStamp());
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

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }

    private void InitiateRefresh() {
        new DummyBackgroundTask().execute(0);
    }

    private void onRefreshComplete(List<String> result) {
        OnFinishRefresh();

        // to notify CustomSwipeRefreshLayout that the refreshing is completed
        mSwipeRefreshLayout.RefreshComplete();
    }

    public class DummyBackgroundTask extends AsyncTask<Integer, Void, List<String>> {
        public static final int TASK_DURATION = 3 * 1000; // 3 seconds
        public static final int LIST_ITEM_COUNT = 20;

        int viewId;

        @Override
        protected List<String> doInBackground(Integer... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DoRefresh();

            // Return a new random list of cheeses
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            // Tell the view that the refresh has completed
            onRefreshComplete(result);
        }
    }

    private void DoRefresh() {
        if (isAdded()) {
            String category = getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];
            if ("Popular".equals(category)) {
                RefreshListWithClearingArray();
            } else {
                RefreshListByTimeStamp();
            }
        } else {
            Log.d(TAG, "doRefresh not fragment added");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void RefreshListByTimeStamp() {
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            Objects.requireNonNull(mLatestTimestamp, "mLatestTimestamp is null");
        } catch (Exception e) {
            e.printStackTrace();
            OnFinishRefresh();
            return;
        }

        String url = URL + "/" + ctg + "/" + mLatestTimestamp.replaceAll(" ", "%20");

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    ParseJsonFeed(response, false);
                }
                OnFinishRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                OnFinishRefresh();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }

    private void RefreshListWithClearingArray() {
        String category = getActivity().getResources().getStringArray(R.array.tabs)[getArguments().getInt("position")];

        String ctg = category;
        try {
            ctg = URLEncoder.encode(category, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            OnFinishRefresh();
        }

        String url = URL + "/" + ctg;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    ParseJsonFeed(response, true);
                }
                OnFinishRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                OnFinishRefresh();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, REQ_TAG);
    }


    private void SetupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new FoodLargeRecyclerViewAdapter(getActivity(), feedItems));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));
    }

    private void OnFinishRefresh() {
        if (feedItems != null && feedItems.size() > 0) {
            SetLatestTimestamp(feedItems.get(0).getTimeStamp());
        }
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    private void ParseJsonFeed(JSONObject response, boolean toClearArray) {
        if (mRecyclerView == null) {
            return;
        }
        if (toClearArray) {
            feedItems.clear();
        }
        try {
            if ("success".equals(response.getString("ret_val"))) {
                JSONArray feedArray = response.getJSONArray("post_info");

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

                    item.setLikeSum(feedObj.getString("like_sum"));
                    String score = feedObj.getString("score");
                    item.setScore("null".equals(score) ? "0" : score);


                    Log.d(TAG, "parseJsonFeed dbname " + feedObj.getString("img_path"));
                    feedItems.add(0, item);
                }

                // notify data changes to list adapater
                mRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                Log.e(TAG, "return fail : " + response.getString("ret_detail"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetLatestTimestamp(String time) {
        mLatestTimestamp = time;
    }
}