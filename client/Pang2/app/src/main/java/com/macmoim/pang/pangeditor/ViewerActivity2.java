package com.macmoim.pang.pangeditor;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.ListDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.KakaoAuth;
import com.macmoim.pang.richeditor.RichViewer;
import com.macmoim.pang.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-10-05.
 */
public class ViewerActivity2 extends AppCompatActivity implements PageMoveListener {
    private static final String TAG = "ViewerActivity2";

    private static final String URL_LIKE = Util.SERVER_ROOT + "/like";
    private static final String URL_STAR = Util.SERVER_ROOT + "/star";
    private static final String URL_POST = Util.SERVER_ROOT + "/post";
    private static final String URL_COMMENT = Util.SERVER_ROOT + "/comment";
    private static final String URL_SHARE = Util.SERVER_ROOT + "/post/share";

    private RichViewer mViewer;
    private LinearLayout mBottomCtlBar = null;
    private Button mLikeBtn;
    private boolean isLikeCheck;
    private ArrayList<ImageView> mRankingStartArr;
    private Button mRankingBtn;

    private int mStar;
    private int mLikeDbId = -1;
    private int mStarDbId = -1;
    private String mThumbFileName;
    private String mTitle;
    private ExtDialog mDialog;

    private LinearLayout mRankingView;
    private Rect mRankingViewRect;

    private String postUserId;
    private String postUserName;
    private String mUserId;

    private RecyclerView mCommentRv;

    private static final String VOLLEY_REQ_TAG_STAR = "get-star";
    private static final String VOLLEY_REQ_TAG_LIKE = "get-like";

    private ViewPager mPageViewPager;
    private static final String VOLLEY_REQ_TAG_POST = "get-post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer_main2);

        mUserId = LoginPreferences.GetInstance().getString(this, LoginPreferences.PROFILE_ID);

        // get like from server
        isLikeCheck = false;

        mBottomCtlBar = (LinearLayout) findViewById(R.id.bottom_control_bar);
        mBottomCtlBar.setVisibility(View.GONE);

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

        mPageViewPager = (ViewPager) findViewById(R.id.page_viewpager);

