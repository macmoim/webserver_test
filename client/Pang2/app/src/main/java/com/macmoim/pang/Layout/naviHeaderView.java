package com.macmoim.pang.Layout;

import android.app.Activity;
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
public class naviHeaderView{
    private final String TAG = "naviHeaderView";
    private Activity mActivity = null;
    private ImageView mCircleImage = null;
    private TextView mNameView = null;
    private TextView mRankingView = null;
    private TextView mScoreView = null;
    private String user_id;
    private static final String UPLOAD_PROFILE_IMAGE_FOLDER = "http://localhost:8080/web_test/image_test/upload_profile_image/";
    private static final String _GET_URL = "http://localhost:8080/web_test/profile";


    public naviHeaderView(Activity activity) {
        this.mActivity = activity;
        mCircleImage = (ImageView) mActivity.findViewById(R.id.cpimage);
        mNameView = (TextView) mActivity.findViewById(R.id.header_name);
        mRankingView = (TextView) mActivity.findViewById(R.id.ranking_text);
        mScoreView = (TextView) mActivity.findViewById(R.id.score_text);
    }

    public void onDraw(){
        user_id = LoginPreferences.GetInstance().getString(this.mActivity, LoginPreferences.PROFILE_ID);
        drawCPImage(user_id);
    }

    private void drawCPImage(String user_id) {

        Map<String, String> obj = new HashMap<String, String>();
        // temp

        obj.put("user_id", user_id);

        String url = _GET_URL + "/" + user_id;

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    JSONObject val = response;
                    drawdata(response);
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

    private void drawdata(JSONObject response){
        String imageURL = null;
        String name = null;
        String ranking = null;
        String score = null;
        try {
            imageURL = response.getString("profile_img_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Glide.with(mActivity).load(new URL(imageURL)).centerCrop().into(mCircleImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            name = response.getString("user_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mNameView.setText(name);

        try {
            ranking = response.getString("user_ranking");
            ranking = "null".equals(ranking) ? "" : ranking;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRankingView.setText(ranking);

        try {
            score = response.getString("user_score");
            if ("null".equals(score)) {
                score = "";
            } else {
                double temp = Double.parseDouble(score);
                score = String.valueOf(Math.round(temp*10d) / 10d);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mScoreView.setText(score);
    }

    public void onDestroy(){
        mActivity = null;
        mCircleImage = null;
        mNameView = null;
        mRankingView = null;
        mScoreView = null;
        user_id = null;
    }

}
