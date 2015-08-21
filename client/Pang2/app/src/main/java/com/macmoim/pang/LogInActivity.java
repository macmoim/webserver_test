package com.macmoim.pang;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.data.CommonSharedPreperences;
import com.macmoim.pang.util.GooglePlustLoginUtils;
import com.macmoim.pang.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P10452 on 2015-07-29.
 */
public class LogInActivity extends AppCompatActivity {
    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final String _URL_PROFILE = "http://localhost:8080/web_test/profile";
    /* *************************************
     *                FACEBOOK             *
     ************************************* */
    /* The login button for Facebook */
    private LoginButton mFacebookLoginButton;
    /* The callback manager for Facebook */
    private CallbackManager mFacebookCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private AccessToken _AccessTocken;
    private ProfileTracker mFaceBookProfileTracker;
    private String mLoginCategory = null;
    private boolean fackbookRequestState = false;
    private GooglePlustLoginUtils gLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        /* Load the view and display it */
        setContentView(R.layout.activity_login);
        _GetSHAKey();
        initFaceBook();
        initGooglePlus();

    }

    private void initGooglePlus() {
        gLogin = new GooglePlustLoginUtils(this, R.id.gplus_login_button);
        gLogin.setLoginStatus(new GooglePlustLoginUtils.GPlusLoginStatus() {
            @Override
            public void OnSuccessGPlusLogin(Bundle profile) {
                Log.i(TAG, profile.getString(GooglePlustLoginUtils.NAME));
                Log.i(TAG, profile.getString(GooglePlustLoginUtils.EMAIL));
                Log.i(TAG, profile.getString(GooglePlustLoginUtils.PHOTO));
                Log.i(TAG, profile.getString(GooglePlustLoginUtils.PROFILE));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gLogin.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gLogin.disconnect();
    }

    public void initFaceBook() {
        Profile _Profile = Profile.getCurrentProfile();
        Log.e(TAG, "Profile = " + _Profile);
        if (_Profile != null) {
            return;
        }

        /* *************************************
         *                FACEBOOK             *
         ************************************* */
        /* Load the Facebook login button and set up the tracker to monitor access token changes */
        mFacebookCallbackManager = CallbackManager.Factory.create();

        /* log in button */
        mFacebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        mFacebookLoginButton.setReadPermissions("user_friends");
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                _AccessTocken = loginResult.getAccessToken();

                GraphRequest _Request = GraphRequest.newMeRequest(_AccessTocken, (new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            fackbookRequestState = true;
                            mLoginCategory = CommonSharedPreperences.CATEGORYY_FACEBOOK;
                            onRequestData(jsonObject);
                        } else {
                            fackbookRequestState = false;
                        }
                    }
                }));

                Bundle _Parameters = new Bundle();
                _Parameters.putString("fields", "id,name,last_name,link,email,picture");
                _Request.setParameters(_Parameters);
                _Request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        /* track access tokens */
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                LogInActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };
        // If the access token is available already assign it.
        _AccessTocken = AccessToken.getCurrentAccessToken();

        /* profile tracker */
        mFaceBookProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {

            }
        };

        mFacebookAccessTokenTracker.startTracking();
        mFaceBookProfileTracker.startTracking();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* *************************************
         *                FACEBOOK             *
         ************************************* */
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
            mFacebookAccessTokenTracker = null;
        }

        // if user logged in with Facebook, stop tracking profile
        if (mFaceBookProfileTracker != null) {
            mFaceBookProfileTracker.stopTracking();
            mFaceBookProfileTracker = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
        if(mFacebookCallbackManager != null) {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if(gLogin != null){
            gLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* *************************************
     *                FACEBOOK             *
     ************************************* */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            //mAuthProgressDialog.show();
        } else {
            // Logged out of Facebook and currently authenticated
            Log.d("TTT", "logout");
            //setDataDelete();
            //setPreperencesDelete();
        }
    }


    private void setDataDelete() {
        String url = _URL_PROFILE + "/" + CommonSharedPreperences.GetInstance(this).getString(CommonSharedPreperences.KEY_ID);

        CustomRequest jsonReq = new CustomRequest(Request.Method.DELETE,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    String ret = "";
                    try {
                        ret = response.getString("ret_val");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(ret)) {
                        SavePreperences(response);
                        Toast.makeText(getApplicationContext(), getText(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getText(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                    }

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


    private void setPreperencesDelete() {
        CommonSharedPreperences.GetInstance(this).setDelete(CommonSharedPreperences.CATEGORYY_FACEBOOK, CommonSharedPreperences.KEY_ID);
    }

    @Override
    public void onBackPressed() {
        if (AccessToken.getCurrentAccessToken() != null) {
            super.onBackPressed();
        }
    }

    private void _GetSHAKey() {
        try {
            PackageInfo _Info = getPackageManager().getPackageInfo("com.macmoim.pang",
                    PackageManager.GET_SIGNATURES);

            for (Signature Signature : _Info.signatures) {
                MessageDigest _Md = MessageDigest.getInstance("SHA");

                _Md.update(Signature.toByteArray());

                String _Log = Util.MakeStringBuilder("KeyHash : ", Base64.encodeToString(_Md.digest(),
                        Base64.DEFAULT));

                Log.e(TAG, _Log);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void onRequestData(JSONObject jsonObject) {
        Map<String, String> obj = new HashMap<String, String>();

        try {
            obj.put("user_id", jsonObject.getString("id"));

            if (jsonObject.getString("name") != null) {
                obj.put("user_name", jsonObject.getString("name"));
            } else {
                obj.put("user_name", jsonObject.getString("id"));
            }
            //obj.put("profile_img_url", jsonObject.getString("picture"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                _URL_PROFILE, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    String ret = "";
                    try {
                        ret = response.getString("ret_val");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(ret)) {
                        SavePreperences(response);
                        Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getText(R.string.failsave), Toast.LENGTH_SHORT).show();
                    }

                }
                Log.d("TTT", "AAA");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
                if (fackbookRequestState) {
                    finish();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void SavePreperences(JSONObject response) {
        CommonSharedPreperences.GetInstance(this).putString(CommonSharedPreperences.KEY_ID, response.optString("user_id"));
        CommonSharedPreperences.GetInstance(this).putString(CommonSharedPreperences.KEY_NAME, response.optString("user_name"));
        CommonSharedPreperences.GetInstance(this).putString(CommonSharedPreperences.KEY_CATEGORY, mLoginCategory);
        finish();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }
}