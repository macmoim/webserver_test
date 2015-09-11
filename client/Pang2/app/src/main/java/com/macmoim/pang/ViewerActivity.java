package com.macmoim.pang;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.Layout.CircleFlatingMenu;
import com.macmoim.pang.Layout.CircleFlatingMenuWithActionView;
import com.macmoim.pang.adapter.FoodCommentRecyclerViewAdapter;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.FoodCommentItem;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.layoutmanager.MyLinearLayoutManager;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.richeditor.RichEditor;
import com.macmoim.pang.richeditor.RichViewer;
import com.macmoim.pang.util.Util;
import com.navercorp.volleyextensions.view.ZoomableNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by P14983 on 2015-07-24.
 */
public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = "ViewerActivity";

    private static final String URL_LIKE = Util.SERVER_ROOT + "/like";
    private static final String URL_STAR = Util.SERVER_ROOT + "/star";
    private static final String URL_POST = Util.SERVER_ROOT + "/post";
    private static final String URL_COMMENT = Util.SERVER_ROOT + "/comment";
    private static final String URL_SHARE = Util.SERVER_ROOT + "/post/share";

    private RichViewer mViewer;
    private Toolbar mToolbar;
    private Button mLikeBtn;
    private boolean isLikeCheck;
    private ArrayList<ImageView> mRankingStartArr;
    private Button mRankingBtn;
    CircleFlatingMenuWithActionView mShareCf;
    private int mStar;
    private int mLikeDbId = -1;
    private int mStarDbId = -1;
    private String mHtmlFileName;
    private String mThumbFileName;
    private String mTitle;
    private CircleImageView profilePic;
    private ZoomableNetworkImageView mZoomInImageView;
    private RelativeLayout mZoomInLayout;
    private ProgressDialog mDialog;

    private LinearLayout mRankingView;
    private Rect mRankingViewRect;

    private static final int REQ_ADD_COMMENT = 1;
    private String postUserId;
    private String postUserName;
    private String mUserId;

    private RecyclerView mCommentRv;
    private ArrayList<FoodCommentItem> foodCommentItems;

    private static final String VOLLEY_REQ_TAG_STAR = "get-star";
    private static final String VOLLEY_REQ_TAG_HTML = "get-html";
    private static final String VOLLEY_REQ_TAG_LIKE = "get-like";
    private static final String VOLLEY_REQ_TAG_COMMENT = "get-comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer_main);

        mUserId = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewer = (RichViewer) findViewById(R.id.richviewer);
        mViewer.setVerticalScrollBarEnabled(false);
        mViewer.getSettings().setJavaScriptEnabled(true);
        mViewer.getSettings().setDefaultTextEncodingName("UTF-8");
