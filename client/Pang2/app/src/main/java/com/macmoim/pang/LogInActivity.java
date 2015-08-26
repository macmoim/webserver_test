package com.macmoim.pang;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SocialProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.macmoim.pang.data.LoginPreferences.USER_AUTHENTICATED;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class LogInActivity extends AppCompatActivity
        implements View.OnClickListener, Auth.OnAuthListener {
    private final String TAG = "LogInActivity";
    private static final String _URL_PROFILE = "http://localhost:8080/web_test/profile";

    Button facebookButton;
    Button googleButton;
    Button kakaoButton;

    GoogleAuth googleAuth;
    FacebookAuth facebookAuth;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        facebookButton = (Button) findViewById(R.id.facebook_login_button);
        googleButton = (Button) findViewById(R.id.gplus_login_button);
        kakaoButton = (Button) findViewById(R.id.kakao_login_button);

        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        kakaoButton.setOnClickListener(this);

        googleAuth = new GoogleAuth(this, this);
        facebookAuth = new FacebookAuth(this, this);

        if ((facebookAuth.isCurrentState())) {
            facebookButton.setText("Log Out");
        } else {
            facebookButton.setText("Facebook");
        }
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
        googleAuth.logout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuth.GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //call connect again because google just authorized app
                googleAuth.login();
            } else {
                onLoginCancel();
            }
        }

        facebookAuth.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.facebook_login_button) {
            if ((facebookAuth.isCurrentState())) {
                facebookAuth.revoke();
            } else {
                facebookAuth.login();
            }
        } else if (viewId == R.id.gplus_login_button) {
            googleAuth.login();
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
            facebookButton.setText("Facebook");
        }

    }

    private void onRequestData(SocialProfile profile) {

        Map<String, String> obj = new HashMap<>();
        obj.put("user_id", profile.getId());
        obj.put("user_name", profile.getName());
        obj.put("user_email", profile.getEmail());
        //obj.put("profile_img_url", mImagefileName);

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
                        facebookButton.setText("Log Out");
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

}