        RequestPageItems();
    }

    private void setupViewPager(ViewPager viewPager, ArrayList<PageItem> items) {
        PagePagerAdapter adapter = new PagePagerAdapter(getSupportFragmentManager(), this.getBaseContext(), items, this);
        viewPager.setAdapter(adapter);
    }

    private void setupSharedButton() {
        ((Button) findViewById(R.id.share_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShareFloationAction();
            }
        });
    }

    private void RequestPageItems() {
        showDialog();
        int id = getIntent().getIntExtra("id", 0);
        String url = Util.SERVER_ROOT + "/post/get_new/" + String.valueOf(id);

        CustomRequest jsonReq = new CustomRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    OnResponseRequestPage(response);
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
                removeDialog();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq, VOLLEY_REQ_TAG_POST);
    }

    private void OnResponseRequestPage(JSONObject response) {
        try {
            if ("success".equals(response.getString("ret_val"))) {
                ArrayList<String> contentList = Util.splitString(response.getString("page_content"), "\\|");
                ArrayList<String> imagePathList = Util.splitString(response.getString("img_path"), "\\|");
                ArrayList<PageItem> pageItems = new ArrayList<>();
                for (int i = 0; i < contentList.size(); i++) {
                    PageItem item = new PageItem(contentList.get(i), Uri.parse(imagePathList.get(i)));
                    pageItems.add(item);
                }

                String thumbImgPath = response.getString("thumb_img_path");
                thumbImgPath = Util.splitFilename(thumbImgPath);
                mThumbFileName = thumbImgPath;
                mTitle = response.getString("title");
                String score = response.getString("rank");
                postUserName = response.getString("user_name");
                String profile_img_url = response.getString("profile_img_url");
                postUserId = response.getString("user_id");
                String userEmail = response.getString("user_email");
                String commentSum = response.getString("comment_sum");
                String pageSum = response.getString("page_sum");
                String uploadDate = response.getString("upload_date");

                FrontPageItem frontPageItem = new FrontPageItem();
                frontPageItem.setImageUri(Uri.parse(thumbImgPath));
                frontPageItem.setTitle(mTitle);
                frontPageItem.setLike(response.getString("like_sum"));
                frontPageItem.setStar(score.equals("null") ? "0" : score);
                frontPageItem.setUserName(postUserName);
                frontPageItem.setProfileImgUrl(profile_img_url);
                frontPageItem.setUserId(postUserId);
                frontPageItem.setUserEmail(userEmail);
                frontPageItem.setCommentSum(commentSum);
                frontPageItem.setPageSum(pageSum);
                frontPageItem.setUploadDate(uploadDate);
                pageItems.add(0, frontPageItem);
                setupViewPager(mPageViewPager, pageItems);

                mPageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            mBottomCtlBar.setVisibility(View.GONE);
                        } else {
                            mBottomCtlBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                setMainBackground(mThumbFileName);

                getLike();
                getStar();
                setupSharedButton();
            }
            removeDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setMainBackground(String imageUrl) {
        ((NetworkImageView) findViewById(R.id.mainBg)).setImageUrl(Util.IMAGE_FOLDER_URL + imageUrl, AppController.getInstance().getImageLoader());
    }

    private void startAddCommentActivity() {
        Intent intent = new Intent(getApplicationContext(), CommentActivity2.class);
        intent.putExtra("comment_user_id", mUserId);
        intent.putExtra("post_id", getIntent().getIntExtra("id", 0));
        intent.putExtra("post_user_id", postUserId);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.viewer_menu, menu);
        return true;
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

    private void setShareFloationAction() {

        ListDialogAttr _Attr = new ListDialogAttr();
        _Attr.Title = getResources().getString(R.string.user_info);
        _Attr.ListItems = new CharSequence[]{"facebook","kakao story", "etc"};
        _Attr.ListCB = new ExtDialog.ListCallback() {
            @Override
            public void OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text) {
                if (which == 0) {
                    new ShareFacebookTask().execute(Util.IMAGE_FOLDER_URL + mThumbFileName);
                } else if (which == 1) {
                    new ShareKaKaokTask().execute(Util.IMAGE_FOLDER_URL + mThumbFileName);
                } else if (which == 2) {
                    new ShareEtcTask().execute(Util.IMAGE_FOLDER_URL + mThumbFileName);

                }
            }
        };

        ExtDialogSt.Get().AlertListDialog(this, _Attr);
    }

    private void shareContent(Uri shareImageUri) {
        int id = getIntent().getIntExtra("id", 0);

        String url = URL_SHARE + "/" + String.valueOf(id);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "] " + mTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareImageUri);
        startActivity(Intent.createChooser(shareIntent, "Share Food"));
    }

    private void shareContentFacebook(Uri contentUri) {
        int id = getIntent().getIntExtra("id", 0);
        String url = URL_SHARE + "/" + String.valueOf(id);

        Auth auth = new FacebookAuth(this, null);
        auth.share(url, contentUri);
    }

    private void shareContentKakaoStory(Uri contentUri) {
        int id = getIntent().getIntExtra("id", 0);
        String url = URL_SHARE + "/" + String.valueOf(id);

        Auth auth = new KakaoAuth(this, null);
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

    private class ShareKaKaokTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... params) {

            return getLocalBitmapFile(params[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Uri contentUri = Util.getImageContentUri(getApplicationContext(), file);
            shareContentKakaoStory(contentUri);

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
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
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

    private void showDialog() {
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

    private void removeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    public void OnChangePage(int index) {
        mPageViewPager.setCurrentItem(index);
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

        if (mBottomCtlBar != null) {
            mBottomCtlBar = null;
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
        if (mViewer != null) {
            mViewer.setOnInitialLoadListener(null);
            mViewer.destroy();
            mViewer = null;
        }
        super.onDestroy();
    }
}
