package com.macmoim.pang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.macmoim.pang.richeditor.RichEditor;
import com.macmoim.pang.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P11872 on 2015-07-21.
 */
public class PangEditorActivity extends AppCompatActivity {
    private static final String TAG = "PangEditorActivity";

    private static final String URL_POST = Util.SERVER_ROOT + "/post";
    private static final String URL_POST_IMAGE = Util.SERVER_ROOT + "/post/image";
    private static final String URL_POST_HTML_UPDATE = Util.SERVER_ROOT + "/post/html/update";

    private String mUserId;
    private RichEditor mEditor = null;
    private MaterialBetterSpinner mSpinner;
    private String mSelectedFood;
    private MaterialEditText mTitleEdit;
    private LinearLayout edit_manubar;

    private ArrayList<String> mImageUrlArr;
    private ExtDialog mDialog;

    private Toolbar mToolbar;

    private Uri mCropImagedUri;

    private WebAppInterface mWebAppInterface;

    private boolean mEditMode = false;
    private int mDbId = -1;
    private String mUpdatedHtmlFilename;
    private String mThumbnailImageURL = "";

    private SelectThumbImageDialog mSelThumbDialog;

    static final int REQ_CODE_PICK_PICTURE = 1;
    static final int REQ_CODE_TAKE_PHOTO = 2;
    static final int REQ_CODE_CROP = 3;

    private static final int PROFILE_IMAGE_ASPECT_X = 4;
    private static final int PROFILE_IMAGE_ASPECT_Y = 3;
    private int PROFILE_IMAGE_OUTPUT_X;
    private int PROFILE_IMAGE_OUTPUT_Y;

    private static final String UPLOAD_IMG_FOLDER = Util.SERVER_ROOT + "/image_test/upload_image/";

    interface HTMLListener {
        void OnGetHTMLSourceCallback(String html);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ediotr_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mUserId = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);

