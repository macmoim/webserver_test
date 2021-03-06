package com.macmoim.pang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.gcm.MyGcmListenerService;
import com.macmoim.pang.pangeditor.PangEditorActivity2;
import com.macmoim.pang.pangeditor.PangEditorEditModeActivity2;
import com.macmoim.pang.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by P11872 on 2015-08-16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyPostActivity extends RequestFeedListActivity implements SwipeFoodRecyclerViewAdapter.Listener {

    private String URL = Util.SERVER_ROOT + "/post/user";
    private String URL_DELETE = Util.SERVER_ROOT + "/post";

    private static final String VOLLEY_REQ_TAG_MYPOST = "get-mypost";
    private static final String VOLLEY_REQ_TAG_DEL_MYPOST = "del-mypost";

    private static final int REQ_EDIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SwipeFoodRecyclerViewAdapter) rv.getAdapter()).setListener(this);

        if (getIntent().getBooleanExtra(MyGcmListenerService.FROM_GCM_EXTRA, false)) {
            MyGcmListenerService.resetNotiId();
        }

    }

    @Override
    public String GetToolBarTitle() {
        return getResources().getString(R.string.my_post);
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        feedItems = new ArrayList<FoodItem>();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SwipeFoodRecyclerViewAdapter(MyPostActivity.this, feedItems));
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_MYPOST);
    }

    @Override
    protected void DeleteItem(int dbId) {
        String url = URL_DELETE + "/" + dbId;


        CustomRequest jsonReq = new CustomRequest(Request.Method.DELETE,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Objects.requireNonNull(response, "response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());

                    String result = response.getString("ret_val");
                    if ("success".equals(result)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                        ShowList();
                    } else {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_DEL_MYPOST);
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
                JSONArray feedArray = response.getJSONArray("my_post");

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
    public void onDeleteButtonClick(int position) {
        try {
            Objects.requireNonNull(feedItems, "feedItems is null");
            ShowDeleteDialog(feedItems.get(position).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEditButtonClick(int position) {
        try {
            Objects.requireNonNull(feedItems, "feedItems is null");
            Intent intent = new Intent(this, PangEditorEditModeActivity2.class);
            intent.putExtra("edit", true);
            intent.putExtra("id", feedItems.get(position).getId());
            startActivityForResult(intent, REQ_EDIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                ShowList();
            }
        }
    }

    @Override
    public void onLikeButtonClick(int dbId) {

    }

    private void ShowDeleteDialog(final int dbId) {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Cancelable = false;
        _Attr.Title = getString(R.string.delete_post_title);
        _Attr.TitleIcon = R.drawable.ic_trash;
        _Attr.Message = getString(R.string.delete_post);
        _Attr.NegativeButton = getString(R.string.no);
        _Attr.PositiveButton = getString(R.string.yes);
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                //finish();
                DeleteItem(dbId);
                super.OnPositive(dialog);
            }

            @Override
            public void OnNegative(ExtDialog dialog) {
                super.OnNegative(dialog);
            }
        };

        ExtDialogSt.Get().AlertExtDialog(this, _Attr);
    }


    @Override
    protected void onDestroy() {
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_DEL_MYPOST);
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_MYPOST);
        ((SwipeFoodRecyclerViewAdapter) rv.getAdapter()).setListener(null);
        super.onDestroy();
    }
}
