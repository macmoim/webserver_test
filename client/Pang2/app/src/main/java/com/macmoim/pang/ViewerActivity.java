package com.macmoim.pang;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.adapter.FoodCommentRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodCommentItem;
import com.macmoim.pang.layoutmanager.MyLinearLayoutManager;
import com.macmoim.pang.richeditor.RichViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-07-24.
 */
public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = "ViewerActivity";

    private static final String URL_LIKE = "http://localhost:8080/web_test/like";
    private static final String URL_STAR = "http://localhost:8080/web_test/star";
    private static final String URL_POST = "http://localhost:8080/web_test/post";
    private static final String URL_COMMENT = "http://localhost:8080/web_test/comment";

    private RichViewer mViewer;
    private Toolbar mToolbar;
    private Button mLikeBtn;
    private boolean isLikeCheck;
    private ViewGroup mRankingLayout;
    private Rect mRankingLayoutRect;
    private ArrayList<ImageView> mRankingStartArr;
    private int mStar;
    private int mLikeDbId = -1;
    private int mStarDbId = -1;

    private static final int REQ_ADD_COMMENT = 1;
    private String postUserId;
    private String mUserId;

    private RecyclerView mCommentRv;
    private ArrayList<FoodCommentItem> foodCommentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer_main);

        mUserId = getRandomID();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewer = (RichViewer) findViewById(R.id.richviewer);
        mViewer.setVerticalScrollBarEnabled(false);
        mViewer.getSettings().setJavaScriptEnabled(true);
        mViewer.getSettings().setDefaultTextEncodingName("UTF-8");
        mViewer.setWebChromeClient(new WebChromeClient());
        mViewer.setFocusable(false);
        mViewer.setFocusableInTouchMode(false);

        // get like from server
        isLikeCheck = false;

        mLikeBtn = (Button) findViewById(R.id.like_btn);
        setLikeBtnBg(isLikeCheck);
        mLikeBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(TAG, "like onclick");
                                            isLikeCheck = !isLikeCheck;
                                            putLike(isLikeCheck);
                                        }
                                    }
        );


        ((Button) findViewById(R.id.comment_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAddCommentActivity();
            }
        });

        mRankingLayout = (ViewGroup) findViewById(R.id.ranking_layout);
        setupRankingStarView();

        ((Button) findViewById(R.id.ranking_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRankingView();
            }
        });

        ((Button) findViewById(R.id.ranking_send_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRankingView();
            }
        });


        mCommentRv = (RecyclerView) findViewById(R.id.recyclerview_comment);
        setupRecyclerView(mCommentRv);


        int id = getIntent().getIntExtra("id", 0);
        showHTML(id);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ADD_COMMENT) {
            if (resultCode == Activity.RESULT_OK) {
                getComment();
            }
        }
    }

    private void startAddCommentActivity() {
        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("comment_user_id", mUserId);
        intent.putExtra("post_id", getIntent().getIntExtra("id", 0));
        intent.putExtra("post_user_id", postUserId);
        startActivityForResult(intent, REQ_ADD_COMMENT);
    }

    private void showHTML(int id) {

        String url = URL_POST + "/" + String.valueOf(id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    String htmlPath = "";
                    String thumbImgPath = "";
                    try {
                        htmlPath = response.getString("filepath");
                        htmlPath += response.getString("db_filename");
                        thumbImgPath = response.getString("thumb_img_path");

                        CollapsingToolbarLayout collapsingToolbar =
                                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                        collapsingToolbar.setTitle(response.getString("title"));

                        postUserId = response.getString("user_id");

                        Log.d(TAG, "showHTML path " + htmlPath);
//                        ((TextView)findViewById(R.id.post_category)).setText(response.getString("category"));
//                        ((TextView)findViewById(R.id.post_title)).setText(response.getString("title"));
//                        mViewer.loadUrl(htmlPath);
                        new ReadHtmlTask().execute(htmlPath);


                        loadBackdrop(thumbImgPath);
                        getComment();
                        getLike();
                        getStar();

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
            mViewer.setHtml(s);
        }
    }

    private void loadBackdrop(String url) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
//        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
        try {
            Glide.with(this).load(new URL(url)).centerCrop().into(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        foodCommentItems = new ArrayList<FoodCommentItem>();
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(recyclerView.getContext(), OrientationHelper.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FoodCommentRecyclerViewAdapter(ViewerActivity.this,
                foodCommentItems));
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void setLikeBtnBg(boolean isLike) {
        if (isLike) {
            mLikeBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_sel));
        } else {
            mLikeBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_nor));
        }
    }

    private void getComment() {
        final int post_id = getIntent().getIntExtra("id", 0);

        String url = URL_COMMENT + "/" + String.valueOf(post_id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null && mCommentRv != null) {
                    try {
                        JSONArray feedArray = response.getJSONArray("comment_info");

                        if (foodCommentItems != null) {
                            foodCommentItems.clear();
                        }
                        int length = feedArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);

                            FoodCommentItem item = new FoodCommentItem();
                            item.setPostId(post_id);
                            item.setPostUserId(postUserId);
                            item.setCommentUserId(feedObj.getString("comment_user_id"));
                            item.setComment(feedObj.getString("comment"));
                            item.setTimeStamp(feedObj.getString("upload_date"));
                            item.setProfileImgUrl(feedObj.getString("user_profile_img_url"));

                            Log.d(TAG, "getcomment comment " + feedObj.getString("comment"));

                            foodCommentItems.add(0, item);
                        }

                        // notify data changes to list adapater
                        mCommentRv.getAdapter().notifyDataSetChanged();
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


    private void getLike() {
        int post_id = getIntent().getIntExtra("id", 0);
        String like_user_id = mUserId;

        String url = URL_LIKE + "/" + like_user_id + "/" + String.valueOf(post_id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        String like = response.getString("like");
                        isLikeCheck = ("0".equals(like) ? false : true);
                        mLikeDbId = response.getInt("id");
                        Log.d(TAG, "like get " + like);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setLikeBtnBg(isLikeCheck);
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

    private void putLike(boolean like) {

        String url = URL_LIKE;
        int method = Request.Method.POST;

        Map<String, String> obj = new HashMap<String, String>();
        int post_id = getIntent().getIntExtra("id", 0);
        String like_user_id = mUserId;
        obj.put("user_id", like_user_id);
        obj.put("like", like ? "1" : "0");
        obj.put("post_id", String.valueOf(post_id));
        obj.put("post_user_id", postUserId);

        // check insert or update
        if (mLikeDbId == -1) {

        } else {
            method = Request.Method.PUT;
            obj.put("id", String.valueOf(mLikeDbId));
            url += "/" + mLikeDbId + "/" + (like ? "1" : "0");
        }

        Request jsonReq = new CustomRequest(method,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        if (response.has("id")) {
                            int like_id = response.getInt("id");
                            mLikeDbId = like_id;
                            Log.d(TAG, "like add db id " + like_id);
                        } else {
                            Log.d(TAG, "like update " + response.get("ret"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setLikeBtnBg(isLikeCheck);
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

    private void getStar() {

        int post_id = getIntent().getIntExtra("id", 0);
        String star_user_id = mUserId;

        String url = URL_STAR + "/" + star_user_id + "/" + String.valueOf(post_id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        mStarDbId = response.getInt("id");
                        int star = response.getInt("star");
                        setRankStar(star - 1);
                        Log.d(TAG, "star get " + star);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error on getStar: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void putStar(int star) {
        String url = URL_STAR;
        int method = Request.Method.POST;
        int post_id = getIntent().getIntExtra("id", 0);
        String like_user_id = mUserId;
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("user_id", like_user_id);
        obj.put("star", String.valueOf(star));
        obj.put("post_id", String.valueOf(post_id));
        obj.put("post_user_id", postUserId);

        if (mStarDbId != -1) {
            method = Request.Method.PUT;
            obj.put("id", String.valueOf(mStarDbId));
            url += "/" + mStarDbId + "/" + star;
        }

        CustomRequest jsonReq = new CustomRequest(method,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        if (response.has("id")) {
                            int star_id = response.getInt("id");
                            Log.d(TAG, "star add db id " + star_id);
                            mStarDbId = star_id;
                        } else {
                            Log.d(TAG, "star update " + response.get("ret"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    private void showRankingView() {
        if (mRankingLayout == null) {
            return;
        }
        if (mRankingLayout.getVisibility() == View.GONE) {
            mRankingLayout.setVisibility(View.VISIBLE);
            mRankingLayout.getHitRect(mRankingLayoutRect);
        }
    }

    private void closeRankingView() {
        if (mRankingLayout == null) {
            return;
        }
        if (mRankingLayout.getVisibility() == View.VISIBLE) {
            mRankingLayout.setVisibility(View.GONE);
            putStar(mStar);
        }
    }

    private void setupRankingStarView() {
        mRankingStartArr = new ArrayList<>();
        StarClickListener starListener = new StarClickListener();
        mRankingStartArr.add((ImageView) findViewById(R.id.star1));
        mRankingStartArr.add((ImageView) findViewById(R.id.star2));
        mRankingStartArr.add((ImageView) findViewById(R.id.star3));
        mRankingStartArr.add((ImageView) findViewById(R.id.star4));
        mRankingStartArr.add((ImageView) findViewById(R.id.star5));

        for (ImageView v : mRankingStartArr) {
            v.setOnClickListener(starListener);
        }

        mRankingLayoutRect = new Rect();
    }

    private class StarClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int clickedIndex = mRankingStartArr.indexOf(v);
            setRankStar(clickedIndex);

        }
    }

    private void setRankStar(int starIndexInArray) {
        mStar = starIndexInArray + 1;
        int length = mRankingStartArr.size();
        for (int i = 0; i < length; i++) {
            if (i <= starIndexInArray) {
                mRankingStartArr.get(i).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_sel));
            } else {
                mRankingStartArr.get(i).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_nor));
            }
        }
    }

    private String getRandomID() {
        return String.valueOf(((int) (Math.random() * 1000000) + 1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mRankingLayoutRect.contains((int) event.getX(), (int) event.getY())) {
            closeRankingView();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mRankingLayout != null && mRankingLayout.getVisibility() == View.VISIBLE) {
            closeRankingView();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mLikeBtn != null) {
            mLikeBtn.setOnClickListener(null);
            mLikeBtn = null;
        }
        if (mRankingStartArr != null) {
            for (ImageView v : mRankingStartArr) {
                v.setOnClickListener(null);
                v = null;
            }
            mRankingStartArr.clear();
            mRankingStartArr = null;
        }
        if (mRankingLayout != null) {
            mRankingLayout = null;
        }
        if (mCommentRv != null) {
            mCommentRv.removeAllViews();
            mCommentRv.setLayoutManager(null);
            mCommentRv.setAdapter(null);
            mCommentRv = null;
        }
        mViewer = null;
        mRankingLayoutRect = null;
        super.onDestroy();

    }
}