//        mViewer.setWebChromeClient(new WebChromeClient());
        mViewer.setFocusable(false);
        mViewer.setFocusableInTouchMode(false);
        mViewer.requestFocus();
        mViewer.addJavascriptInterface(new WebAppInterface(), "Android");
        mViewer.setOnInitialLoadListener(new RichEditor.AfterInitialLoadListener() {
            @Override
            public void onAfterInitialLoad(boolean isReady) {
                if (isReady) {
                    int id = getIntent().getIntExtra("id", 0);
                    showHTML(id);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_problem), Toast.LENGTH_LONG).show();
                }
                removeDialog();
            }
        });
        showDialog();

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


        mRankingView = (LinearLayout) findViewById(R.id.star_view);
        mRankingBtn = (Button) findViewById(R.id.ranking_btn);
        setupRankingStarView();


        setShareFloationAction((Button) findViewById(R.id.share_btn));

        mCommentRv = (RecyclerView) findViewById(R.id.recyclerview_comment);
        setupRecyclerView(mCommentRv);

        profilePic = (CircleImageView) findViewById(R.id.profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOtherProfileActivity();
            }
        });

        mZoomInLayout = (RelativeLayout) findViewById(R.id.zoomin_layout);
        mZoomInImageView = (ZoomableNetworkImageView) findViewById(R.id.zoomin_imageview);
        mZoomInImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideZoomImage();
            }
        });

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
                    onResponseHTML(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_HTML);
    }

    private void onResponseHTML(JSONObject response) {
        String htmlPath = "";
        String thumbImgPath = "";
        try {
            if ("success".equals(response.getString("ret_val"))) {

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.network_problem), Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "onResponseHTML");

            htmlPath = response.getString("filepath");
            htmlPath += response.getString("db_filename");
            thumbImgPath = response.getString("thumb_img_path");
            thumbImgPath = Util.splitFilename(thumbImgPath);
            mThumbFileName = thumbImgPath;
            mHtmlFileName = Util.splitFilename(htmlPath);

            mTitle = response.getString("title");
            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(mTitle);

            ((TextView) findViewById(R.id.like_text)).setText("  " + response.getString("like_sum"));
            String score = response.getString("rank");
            ((TextView) findViewById(R.id.score_text)).setText("  " + (score.equals("null") ? "0" : score));
            postUserName = response.getString("user_name");
            ((TextView) findViewById(R.id.user_name_text)).setText(postUserName);

            String profile_img_url = response.getString("profile_img_url");
            if (profile_img_url != null) {
                Glide.with(profilePic.getContext())
                        .load(profile_img_url)
                        .fitCenter()
                        .into(profilePic);
            } else {
                Glide.with(profilePic.getContext())
                        .load(R.drawable.person)
                        .fitCenter()
                        .into(profilePic);
            }

            postUserId = response.getString("user_id");

            Log.d(TAG, "showHTML path " + htmlPath);
//                        ((TextView)findViewById(R.id.post_category)).setText(response.getString("category"));
//                        ((TextView)findViewById(R.id.post_title)).setText(response.getString("title"));
//                        mViewer.loadUrl(htmlPath);
            new ReadHtmlTask().execute(htmlPath);


            loadBackdrop(Util.IMAGE_FOLDER_URL + thumbImgPath);
            getComment();
            getLike();
            getStar();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setLikeBtnBg(boolean isLike) {
        if (mLikeBtn == null) {
            return;
        }
        if (isLike) {
            mLikeBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_sel));
        } else {
            mLikeBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_nor_white));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setRankingBtnBg(boolean isStarSelected) {
        if (mRankingBtn == null) {
            return;
        }
        if (isStarSelected) {
            mRankingBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_sel));
        } else {
            mRankingBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_nor_white));
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
                            item.setCommentUserName(feedObj.getString("comment_user_name"));
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_COMMENT);
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_LIKE);
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_LIKE);
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_STAR);
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
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_STAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    private void setShareFloationAction(View actionView) {
        final int[] id = {R.drawable.facebook_icon, R.drawable.ic_dashboard, R.drawable.ic_pencil};

        mShareCf = new CircleFlatingMenuWithActionView(this, actionView);
        mShareCf.setListener(new CircleFlatingMenu.Listener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if ((int) v.getTag() == R.drawable.ic_facebook) {
                        new ShareFacebookTask().execute(Util.IMAGE_FOLDER_URL + mThumbFileName);
                        mShareCf.menuClose(false);
                    } else if ((int) v.getTag() == R.drawable.ic_dashboard) {
                        new ShareEtcTask().execute(Util.IMAGE_THUMBNAIL_FOLDER_URL + mThumbFileName);
                        mShareCf.menuClose(false);
                    } else if ((int) v.getTag() == R.drawable.ic_pencil) {
                        new ShareEtcTask().execute(Util.IMAGE_THUMBNAIL_FOLDER_URL + mThumbFileName);
                        mShareCf.menuClose(false);
                    }

                }
                return true;
            }
        });
        mShareCf.addResId(id);
        mShareCf.setItemAngle(-135, -45);
        mShareCf.setItemRadius(getResources().getDimensionPixelSize(R.dimen.radius_medium));
        mShareCf.setFloationAction();
    }

    private void setupRankingStarView() {
        if (mRankingStartArr == null) {
            mRankingStartArr = new ArrayList<>();
        }

        if (mRankingViewRect == null) {
            mRankingViewRect = new Rect();
            mRankingView.getHitRect(mRankingViewRect);
        }

        int[] starViewIds = {R.id.star01, R.id.star02, R.id.star03, R.id.star04, R.id.star05};
        for (int i = 0; i < starViewIds.length; i++) {
            mRankingStartArr.add((ImageView) findViewById(starViewIds[i]));
        }

        mRankingView.setOnTouchListener(new RankingStarTouchListener());

        mRankingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRankingView.getVisibility() != View.VISIBLE) {
                    showRankingViewAnim();
                } else {
                    invisibleRankingViewAnim();
                }
            }
        });
    }

    private void showRankingViewAnim() {
        mRankingView.setVisibility(View.VISIBLE);

        float startXPoint = mRankingStartArr.get(0).getLeft();
        float[] endXPoint = new float[4];
        ObjectAnimator[] animation = new ObjectAnimator[4];
        for (int i = 1; i < mRankingStartArr.size(); i++) {
            endXPoint[i - 1] = mRankingStartArr.get(i).getLeft();

            PropertyValuesHolder pvhTransX = PropertyValuesHolder.ofFloat(View.X, startXPoint, endXPoint[i - 1]);
            animation[i - 1] = ObjectAnimator.ofPropertyValuesHolder(mRankingStartArr.get(i), pvhTransX);
            animation[i - 1].setDuration(1000);
            animation[i - 1].start();
        }

    }

    private class RankingStarTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mRankingStartArr == null) {
                return false;
            }
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                int hitImageIndex = 0;
                int arrLength = mRankingStartArr.size();
                Rect r = new Rect();
                for (int i = 0; i < arrLength; i++) {
                    mRankingStartArr.get(i).getHitRect(r);
                    if (r.contains(touchX, touchY)) {
                        hitImageIndex = i;
                        break;
                    }
                }
                if (hitImageIndex == 0) {
                    if (touchX > mRankingStartArr.get(arrLength - 1).getRight()) {
                        hitImageIndex = arrLength - 1;
                    }
                }
                setRankStar(hitImageIndex);
            }

            return true;
        }
    }

    private void invisibleRankingViewAnim() {
        if (mRankingView != null) {
            mRankingView.setVisibility(View.INVISIBLE);
        }
        if (mStar > 0) {
            putStar(mStar);
        }
    }

    private void shareContent(Uri shareImageUri) {
        String url = URL_SHARE + "/" + mHtmlFileName.toLowerCase();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "] " + mTitle);
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareImageUri);
//        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share Food"));

    }

    private void shareContentFacebook(Uri contentUri) {
        String url = URL_SHARE + "/" + mHtmlFileName.toLowerCase();

        Auth auth = new FacebookAuth(this, null);
        auth.share(url, contentUri);

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
        setRankingBtnBg(true);
    }

    private class ShareEtcTask extends AsyncTask<String, Void, Uri> {
        @Override
        protected Uri doInBackground(String... params) {

            return getLocalBitmapUri(params[0]);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            shareContent(uri);
        }
    }

    private class ShareFacebookTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... params) {

            return getLocalBitmapFile(params[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Uri contentUri = Util.getImageContentUri(getApplicationContext(), file);
            shareContentFacebook(contentUri);

        }
    }

    public Uri getLocalBitmapUri(String imageUrl) {
        Uri bmpUri = Uri.fromFile(getLocalBitmapFile(imageUrl));
        return bmpUri;
    }

    public File getLocalBitmapFile(String imageUrl) {
        // Extract Bitmap from ImageView drawable
        Bitmap bmp;
        bmp = GetImageFromURL(imageUrl);
        // Store image to default external storage directory

        File file = null;
        try {
            File newDirectory = new File(Environment.getExternalStorageDirectory() + "/smtc/");
            if (!newDirectory.exists()) {
                if (newDirectory.mkdir()) {
                    Log.d(getApplicationContext().getClass().getName(), newDirectory.getAbsolutePath() + " directory created");
                }
            }
            file = new File(newDirectory, "share_image.jpg");
            if (file.exists()) {
                //this wont be executed
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "getLocalBitmapUri file making io exception");
            e.printStackTrace();
        }
        return file;
    }

    private Bitmap GetImageFromURL(String strImageURL) {
        Bitmap imgBitmap = null;

        try {
            URL url = new URL(strImageURL);
            Log.d(TAG, "GetImageFromURL " + strImageURL);
            URLConnection conn = url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);

            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgBitmap;
    }

    private void startOtherProfileActivity() {
        if (postUserId == null || postUserName == null) {
            return;
        }
        Intent intent = new Intent(ViewerActivity.this, OtherUserProfileActivity.class);
        intent.putExtra("other-user-id", postUserId);
        intent.putExtra("other-user-name", postUserName);
        startActivity(intent);
    }

    private void showZoomImage(String url) {
        if (mZoomInLayout != null && mZoomInImageView != null) {
            mZoomInLayout.setVisibility(View.VISIBLE);
            mZoomInImageView.setImageUrl(url, AppController.getInstance().getImageLoader());
        }
    }

    private boolean hideZoomImage() {
        if (mZoomInLayout != null && mZoomInLayout.getVisibility() == View.VISIBLE) {
            mZoomInLayout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void showDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            mDialog = new ProgressDialog(this);
        }

        mDialog.show();

    }

    private void removeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mRankingViewRect != null && !mRankingViewRect.contains((int) event.getX(), (int) event.getY())) {
            invisibleRankingViewAnim();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mRankingView != null && mRankingView.getVisibility() == View.VISIBLE) {
            invisibleRankingViewAnim();
            return;
        } else if (hideZoomImage()) {
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
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_STAR);
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_LIKE);
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_HTML);
        AppController.getInstance().cancelPendingRequests(VOLLEY_REQ_TAG_COMMENT);
        if (mShareCf != null) {
            mShareCf.setListener(null);
            mShareCf = null;
        }
        if (mLikeBtn != null) {
            mLikeBtn.setOnClickListener(null);
            mLikeBtn = null;
        }
        if (mRankingBtn != null) {
            mRankingBtn = null;
        }
        if (mRankingView != null) {
            mRankingView.setOnTouchListener(null);
            mRankingView = null;
        }
        mRankingViewRect = null;
        if (mRankingStartArr != null) {
            for (View v : mRankingStartArr) {
                v.setOnClickListener(null);
                v = null;
            }
            mRankingStartArr.clear();
            mRankingStartArr = null;
        }
        if (mCommentRv != null) {
            mCommentRv.removeAllViews();
            mCommentRv.setLayoutManager(null);
            mCommentRv.setAdapter(null);
            mCommentRv = null;
        }
        mZoomInImageView = null;
        mZoomInLayout = null;
        if (mViewer != null) {
            mViewer.setOnInitialLoadListener(null);
            mViewer.destroy();
            mViewer = null;
        }
        super.onDestroy();

    }

    private class WebAppInterface {


        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface() {
        }

        /**
         * Show a toast from the web page
         */

        @JavascriptInterface
        public void onImageItemClick(String value) {
            final String filename = (String) value.subSequence(value.lastIndexOf("/") + 1, value.length());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showZoomImage(Util.IMAGE_FOLDER_URL + filename);
                }
            });
        }
    }
}
