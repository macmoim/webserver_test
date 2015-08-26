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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import android.widget.Button;

import com.macmoim.pang.Layout.naviHeaderView;
import com.macmoim.pang.adapter.MyPagerAdapter;
import com.macmoim.pang.data.LoginPreferences;
import com.macmoim.pang.login.Auth;
import com.macmoim.pang.login.FacebookAuth;
import com.macmoim.pang.login.GoogleAuth;
import com.macmoim.pang.login.SimpleAuthListener;
import com.macmoim.pang.login.SocialProfile;

import java.util.Objects;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    Auth auth;

    private Button mLogIn;
    private ViewPager mViewPager;
    naviHeaderView mNHview;

    private SimpleAuthListener authListener = new SimpleAuthListener() {
        @Override
        public void onRevoke() {
            Log.d(TAG, "SimpleAuthListener()");
            logoutUser();
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        String socialNetwork = getNetworkInfo();
        //create correct auth manager according user account
        if (socialNetwork.equals(SocialProfile.FACEBOOK)) {
            auth = new FacebookAuth(this, authListener);
        } else if (socialNetwork.equals(SocialProfile.GOOGLE)) {
            auth = new GoogleAuth(this, authListener);
            auth.login();
        } else {
            //TODO : KAKAO
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        mNHview = new naviHeaderView(this);
        mNHview.onDraw();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        final View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPangEditorActivity();
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("New Post", mSnackBarClickListener).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(MainActivity.this, MyPostActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_foavorite:
                        Intent tmptent = new Intent(MainActivity.this, LikeActivity.class);
                        startActivity(tmptent);
                        break;

                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;

                    case R.id.nav_notice:
                        break;
                }
                return false;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private String getNetworkInfo() {
        return LoginPreferences.GetInstance().getString(this, LoginPreferences.USER_SOCIAL);
    }


    private void logoutUser() {
        //clear share preferences
        LoginPreferences.GetInstance().clear(this);

        //clear back stack activity and back to login activity
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNHview = new naviHeaderView(this);
                mNHview.onDraw();
                mDrawerLayout.openDrawer(GravityCompat.START);
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

    private void startPangEditorActivity() {
        Intent i = new Intent(getApplicationContext(), PangEditorActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        if (mViewPager != null) {
            mViewPager = null;
        }
        if (mNHview != null) {
            mNHview.onDestroy();
            mNHview = null;
        }
        super.onDestroy();
    }

}
