package com.macmoim.pang.custom.swiperefresh;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.macmoim.pang.R;
import com.macmoim.pang.custom.swiperefresh.CustomSwipeRefreshLayout.State;

public class CustomSwipeHeadView extends LinearLayout implements CustomSwipeRefreshLayout.CustomSwipeRefreshHeadLayout {
    private ViewGroup mContainer;
    private TextView tvState = null;
    private ImageView ivArrow = null;
    private ImageView ivComplete = null;
    private ProgressBar mProgressBar;

    public CustomSwipeHeadView(Context context, int Layout) {
        super(context);
        SetupLayout(Layout);
    }

    private void SetupLayout(int Layout) {
        ViewGroup.LayoutParams _Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(Layout, null);
        addView(mContainer, _Lp);
        setGravity(Gravity.BOTTOM);
        ivArrow = (ImageView) findViewById(R.id.header_arrow_iv);
        ivComplete = (ImageView) findViewById(R.id.header_complete_iv);
        tvState = (TextView) findViewById(R.id.header_state_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.header_pgb);
    }

    @Override
    public void OnStateChange(State CurrentState, State LastState) {
        Log.d("csrh", "onStateChange() :: state = " + CurrentState + ", last state = " + LastState);

        int _StateCode = CurrentState.getRefreshState();
        int _LastStateCode = LastState.getRefreshState();
        float _Percent = CurrentState.getPercent();

        switch (_StateCode) {
            case CustomSwipeRefreshLayout.State.STATE_NORMAL: {
                if (_Percent > 0.5f) {
                    SetImageRotation((_Percent - 0.5f) * 180 / 0.5f);
                    tvState.setTextColor(Color.argb(0xff, (int) ((_Percent - 0.5f) * 255 / 0.5f), 0, 0));
                } else {
                    SetImageRotation(0);
                    tvState.setTextColor(Color.BLACK);
                }

                if (_StateCode != _LastStateCode) {
                    ivArrow.setVisibility(View.VISIBLE);
                    ivComplete.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    tvState.setText(getResources().getString(R.string.swipe_refresh_state_ready));
                }
                break;
            }
            case CustomSwipeRefreshLayout.State.STATE_READY: {
                if (_StateCode != _LastStateCode) {
                    ivArrow.setVisibility(View.VISIBLE);
                    ivComplete.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    SetImageRotation(180);
                    tvState.setText(getResources().getString(R.string.swipe_refresh_state_release));
                    tvState.setTextColor(Color.RED);
                }
                break;
            }
            case CustomSwipeRefreshLayout.State.STATE_REFRESHING: {
                if (_StateCode != _LastStateCode) {
                    ivArrow.clearAnimation();
                    ivArrow.setVisibility(View.INVISIBLE);
                    ivComplete.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    tvState.setText(getResources().getString(R.string.swipe_refresh_state_refresh));
                    tvState.setTextColor(Color.RED);
                }
                break;
            }
            case CustomSwipeRefreshLayout.State.STATE_COMPLETE: {
                if (_StateCode != _LastStateCode) {
                    ivArrow.setVisibility(View.INVISIBLE);
                    ivComplete.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
                        Integer colorFrom = Color.RED;
                        Integer colorTo = Color.BLACK;
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                tvState.setTextColor((Integer) animator.getAnimatedValue());
                            }
                        });
                        colorAnimation.setDuration(1000);
                        colorAnimation.start();
                    } else {
                        tvState.setTextColor(Color.BLACK);
                    }
                }
                tvState.setText(getResources().getString(R.string.swipe_refresh_state_refresh_complete));
                break;
            }
            default:
        }
    }

    private void SetImageRotation(float Rotation) {
        int _OS = android.os.Build.VERSION.SDK_INT;

        if (_OS >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ivArrow.setRotation(Rotation);
        } else {
            if (ivArrow.getTag() == null) {
                ivArrow.setTag(0f);
            }
            ivArrow.clearAnimation();
            Float lastDegree = (Float) ivArrow.getTag();
            RotateAnimation rotate = new RotateAnimation(lastDegree, Rotation,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ivArrow.setTag(Rotation);
            rotate.setFillAfter(true);
            ivArrow.startAnimation(rotate);
        }
    }
}
