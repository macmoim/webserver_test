package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.layoutmanager.MyLinearLayoutManager;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.macmoim.pang.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-10-02.
 */
public class PangEditorEditModeActivity2 extends PangEditorActivity2 {

    static final String TAG = "PangEditorEditModeActivity2";

    protected static final String URL_UPDATE_POST = Util.SERVER_ROOT + "/post/post_update_new";

    private int mChagedIndex;
    private String mOldUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupActionBar() {
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:
            case android.R.id.home:
                requestUpdatePost();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        requestUpdatePost();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_PAGE) {
            if (resultCode == Activity.RESULT_OK) {
                PageItem item = new PageItem();
                item.setContents(data.getStringExtra("content"));
                String localUri = null;
                if (data.hasExtra("image-uri")) {
                    localUri = data.getStringExtra("image-uri");
                }
//                item.setImageUri(Uri.parse(data.getStringExtra("image-uri")));
                if (data.hasExtra("old-image-url")) {
                    mOldUrl = data.getStringExtra("old-image-url");
                } else {
                    mOldUrl = null;
                }
                mChagedIndex = data.getIntExtra("index", 0);
                mPageItems.set(mChagedIndex, item);


                if (localUri != null && localUri.contains("storage")) {
                    File file = new File(Uri.parse(localUri).getPath());
                    new ResizeBitmapTask().execute(file);
                } else {
                    requestPageOnlyContent();
                }
            }
        } else if (requestCode == REQ_ADD_PAGE) {
            if (resultCode == Activity.RESULT_OK) {
                PageItem item = new PageItem();
                item.setContents(data.getStringExtra("content"));
//                item.setImageUri(Uri.parse(data.getStringExtra("image-uri")));
                mPageItems.add(item);
                mChagedIndex = mPageItems.size()-1;


                File file = new File(Uri.parse(data.getStringExtra("image-uri")).getPath());
                new ResizeBitmapTask().execute(file);


            }
        }
    }

    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        mPageItems = new ArrayList<PageItem>();

        RequestPageItems();
    }

    private void setupRecyclerViewEditMode() {
        mRecyclerView.setHasFixedSize(true);
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(mRecyclerView.getContext(), OrientationHelper.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        PangEditorEditModeAdapter adapter = new PangEditorEditModeAdapter(PangEditorEditModeActivity2.this, mPageItems);
        adapter.setListener(new PangEditorEditModeAdapter.EditClickListener() {
            @Override
            public void OnEditViewClick(int index) {

                Intent i = new Intent(PangEditorEditModeActivity2.this, EditPageActivity.class);
                i.putExtra("index", index);
                i.putExtra("content", mPageItems.get(index).getContents());
                i.putExtra("img_url", mPageItems.get(index).getImageUri().toString());
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(i, REQ_EDIT_PAGE);
            }
        });



        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    private void RequestPageItems() {
        showDialog();
        int id = getIntent().getIntExtra("id", 0);
        String url = Util.SERVER_ROOT + "/post/get_new/"+String.valueOf(id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    OnResponseRequestPage(response);

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
                removeDialog();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_POST);
    }

    private void OnResponseRequestPage(JSONObject response) {
        {
            try {
                if ("success".equals(response.getString("ret_val"))) {
                    ArrayList<String> contentList = Util.splitString(response.getString("page_content"), "\\|");
                    ArrayList<String> imagePathList = Util.splitString(response.getString("img_path"), "\\|");
                    for (int i=0; i<contentList.size(); i++) {
                        PageItem item = new PageItem(contentList.get(i), Uri.parse(imagePathList.get(i)));
                        mPageItems.add(item);
                    }

                    String thumbImgPath = response.getString("thumb_img_path");
                    thumbImgPath = Util.splitFilename(thumbImgPath);
                    String title = response.getString("title");
                    mTitleEdit.setText(title);
                    mSelectedFood = response.getString("category");

                    mTitleEdit.setText(title);
                    mSpinner.setText(mSelectedFood);

                    setupRecyclerViewEditMode();

//                    ((PangEditorEditModeAdapter)mRecyclerView.getAdapter()).notifyDataSetChanged();

                }
                removeDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class ResizeBitmapTask extends AsyncTask<File, Void, File> {

        @Override
        protected File doInBackground(File... params) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                long fileSize = params[0].length();
                if (fileSize > 2 * 1024 * 1024) {
                    options.inSampleSize = 2;
                } else if (fileSize < 700 * 1024) {
                    return params[0];
                } else {
                    options.inSampleSize = 1;
                }

                Bitmap bitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath(), options);


                OutputStream out = null;
                try {
                    out = new FileOutputStream(params[0]);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return params[0];

        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            requestPage(s);
        }
    }

    @Override
    public void requestPages(ArrayList<File> thumbFile) {

    }

    private void requestPage(File file) {
        String title = mTitleEdit.getText().toString();
        if ("".equals(title)) {
            Toast.makeText(getApplicationContext(), "제목을 입력하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(mSelectedFood)) {
            Toast.makeText(getApplicationContext(), "카테고리를 선택하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", title);
        obj_body.put("category", mSelectedFood);
        obj_body.put("user_id", mUserId);
        obj_body.put("thumbnail_index", "0");
        obj_body.put("id", String.valueOf(getIntent().getIntExtra("id", 0)));
        obj_body.put("index", String.valueOf(mChagedIndex));
        obj_body.put("content", mPageItems.get(mChagedIndex).getContents());
        if (mOldUrl != null) {
            Log.d(TAG, "old_image_url " + mOldUrl);
            obj_body.put("old_image_url", mOldUrl);
        }


        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", file);


        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                Util.SERVER_ROOT + "/page/update", JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Log.d(TAG, "requestPage success");
                            Toast.makeText(getApplicationContext(), "저장성공", Toast.LENGTH_LONG).show();
                            mOldUrl = null;
                            setNetworkImage(response.getString("img_path"));
                            removeCropFiles();
                            removeDialog();
                        }

                    } catch (JSONException e) {
                        removeDialog();
                        e.printStackTrace();
                    }
                }
                VolleyLog.d(TAG, "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                removeDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestThumbImage requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
        showDialog();
    }

    private void setNetworkImage(String imageUrl) {
        mPageItems.get(mChagedIndex).setImageUri(Uri.parse(imageUrl));
        ((PangEditorEditModeAdapter)mRecyclerView.getAdapter()).notifyDataSetChanged();
    }

    private void requestPageOnlyContent() {
        String title = mTitleEdit.getText().toString();
        if ("".equals(title)) {
            Toast.makeText(getApplicationContext(), "제목을 입력하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(mSelectedFood)) {
            Toast.makeText(getApplicationContext(), "카테고리를 선택하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", title);
        obj_body.put("category", mSelectedFood);
        obj_body.put("user_id", mUserId);
        obj_body.put("thumbnail_index", "0");
        obj_body.put("id", String.valueOf(getIntent().getIntExtra("id", 0)));
        obj_body.put("index", String.valueOf(mChagedIndex));
        obj_body.put("content", mPageItems.get(mChagedIndex).getContents());


        @SuppressWarnings("unchecked")
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                Util.SERVER_ROOT + "/page/update_content", obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Log.d(TAG, "requestPageOnlyContent success");
                            Toast.makeText(getApplicationContext(), "저장성공", Toast.LENGTH_LONG).show();
                            removeDialog();
                        }

                    } catch (JSONException e) {
                        removeDialog();
                        e.printStackTrace();
                    }
                }
                VolleyLog.d(TAG, "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                removeDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestThumbImage requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
        showDialog();
    }


    public void requestUpdatePost() {
        String title = mTitleEdit.getText().toString();
        if ("".equals(title)) {
            Toast.makeText(getApplicationContext(), "제목을 입력하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(mSelectedFood)) {
            Toast.makeText(getApplicationContext(), "카테고리를 선택하세요.",  Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", title);
        obj_body.put("category", mSelectedFood);
        obj_body.put("user_id", mUserId);
        obj_body.put("thumbnail_index", "0");
        obj_body.put("id", String.valueOf(getIntent().getIntExtra("id", 0)));


        @SuppressWarnings("unchecked")
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                URL_UPDATE_POST, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Log.d(TAG, "requestUpdatePost success");
                            Toast.makeText(getApplicationContext(), "저장성공", Toast.LENGTH_LONG).show();
                            removeCropFiles();
                            removeDialog();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }

                    } catch (JSONException e) {
                        removeDialog();
                        e.printStackTrace();
                    }
                }
                VolleyLog.d(TAG, "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                removeDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestThumbImage requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
        showDialog();
    }
}