        mImageUrlArr = new ArrayList<>();
        edit_manubar = (LinearLayout) findViewById(R.id.edit_menubar);

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setPlaceholder("Insert text here...");
        mWebAppInterface = new WebAppInterface(this);
        mEditor.addJavascriptInterface(mWebAppInterface, "Android");
        mEditor.setOnInitialLoadListener(new RichEditor.AfterInitialLoadListener() {
            @Override
            public void onAfterInitialLoad(boolean isReady) {
                if (isReady) {
                    mEditor.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (edit_manubar.getVisibility() == View.GONE) {
                                edit_manubar.setVisibility(View.VISIBLE);
                                mEditor.focusEditor();
                            }
                            return false;
                        }
                    });
                }
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        /*findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });
        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });*/

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });


        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mEditor.insertImage(
//                        "http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//                        "dachshund");

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                //retrieve data on return
                intent.putExtra("return-data", true);


                startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
            }
        });

        findViewById(R.id.action_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        mTitleEdit = (MaterialEditText) findViewById(R.id.title);
        mTitleEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (edit_manubar.getVisibility() == View.VISIBLE) {
                    edit_manubar.setVisibility(View.GONE);
                    mTitleEdit.requestFocus();
                }
                return false;
            }
        });

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


        if (getIntent().getBooleanExtra("edit", false)) {
            int id = getIntent().getIntExtra("id", 0);
            if (id > 0) {
                mEditMode = true;
                mDbId = id;
                getPostImageFilename(mDbId);
                editHTML(id);
            }
        }
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
                if (mTitleEdit.getText() == null || "".equals(mTitleEdit.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (mSelectedFood == null || "".equals(mSelectedFood)) {
                    Toast.makeText(getApplicationContext(), "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Log.d(TAG, "titleedit " + mTitleEdit.getText().toString());

                Util.closeKeyBoard(getApplicationContext());

                if (mImageUrlArr.size() > 1) {
                    if (mSelThumbDialog != null) {
                        mSelThumbDialog.setListener(null);
                        mSelThumbDialog = null;
                    }

                    mSelThumbDialog = new SelectThumbImageDialog(PangEditorActivity.this, mImageUrlArr);
                    mSelThumbDialog.setListener(new SelectThumbImageDialog.Listener() {
                        @Override
                        public void onSeletedThumbnail(String url) {
                            mThumbnailImageURL = url;
                            Log.d(TAG, "onSeletedThumbnail " + url);
                            sendRequest();
                        }
                    });
                    mSelThumbDialog.show();
                } else {
                    if (mImageUrlArr.size() == 1) {
                        mThumbnailImageURL = mImageUrlArr.get(0);
                    }
                    sendRequest();
                }


                return true;
            }
            case android.R.id.home: {
                ShowEditCancelDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ShowEditCancelDialog();
//        super.onBackPressed();
    }

    private void sendRequest() {
        showDialog();

        mWebAppInterface.getCurrentHtml(new HTMLListener() {
            @Override
            public void OnGetHTMLSourceCallback(String html) {
                if (mEditMode) {
                    updatePostHTML(html);
//                            updatePostDb();
                } else {
                    insertPost(html);
                }
            }
        });
    }

    private void insertPost(String html) {

        File file = saveHTML(/*mEditor.getHtml()*/html);


        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("user_id", mUserId);
        obj_body.put("title", mTitleEdit.getText().toString());
        obj_body.put("category", mSelectedFood);
        if (mImageUrlArr.size() > 0) {
            // post thumbnail image name
            obj_body.put("thumb_img_url", mThumbnailImageURL);

            // post name of images in html file
            String imgNames = "";
            int length = mImageUrlArr.size();
            for (int i = 0; i < length; i++) {
                imgNames += mImageUrlArr.get(i) + (i == length - 1 ? "" : ":");
            }
            obj_body.put("images_name", imgNames);
        }


        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("html_file", file);


        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                URL_POST, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    parseJsonHtml(response);
                    finishWithResult(Activity.RESULT_OK);
                } else {
                    VolleyLog.d(TAG, "Error: response is null!!!!");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "insertPost requestError : " + error.getMessage());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "PangEditor insert post onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }

                removeDialog();
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);

    }

    private void updatePostHTML(String html) {

        File file = saveHTML(html);


        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("html_file", file);

        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("none", "none");


        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                URL_POST_HTML_UPDATE, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Toast.makeText(getApplicationContext(), "html저장성공", Toast.LENGTH_SHORT).show();
                            mUpdatedHtmlFilename = response.getString("updated_filename");
                            updatePostDb();
                        } else {
                            Toast.makeText(getApplicationContext(), "html저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                    }
                } else {
                    VolleyLog.d(TAG, "Error: response is null!!!!");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "updatePostHTML requestError : " + error.getMessage());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "PangEditor update post onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);

    }

    private void updatePostDb() {


        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("user_id", mUserId);
        obj_body.put("title", mTitleEdit.getText().toString());
        obj_body.put("category", mSelectedFood);
        obj_body.put("db_filename", mUpdatedHtmlFilename);
        if (mImageUrlArr.size() > 0) {
            // post thumbnail image name
            obj_body.put("thumb_img_path", mThumbnailImageURL);

            // post name of images in html file
            String imgNames = "";
            int length = mImageUrlArr.size();
            for (int i = 0; i < length; i++) {
                imgNames += mImageUrlArr.get(i) + (i == length - 1 ? "" : ":");
            }
            obj_body.put("images_name", imgNames);
        }

        String url = URL_POST + "/" + mDbId;
        @SuppressWarnings("unchecked")
        CustomRequest jsonReq = new CustomRequest(Request.Method.PUT,
                url, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                removeDialog();
                if (response != null) {
                    try {
                        if ("success".equals(response.getString("ret_val"))) {
                            Toast.makeText(getApplicationContext(), "post update 성공", Toast.LENGTH_SHORT).show();
                            finishWithResult(Activity.RESULT_OK);
                        } else {
                            Toast.makeText(getApplicationContext(), "post update  실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "PangEditor updatePostDb onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                removeDialog();
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    private File createNewFile(String prefix) {
        if (prefix == null || "".equalsIgnoreCase(prefix)) {
            prefix = "IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory() + "/smtc/");
        if (!newDirectory.exists()) {
            if (newDirectory.mkdir()) {
                Log.d(getApplicationContext().getClass().getName(), newDirectory.getAbsolutePath() + " directory created");
            }
        }
        File file = new File(newDirectory, (prefix + "crop_temp.jpg"));
        if (file.exists()) {
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File f = createNewFile("CROP_");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            if (f != null) {
                mCropImagedUri = Uri.fromFile(f);
                // Continue only if the File was successfully created
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mCropImagedUri);

                startActivityForResult(takePictureIntent, REQ_CODE_TAKE_PHOTO);
            }

        }
    }

    private void dispatchCropIntent(Uri imageCaptureUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        PROFILE_IMAGE_OUTPUT_X = screenSize.x / 2;//mEditor.getWidth() / 2;
        PROFILE_IMAGE_OUTPUT_Y = PROFILE_IMAGE_OUTPUT_X * 3 / 4;
        intent.setDataAndType(imageCaptureUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
        intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
        intent.putExtra("outputX", PROFILE_IMAGE_OUTPUT_X);
        intent.putExtra("outputY", PROFILE_IMAGE_OUTPUT_Y);
        intent.putExtra("scale", true);
        //retrieve data on return
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);

        startActivityForResult(intent, REQ_CODE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();

                File f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);

                dispatchCropIntent(selectedImageUri);
            }
        } else if (requestCode == REQ_CODE_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (mCropImagedUri == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                dispatchCropIntent(mCropImagedUri);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == REQ_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                mCropImagedUri = data.getData();

                new ResizeBitmapTask().execute(new File(mCropImagedUri.getPath()));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.crop_error), Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void parseJson(JSONObject response) {
        String url = "";
        String fileName = "";
        int width = 0;
        int height = 0;
        try {
            url = UPLOAD_IMG_FOLDER + response.getString("file_url");
            fileName = response.getString("file_url");
            width = response.getInt("width");
            height = response.getInt("height");
            Log.d(TAG, "parseJsonFeed upload url " + url + " width " + width + " height " + height);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "upload success width " + width + " height " + height, Toast.LENGTH_SHORT).show();


        mEditor.insertImageFitWindow(url, "food"/*, width, height*/);

        mImageUrlArr.add(fileName);

        if (mEditor != null) {
            mEditor.scrollTo(0, (int) (mEditor.getContentHeight() * mEditor.getScaleY()) + 5000);
        }

        Util.openKeyBoard(getApplicationContext());

    }

    private void parseJsonHtml(JSONObject response) {
        String id = "";
        try {
            id = response.getString("id");
            Log.d(TAG, "parseJsonFeed html upload id " + id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        removeDialog();
        Toast.makeText(getApplicationContext(), "html upload success " + id, Toast.LENGTH_SHORT).show();
    }

    private File saveHTML(String htmlText) {
        String dirPath = getFilesDir().getAbsolutePath();
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File savefile = new File(dirPath + "/test.html");
        try {
            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(htmlText.getBytes());
            fos.close();
            Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savefile;
    }

    private void getPostImageFilename(int id) {
        String url = URL_POST_IMAGE + "/" + String.valueOf(id);


        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        JSONArray feedArray = response.getJSONArray("image_name_info");

                        if (mImageUrlArr != null && mImageUrlArr.size() > 0) {
                            mImageUrlArr.clear();
                        }
                        int length = feedArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);

                            String filename = feedObj.getString("image_filename");
                            mImageUrlArr.add(filename);
                        }

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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void editHTML(int id) {
        String url = URL_POST + "/" + String.valueOf(id);


        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    String htmlPath = "";
                    try {
                        htmlPath = response.getString("filepath");
                        htmlPath += response.getString("db_filename");

                        new ReadHtmlTask().execute(htmlPath);

                        mTitleEdit.setText(response.getString("title"));
                        mSelectedFood = response.getString("category");
                        mSpinner.setText(mSelectedFood);

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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    class ReadHtmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer contents = new StringBuffer("");
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //connect
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    contents.append(new String(buffer, 0, bufferLength));
                }

            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return contents.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mEditor.setHtml(s);
            removeDialog();
        }
    }

    class ResizeBitmapTask extends AsyncTask<File, Void, File> {

        @Override
        protected File doInBackground(File... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            long fileSize = params[0].length();
            if (fileSize > 2 * 1024 * 1024) {
                options.inSampleSize = 4;
            } else if (fileSize < 700 * 1024) {
                return params[0];
            } else {
                options.inSampleSize = 2;
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
            Log.d(TAG, "requestThumbImage path " + s.getAbsolutePath() + " size " + s.length());
            requestThumbImage(s);
        }
    }

    private void requestThumbImage(File thumbFile) {
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", "editor_image.jpg");

        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", thumbFile);

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                URL_POST_IMAGE, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJson(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestThumbImage requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
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

    private void finishWithResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    private void ShowEditCancelDialog() {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Title = getString(R.string.editor_exit_title);
        _Attr.TitleColor = R.color.ExtDialogTitleColor;
        // _Attr.TitleIcon = R.drawable.ic_trash;
        _Attr.Message = getString(R.string.editor_exit);
        _Attr.MessageColor = R.color.ExtDialogMessageColor;
        _Attr.NegativeButton = getString(R.string.no);
        _Attr.NegativeButtonColor = R.color.ExtDialogNegativeButtonTextColor;
        _Attr.PositiveButton = getString(R.string.yes);
        _Attr.PositiveButtonColor = R.color.ExtDialogPositiveButtonTextColor;
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                finish();
            }

            @Override
            public void OnNegative(ExtDialog dialog) {
                super.OnNegative(dialog);
                dialog.cancel();
            }
        };

        ExtDialogSt.Get().AlertExtDialog(this, _Attr);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Util.closeKeyBoard(getApplicationContext());
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mSelThumbDialog != null) {
            mSelThumbDialog.setListener(null);
            mSelThumbDialog = null;
        }
        if (mEditor != null) {
            mEditor.setOnTouchListener(null);
            mEditor.setOnInitialLoadListener(null);
            mEditor.destroy();
            mEditor = null;
        }
        mImageUrlArr = null;
        mSpinner.setOnItemSelectedListener(null);
        mSpinner = null;
        mTitleEdit = null;
        mWebAppInterface = null;
        edit_manubar = null;
        super.onDestroy();

    }

    private class WebAppInterface {

        HTMLListener mListener;
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        public void getCurrentHtml(HTMLListener l) {
            mListener = l;
            mEditor.loadUrl("javascript:showHTML()");
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void onImageDelClick(String value) {
            String filename = (String) value.subSequence(value.lastIndexOf("/") + 1, value.length());
            mImageUrlArr.remove(filename);
        }

        @JavascriptInterface
        public void processHTML(String html) {
            if (mListener != null) {
                mListener.OnGetHTMLSourceCallback(html);
            }
        }

        @JavascriptInterface
        public void onImageItemClick(String value) {
        }
    }


}
