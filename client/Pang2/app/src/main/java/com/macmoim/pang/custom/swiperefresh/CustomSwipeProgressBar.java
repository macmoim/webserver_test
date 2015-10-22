package com.macmoim.pang.custom.swiperefresh;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

final class CustomSwipeProgressBar {
    private final String TAG = getClass().getName();

    // Default progress animation colors are grays.
    private final static int COLOR1 = 0xB3000000;
    private final static int COLOR2 = 0x80000000;
    private final static int COLOR3 = 0x4d000000;
    private final static int COLOR4 = 0x1a000000;

    // The duration of the animation cycle.
    private static final int ANIMATION_DURATION_MS = 2000;

    // The duration of the animation to clear the bar.
    private static final int FINISH_ANIMATION_DURATION_MS = 1000;

    // Interpolator for varying the speed of the animation.
    private static final Interpolator INTERPOLATOR = CustomInterpolator.getInstance();

    private final Paint mPaint = new Paint();
    private final RectF mClipRect = new RectF();
    private float mTriggerPercentage;
    private long mStartTime;
    private long mFinishTime;
    private boolean mRunning;

    // Colors used when rendering the animation,
    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColor4;
    private View mParent;

    private Rect mBounds = new Rect();

    public CustomSwipeProgressBar(View parent) {
        mParent = parent;
        mColor1 = COLOR1;
        mColor2 = COLOR2;
        mColor3 = COLOR3;
        mColor4 = COLOR4;
    }

    void SetColorScheme(int Color1, int Color2, int Color3, int Color4) {
        mColor1 = Color1;
        mColor2 = Color2;
        mColor3 = Color3;
        mColor4 = Color4;
    }

    void SetTriggerPercentage(float Percentage) {
        mTriggerPercentage = Percentage;
        mStartTime = 0;
        ViewCompat.postInvalidateOnAnimation(mParent);
    }

    void Start() {
        if (!mRunning) {
            mTriggerPercentage = 0;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mRunning = true;
            mParent.postInvalidate();
        }
    }

    void Stop() {
        if (mRunning) {
            mTriggerPercentage = 0;
            mFinishTime = AnimationUtils.currentAnimationTimeMillis();
            mRunning = false;
            mParent.postInvalidate();
        }
    }

    void Draw(Canvas canvas) {
        final int _Width = mBounds.width();
        final int _Height = mBounds.height();
        final int _Cx = mBounds.left + _Width / 2;
        final int _Cy = mBounds.top + _Height / 2;
        boolean _DrawTriggerWhileFinishing = false;
        int _RestoreCount = canvas.save();

        canvas.clipRect(mBounds);

        if (mRunning || (mFinishTime > 0)) {
            long _Now = AnimationUtils.currentAnimationTimeMillis();
            long _Elapsed = (_Now - mStartTime) % ANIMATION_DURATION_MS;
            long _Iterations = (_Now - mStartTime) / ANIMATION_DURATION_MS;
            float _RawProgress = (_Elapsed / (ANIMATION_DURATION_MS / 100f));

            // If we're not running anymore, that means we're running through
            // the finish animation.
            if (!mRunning) {
                // If the finish animation is done, don't draw anything, and
                // don't repost.
                if ((_Now - mFinishTime) >= FINISH_ANIMATION_DURATION_MS) {
                    mFinishTime = 0;
                    canvas.restoreToCount(_RestoreCount);
                    return;
                }

                // Otherwise, use a 0 opacity alpha layer to clear the animation
                // from the inside out. This layer will prevent the circles from
                // drawing within its bounds.
                long _FinishElapsed = (_Now - mFinishTime) % FINISH_ANIMATION_DURATION_MS;
                float _FinishProgress = (_FinishElapsed / (FINISH_ANIMATION_DURATION_MS / 100f));
                float _Pct = (_FinishProgress / 100f);
                // Radius of the circle is half of the screen.
                float _ClearRadius = _Width / 2 * INTERPOLATOR.getInterpolation(_Pct);

                mClipRect.set(_Cx - _ClearRadius, mBounds.top, _Cx + _ClearRadius, mBounds.bottom);
                canvas.saveLayerAlpha(mClipRect, 0, 0);
                // Only draw the trigger if there is a space in the center of
                // this refreshing view that needs to be filled in by the
                // trigger. If the progress view is just still animating, let it
                // continue animating.
                _DrawTriggerWhileFinishing = true;
            }

            // First fill in with the last color that would have finished drawing.
            if (_Iterations == 0) {
                canvas.drawColor(mColor1);
            } else {
                if (_RawProgress >= 0 && _RawProgress < 25) {
                    canvas.drawColor(mColor4);
                } else if (_RawProgress >= 25 && _RawProgress < 50) {
                    canvas.drawColor(mColor1);
                } else if (_RawProgress >= 50 && _RawProgress < 75) {
                    canvas.drawColor(mColor2);
                } else {
                    canvas.drawColor(mColor3);
                }
            }

            // Then draw up to 4 overlapping concentric circles of varying radii, based on how far
            // along we are in the cycle.
            // progress 0-50 draw mColor2
            // progress 25-75 draw mColor3
            // progress 50-100 draw mColor4
            // progress 75 (wrap to 25) draw mColor1
            if ((_RawProgress >= 0 && _RawProgress <= 25)) {
                float pct = (((_RawProgress + 25) * 2) / 100f);
                DrawCircle(canvas, _Cx, _Cy, mColor1, pct);
            }
            if (_RawProgress >= 0 && _RawProgress <= 50) {
                float pct = ((_RawProgress * 2) / 100f);
                DrawCircle(canvas, _Cx, _Cy, mColor2, pct);
            }
            if (_RawProgress >= 25 && _RawProgress <= 75) {
                float pct = (((_RawProgress - 25) * 2) / 100f);
                DrawCircle(canvas, _Cx, _Cy, mColor3, pct);
            }
            if (_RawProgress >= 50 && _RawProgress <= 100) {
                float pct = (((_RawProgress - 50) * 2) / 100f);
                DrawCircle(canvas, _Cx, _Cy, mColor4, pct);
            }
            if ((_RawProgress >= 75 && _RawProgress <= 100)) {
                float pct = (((_RawProgress - 75) * 2) / 100f);
                DrawCircle(canvas, _Cx, _Cy, mColor1, pct);
            }
            if (mTriggerPercentage > 0 && _DrawTriggerWhileFinishing) {
                // There is some portion of trigger to draw. Restore the canvas,
                // then draw the trigger. Otherwise, the trigger does not appear
                // until after the bar has finished animating and appears to
                // just jump in at a larger width than expected.
                canvas.restoreToCount(_RestoreCount);
                _RestoreCount = canvas.save();
                canvas.clipRect(mBounds);
                DrawTrigger(canvas, _Cx, _Cy);
            }
            // Keep running until we finish out the last cycle.
            ViewCompat.postInvalidateOnAnimation(mParent);
        } else {
            // Otherwise if we're in the middle of a trigger, draw that.
            if (mTriggerPercentage > 0 && mTriggerPercentage <= 1.0) {
                DrawTrigger(canvas, _Cx, _Cy);
            }
        }
        canvas.restoreToCount(_RestoreCount);
    }

