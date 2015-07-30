package com.macmoim.pang;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.macmoim.pang.util.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by P10452 on 2015-07-29.
 */
public class LogInActivity extends AppCompatActivity {
    private static final String TAG = LogInActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        /* Load the view and display it */
        setContentView(R.layout.activity_login);
        _GetSHAKey();

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
                Profile _Profile = Profile.getCurrentProfile();
                Log.e(TAG, "Profile = " + _Profile);

                /* test */
//                Bundle _Parameters = new Bundle();
//                _Parameters.putString("fields", "id,name,last_name,link,email,picture");
//                GraphRequest _Request = GraphRequest.newMeRequest(loginResult.getAccessToken(), ((jsonObject, graphResponse) -> {
//                    String _Id = null;
//                    if (jsonObject != null) {
//                        try {
//                            _Id = jsonObject.getString("id") ;
//                            Log.e(TAG, "ID : " + _Id);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }));
//                _Request.setParameters(_Parameters);
//                _Request.executeAsync();
                /* End of Test */
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
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* *************************************
     *                FACEBOOK             *
     ************************************* */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            //mAuthProgressDialog.show();
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
//            if (this.mAuthData != null && this.mAuthData.getProvider().equals("facebook")) {
//                mFirebaseRef.unauth();
//                setAuthenticatedUser(null);
//            }
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
}