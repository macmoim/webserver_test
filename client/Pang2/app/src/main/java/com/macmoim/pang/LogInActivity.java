package com.macmoim.pang;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.adapter.PagerViewAdapter;
import com.macmoim.pang.adapter.PagerViewTransform;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SocialProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.macmoim.pang.data.LoginPreferences.USER_AUTHENTICATED;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class LogInActivity extends AppCompatActivity
        implements View.OnClickListener, Auth.OnAuthListener {
    private final String TAG = "LogInActivity";
    private static final String _URL_PROFILE = "http://localhost:8080/web_test/profile";

    LinearLayout lFaceBook;
    TextView tvFaceBook;
    LinearLayout lGoogle;
    LinearLayout lKakao;

    GoogleAuth GoogleAuth;
    FacebookAuth FacebookAuth;
    Context mContext;

    private ViewPager LogInPagerView;
    private PagerViewAdapter LogInPagerViewAdapter;
    private LinearLayout PagerViewIndicatorLayout;
    private int LogInPagerViewLayoutId;

    private final List<Integer> LogInBg = Arrays.asList(
            R.drawable.bg_login_1,
            R.drawable.bg_login_2,
            R.drawable.bg_login_3,
            R.drawable.bg_login_4,
            R.drawable.bg_login_5
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();

        lFaceBook = (LinearLayout) findViewById(R.id.facebook_area);
        tvFaceBook = (TextView) findViewById(R.id.facebook_tv);
        lGoogle = (LinearLayout) findViewById(R.id.google_plus_area);
        lKakao = (LinearLayout) findViewById(R.id.kakao_area);

        lFaceBook.setOnClickListener(this);
        lGoogle.setOnClickListener(this);
        lKakao.setOnClickListener(this);

        GoogleAuth = new GoogleAuth(this, this);
        FacebookAuth = new FacebookAuth(this, this);

        if ((FacebookAuth.isCurrentState())) {
            tvFaceBook.setText("Log Out");
        } else {
            tvFaceBook.setText(getResources().getString(R.string.login_with_facebook));
        }

        InitLogInPagerViewSetAdapter();
    }

    @Override
    public void onBackPressed() {
        if (isLogged()) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    private boolean isLogged() {
        return LoginPreferences.GetInstance().getBoolean(this, USER_AUTHENTICATED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //disconnect google client api
        GoogleAuth.logout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuth.GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //call connect again because google just authorized app
                GoogleAuth.login();
            } else {
                onLoginCancel();
            }
        }

        FacebookAuth.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.facebook_area) {
            if ((FacebookAuth.isCurrentState())) {
                FacebookAuth.revoke();
            } else {
                FacebookAuth.login();
            }
        } else if (viewId == R.id.google_plus_area) {
            GoogleAuth.login();
        } else {
            //TODO : KAKAO
        }

    }

    @Override
    public void onLoginSuccess(SocialProfile profile) {
        Log.d(TAG, "onLoginSuccess");
        //save on shared preferences
        saveAuthenticatedUser(profile);
        onRequestData(profile);
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onLoginError(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onLoginCancel() {
    }

    @Override
    public void onRevoke() {
        Log.d(TAG, "Logout Success");
        SocialLogout(SocialProfile.FACEBOOK);
        Toast.makeText(this, "Log out 되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void SocialLogout(String tag) {
        if (tag.equals(SocialProfile.FACEBOOK)) {
            Log.d(TAG, "profile clear");
            LoginPreferences.GetInstance().clear(this);
            tvFaceBook.setText(getResources().getString(R.string.login_with_facebook));
        }
    }

    private void onRequestData(SocialProfile profile) {
        Map<String, String> obj = new HashMap<>();
        obj.put("user_id", profile.getId());
        obj.put("user_name", profile.getName());
        obj.put("user_email", profile.getEmail());
        obj.put("profile_img_url", profile.getImage());

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                _URL_PROFILE, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Objects.requireNonNull(response);
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    String ret = "";
                    try {
                        ret = response.getString("ret_val");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(ret)) {
                        tvFaceBook.setText("Log Out");
                        Toast.makeText(getApplicationContext(), "Log in 되었습니다.", Toast.LENGTH_SHORT).show();
                        gotoMain();
                    } else if ("duplicate".equals(ret)) {
                        gotoMain();
                    } else {
                        if (LoginPreferences.GetInstance().getString(getApplicationContext(), LoginPreferences.USER_SOCIAL) == SocialProfile.FACEBOOK) {
                            SocialLogout(SocialProfile.FACEBOOK);
                        }
                    }
                } catch (Exception e) {
                    SocialLogout(SocialProfile.FACEBOOK);
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
                SocialLogout(SocialProfile.FACEBOOK);
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void saveAuthenticatedUser(SocialProfile profile) {
        LoginPreferences.GetInstance().putProfile(this, profile);
    }

    private void InitLogInPagerViewSetAdapter() {
        LogInPagerViewLayoutId = R.layout.login_pager_view_element;

        LogInPagerView = (ViewPager) findViewById(R.id.login_view_pager);

        LogInPagerView.setPageTransformer(false, new PagerViewTransform());
        LogInPagerViewAdapter = new PagerViewAdapter(this, LogInBg, LogInPagerViewLayoutId);
        LogInPagerView.setAdapter(LogInPagerViewAdapter);
        LogInPagerView.setOffscreenPageLimit(LogInBg.size());

        LogInPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SetPagerViewIndicatorLayout(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        BuildPagerViewIndicatorLayout();
    }

    private void BuildPagerViewIndicatorLayout() {
        PagerViewIndicatorLayout = LinearLayout.class.cast(findViewById(R.id.view_pager_indicator));

        int _Padding = (int) getResources().getDimension(R.dimen.view_pager_indicator_normal_margin);

        for (int i = 0; i < LogInPagerViewAdapter.getCount(); i++) {
            ImageView _Indicator = new ImageView(this);

            _Indicator.setImageResource(R.drawable.circle_mustard_normal);
            _Indicator.setLayoutParams(new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.view_pager_indicator_area_size),
                    (int) getResources().getDimension(R.dimen.view_pager_indicator_area_size)));
            _Indicator.setAdjustViewBounds(true);
            _Indicator.setScaleType(ImageView.ScaleType.FIT_CENTER);
            _Indicator.setPadding(_Padding, _Padding, _Padding, _Padding);

            PagerViewIndicatorLayout.addView(_Indicator);
        }

        SetPagerViewIndicatorLayout(0);
    }

    private void SetPagerViewIndicatorLayout(int index) {
        int _Padding = (int) getResources().getDimension(R.dimen.view_pager_indicator_normal_margin);

        if (index < LogInPagerViewAdapter.getCount()) {
            for (int i = 0; i < LogInPagerViewAdapter.getCount(); i++) {
                ImageView _Indicator = (ImageView) PagerViewIndicatorLayout.getChildAt(i);
                _Padding = (i == index) ? (int) getResources().getDimension(R.dimen.view_pager_indicator_select_margin)
                        : (int) getResources().getDimension(R.dimen.view_pager_indicator_normal_margin);

                if (i == index) {
                    _Indicator.setImageResource(R.drawable.circle_mustard_selected);
                } else {
                    _Indicator.setImageResource(R.drawable.circle_mustard_normal);
                }

                _Indicator.setPadding(_Padding, _Padding, _Padding, _Padding);
            }
        }
    }
}