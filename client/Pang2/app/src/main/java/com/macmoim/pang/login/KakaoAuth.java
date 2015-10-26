package com.macmoim.pang.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ErrorResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.StoryProtocol;
import com.kakao.util.helper.TalkProtocol;
import com.macmoim.pang.KakaoPostActivity;
import com.macmoim.pang.LogInActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.ListDialogAttr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by P16018 on 2015-09-30.
 */
public class KakaoAuth extends Auth{
    private SessionCallback callback;
    Activity hostActivity;
    private final String TAG = getClass().getName();
    private Uri mImageOrVideo;
    private boolean mbDirectGoToPostActivity;  //for directpost kakastory after login;

    public KakaoAuth(Activity activity, OnAuthListener authListener) {
        hostActivity = activity;
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        setOnAuthListener(authListener);
        AppController.setCurrentActivity(activity);

        Log.d(TAG, "compent = " + AppController.getCurrentActivity().getComponentName().getClassName() + "Activity = " + LogInActivity.class.getName());
    }

    private static class Item {
        public final int textId;
        public final int icon;
        public final AuthType authType;
        public Item(final int textId, final Integer icon, final AuthType authType) {
            this.textId = textId;
            this.icon = icon;
            this.authType = authType;
        }
    }

    private List<AuthType> getAuthTypes() {
        final List<AuthType> availableAuthTypes = new ArrayList<AuthType>();
        if(TalkProtocol.existCapriLoginActivityInTalk(hostActivity, Session.getCurrentSession().isProjectLogin())){
            availableAuthTypes.add(AuthType.KAKAO_TALK);
            availableAuthTypes.add(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN);
        }
        if(StoryProtocol.existCapriLoginActivityInStory(hostActivity, Session.getCurrentSession().isProjectLogin())){
            availableAuthTypes.add(AuthType.KAKAO_STORY);
        }
        availableAuthTypes.add(AuthType.KAKAO_ACCOUNT);

        final AuthType[] selectedAuthTypes = Session.getCurrentSession().getAuthTypes();
        availableAuthTypes.retainAll(Arrays.asList(selectedAuthTypes));

        // 개발자가 설정한 것과 available 한 타입이 없다면 직접계정 입력이 뜨도록 한다.
        if(availableAuthTypes.size() == 0){
            availableAuthTypes.add(AuthType.KAKAO_ACCOUNT);
        }
        return availableAuthTypes;
    }
    @Override
    public void login() {
        final List<AuthType> authTypes = getAuthTypes();
        if(authTypes.size() == 1){
            Session.getCurrentSession().open(authTypes.get(0), hostActivity);
        } else {
            onClickLoginButton(authTypes);
        }
    }

    private void onClickLoginButton(final List<AuthType> authTypes){

        final List<Item> itemList = new ArrayList<Item>();
        if(authTypes.contains(AuthType.KAKAO_TALK)) {
            itemList.add(new Item(com.kakao.usermgmt.R.string.com_kakao_kakaotalk_account, com.kakao.usermgmt.R.drawable.kakaotalk_icon, AuthType.KAKAO_TALK));
        }
        if(authTypes.contains(AuthType.KAKAO_STORY)) {
            itemList.add(new Item(com.kakao.usermgmt.R.string.com_kakao_kakaostory_account, com.kakao.usermgmt.R.drawable.kakaostory_icon, AuthType.KAKAO_STORY));
        }
        if(authTypes.contains(AuthType.KAKAO_ACCOUNT)){
            itemList.add(new Item(com.kakao.usermgmt.R.string.com_kakao_other_kakaoaccount, com.kakao.usermgmt.R.drawable.kakaoaccount_icon, AuthType.KAKAO_ACCOUNT));
        }
        itemList.add(new Item(com.kakao.usermgmt.R.string.com_kakao_account_cancel, 0, null)); //no icon for this one

        final Item[] items = itemList.toArray(new Item[itemList.size()]);
        ListDialogAttr _Attr = new ListDialogAttr();
        _Attr.ListItems = new CharSequence[]{hostActivity.getString(items[0].textId), hostActivity.getString(items[1].textId), hostActivity.getString(items[2].textId)};
        _Attr.ListCB = new ExtDialog.ListCallback() {
            @Override
            public void OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text) {
                dialog.dismiss();

                final AuthType authType = items[which].authType;
                if (authType != null) {
                    Session.getCurrentSession().open(authType, hostActivity);

                }
            }
        };
        ExtDialogSt.Get().AlertListDialog(hostActivity, _Attr);
    }

    @Override
    public void logout() {
        Activity currActivity = AppController.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            AppController.setCurrentActivity(null);
        }
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    public void revoke() {

    }

    @Override
    public void share(String content, final Uri imageOrVideo) {
        mImageOrVideo = imageOrVideo;
        if(Session.getCurrentSession().isOpened()) {
            redirectPostActivity();
        } else {
            login();
            mbDirectGoToPostActivity = true;
        }
    }

    private void redirectPostActivity() {
        final Intent intent = new Intent(hostActivity, KakaoPostActivity.class);
        intent.setData(mImageOrVideo);
        intent.putExtra("content-link", content);
        hostActivity.startActivity(intent);
        mImageOrVideo = null;
        logout();
    }
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.d(TAG, "onSessionOpened");
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.e(TAG, exception.getMessage());
            }
        }
    }
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Log.d(TAG, message);

                if (errorResult.getErrorCode() == ErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(hostActivity, hostActivity.getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onFailure");
//                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "onSessionClosed");
                //redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
//                redirectMainActivity();
                Log.d(TAG, "onSuccess");
                if(onAuthListener != null) {  //from login Activity
                    SocialProfile profile = getProfileInfo(); // one time for login()
                    onAuthListener.onLoginSuccess(profile);
                }
                if(mbDirectGoToPostActivity) {
                    redirectPostActivity();
                    mbDirectGoToPostActivity = false;
                }
            }

            @Override
            public void onNotSignedUp() {
                showSignup();
            }
        });
    }

    private SocialProfile setProfile(UserProfile userProfile) {
        SocialProfile profile = new SocialProfile();

        profile.name = userProfile.getNickname();
        profile.id = String.valueOf(userProfile.getId());
        profile.image = userProfile.getProfileImagePath();
        profile.network = SocialProfile.KAKAO;
        profile.email = null;

        return profile;
    }

    private SocialProfile getProfileInfo(){
        SocialProfile profile = new SocialProfile();
        UserProfile userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            return setProfile(userProfile);

        } else {
            KakaoTalkService.requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
                @Override
                public void onNotKakaoTalkUser() {
//                    final Intent intent = new Intent(hostActivity, LogInActivity.class);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e(TAG, errorResult.toString());

                }

                @Override
                public void onNotSignedUp() {
//                    final Intent intent = new Intent(KakaoServiceActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e(TAG, errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoTalkProfile result) {
                    SocialProfile profile = new SocialProfile();
                    profile.name = result.getNickName();
                    profile.image = result.getProfileImageUrl();
                    profile.network = SocialProfile.KAKAO;
                    profile.email = null;
                }
            });
        }
        return profile;
    }
    protected void showSignup() {
        Log.d(TAG, "not registered user");
//        redirectLoginActivity();
        String message = "not registered user.\nYou should signup at UserManagememt menu.";
        Toast.makeText(hostActivity,message, Toast.LENGTH_SHORT).show();
        /*Dialog dialog = new DialogBuilder(hostActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        dialog.show();*/
    }
}
