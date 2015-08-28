package com.macmoim.pang.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.macmoim.pang.R;

import java.util.List;

/**
 * Created by P10452 on 2015-08-28.
 */
public class PagerViewAdapter extends PagerAdapter {
    private final LayoutInflater Inflater;
    private final List<Integer> Items;
    private final int LayoutId;

    public PagerViewAdapter(Context context, List<Integer> list, int layoutId) {
        this.Inflater = LayoutInflater.from(context);
        this.Items = list;
        this.LayoutId = layoutId;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = Inflater.inflate(this.LayoutId, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        PagerViewHolder holder = new PagerViewHolder(image);

        image.setImageResource(Items.get(position));
        container.addView(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
