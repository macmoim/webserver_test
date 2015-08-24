package com.macmoim.pang.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macmoim.pang.MyPostActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.data.FoodItem;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodLargeRecyclerViewAdapter extends FoodRecyclerViewAdapter {


    public FoodLargeRecyclerViewAdapter(Activity activity, List<FoodItem> items) {
        super(activity, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_large, parent, false);
        return new ViewHolder(view);
    }
}
