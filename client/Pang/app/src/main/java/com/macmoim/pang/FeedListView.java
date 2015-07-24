package com.macmoim.pang;

import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.adapter.FeedListAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by P11872 on 2015-07-24.
 */
public class FeedListView {
    private final static String TAG = "FeedListView";

    private Activity mAct;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://localhost:8080/web_test/image_test/getThumbImageList.php";//"http://api.androidhive.info/feed/feed.json";

    public FeedListView(Activity activity) {
        mAct = activity;
    }

    public void inflate(View layout) {

        listView = (ListView) layout.findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(mAct, feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) mAct);

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
//		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
//		getSupportActionBar().setIcon(
//				new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
//		if (entry != null) {
//			// fetch the data from cache
//			try {
//			    Log.d(TAG, "now on cache");
//				String data = new String(entry.data, "UTF-8");
//				try {
//					parseJsonFeed(new JSONObject(data));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//
//		} else {
        Log.d(TAG, "now on new connection");
        // making fresh volley request and getting json
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("action", "get_thumb_images");

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                URL_FEED, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
//	}
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("post_info");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("filename"));

                // Image might be null sometimes
                String image = feedObj.isNull("img_path") ? null : feedObj
                        .getString("img_path");
                item.setImge(image);
                item.setTimeStamp(feedObj.getString("date"));


                Log.d(TAG, "parseJsonFeed dbname " + feedObj
                        .getString("img_path"));
                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
