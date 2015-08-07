package com.macmoim.pang;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by P11872 on 2015-08-06.
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String UPLOAD_PROFILE_IMAGE_FOLDER = "http://localhost:8080/web_test/image_test/upload_profile_image/";
    private static final String _POST_URL = "http://localhost:8080/web_test/putprofile.php";
    private static final String _GET_URL = "http://localhost:8080/web_test/getprofile.php";
    private static final int PROFILE_IMAGE_ASPECT_X = 4;
    private static final int PROFILE_IMAGE_ASPECT_Y = 3;
    private FeedItem mFeedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.propile);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int id = getIntent().getIntExtra("id", 0);

        mFeedItem = new FeedItem();
        loadBackdrop();
        setFloationAction();

    }

    private void setFloationAction() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData();
                onRequestData();
            }
        });
    }

    private void setData() {
        ViewHolder nViewHolder = new ViewHolder(this);
        mFeedItem.set_mName(nViewHolder.getName());
        mFeedItem.set_mEmail(nViewHolder.getEmail());
        mFeedItem.set_mGender(nViewHolder.getEmail());
        mFeedItem.set_mScore(nViewHolder.getScore());
    }

    private void onRequestData() {

        Map<String, String> obj = new HashMap<String, String>();
        obj.put("user_id", "");
        obj.put("user_name", mFeedItem.get_mName());
        obj.put("user_email", mFeedItem.get_mEmail());
        obj.put("user_score", mFeedItem.get_mScore());
        obj.put("user_gender", mFeedItem.get_mGender());
        obj.put("user_bookmark", "sdfasfd");

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                _POST_URL, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                Toast.makeText(getApplicationContext(), getText(R.string.failsave), Toast.LENGTH_SHORT).show();
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
        ImageView imageView = (ImageView) findViewById(R.id.profile_backdrop);
        imageView.setImageResource(R.drawable.person);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
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
        private EditText nEmailView = null;
        private EditText nGenderView = null;
        private EditText nScoreView = null;

        public ViewHolder(Activity activity) {
            this.mActivity = activity;
            nNameView = (EditText) mActivity.findViewById(R.id.textViewNameValue);
            nEmailView = (EditText) mActivity.findViewById(R.id.textViewEmailValue);
            nGenderView = (EditText) mActivity.findViewById(R.id.textViewGenderValue);
            nScoreView = (EditText) mActivity.findViewById(R.id.textViewScoreLabelValue);

        }

        public String getName() {
            return String.valueOf(nNameView.getText());
        }

        public String getEmail() {
            return String.valueOf(nEmailView.getText());
        }

        public String getGender() {
            return String.valueOf(nGenderView.getText());
        }

        public String getScore() {
            return String.valueOf(nScoreView.getText());
        }


    }

    private class FeedItem {
        private String _mName;
        private String _mEmail;
        private String _mGender;
        private String _mScore;

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
    }

}
