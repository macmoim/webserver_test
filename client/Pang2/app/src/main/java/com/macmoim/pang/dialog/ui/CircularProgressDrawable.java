package com.macmoim.pang.dialog.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by P10452 on 2015-09-05.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CircularProgressDrawable extends Drawable implements Animatable {
    private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANGLE_ANIMATOR_DURATION = 2000;
    private static final int SWEEP_ANIMATOR_DURATION = 600;
    private static final int MIN_SWEEP_ANGLE = 30;

    private final RectF fBounds = new RectF();

    private ObjectAnimator mObjectAnimatorSweep;
    private ObjectAnimator mObjectAnimatorAngle;
    private Paint mPaint;

    private boolean mModeAppearing;

    private float mCurrentGlobalAngleOffset;
    private float mCurrentGlobalAngle;
    private float mCurrentSweepAngle;
    private float mBorderWidth;
    private boolean mRunning;

    public CircularProgressDrawable(int Color, float BorderWidth) {
        mBorderWidth = BorderWidth;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(BorderWidth);
        mPaint.setColor(Color);

        SetupAnimations();
    }

    @Override
    public void draw(Canvas canvas) {
        float _StartAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
        float _SweepAngle = mCurrentSweepAngle;

        if (!mModeAppearing) {
            _StartAngle = _StartAngle + _SweepAngle;
            _SweepAngle = 360 - _SweepAngle - MIN_SWEEP_ANGLE;
        } else {
            _SweepAngle += MIN_SWEEP_ANGLE;
        }
        canvas.drawArc(fBounds, _StartAngle, _SweepAngle, false, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        mRunning = true;
        mObjectAnimatorAngle.start();
        mObjectAnimatorSweep.start();
        invalidateSelf();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        mRunning = false;
        mObjectAnimatorAngle.cancel();
        mObjectAnimatorSweep.cancel();
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        fBounds.left = bounds.left + mBorderWidth / 2f + .5f;
        fBounds.right = bounds.right - mBorderWidth / 2f - .5f;
        fBounds.top = bounds.top + mBorderWidth / 2f + .5f;
        fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f;
    }

    public void SetCurrentGlobalAngle(float CurrentGlobalAngle) {
        mCurrentGlobalAngle = CurrentGlobalAngle;
        invalidateSelf();
    }

    public float GetCurrentGlobalAngle() {
        return mCurrentGlobalAngle;
    }

    public void SetCurrentSweepAngle(float CurrentSweepAngle) {
        mCurrentSweepAngle = CurrentSweepAngle;
        invalidateSelf();
    }

    public float GetCurrentSweepAngle() {
        return mCurrentSweepAngle;
    }

    private void ToggleAppearingMode() {
        mModeAppearing = !mModeAppearing;

        if (mModeAppearing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
        }
    }

    private Property<CircularProgressDrawable, Float> mAngleProperty
            = new Property<CircularProgressDrawable, Float>(Float.class, "angle") {
        @Override
        public Float get(CircularProgressDrawable object) {
            return object.GetCurrentGlobalAngle();
        }

        @Override
        public void set(CircularProgressDrawable object, Float value) {
            object.SetCurrentGlobalAngle(value);
        }
    };

    private Property<CircularProgressDrawable, Float> mSweepProperty
            = new Property<CircularProgressDrawable, Float>(Float.class, "arc") {
        @Override
        public Float get(CircularProgressDrawable object) {
            return object.GetCurrentSweepAngle();
        }

        @Override
        public void set(CircularProgressDrawable object, Float value) {
            object.SetCurrentSweepAngle(value);
        }
    };

    private void SetupAnimations() {
        mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f);
        mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
        mObjectAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
        mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f - MIN_SWEEP_ANGLE * 2);
        mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
        mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                ToggleAppearingMode();
            }
        });
    }
}