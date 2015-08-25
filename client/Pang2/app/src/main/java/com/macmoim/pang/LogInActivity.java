package com.macmoim.pang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.macmoim.pang.MainActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SocialProfile;

public class LogInActivity extends AppCompatActivity
        implements View.OnClickListener, Auth.OnAuthListener{

    public static final String USER_AUTHENTICATED = "user_authenticated"; //value is a Boolean
    public static final String USER_SOCIAL = "user_social"; //value is a String and means user is logged with Social.FACEBOOK or Social.GOOGLE
    public static final String PROFILE_NAME = "profile_name";  //value is a String
    public static final String PROFILE_EMAIL = "profile_email";
    public static final String PROFILE_IMAGE = "profile_image";  //value is a String
    public static final String PROFILE_COVER = "profile_cover"; //value is a String
    

    Button facebookButton;
    Button googleButton;
    Button kakaoButton;

    GoogleAuth googleAuth;
    FacebookAuth facebookAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookButton = (Button) findViewById(R.id.facebook_login_button);
        googleButton = (Button) findViewById(R.id.gplus_login_button);
        kakaoButton = (Button) findViewById(R.id.kakao_login_button);

        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        kakaoButton.setOnClickListener(this);

        googleAuth = new GoogleAuth(this, this);
        facebookAuth = new FacebookAuth(this, this);
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

        if(requestCode == GoogleAuth.GOOGLE_SIGN_IN){
            if(resultCode == RESULT_OK) {
                //call connect again because google just authorized app
                googleAuth.login();
            }else{
                onLoginCancel();
            }
        }

        facebookAuth.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.facebook_login_button){
            facebookAuth.login();
        }else if(viewId == R.id.gplus_login_button){
            googleAuth.login();
        }else{
            //TODO : KAKAO
        }

    }

    @Override
    public void onLoginSuccess(SocialProfile profile) {
        //save on shared preferences
        saveAuthenticatedUser(profile);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onLoginError(String message) {
        Log.e("teste", message);
    }

    @Override
    public void onLoginCancel() {}

    @Override
    public void onRevoke() {}

    private void saveAuthenticatedUser(SocialProfile profile){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(USER_AUTHENTICATED, true);
        editor.putString(USER_SOCIAL,   profile.getNetwork());
        editor.putString(PROFILE_NAME,  profile.getName());
        editor.putString(PROFILE_EMAIL, profile.getEmail());
        editor.putString(PROFILE_IMAGE, profile.getImage());
        editor.putString(PROFILE_COVER, profile.getCover());
        editor.apply();
    }

}