    private void DrawTrigger(Canvas canvas, int Cx, int Cy) {
        mPaint.setColor(mColor1);
        canvas.drawCircle(Cx, Cy, Cx * mTriggerPercentage, mPaint);
    }

    private void DrawCircle(Canvas canvas, float Cx, float Cy, int Color, float Pct) {
        mPaint.setColor(Color);
        canvas.save();
        canvas.translate(Cx, Cy);

        float _RadiusScale = INTERPOLATOR.getInterpolation(Pct);

        canvas.scale(_RadiusScale, _RadiusScale);
        canvas.drawCircle(0, 0, Cx, mPaint);
        canvas.restore();
    }

    void SetBounds(int L, int T, int R, int B) {
        mBounds.left = L;
        mBounds.top = T;
        mBounds.right = R;
        mBounds.bottom = B;
    }

    static final class CustomInterpolator implements Interpolator {
        private static final CustomInterpolator INSTANCE = new CustomInterpolator();

        public final static CustomInterpolator getInstance() {
            return INSTANCE;
        }

        private CustomInterpolator() {
            super();
        }

        /**
         * Lookup table values.
         * Generated using a Bezier curve from (0,0) to (1,1) with control points:
         * P0 (0,0)
         * P1 (0.4, 0)
         * P2 (0.2, 1.0)
         * P3 (1.0, 1.0)
         * <p/>
         * Values sampled with x at regular intervals between 0 and 1.
         */
        private static final float[] VALUES = new float[]{
                0.0f, 0.0002f, 0.0009f, 0.0019f, 0.0036f, 0.0059f, 0.0086f, 0.0119f, 0.0157f, 0.0209f,
                0.0257f, 0.0321f, 0.0392f, 0.0469f, 0.0566f, 0.0656f, 0.0768f, 0.0887f, 0.1033f, 0.1186f,
                0.1349f, 0.1519f, 0.1696f, 0.1928f, 0.2121f, 0.237f, 0.2627f, 0.2892f, 0.3109f, 0.3386f,
                0.3667f, 0.3952f, 0.4241f, 0.4474f, 0.4766f, 0.5f, 0.5234f, 0.5468f, 0.5701f, 0.5933f,
                0.6134f, 0.6333f, 0.6531f, 0.6698f, 0.6891f, 0.7054f, 0.7214f, 0.7346f, 0.7502f, 0.763f,
                0.7756f, 0.7879f, 0.8f, 0.8107f, 0.8212f, 0.8326f, 0.8415f, 0.8503f, 0.8588f, 0.8672f,
                0.8754f, 0.8833f, 0.8911f, 0.8977f, 0.9041f, 0.9113f, 0.9165f, 0.9232f, 0.9281f, 0.9328f,
                0.9382f, 0.9434f, 0.9476f, 0.9518f, 0.9557f, 0.9596f, 0.9632f, 0.9662f, 0.9695f, 0.9722f,
                0.9753f, 0.9777f, 0.9805f, 0.9826f, 0.9847f, 0.9866f, 0.9884f, 0.9901f, 0.9917f, 0.9931f,
                0.9944f, 0.9955f, 0.9964f, 0.9973f, 0.9981f, 0.9986f, 0.9992f, 0.9995f, 0.9998f, 1.0f, 1.0f
        };

        private static final float STEP_SIZE = 1.0f / (VALUES.length - 1);

        @Override
        public float getInterpolation(float input) {
            if (input >= 1.0f) {
                return 1.0f;
            }

            if (input <= 0f) {
                return 0f;
            }

            int _Position = Math.min((int) (input * (VALUES.length - 1)), VALUES.length - 2);
            float _Quantized = _Position * STEP_SIZE;
            float _Difference = input - _Quantized;
            float _Weight = _Difference / STEP_SIZE;

            return VALUES[_Position] + _Weight * (VALUES[_Position + 1] - VALUES[_Position]);
        }
    }
}