package com.macmoim.pang.pangeditor;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.R;
import com.macmoim.pang.adapter.FoodCommentRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodCommentItem;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.layout.SimpleDividerItemDecoration;
import com.macmoim.pang.layoutmanager.MyLinearLayoutManager;
import com.macmoim.pang.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-08-11.
 */
public class CommentActivity2 extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private MaterialEditText mCommentEdit;
    private ExtDialog mDialog;

    private RecyclerView mCommentRv;
    private ArrayList<FoodCommentItem> arFoodCommentItems = null;
    private Button mSendBtn;

    private int mPostId = 0;
    private String mPostUserId;
    private String mCommentUserId;

    private static final String URL_COMMENT = Util.SERVER_ROOT + "/comment";

    private static final String VOLLEY_REQ_TAG_COMMENT = "get-comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_main2);

        mPostId = getIntent().getIntExtra("post_id", 0);
        mPostUserId = getIntent().getStringExtra("post_user_id");
        mCommentUserId = getIntent().getStringExtra("comment_user_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mCommentEdit = (MaterialEditText) findViewById(R.id.comment_edit);
        mCommentEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.server_define_comment))});

        mCommentRv = (RecyclerView) findViewById(R.id.recyclerview_comment);
        SetUpRecyclerView(mCommentRv);

        mSendBtn = (Button) findViewById(R.id.comment_send_btn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putComment();
            }
        });

        GetComment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.editor_menu, menu);
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void SetUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

        if (arFoodCommentItems == null) {
            arFoodCommentItems = new ArrayList<FoodCommentItem>();
        }

        MyLinearLayoutManager _LayoutManager = new MyLinearLayoutManager(recyclerView.getContext(), OrientationHelper.VERTICAL, false);
        _LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_LayoutManager);
        recyclerView.setAdapter(new FoodCommentRecyclerViewAdapter(CommentActivity2.this, arFoodCommentItems));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext(), getResources().getDrawable(R.drawable.line_divider_violet)));
    }

    private void GetComment() {
        Log.d(TAG, "GetComment postid " + mPostId);
        String _Url = URL_COMMENT + "/" + String.valueOf(mPostId);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, _Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null && mCommentRv != null) {
                    try {
                        JSONArray _FeedArray = response.getJSONArray("comment_info");

                        if (arFoodCommentItems != null) {
                            arFoodCommentItems.clear();
                        }

                        int _Length = _FeedArray.length();

                        for (int i = 0; i < _Length; i++) {
                            JSONObject _Obj = (JSONObject) _FeedArray.get(i);

                            FoodCommentItem _Item = new FoodCommentItem();

                            _Item.setPostId(mPostId);
                            _Item.setPostUserId(mPostUserId);
                            _Item.setCommentUserId(_Obj.getString("comment_user_id"));
                            _Item.setCommentUserName(_Obj.getString("comment_user_name"));
                            _Item.setComment(_Obj.getString("comment"));
                            _Item.setTimeStamp(_Obj.getString("upload_date"));
                            _Item.setProfileImgUrl(_Obj.getString("user_profile_img_url"));

                            Log.d(TAG, "comment = " + _Obj.getString("comment"));

                            if (!arFoodCommentItems.contains(_Item)) {
                                arFoodCommentItems.add(0, _Item);
                            }
                        }

                        // notify data changes to list adapater
                        mCommentRv.getAdapter().notifyDataSetChanged();

//                        if (arFoodCommentItems.isEmpty()) {
//                            lNoComments.setVisibility(View.VISIBLE);
//                        } else {
//                            lNoComments.setVisibility(View.GONE);
//                        }
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_COMMENT);
    }

    private void putComment() {
        ShowDialog();
        String comment = mCommentEdit.getText().toString();
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("post_id", String.valueOf(mPostId));
        obj.put("post_user_id", mPostUserId);
        obj.put("comment_user_id", mCommentUserId);
        obj.put("comment", comment);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                URL_COMMENT, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        int comment_id = response.getInt("id");
                        Log.d(TAG, "comment add db id " + comment_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                RemoveDialog();
                mCommentEdit.setText("");
                GetComment();
//                setResult(Activity.RESULT_OK);
//                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                RemoveDialog();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void ShowDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            ProgressCircleDialogAttr _Attr = new ProgressCircleDialogAttr();
            _Attr.Message = getResources().getString(R.string.loading);
            _Attr.Cancelable = false;

            mDialog = ExtDialogSt.Get().GetProgressCircleExtDialog(this, _Attr);
        }

        mDialog.show();
    }

    private void RemoveDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCommentEdit = null;
        super.onDestroy();
    }
}
