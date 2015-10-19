package com.macmoim.pang.pangeditor;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by P11872 on 2015-07-23.
 */
public class PagePagerAdapter extends FragmentPagerAdapter {
    Context mContext;

    ArrayList<PageItem> mPageItems;
    PageMoveListener mListener;
    //Defined from strings.xml

    public PagePagerAdapter(FragmentManager fm) {
        super(fm);
        //Initialising the strings array of tabs
    }

    public PagePagerAdapter(FragmentManager fm, Context context, ArrayList<PageItem> items, PageMoveListener l) {
        super(fm);
        //Initialising the strings array of tabs
        mContext = context;
        mPageItems = items;
        mListener = l;
    }

    @Override
    public Fragment getItem(int position) {
        //Initialising Fragment
        //Passing in the position so that position of the fragment is returned
        Fragment myFragment;
        if (position == 0) {
            myFragment = PageViewerFrontPageFragment.GetInstance(position);
            ((PageViewerFrontPageFragment)myFragment).setPageItem((FrontPageItem)mPageItems.get(position));
            ((PageViewerFrontPageFragment)myFragment).setPageListener(mListener);
        } else {
            myFragment = PageViewerFragment.GetInstance(position);
            ((PageViewerFragment)myFragment).setPageItem(mPageItems.get(position));
            ((PageViewerFragment)myFragment).SetPageInfo(position, getCount() - 1);
        }

        return myFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mPageItems.size();
    }
}