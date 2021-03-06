package com.macmoim.pang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.macmoim.pang.NavigationDrawer.NavigationDrawerCallbacks;
import com.macmoim.pang.NavigationDrawer.NavigationDrawerFragment;
import com.macmoim.pang.tabs.MyPagerAdapter;
import com.macmoim.pang.tabs.SlidingTabLayout;

public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ViewPager mPager;
    private SlidingTabLayout mTabs;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onMaketoolbar();
        onMakeNavigationDrawer();
        onMakeTabandPager();
    }

    private void onMaketoolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void onMakeNavigationDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    private void onMakeTabandPager() {
        mPager = (ViewPager) findViewById(R.id.pager);
        //Setting the Adapter on the view pager first. Passing the fragment manager through as an argument
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this.getBaseContext()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);


        //Setting the custom Tab View as the Sliding Tabs Layout
        mTabs.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);

        mTabs.setDistributeEvenly(true);

        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.tabIndicatorColour));

        mTabs.setBackgroundColor(getResources().getColor(R.color.basePrimaryBackgroundColour));

        //Setting the ViewPager as the tabs
        mTabs.setViewPager(mPager);
    }

    @Nullable
    @Override

    public View onCreateView(String name, Context context, AttributeSet attrs) {

        Log.d("TTT", "onCreateview");
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }


}
