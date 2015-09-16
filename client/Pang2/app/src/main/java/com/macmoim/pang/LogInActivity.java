package com.macmoim.pang;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.macmoim.pang.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.macmoim.pang.data.LoginPreferences.USER_AUTHENTICATED;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class LogInActivity extends AppCompatActivity implements Auth.OnAuthListener {
    private final String TAG = getClass().getName();

    private static final String _URL_PROFILE = Util.SERVER_ROOT + "/profile";

    @BindDimen(R.dimen.view_pager_indicator_normal_margin)
    int iViewPagerIndicatorNormalMargin;
    @BindDimen(R.dimen.view_pager_indicator_select_margin)
    int iViewPagerIndicatorSelectMargin;

    @BindDrawable(R.drawable.circle_mustard_normal)
    Drawable CircleMustardNormal;
    @BindDrawable(R.drawable.circle_mustard_selected)
    Drawable CircleMustardSelect;

    @Bind(R.id.facebook_tv)
    TextView tvFaceBook;

    @OnClick({R.id.facebook_area, R.id.google_plus_area, R.id.kakao_area})
    public void OnClickLogIn(View view) {
        int _Id = view.getId();

        if (_Id == R.id.facebook_area) {
            if ((mFacebookAuth.isCurrentState())) {
                mFacebookAuth.revoke();
            } else {
                mFacebookAuth.login();
            }
        } else if (_Id == R.id.google_plus_area) {
            mGoogleAuth.login();
        } else if (_Id == R.id.kakao_area) {
            // TODO : KAKAO
        } else {
            // none
        }
    }

    GoogleAuth mGoogleAuth;
    FacebookAuth mFacebookAuth;

    Context mContext;

    private ViewPager LogInPagerView;
    private PagerViewAdapter LogInPagerViewAdapter;
    private LinearLayout PagerViewIndicatorLayout;
    private int LogInPagerViewLayoutId;

    private final List<Integer> LogInBg = Arrays.asList(
            R.drawable.bg_login_6,
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

        ButterKnife.bind(this);

        mGoogleAuth = new GoogleAuth(this, this);
        mFacebookAuth = new FacebookAuth(this, this);

        if ((mFacebookAuth.isCurrentState())) {
            tvFaceBook.setText(getResources().getString(R.string.logout));
        } else {
            tvFaceBook.setText(getResources().getString(R.string.login_with_facebook));
        }

        InitLogInPagerViewSetAdapter();
    }

    @Override
    public void onBackPressed() {
        if (IsLogged()) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    private boolean IsLogged() {
        return LoginPreferences.GetInstance().getBoolean(this, USER_AUTHENTICATED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //disconnect google client api
        mGoogleAuth.logout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuth.GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //call connect again because google just authorized app
                mGoogleAuth.login();
            } else {
                onLoginCancel();
            }
        }

        mFacebookAuth.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
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
        Toast.makeText(this, getResources().getString(R.string.logout_done), Toast.LENGTH_SHORT).show();
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
                        tvFaceBook.setText(getResources().getString(R.string.logout));
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

        int _Padding = iViewPagerIndicatorNormalMargin;

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
        int _Padding = iViewPagerIndicatorNormalMargin;

        if (index < LogInPagerViewAdapter.getCount()) {
            for (int i = 0; i < LogInPagerViewAdapter.getCount(); i++) {
                ImageView _Indicator = (ImageView) PagerViewIndicatorLayout.getChildAt(i);

                _Padding = (i == index) ? iViewPagerIndicatorSelectMargin : iViewPagerIndicatorNormalMargin;

                if (i == index) {
                    _Indicator.setImageDrawable(CircleMustardSelect);
                } else {
                    _Indicator.setImageDrawable(CircleMustardNormal);
                }

                _Indicator.setPadding(_Padding, _Padding, _Padding, _Padding);
            }
        }
    }
}