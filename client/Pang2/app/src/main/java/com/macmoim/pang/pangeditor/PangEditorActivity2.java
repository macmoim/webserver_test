package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.R;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.util.Util;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.layoutmanager.MyLinearLayoutManager;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

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
public class PangEditorActivity2 extends AppCompatActivity {

    static final String TAG = "PangEditorActivity2";

    protected static final String URL_POST_IMAGE = Util.SERVER_ROOT + "/page/image";
    protected static final String URL_POST_POST = Util.SERVER_ROOT + "/post/post_new";

    protected MaterialEditText mTitleEdit;
    protected MaterialBetterSpinner mSpinner;
    protected String mSelectedFood;
    protected Button mPageAddBtn;
    protected RecyclerView mRecyclerView;
    protected String mUserId;
    protected ExtDialog mDialog;

    protected ArrayList<PageItem> mPageItems;


    protected final int REQ_ADD_PAGE = 1;
    protected final int REQ_EDIT_PAGE = 2;
    protected final String VOLLEY_REQ_TAG_POST = "req-post-edit";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ediotr_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupActionBar();

        mUserId = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);

        mTitleEdit = (MaterialEditText) findViewById(R.id.title);
        mTitleEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.server_define_title))});

        final String[] spinnerArr = getResources().getStringArray(R.array.food_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArr);
        adapter.setDropDownViewResource(R.layout.food_spinner_item);
        mSpinner = (MaterialBetterSpinner) findViewById(R.id.food_spinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedFood = spinnerArr[position];
            }
        });


        mPageAddBtn = (Button) findViewById(R.id.add_page_btn);
        mPageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAddPageDialog();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.editor_item_recycler_view);
        setupRecyclerView(mRecyclerView);
    }

    public void setupActionBar() {
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit: {
                File[] files = new File[mPageItems.size()];
                for (int i = 0; i < files.length; i++) {
                    files[i] = new File(mPageItems.get(i).getImageUri().getPath());
                }
                new ResizeBitmapTask().execute(files);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        mPageItems = new ArrayList<PageItem>();
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(recyclerView.getContext(), OrientationHelper.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new PangEditorAdapter(PangEditorActivity2.this, mPageItems));
        recyclerView.setNestedScrollingEnabled(false);
    }

    public void ShowAddPageDialog() {
        Intent intent = new Intent(this, AddPageActivity.class);
        startActivityForResult(intent, REQ_ADD_PAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD_PAGE) {
            if (resultCode == Activity.RESULT_OK) {
                PageItem item = new PageItem();
                item.setContents(data.getStringExtra("content"));
                Log.d(TAG, "returned addPageSetResult content  " + data.getStringExtra("content"));
                Log.d(TAG, "returned addPageSetResult uri " + Uri.parse(data.getStringExtra("image-uri")));
                item.setImageUri(Uri.parse(data.getStringExtra("image-uri")));
                boolean isThumbChecked = data.getBooleanExtra("thumb-img-checked", false);
                item.setIsThumbImg(isThumbChecked);
                if (isThumbChecked) {
                    for (PageItem i : mPageItems) {
                        i.setIsThumbImg(false);
                    }
                }
                mPageItems.add(item);
                mRecyclerView.getAdapter().notifyDataSetChanged();

            }
        }
    }

    class ResizeBitmapTask extends AsyncTask<File, Void, ArrayList<File>> {

        @Override
        protected ArrayList<File> doInBackground(File... params) {
            ArrayList<File> files = new ArrayList<>();
            for (File f : params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                long fileSize = f.length();
                if (fileSize > 2 * 1024 * 1024) {
                    options.inSampleSize = 2;
                } else if (fileSize < 700 * 1024) {
                    files.add(f);
                    continue;
                } else {
                    options.inSampleSize = 1;
                }

                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);


                OutputStream out = null;
                try {
                    out = new FileOutputStream(f);

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
                files.add(f);
            }


            return files;
        }

        @Override
        protected void onPostExecute(ArrayList<File> s) {
            super.onPostExecute(s);
            requestPages(s);
        }
    }

    public void requestPages(ArrayList<File> thumbFile) {
        String title = mTitleEdit.getText().toString();
        if ("".equals(title)) {
            Toast.makeText(getApplicationContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(mSelectedFood)) {
            Toast.makeText(getApplicationContext(), "카테고리를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", title);
        obj_body.put("category", mSelectedFood);
        obj_body.put("user_id", mUserId);
        obj_body.put("thumbnail_index", String.valueOf(getThumbIndex(mPageItems)));
        int content_count = 0;
        for (PageItem p : mPageItems) {
            obj_body.put("content" + String.valueOf(content_count++), p.getContents());
        }

        Map<String, File> obj_file = new HashMap<String, File>();
        int key_count = 0;
        for (File f : thumbFile) {
            obj_file.put("image" + String.valueOf(key_count++), f);
            Log.d(TAG, "requestPages file path " + (f == null ? "null" : f.getAbsolutePath()));
        }

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                URL_POST_POST, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Toast.makeText(getApplicationContext(), "저장성공", Toast.LENGTH_LONG).show();
                            removeCropFiles();
                            removeDialog();
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
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
        showDialog();
    }

    public void removeCropFiles() {
        for (PageItem p : mPageItems) {
            if (p.getImageUri() != null) {
                File file = new File(p.getImageUri().getPath());
                file.delete();
            }
        }

    }

    public int getThumbIndex(ArrayList<PageItem> items) {
        int thumbIndex = items.size()-1;
        for (int i=0; i<items.size(); i++) {
            if (items.get(i).getIsThumbImg()) {
                thumbIndex = i;
                break;
            }
        }
        return thumbIndex;
    }

    public void showDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            ProgressCircleDialogAttr _Attr = new ProgressCircleDialogAttr();
            _Attr.Cancelable = false;
            _Attr.Message = getResources().getString(R.string.loading);
            mDialog = ExtDialogSt.Get().GetProgressCircleExtDialog(this, _Attr);
        }

        mDialog.show();

    }

    public void removeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    public void onBackPressed() {
        ShowExitEditorDialog();
    }

    public void ShowExitEditorDialog() {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Cancelable = false;
        _Attr.Title = getString(R.string.editor_exit_title);
        _Attr.TitleIcon = R.drawable.ic_pencil;
        _Attr.Message = getString(R.string.editor_exit);
        _Attr.NegativeButton = getString(R.string.no);
        _Attr.PositiveButton = getString(R.string.yes);
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                finish();
                super.OnPositive(dialog);
            }

            @Override
            public void OnNegative(ExtDialog dialog) {
                super.OnNegative(dialog);
            }
        };

        ExtDialogSt.Get().AlertExtDialog(this, _Attr);
    }

}
