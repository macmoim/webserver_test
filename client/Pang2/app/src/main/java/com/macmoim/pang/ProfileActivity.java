package com.macmoim.pang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.login.SocialProfile;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by P11872 on 2015-08-06.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String UPLOAD_PROFILE_IMAGE_FOLDER = "http://localhost:8080/web_test/image_test/upload_profile_image/";
    private static final String _URL_PROFILE = "http://localhost:8080/web_test/profile";
    private static final String _URL_PROFILE_IMAGE = "http://localhost:8080/web_test/profile/image";
    private static final String _URL_PROFILE_IMAGE_UPDATE = "http://localhost:8080/web_test/profile/image/update";
    private static final int PROFILE_IMAGE_ASPECT_X = 4;
    private static final int PROFILE_IMAGE_ASPECT_Y = 3;
    private FeedItem mFeedItem;
    private ViewHolder nViewHolder;
    private String user_id = null;
    private String user_name = null;
    private boolean editsate = false;
    private Uri mCropImagedUri;
    static final int REQ_CODE_PICK_PICTURE = 1;
    private String mImageURL;
    ImageView backdropimageView;

    private int mProfileDbId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.propile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_id = LoginPreferences.GetInstance().getString(this,LoginPreferences.PROFILE_ID);
        user_name = LoginPreferences.GetInstance().getString(this,LoginPreferences.PROFILE_NAME);

        mFeedItem = new FeedItem();
        nViewHolder = new ViewHolder(this);
        nViewHolder.setviewAllFocus(false);

        backdropimageView = (ImageView) findViewById(R.id.profile_backdrop);
        backdropimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editsate) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
                    intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
                    intent.putExtra("outputX", 640);
                    intent.putExtra("outputY", 480);
                    intent.putExtra("scale", true);
                    //retrieve data on return
                    intent.putExtra("return-data", false);

                    File f = createNewFile("CROP_");
                    try {
                        f.createNewFile();
                    } catch (IOException ex) {
                        Log.e("io", ex.getMessage());
                    }

                    mCropImagedUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);

                    startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
                }
            }
        });

        findViewById(R.id.loginView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, LogInActivity.class));
            }
        });
        OnGetData();
        setFloationAction();

    }

    @Override
    public void onBackPressed() {
        if(editsate){

        }else{
            super.onBackPressed();
        }
    }

    private void setFloationAction() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editsate) {

                    if (nViewHolder.getName() == null) {
                        Toast.makeText(getApplicationContext(), "input name text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((nViewHolder.getEmail() == null)) {
                        Toast.makeText(getApplicationContext(), "input email text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (nViewHolder.getIntro() == null) {
                        Toast.makeText(getApplicationContext(), "input intro text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mImageURL == null) {
                        Toast.makeText(getApplicationContext(), "input image", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mProfileDbId != -1) {
                        onRequestUpdateData();
                    } else {
                        onRequestData();
                    }
                    editsate = false;
                    nViewHolder.setviewAllFocus(false);
                    Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    editsate = true;
                    nViewHolder.setviewAllFocus(true);
                    Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData(JSONObject response) throws JSONException {
        mFeedItem.set_mId(response.getString("user_id"));
        mFeedItem.set_mName(response.getString("user_name"));
        mFeedItem.set_mEmail(response.getString("user_email"));
        mFeedItem.set_mGender(response.getString("user_score"));
        mFeedItem.set_mScore(response.getString("user_gender"));
        mFeedItem.set_mIntro(response.getString("user_intro"));
        //mImageURL = UPLOAD_PROFILE_IMAGE_FOLDER + response.getString("profile_img_url");
        mImageURL = response.getString("profile_img_url");
        if (response.has("id")) {
            mProfileDbId = response.getInt("id");
        }
    }

    private void OnGetData() {

        String url = _URL_PROFILE + "/" + user_id;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                JSONObject val = response;
                try {
                    Objects.requireNonNull(val);
                    showJSONResponseData(val);
                } catch (Exception e) {
                    //e.printStackTrace();
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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void ShowView(JSONObject response) throws JSONException {
        nViewHolder.setID(response.getString("user_id"));
        nViewHolder.setName(response.getString("user_name"));
        nViewHolder.setEmail(response.getString("user_email"));
        nViewHolder.setmScore(response.getString("user_score"));
        nViewHolder.setGender(response.getString("user_gender"));
        nViewHolder.setIntro(response.getString("user_intro"));
        String category = LoginPreferences.GetInstance().getString(this, LoginPreferences.USER_SOCIAL);
        if(category != null) {
            nViewHolder.setLoginView(category);
        }
        loadBackdrop();
    }

    private void showJSONResponseData(JSONObject response) {
        try {
            setData(response);
            ShowView(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onRequestData() {

        Map<String, String> obj = new HashMap<String, String>();
        // temp

        obj.put("user_id", nViewHolder.getID());
        obj.put("user_name", nViewHolder.getName());
        obj.put("user_email", nViewHolder.getEmail());
        obj.put("user_score", nViewHolder.getScore());
        obj.put("user_gender", nViewHolder.getGender());
        obj.put("user_intro", nViewHolder.getIntro());
        obj.put("profile_img_url", mImageURL);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                _URL_PROFILE, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    showJSONResponseData(response);
                    Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void onRequestUpdateData() {
        Map<String, String> obj = new HashMap<String, String>();
        // temp

        obj.put("user_id", nViewHolder.getID());
        obj.put("user_name", nViewHolder.getName());
        obj.put("user_email", nViewHolder.getEmail());
        obj.put("user_score", nViewHolder.getScore());
        obj.put("user_gender", nViewHolder.getGender());
        obj.put("user_intro", nViewHolder.getIntro());
        obj.put("profile_img_url", mImageURL);

        String url = _URL_PROFILE + "/" + mProfileDbId;

        CustomRequest jsonReq = new CustomRequest(Request.Method.PUT,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Objects.requireNonNull(response, " response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());

                    String ret = "";
                    try {
                        ret = response.getString("ret_val");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(ret)) {
//                        showJSONResponseData(response);
                        Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getText(R.string.failsave), Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void loadBackdrop() {

        try {
            Objects.requireNonNull(backdropimageView, " backdropimageView is null");
            Objects.requireNonNull(mImageURL,"mImageURL is null ");

            if ((mImageURL == null)) {
                backdropimageView.setImageResource(R.drawable.person);
            } else {
                backdropimageView.setImageURI(Uri.parse(mImageURL));
            }

            Glide.with(this).load(new URL(mImageURL)).centerCrop().into(backdropimageView);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        File file = new File(newDirectory, (prefix + "crop_profile_temp.jpg"));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Log.d(TAG, "cropresult " + mCropImagedUri + " string " + mCropImagedUri.toString());

                File mFile = new File(mCropImagedUri.getPath());
                if (mProfileDbId != -1) {
                    requestUpdateThumbImage(mFile);
                } else {
                    requestThumbImage(mFile);
                }
            }
        }
    }

    private void requestThumbImage(File thumbFile) {
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", "profile_image.jpg");

        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", thumbFile);

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                _URL_PROFILE_IMAGE, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Objects.requireNonNull(response, "response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    parseJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
    }

    private void requestUpdateThumbImage(File thumbFile) {
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", "profile_image.jpg");
        obj_body.put("filename_old", mImageURL);

        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", thumbFile);

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                _URL_PROFILE_IMAGE_UPDATE, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Objects.requireNonNull(response, "response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    parseJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
    }

    private void parseJson(JSONObject response) {
        int width = 0;
        int height = 0;
        try {
            mImageURL = UPLOAD_PROFILE_IMAGE_FOLDER + response.getString("file_url");
            width = response.getInt("width");
            height = response.getInt("height");
            Log.d(TAG, "parseJsonFeed upload url " + mImageURL + " width " + width + " height " + height);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "upload success width " + width + " height " + height, Toast.LENGTH_SHORT).show();

        loadBackdrop();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class ViewHolder {
        private Activity mActivity;
        private EditText mIDView = null;
        private ImageView mloginView = null;
        private EditText mNameView = null;
        private EditText mEmailView = null;
        private EditText mGenderView = null;
        private EditText mScoreView = null;
        private EditText mIntroView = null;
        private MaterialBetterSpinner mSpinner;
        private String mSelectedGender;

        public ViewHolder(Activity activity) {
            this.mActivity = activity;
            mIDView = (EditText) mActivity.findViewById(R.id.textViewIDValue);
            mloginView = (ImageView) mActivity.findViewById(R.id.loginView);
            mNameView = (EditText) mActivity.findViewById(R.id.textViewNameValue);
            mEmailView = (EditText) mActivity.findViewById(R.id.textViewEmailValue);
            mGenderView = (EditText) mActivity.findViewById(R.id.textViewGenderValue);
            mScoreView = (EditText) mActivity.findViewById(R.id.textViewScoreLabelValue);
            mIntroView = (EditText) mActivity.findViewById(R.id.textviewIntroLabelValue);

            final String[] spinnerArr = getResources().getStringArray(R.array.gender_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, spinnerArr);
            adapter.setDropDownViewResource(R.layout.food_spinner_item);

            mSpinner = (MaterialBetterSpinner) findViewById(R.id.textViewGenderValue);
            mSpinner.setAdapter(adapter);
            mSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedGender = spinnerArr[position];
                }
            });

        }

        public void setviewAllFocus(boolean state) {
            mNameView.setFocusableInTouchMode(state);
            mNameView.setFocusable(state);
            mEmailView.setFocusableInTouchMode(state);
            mEmailView.setFocusable(state);
            mGenderView.setFocusableInTouchMode(state);
            mScoreView.setFocusableInTouchMode(state);
            mScoreView.setFocusable(state);
            mIntroView.setFocusableInTouchMode(state);
            mIntroView.setFocusable(state);
            mloginView.setFocusable(state);
        }

        public String getID(){
            return String.valueOf((mIDView.getText() == null) ? ("") : mIDView.getText());
        }

        public String getName() {
            return String.valueOf((mNameView.getText() == null) ? ("") : mNameView.getText());
        }

        public String getEmail() {
            return String.valueOf((mEmailView.getText() == null) ? ("") : mEmailView.getText());
        }

        public String getGender() {
            return mSelectedGender == null ? (String.valueOf((mGenderView.getText() == null) ? ("") : mGenderView.getText())) : mSelectedGender;
        }

        public String getScore() {
            return String.valueOf((mScoreView.getText() == null) ? ("") : mScoreView.getText());
        }

        public String getIntro() {
            return String.valueOf((mIntroView.getText() == null) ? ("") : mIntroView.getText());
        }

        public void setID(String value) {
            try {
                Objects.requireNonNull(value);
                mIDView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setName(String value) {
            try {
                Objects.requireNonNull(value);
                mNameView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setEmail(String value)
        {
            try {
                Objects.requireNonNull(value);
                mEmailView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setGender(String value)
        {
            try {
                Objects.requireNonNull(value);
                mGenderView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setmScore(String value) {
            try {
                Objects.requireNonNull(value);
                mScoreView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setIntro(String value) {
            try {
                Objects.requireNonNull(value);
                mIntroView.setText(value.equals("null") ? "" : value);
            } catch (Exception e) {

            }
        }

        public void setLoginView(String category){
            try {
                Objects.requireNonNull(category);
                if(category.equals(SocialProfile.FACEBOOK)) {
                    mloginView.setBackgroundResource(R.drawable.com_facebook_button_icon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class FeedItem {
        private String _mId;
        private String _mName;
        private String _mEmail;
        private String _mGender;
        private String _mScore;
        private String _mIntro;

        private FeedItem() {
        }

        public String get_mName() {
            return _mName;
        }

        public void set_mName(String _mName) {
            try {
                Objects.requireNonNull(_mName);
                this._mName = String_Nulltonull(_mName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String get_mEmail() {
            return _mEmail;
        }

        public void set_mEmail(String _mEmail) {
            try {
                Objects.requireNonNull(_mEmail);
                this._mEmail= String_Nulltonull(_mEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String get_mGender() {
            return _mGender;
        }

        public void set_mGender(String _mGender) {
            try {
                Objects.requireNonNull(_mGender);
                this._mGender= String_Nulltonull(_mGender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String get_mScore() {
            return _mScore;
        }

        public void set_mScore(String _mScore) {
            try {
                Objects.requireNonNull(_mScore);
                this._mScore= String_Nulltonull(_mScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void set_mIntro(String _mIntro) {
            try {
                Objects.requireNonNull(_mIntro);
                this._mIntro= String_Nulltonull(_mIntro);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String get_mIntro() {
            return _mIntro;
        }

        public String get_mId() {
            return _mId;
        }

        public void set_mId(String _mId) {
            try {
                Objects.requireNonNull(_mId);
                this._mId= String_Nulltonull(_mId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private String String_Nulltonull(String t){
            String tt = null;
            try {
                Objects.requireNonNull(t);
                if(t.equals("null")){
                    tt = null;

                }
            } catch (Exception e) {
            }
            return tt;
        }
    }
}
