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

/**
 * Created by P11872 on 2015-08-06.
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String UPLOAD_PROFILE_IMAGE_FOLDER = "http://localhost:8080/web_test/image_test/upload_profile_image/";
    private static final String _URL_PROFILE = "http://localhost:8080/web_test/profile.php";
    private static final int PROFILE_IMAGE_ASPECT_X = 4;
    private static final int PROFILE_IMAGE_ASPECT_Y = 3;
    private FeedItem mFeedItem;
    private ViewHolder nViewHolder;
    private int __ID = 0;
    private boolean editsate = false;
    private Uri mCropImagedUri;
    static final int REQ_CODE_PICK_PICTURE = 1;
    private String mImagefileName;
    private String mImageURL;
    ImageView backdropimageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.propile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        __ID = getIntent().getIntExtra("id", 0);

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
        OnGetData();
        setFloationAction();

    }

    private void setFloationAction() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editsate) {

                    if (mFeedItem.get_mName() == null) {
                        Toast.makeText(getApplicationContext(), "input name text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((mFeedItem.get_mEmail() == null)) {
                        Toast.makeText(getApplicationContext(), "input email text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mFeedItem.get_mIntro() == null) {
                        Toast.makeText(getApplicationContext(), "input intro text", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mImageURL == null) {
                        Toast.makeText(getApplicationContext(), "input image", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    onRequestData();
                    editsate = false;
                    nViewHolder.setviewAllFocus(false);
                    Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                } else {
                    editsate = true;
                    nViewHolder.setviewAllFocus(true);
                    Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData(JSONObject response) throws JSONException {
        mFeedItem.set_mName(response.getString("user_name"));
        mFeedItem.set_mEmail(response.getString("user_email"));
        mFeedItem.set_mGender(response.getString("user_score"));
        mFeedItem.set_mScore(response.getString("user_gender"));
        mFeedItem.set_mIntro(response.getString("user_intro"));
        mImageURL = UPLOAD_PROFILE_IMAGE_FOLDER + response.getString("profile_img_url");
        mImagefileName = response.getString("profile_img_url");
    }

    private void OnGetData() {

//        Map<String, String> obj = new HashMap<String, String>();
//        // temp
//
//        obj.put("user_id", "420158");

        String url = _URL_PROFILE + "/" + "420158" + "/";

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    JSONObject val = response;
                    showJSONResponseData(val);
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
        nViewHolder.setName(response.getString("user_name"));
        nViewHolder.setEmail(response.getString("user_email"));
        nViewHolder.setmScore(response.getString("user_score"));
        nViewHolder.setGender(response.getString("user_gender"));
        nViewHolder.setIntro(response.getString("user_intro"));
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

        String id = String.valueOf(((int) (Math.random() * 1000000) + 1));
        obj.put("user_id", id);
        obj.put("user_name", nViewHolder.getName());
        obj.put("user_email", nViewHolder.getEmail());
        obj.put("user_score", nViewHolder.getScore());
        obj.put("user_gender", nViewHolder.getGender());
        obj.put("user_intro", nViewHolder.getIntro());
        obj.put("profile_img_url", mImagefileName);

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


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void loadBackdrop() {

        if ((mImageURL == null)) {
            backdropimageView.setImageResource(R.drawable.person);
        } else {
            backdropimageView.setImageURI(Uri.parse(mImageURL));
        }
        try {
            Glide.with(this).load(new URL(mImageURL)).centerCrop().into(backdropimageView);
        } catch (MalformedURLException e) {
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
                requestThumbImage(mFile);
            }
        }
    }

    private void requestThumbImage(File thumbFile) {
        String url = "http://localhost:8080/web_test/putProfileImage.php";

        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", "profile_image.jpg");

        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", thumbFile);

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                url, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

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
                Log.d(TAG, "requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void parseJson(JSONObject response) {
        int width = 0;
        int height = 0;
        try {
            mImageURL = UPLOAD_PROFILE_IMAGE_FOLDER + response.getString("file_url");
            mImagefileName = response.getString("file_url");
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
        private EditText nNameView = null;
        private EditText mEmailView = null;
        private EditText mGenderView = null;
        private EditText mScoreView = null;
        private EditText mIntroView = null;
        private MaterialBetterSpinner mSpinner;
        private String mSelectedGender;

        public ViewHolder(Activity activity) {
            this.mActivity = activity;
            nNameView = (EditText) mActivity.findViewById(R.id.textViewNameValue);
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
            nNameView.setFocusableInTouchMode(state);
            nNameView.setFocusable(state);
            mEmailView.setFocusableInTouchMode(state);
            mEmailView.setFocusable(state);
            mGenderView.setFocusableInTouchMode(state);
            mScoreView.setFocusableInTouchMode(state);
            mScoreView.setFocusable(state);
            mIntroView.setFocusableInTouchMode(state);
            mIntroView.setFocusable(state);
        }

        public String getName() {
            return String.valueOf((nNameView.getText() == null) ? ("") : nNameView.getText());
        }

        public String getEmail() {
            return String.valueOf((mEmailView.getText() == null) ? ("") : nNameView.getText());
        }

        public String getGender() {
            return mSelectedGender;
        }

        public String getScore() {
            return String.valueOf((mScoreView.getText() == null) ? ("") : nNameView.getText());
        }

        public String getIntro() {
            return String.valueOf((mIntroView.getText() == null) ? ("") : nNameView.getText());
        }

        public void setName(String value) {
            nNameView.setText(value);
        }

        public void setEmail(String value) {
            mEmailView.setText(value);
        }

        public void setGender(String value) {
            mGenderView.setText(value);
        }

        public void setmScore(String value) {
            mScoreView.setText(value);
        }

        public void setIntro(String value) {
            mIntroView.setText(value);
        }


    }

    private class FeedItem {
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
            if (_mName == null) {
                this._mName = "";
            } else {
                this._mName = _mName;
            }
        }

        public String get_mEmail() {
            return _mEmail;
        }

        public void set_mEmail(String _mEmail) {
            if (_mEmail == null) {
                this._mEmail = "";
            } else {
                this._mEmail = _mEmail;
            }
        }

        public String get_mGender() {
            return _mGender;
        }

        public void set_mGender(String _mGender) {
            if (_mGender == null) {
                this._mGender = "";
            } else {
                this._mGender = _mGender;
            }
        }

        public String get_mScore() {
            return _mScore;
        }

        public void set_mScore(String _mScore) {
            if (_mScore == null) {
                this._mScore = "";
            } else {
                this._mScore = _mScore;
            }
        }

        public void set_mIntro(String _mIntro) {
            if (_mIntro == null) {
                this._mIntro = "";
            } else {
                this._mIntro = _mIntro;
            }

        }

        public String get_mIntro() {
            return _mIntro;
        }
    }

}
