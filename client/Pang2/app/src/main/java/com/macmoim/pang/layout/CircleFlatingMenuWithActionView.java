package com.macmoim.pang.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.macmoim.pang.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.Objects;

/**
 * Created by P11872 on 2015-09-01.
 */
public class CircleFlatingMenuWithActionView extends CircleFlatingMenu {

    private View mActionView;

    public CircleFlatingMenuWithActionView(Activity activity, View actionView) {
        super(activity);
        mActionView = actionView;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setFloationAction() {
        try {
            Objects.requireNonNull(Resid, "Resid is null");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (mTags == null) {
            mTags = Resid;
        }

        int blueSubActionButtonSize = mActivity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(mActivity);
        lCSubBuilder.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);


        ImageView[] iv = new ImageView[Resid.length];

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons

        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(mActivity);
        if (mStartAngle != -1) {
            builder.setStartAngle(mStartAngle);
        }
        if (mEndAngle != -1) {
            builder.setEndAngle(mEndAngle);
        }
        if (mRadius != -1) {
            builder.setRadius(mRadius);
        }

        for (int i = 0; i < Resid.length; i++) {
            iv[i] = new ImageView(mActivity);
            iv[i].setImageDrawable(mActivity.getResources().getDrawable(Resid[i]));
            iv[i].setOnTouchListener(this);
            iv[i].setTag(mTags[i]);
            builder.addSubActionView(lCSubBuilder.setContentView(iv[i]).build());
        }
        rightLowerMenu = builder.attachTo(mActionView).build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
            }
        });
    }

    public void menuOpen(boolean animated) {
        rightLowerMenu.open(animated);
    }

    public void menuClose(boolean animated) {
        rightLowerMenu.close(animated);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mListener.onTouch(v, event);
    }
}
