/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.macmoim.pang;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.macmoim.pang.adapter.MyPagerAdapter;
import com.macmoim.pang.data.AppPreferences;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;
import com.macmoim.pang.gcm.RegistrationIntentService;
import com.macmoim.pang.layout.NaviHeaderView;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SocialProfile;
import com.macmoim.pang.pangeditor.PangEditorActivity2;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements Auth.OnAuthListener {
    private final String TAG = getClass().getName();

    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    NaviHeaderView mNHview;
    // CircleFlatingMenu mCf;
    com.github.clans.fab.FloatingActionMenu mStraightFloatingMenu;
    private ExtDialog mDialog;

    FacebookAuth mFaceBookAuth;
    GoogleAuth mGoogleAuth;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFaceBookAuth = new FacebookAuth(this, this);
        mGoogleAuth = new GoogleAuth(this, this);

        if (SocialProfile.GOOGLE.equals(LoginPreferences.GetInstance().getString(getApplicationContext(), LoginPreferences.USER_SOCIAL))) {
            // use this only when GooglePlusLogin. login again to check user connected to google.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleAuth.login();
                }
            }).start();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        mNHview = new NaviHeaderView(this);
        mNHview.OnDraw();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

//        setFloationAction();
        setStraightFloationAction();

//        final View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startPangEditorActivity();
//            }
//        };

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("New Post", mSnackBarClickListener).show();
//            }
//        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home: {
                        Intent _Intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(_Intent);
                        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                        break;
                    }
                    case R.id.nav_post: {
                        Intent _Intent = new Intent(MainActivity.this, MyPostActivity.class);
                        startActivity(_Intent);
                        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                        break;
                    }
                    case R.id.nav_foavorite: {
                        Intent _Intent = new Intent(MainActivity.this, LikeActivity.class);
                        startActivity(_Intent);
                        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                        break;
                    }
                    case R.id.nav_profile: {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                        break;
                    }
                    case R.id.nav_notice: {
                        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                        break;
                    }
                    case R.id.nav_logout: {
                        Log.e(TAG, "facebook auth is" + mFaceBookAuth.isCurrentState());
                        String social = LoginPreferences.GetInstance().getString(getApplicationContext(), LoginPreferences.USER_SOCIAL);
                        if (SocialProfile.GOOGLE.equals(social)) {
                            mGoogleAuth.revoke();
                        } else if (SocialProfile.FACEBOOK.equals(social)) {
                            mFaceBookAuth.revoke();
                        }
                        break;
                    }
                }
                return false;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (!AppPreferences.GetInstance().getBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE_POPUP_SHOWN) ||
                !AppPreferences.GetInstance().getBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE)) {
            ShowGcmSetUplDialog();
        }
    }

    /*protected void setFloationAction() {
        final int[] id = {R.drawable.ic_edit, R.drawable.sewa, R.drawable.ic_dashboard_white, R.drawable.ic_search_white};

        mCf = new CircleFlatingMenu(this);
        mCf.setListener(new CircleFlatingMenu.Listener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDrawerLayout.closeDrawers();
                    if ((int) v.getTag() == id[0]) {
                        mCf.menuClose(false);
                        startPangEditorActivity();
                    } else if ((int) v.getTag() == id[1]) {
                        mCf.menuClose(false);
                        startActivity(new Intent(MainActivity.this, LikeActivity.class));
                    } else if ((int) v.getTag() == id[2]) {
                        mCf.menuClose(false);
                        startActivity(new Intent(MainActivity.this, MyPostActivity.class));
                    } else if ((int) v.getTag() == id[3]) {
                        mCf.menuClose(false);
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    } else {

                    }

                }
                return true;
            }
        });
        mCf.addResId(id);
        mCf.setFloationAction();
    }*/

    protected void setStraightFloationAction() {
        mStraightFloatingMenu = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.main_fab_menu);
        mStraightFloatingMenu.setClosedOnTouchOutside(true);
        mStraightFloatingMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                } else {
                }
            }
        });

        com.github.clans.fab.FloatingActionButton fab_edit = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_main_edit);
        com.github.clans.fab.FloatingActionButton fab_like = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_main_like);
        com.github.clans.fab.FloatingActionButton fab_post = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_main_post);
        com.github.clans.fab.FloatingActionButton fab_search = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_main_search);

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartPangEditorActivity();
            }
        });

        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LikeActivity.class));
            }
        });

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyPostActivity.class));
            }
        });

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mStraightFloatingMenu != null && mStraightFloatingMenu.isOpened()) {
            mStraightFloatingMenu.close(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
                mNHview = new NaviHeaderView(this);
                mNHview.OnDraw();
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
//                if (mCf != null) {
//                    mCf.menuClose(false);
//                }
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), this.getBaseContext());
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void StartPangEditorActivity() {
        Intent i = new Intent(getApplicationContext(), PangEditorActivity2.class);
        startActivity(i);
    }

    private void ShowGcmSetUplDialog() {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Cancelable = false;
        _Attr.Title = getString(R.string.push_agree_title);
        _Attr.TitleIcon = R.drawable.ic_pencil;
        _Attr.Message = getString(R.string.push_agree);
        _Attr.NegativeButton = getString(R.string.disagree);
        _Attr.PositiveButton = getString(R.string.agree);
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                registerGCM();
                AppPreferences.GetInstance().putBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE_POPUP_SHOWN, true);
                AppPreferences.GetInstance().putBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE, true);
                super.OnPositive(dialog);
            }

            @Override
            public void OnNegative(ExtDialog dialog) {
                dialog.cancel();
                AppPreferences.GetInstance().putBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE_POPUP_SHOWN, true);
                AppPreferences.GetInstance().putBoolean(getApplicationContext(), AppPreferences.PUSH_AGREE, false);
                super.OnNegative(dialog);
            }
        };

        ExtDialogSt.Get().AlertExtDialog(this, _Attr);
    }

    @Override
    public void onLoginSuccess(SocialProfile profile) {

    }

    @Override
    public void onLoginError(String message) {

    }

    @Override
    public void onLoginCancel() {

    }

    private void GotoStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        mDrawerLayout.closeDrawers();
