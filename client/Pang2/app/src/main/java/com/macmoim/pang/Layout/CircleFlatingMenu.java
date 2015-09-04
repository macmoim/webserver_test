package com.macmoim.pang.Layout;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.macmoim.pang.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by P11872 on 2015-09-01.
 */
public class CircleFlatingMenu implements View.OnTouchListener {
    protected Activity mActivity;
    protected int[] Resid = null;
    protected int[] mTags = null;

    protected int mStartAngle = -1;
    protected int mEndAngle = -1;
    protected int mRadius = -1;

    protected Listener mListener;
    protected FloatingActionMenu rightLowerMenu;

    public interface Listener {
        public boolean onTouch(View v, MotionEvent event);
    }

    public void setListener(Listener l) {
        mListener = l;
    }


    public CircleFlatingMenu(Activity activity) {
        this.mActivity = activity;
    }

    public void addResId(int[] id) {
        Resid = id;
    }
    public void addViewTags(int[] viewTags) {
        mTags = viewTags;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setFloationAction() {
        try {
            Objects.requireNonNull(Resid, "Resid is null");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int blueSubActionButtonSize = mActivity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconNew = new ImageView(mActivity);
        fabIconNew.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_action_new_light));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(mActivity)
                .setContentView(fabIconNew)
                .setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.button_action_blue_selector))
                .build();
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

        for (int i = 0; i < Resid.length; i++) {
            iv[i] = new ImageView(mActivity);
            iv[i].setImageDrawable(mActivity.getResources().getDrawable(Resid[i]));
            iv[i].setOnTouchListener(this);
            iv[i].setTag(Resid[i]);
            builder.addSubActionView(lCSubBuilder.setContentView(iv[i]).build());
        }
        rightLowerMenu = builder.attachTo(rightLowerButton).build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }


        });
    }

    public ArrayList<View> getSubactionViews() {
        if (rightLowerMenu == null) {
            return null;
        }
        ArrayList<View> subViews = new ArrayList<>();
        for (FloatingActionMenu.Item item : rightLowerMenu.getSubActionItems()) {
            subViews.add(item.view);
        }
        return subViews;
    }

    public void menuOpen(boolean animated){
        rightLowerMenu.open(animated);
    }

    public void menuClose(boolean animated){
        rightLowerMenu.close(animated);
    }

    public void setItemAngle(int startAngle, int endAngle) {
        mStartAngle = startAngle;
        mEndAngle = endAngle;
    }

    public void setItemRadius(int radius) {
        mRadius = radius;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mListener.onTouch(v, event);
    }
}
