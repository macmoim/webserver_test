package com.macmoim.pang.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by P10452 on 2015-08-28.
 */
public class PagerViewTransform implements ViewPager.PageTransformer {
    private float Speed = 0.3f;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void transformPage(View page, float position) {
        if (page.getTag() == null || !(page.getTag() instanceof PagerViewHolder)) {
            return;
        }

        View PagerView = ((PagerViewHolder) page.getTag()).GetPagerView();

        if (PagerView == null) {
            return;
        }

        if (position <= -1 || position >= 1) {
            return;
        }

        PagerView.setTranslationX(-position * Speed * page.getWidth());
    }

    public void setParallaxSpeed(float speed) {
        this.Speed = speed;
    }
}