//                if (mCf != null) {
//                    mCf.menuClose(true);
//                }
    }

    private void SocialLogout(String Tag) {
        if (Tag.equals(SocialProfile.FACEBOOK)) {
            LoginPreferences.GetInstance().clear(this);
            GotoStartActivity();
        }
    }

    @Override
    public void onRevoke() {
        SocialLogout(SocialProfile.FACEBOOK);
        Toast.makeText(this, getResources().getString(R.string.logout_done), Toast.LENGTH_SHORT).show();
    }

    class GCMBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean sentToken = AppPreferences.GetInstance().getBoolean(getApplicationContext(), AppPreferences.SENT_TOKEN_TO_SERVER);
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
            RemoveDialog();
        }
    }

    private void registerGCM() {
        ShowDialog();
        mRegistrationBroadcastReceiver = new GCMBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppPreferences.REGISTRATION_COMPLETE));

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void ShowDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            ProgressCircleDialogAttr _Attr = new ProgressCircleDialogAttr();
            _Attr.Message = getResources().getString(R.string.loading);
            _Attr.Cancelable = false;
            mDialog = ExtDialogSt.Get().GetProgressCircleExtDialog(this, _Attr);
        }

        mDialog.show();
    }

    private void RemoveDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStraightFloatingMenu != null && mStraightFloatingMenu.isOpened()) {
            mStraightFloatingMenu.close(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mStraightFloatingMenu != null) {
            mStraightFloatingMenu.setOnMenuToggleListener(null);
            mStraightFloatingMenu = null;
        }
        if (mViewPager != null) {
            mViewPager = null;
        }
        if (mNHview != null) {
            mNHview.OnDestroy();
            mNHview = null;
        }
        mRegistrationBroadcastReceiver = null;
        super.onDestroy();
    }
}
