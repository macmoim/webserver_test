package com.macmoim.pang.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macmoim.pang.R;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.util.Util;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodLargeRecyclerViewAdapter extends FoodRecyclerViewAdapter {
    public FoodLargeRecyclerViewAdapter(Activity activity, List<FoodItem> items) {
        super(activity, items);
    }

    public static class ViewHolderLarge extends ViewHolder {
        public final TextView mLikeSumTv;
        public final TextView mScoreTv;

        public ViewHolderLarge(View view) {
            super(view);
            mLikeSumTv = (TextView) view.findViewById(R.id.like_text);
            mScoreTv = (TextView) view.findViewById(R.id.score_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_large, parent, false);
        return new ViewHolderLarge(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        FoodItem item = mValues.get(position);
        ((ViewHolderLarge) holder).mLikeSumTv.setText("  " + item.getLikeSum());
        ((ViewHolderLarge) holder).mScoreTv.setText("  " + item.getScore());
    }

    @Override
    public String getImageFolderURL() {
        return Util.IMAGE_FOLDER_URL;
    }
}
