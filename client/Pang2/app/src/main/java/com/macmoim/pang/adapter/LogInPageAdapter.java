package com.macmoim.pang.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.macmoim.pang.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by P10452 on 2015-08-26.
 */
public class LogInPageAdapter extends PagerAdapter {
    private final Activity activity;
    private int[] imagesId;
    Map<Integer, View> imageViews = new HashMap<Integer, View>();

    public LogInPageAdapter(Activity activity, int[] imagesId) {
        this.activity = activity;
        this.imagesId = imagesId;
    }

    public Map<Integer, View> getImageViews() {
        return imageViews;
    }

    @Override
    public int getCount() {
        return imagesId.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.login_page_adapter_element, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(imagesId[position]);
        container.addView(view);
        imageViews.put(position, imageView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}
