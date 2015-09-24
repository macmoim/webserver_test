package com.macmoim.pang.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by P11872 on 2015-08-17.
 */
public class NaviHeaderView {
    private final String TAG = getClass().getName();

    private static final String _GET_URL = Util.SERVER_ROOT + "/profile";
    private static final String VOLLEY_REQ_TAG_PROFILE = "get-profile";
    private static final String MAP_KEY_USER_ID = "user_id";

    private Activity mActivity = null;
    private ImageView ivUserPic = null;
    private TextView tvUserName = null;
    private TextView tvUserEmail = null;
    private TextView tvRanking = null;
    private TextView tvScore = null;

    private String UserId = null;

    public NaviHeaderView(Activity activity) {
        this.mActivity = activity;

        ivUserPic = (ImageView) mActivity.findViewById(R.id.user_picture);
        tvUserName = (TextView) mActivity.findViewById(R.id.user_name);
        tvUserEmail = (TextView) mActivity.findViewById(R.id.user_email);
        tvRanking = (TextView) mActivity.findViewById(R.id.ranking_text);
        tvScore = (TextView) mActivity.findViewById(R.id.score_text);

        UserId = LoginPreferences.GetInstance().getString(this.mActivity, LoginPreferences.PROFILE_ID);
    }

    public void OnDraw() {
        Map<String, String> _Map = new HashMap<String, String>();

        _Map.put(MAP_KEY_USER_ID, UserId);

        String _Url = _GET_URL + "/" + UserId;

        CustomRequest _JsonReq = new CustomRequest(Request.Method.GET, _Url, null, new Response.Listener<JSONObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Objects.requireNonNull(response, "response is null");
                    VolleyLog.d(TAG, "Response : " + response.toString());
                    JSONObject _Val = response;
                    DrawData(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error : " + error.getMessage());
                NetworkResponse _Response = error.networkResponse;
                if (_Response != null && _Response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + _Response.statusCode + ", data = " + new String(_Response.data));
                }
            }
        });
        AppController.getInstance().addToRequestQueue(_JsonReq, VOLLEY_REQ_TAG_PROFILE);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void DrawData(JSONObject response) {
        String _ImageURL = null;
        String _Name = null;
        String _Email = null;
        String _Ranking = null;
        String _Score = null;
        try {
            _ImageURL = response.getString("profile_img_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Glide.with(mActivity).load(new URL(_ImageURL)).centerCrop().into(ivUserPic);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            _Name = response.getString("user_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvUserName.setText(_Name);

        try {
            _Email = response.getString("user_email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvUserEmail.setText(_Email);

        try {
            _Ranking = response.getString("user_ranking");
            _Ranking = "null".equals(_Ranking) ? "-" : _Ranking;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvRanking.setText(_Ranking);

        try {
            _Score = response.getString("user_score");
            Objects.requireNonNull(_Score, "score is null");

            if ("null".equals(_Score)) {
                _Score = "-.-";
            } else {
                double temp = Double.parseDouble(_Score);
                _Score = String.valueOf(Math.round(temp * 10d) / 10d);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tvScore.setText(_Score);
        }
    }

    public void OnDestroy() {
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_PROFILE);
        mActivity = null;
        ivUserPic = null;
        tvUserName = null;
        tvUserEmail = null;
        tvRanking = null;
        tvScore = null;
        UserId = null;
    }
}
