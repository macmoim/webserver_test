package com.macmoim.pang;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-08-11.
 */
public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private MaterialEditText mCommentEdit;
    private ExtDialog mDialog;

    private static final String URL_COMMENT = Util.SERVER_ROOT+"/comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mCommentEdit = (MaterialEditText) findViewById(R.id.comment_edit);
        mCommentEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.server_define_comment))});
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit: {
                putComment();
                return true;
            }
            case android.R.id.home: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void putComment() {
        showDialog();
        int post_id = getIntent().getIntExtra("post_id", 0);
        String postUserId = getIntent().getStringExtra("post_user_id");
        String comment_user_id = getIntent().getStringExtra("comment_user_id");
        String comment = mCommentEdit.getText().toString();
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("post_id", String.valueOf(post_id));
        obj.put("post_user_id", postUserId);
        obj.put("comment_user_id", comment_user_id);
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
                removeDialog();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                removeDialog();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void showDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            ProgressCircleDialogAttr attr = new ProgressCircleDialogAttr();
            attr.Message = getResources().getString(R.string.loading);
            attr.MessageColor = R.color.white_op100;
            mDialog = ExtDialogSt.Get().GetProgressCircleExtDialog(this, attr);
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
    }

    @Override
    protected void onDestroy() {
        mCommentEdit = null;
        super.onDestroy();
    }
}
