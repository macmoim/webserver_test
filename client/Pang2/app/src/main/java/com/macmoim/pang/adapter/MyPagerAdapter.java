package com.macmoim.pang.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.macmoim.pang.FoodListFragment;
import com.macmoim.pang.R;

/**
 * Created by P11872 on 2015-07-23.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    Context mContext;

    //Setting up integer array of icons
    int icons[] = {R.drawable.about_us, R.drawable.campus, R.drawable.events, R.drawable.learning, R.drawable.sewa};

    //Defined from strings.xml
    String[] tabText = null;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        //Initialising the strings array of tabs
        tabText = mContext.getResources().getStringArray(R.array.food_spinner);
    }

    public MyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        //Initialising the strings array of tabs
        mContext = context;
        tabText = mContext.getResources().getStringArray(R.array.tabs);

    }

    @Override
    public Fragment getItem(int position) {

        //Initialising Fragment
        //Passing in the position so that position of the fragment is returned
        FoodListFragment myFragment = FoodListFragment.getInstance(position);

        return myFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("TTT","position = " + position );
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position){

//        //Constructing drawable object from the icon position
//        Drawable drawable = mContext.getResources().getDrawable(icons[position]);
//
//        //Defining the bounds for each icon as this is not automatically calculated
//        drawable.setBounds(0,0,90,90);
//
//        //Passing icons as drawable objects into the imageSpan. This means it can be placed amongst the text
//        ImageSpan imageSpan = new ImageSpan(drawable);
//
//        //Spannable strings allows us to embed images with text (attach/detach images)
//        SpannableString spannableString = new SpannableString(" ");
//
//        //Here setting the span of the icons amongst the scroll bar. Using the array of icons, starting at position 0,
//        //till the end, SPAN_EXCLUSIVE_EXCLUSIVE will ensure only the images in the range are included, nothing more,
//        //nothing less
//        spannableString.setSpan(imageSpan,0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        //Return the spannable string with icons embedded
//        return spannableString;

        return tabText[position];
    }

    @Override
    public int getCount() {
        return tabText.length;
    }
}