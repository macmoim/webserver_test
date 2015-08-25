package com.macmoim.pang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SocialProfile;

import static com.macmoim.pang.data.LoginPreferences.USER_AUTHENTICATED;

public class LogInActivity extends AppCompatActivity
        implements View.OnClickListener, Auth.OnAuthListener{
    private final String TAG = "LogInActivity";

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

        if((facebookAuth.isCurrentState())){
            facebookButton.setText("Log Out");
        }else{
            facebookButton.setText("Facebook");
        }
    }

    @Override
    public void onBackPressed() {
        if(isLogged()) {
            super.onBackPressed();
        }
    }

    private boolean isLogged(){
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
            if((facebookAuth.isCurrentState())){
                facebookAuth.revoke();
            }else{
                facebookAuth.login();
            }
        }else if(viewId == R.id.gplus_login_button){
            googleAuth.login();
        }else{
            //TODO : KAKAO
        }

    }

    @Override
    public void onLoginSuccess(SocialProfile profile) {
        Log.d(TAG,"onLoginSuccess" );
        Toast.makeText(this,"Log in 되었습니다.",Toast.LENGTH_SHORT).show();
        //save on shared preferences
        saveAuthenticatedUser(profile);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onLoginError(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onLoginCancel() {}

    @Override
    public void onRevoke() {
        Log.d(TAG,"Logout Success" );
        LoginPreferences.GetInstance().clear(this);
        facebookButton.setText("Facebook");
        Toast.makeText(this,"Log out 되었습니다.",Toast.LENGTH_SHORT).show();
    }

    private void saveAuthenticatedUser(SocialProfile profile){
        LoginPreferences.GetInstance().putProfile(this, profile);
    }

}