package com.macmoim.pang.dialog.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.macmoim.pang.dialog.typedef.GravityEnum;
import com.macmoim.pang.dialog.util.Utils;
import com.macmoim.pang.R;


/**
 * Created by P10452 on 2015-09-05.
 */
public class DialogButton extends TextView {
    private boolean mStacked = false;
    private GravityEnum mStackedGravity;

    private int mStackedEndPadding;
    private Drawable mStackedBackground;
    private Drawable mDefaultBackground;

    public DialogButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs, 0, 0);
    }

    public DialogButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialogButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void Init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mStackedEndPadding = context.getResources().getDimensionPixelSize(R.dimen.ext_dialog_margin);
        mStackedGravity = GravityEnum.END;
    }

    /*public*/ void SetStacked(boolean stacked, boolean force) {
        if (mStacked != stacked || force) {
            setGravity(stacked ? (Gravity.CENTER_VERTICAL | mStackedGravity.GetGravity()) : Gravity.CENTER);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                setTextAlignment(stacked ? mStackedGravity.GetTextAlignment() : TEXT_ALIGNMENT_CENTER);
            }

            Utils.SetBackground(this, stacked ? mStackedBackground : mDefaultBackground);

            if (stacked) {
                setPadding(mStackedEndPadding, getPaddingTop(), mStackedEndPadding, getPaddingBottom());
            } /* Else the padding was properly reset by the drawable */

            mStacked = stacked;
        }
    }

    public void SetStackedGravity(GravityEnum gravity) {
        mStackedGravity = gravity;
    }

    public void SetStackedSelector(Drawable d) {
        mStackedBackground = d;
        if (mStacked) {
            SetStacked(true, true);
        }
    }

    public void SetDefaultSelector(Drawable d) {
        mDefaultBackground = d;
        if (!mStacked) {
            SetStacked(false, true);
        }
    }

    public void SetAllCapsCompat(boolean allCaps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setAllCaps(allCaps);
        } else {
            if (allCaps) {
                setTransformationMethod(new AllCapsTransformationMethod(getContext()));
            } else {
                setTransformationMethod(null);
            }
        }
    }
}