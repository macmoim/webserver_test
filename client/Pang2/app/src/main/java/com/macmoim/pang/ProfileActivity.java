package com.macmoim.pang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.Layout.CircleFlatingMenu;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.macmoim.pang.util.Util;
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
    protected static final String TAG = "ProfileActivity";
    protected static final String UPLOAD_PROFILE_IMAGE_FOLDER = Util.SERVER_ROOT + "/image_test/upload_profile_image/";
    protected static final String _URL_PROFILE = Util.SERVER_ROOT + "/profile";
    protected static final String _URL_PROFILE_IMAGE = Util.SERVER_ROOT + "/profile/image";
    protected static final String _URL_PROFILE_IMAGE_UPDATE = Util.SERVER_ROOT + "/profile/image/update";
    protected static final int PROFILE_IMAGE_ASPECT_X = 4;
    protected static final int PROFILE_IMAGE_ASPECT_Y = 3;
    protected FeedItem mFeedItem;
    protected ViewHolder nViewHolder;
    protected String user_id = null;
    protected String user_name = null;
    protected Uri mCropImagedUri;
    static final int REQ_CODE_PICK_PICTURE = 1;
    protected String mImageURL;
    RelativeLayout BackdrropLayout;
    CircleFlatingMenu mCf;

    ImageView ivProfile = null;
    ImageView ivProfileCircle = null;

    protected int mProfileDbId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        setUserId();
        setBackDropInit();
        mFeedItem = new FeedItem();
        setAllFocus();
        OnGetData();
        setFloationAction();

    }

    protected void setUserId() {
        user_id = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);
        user_name = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_NAME);
    }

    protected void BackDropSetOnClickListener() {

    }

    protected void setBackDropInit() {
        BackdrropLayout = (RelativeLayout) findViewById(R.id.profile_backdrop);
        ivProfile = (ImageView) findViewById(R.id.profile_image);
        ivProfileCircle = (ImageView) findViewById(R.id.profile_circle_image);
        BackDropSetOnClickListener();
    }

    protected void setAllFocus() {
        nViewHolder = new ViewHolder(this);
        nViewHolder.setviewAllFocus(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void setFloationAction() {
        final int[] id = {R.drawable.ic_edit, R.drawable.com_facebook_button_icon};

        mCf = new CircleFlatingMenu(this);
        mCf.setListener(new CircleFlatingMenu.Listener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if ((int) v.getTag() == R.drawable.ic_edit) {
                        mCf.menuClose(false);
                        startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                        finish();
                    } else if ((int) v.getTag() == R.drawable.com_facebook_button_icon) {
                        mCf.menuClose(false);
                        startActivity(new Intent(ProfileActivity.this, LogInActivity.class));

                    }

                }
                return true;
            }
        });
        mCf.addResId(id);
        mCf.setFloationAction();
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

    protected void OnGetData() {

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
        loadBackdrop();
    }

    protected void showJSONResponseData(JSONObject response) {
        try {
            setData(response);
            ShowView(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadBackdrop() {
        try {
            Objects.requireNonNull(ivProfileCircle, " ivProfileCircle is null");
            Objects.requireNonNull(mImageURL, "mImageURL is null ");

            if ((mImageURL == null)) {
                ivProfileCircle.setImageResource(R.drawable.person);
            } else {
                ivProfile.setImageURI(Uri.parse(mImageURL));
                ivProfileCircle.setImageURI(Uri.parse(mImageURL));
            }

            Glide.with(this).load(new URL(mImageURL)).centerCrop().into(ivProfile);
            Glide.with(this).load(new URL(mImageURL)).centerCrop().into(ivProfileCircle);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected File createNewFile(String prefix) {
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
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected");
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mCf = null;
        super.onDestroy();

    }

    protected class ViewHolder {
        private Activity mActivity;
        private EditText mIDView = null;
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
            mIntroView.setFocusableInTouchMode(state);
            mIntroView.setFocusable(state);
        }

        public String getID() {
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
                mIDView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

            }
        }

        public void setName(String value) {
            try {
                Objects.requireNonNull(value);
                mNameView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

            }
        }

        public void setEmail(String value) {
            try {
                Objects.requireNonNull(value);
                mEmailView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

            }
        }

        public void setGender(String value) {
            try {
                Objects.requireNonNull(value);
                mGenderView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

            }
        }

        public void setmScore(String value) {
            try {
                Objects.requireNonNull(value);
                mScoreView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

            }
        }

        public void setIntro(String value) {
            try {
                Objects.requireNonNull(value);
                mIntroView.setText(value.equals("null") ? null : value);
            } catch (Exception e) {

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
                this._mEmail = String_Nulltonull(_mEmail);
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
                this._mGender = String_Nulltonull(_mGender);
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
                this._mScore = String_Nulltonull(_mScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void set_mIntro(String _mIntro) {
            try {
                Objects.requireNonNull(_mIntro);
                this._mIntro = String_Nulltonull(_mIntro);
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
                this._mId = String_Nulltonull(_mId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private String String_Nulltonull(String t) {
            String tt = null;
            try {
                Objects.requireNonNull(t);
                if (t.equals("null")) {
                    tt = null;

                }
            } catch (Exception e) {
            }
            return tt;
        }
    }
}
