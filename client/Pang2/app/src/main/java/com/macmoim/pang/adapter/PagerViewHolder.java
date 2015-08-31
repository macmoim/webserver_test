package com.macmoim.pang.adapter;

import android.view.View;

/**
 * Created by P10452 on 2015-08-28.
 */
public class PagerViewHolder {
    private View PagerView;

    private PagerViewHolder() {
        this.PagerView = null;
    }

    public PagerViewHolder(View view) {
        this.PagerView = view;
    }

    public View GetPagerView() {
        return PagerView;
    }

    public void SetPagerView(View pagerView) {
        this.PagerView = pagerView;
    }
}
