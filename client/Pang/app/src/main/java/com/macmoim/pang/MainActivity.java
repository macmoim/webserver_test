package com.macmoim.pang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.NavigationDrawer.NavigationDrawerCallbacks;
import com.macmoim.pang.NavigationDrawer.NavigationDrawerFragment;
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

public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {
	private static final String TAG = MainActivity.class.getSimpleName();
	private ListView listView;
	private FeedListAdapter listAdapter;
	private List<FeedItem> feedItems;
	private String URL_FEED = "http://localhost:8080/web_test/image_test/image_rest.php";//"http://api.androidhive.info/feed/feed.json";

	private Toolbar mToolbar;
	private NavigationDrawerFragment mNavigationDrawerFragment;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
		mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

		listView = (ListView) findViewById(R.id.list);

		feedItems = new ArrayList<FeedItem>();

		listAdapter = new FeedListAdapter(this, feedItems);
		listView.setAdapter(listAdapter);
		
		// These two lines not needed,
		// just to get the look of facebook (changing background color & hiding the icon)
//		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
//		getActionBar().setIcon(
//				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		// We first check for cached request
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(URL_FEED);
		if (entry != null) {
			// fetch the data from cache
			try {
			    Log.d(TAG, "now on cache");
				String data = new String(entry.data, "UTF-8");
				try {
					parseJsonFeed(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
		    Log.d(TAG, "now on new connection");
			// making fresh volley request and getting json
		    Map<String, String> obj = new HashMap<String, String>();
            obj.put("action", "get_all_images");
            
			CustomRequest jsonReq = new CustomRequest(Method.POST,
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

			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq);
		}

	}

	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response) {
		try {
			JSONArray feedArray = response.getJSONArray("image_info");

			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);

				FeedItem item = new FeedItem();
				item.setId(feedObj.getInt("id"));
				item.setName(feedObj.getString("title"));

				// Image might be null sometimes
				String image = feedObj.isNull("image") ? null : feedObj
						.getString("image");
				item.setImge(image);
				item.setTimeStamp(feedObj.getString("date"));


				Log.d(TAG, "parseJsonFeed dbname " + feedObj
                        .getString("image"));
				feedItems.add(item);
			}

			// notify data changes to list adapater
			listAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		//Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
	}

}
