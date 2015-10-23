package com.macmoim.pang.custom.swiperefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.macmoim.pang.R;

public class CustomSwipeRefreshLayout extends ViewGroup {
    private final String TAG = getClass().getName();

    // time out for no movements during swipe action
    private static final int RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 500;

    // time out for showing refresh complete
    private static final int REFRESH_COMPLETE_POSITION_TIMEOUT = 1000;

    // Duration of the animation from the top of the content view to parent top
    private static final int RETURN_TO_TOP_DURATION = 500;

    // Duration of the animation from the top of the content view to the height of header
    private static final int RETURN_TO_HEADER_DURATION = 500;

    // acceleration of progress bar
    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;

    // deceleration of progress bar
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    // height of progress bar
    private static final int PROGRESS_BAR_HEIGHT = 4;

    // maximum swipe distance( percent of parent container)
    private static final float MAX_SWIPE_DISTANCE_FACTOR = .5f;

    // swipe distance to trigger refreshing
    private static final int SWIPE_REFRESH_TRIGGER_DISTANCE = 100;

    // swipe resistance factor
    private static final float RESISTANCE_FACTOR = .5f;

    private final DecelerateInterpolator mDecelerateInterpolator;
    private final AccelerateInterpolator mAccelerateInterpolator;
    private final Animation mAnimateStayComplete = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            // DO NOTHING
        }
    };
    boolean enableTopProgressBar = true;

    State mCurrentState = new State(State.STATE_NORMAL);
    State mLastState = new State(-1);

    private RefreshCheckHandler mRefreshCheckHandler;
    private ScrollUpHandler mScrollUpHandler;
    private ScrollLeftOrRightHandler mScrollLeftOrRightHandler;
    private int mReturnToOriginalTimeout = RETURN_TO_ORIGINAL_POSITION_TIMEOUT;
    private int mRefreshCompleteTimeout = REFRESH_COMPLETE_POSITION_TIMEOUT;
    private float mResistanceFactor = RESISTANCE_FACTOR;
    private int mTriggerDistance = SWIPE_REFRESH_TRIGGER_DISTANCE;
    private int mProgressBarHeight = PROGRESS_BAR_HEIGHT;
    private int mReturnToTopDuration = RETURN_TO_TOP_DURATION;
    private int mReturnToHeaderDuration = RETURN_TO_HEADER_DURATION;
    private int mConvertedProgressBarHeight;
    private CustomSwipeProgressBar mTopProgressBar;
    private View mHeadView;

    //the content that gets pulled down
    private View mTarget = null;
    private int mTargetOriginalTop;
    private OnRefreshListener mListener;
    private MotionEvent mDownEvent;
    private int mFrom;
    private boolean mRefreshing = false;
    private int mTouchSlop;
    private int mDistanceToTriggerSync = -1;
    private float mPrevY;
    private float mFromPercentage = 0;
    private float mCurrPercentage = 0;
    private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrPercentage = 0;
        }
    };
    private boolean enableHorizontalScroll = true;
    private boolean isHorizontalScroll;
    private boolean checkHorizontalMove;
    private boolean mCheckValidMotionFlag = true;
    private int mCurrentTargetOffsetTop = 0;
    private final AnimationListener mReturningAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            // mCurrentTargetOffsetTop = 0;
            mInReturningAnimation = false;
        }
    };

    private boolean mInReturningAnimation;
    private int mTriggerOffset = 0;

    private final Runnable mReturnToTrigerPosition = new Runnable() {

        @Override
        public void run() {
            mInReturningAnimation = true;
            AnimateOffsetToTrigerPosition(mTarget.getTop(), mReturningAnimationListener);
        }

    };

    private final Runnable mReturnToStartPosition = new Runnable() {

        @Override
        public void run() {
            mInReturningAnimation = true;
            AnimateOffsetToStartPosition(mTarget.getTop(), mReturningAnimationListener);
        }

    };

    private final AnimationListener mStayCompleteListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            mReturnToStartPosition.run();
            mRefreshing = false;
        }
    };

    private final Runnable mStayRefreshCompletePosition = new Runnable() {

        @Override
        public void run() {
            AnimateStayComplete(mStayCompleteListener);
        }

    };

    private Animation mShrinkTrigger = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float percent = mFromPercentage + ((0 - mFromPercentage) * interpolatedTime);
            mTopProgressBar.SetTriggerPercentage(percent);
        }
    };

    // Cancel the refresh gesture and animate everything back to its original state.
    private final Runnable mCancel = new Runnable() {
        @Override
        public void run() {
            mInReturningAnimation = true;
            // Timeout fired since the user last moved their finger; animate the
            // trigger to 0 and put the target back at its original position
            if (mTopProgressBar != null && enableTopProgressBar) {
                mFromPercentage = mCurrPercentage;
                mShrinkTrigger.setDuration(mReturnToTopDuration);
                mShrinkTrigger.setAnimationListener(mShrinkAnimationListener);
                mShrinkTrigger.reset();
                mShrinkTrigger.setInterpolator(mDecelerateInterpolator);
                startAnimation(mShrinkTrigger);
            }
            AnimateOffsetToStartPosition(mTarget.getTop(), mReturningAnimationListener);
        }
    };

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int _TargetTop = mTargetOriginalTop;
            if (mFrom != mTargetOriginalTop) {
                _TargetTop = (mFrom + (int) ((mTargetOriginalTop - mFrom) * interpolatedTime));
            }
            int _Offset = _TargetTop - mTarget.getTop();
            final int currentTop = mTarget.getTop();
            if (_Offset + currentTop < 0) {
                _Offset = 0 - currentTop;
            }
            SetTargetOffsetTop(_Offset, true);
        }
    };

    private final Animation mAnimateToTrigerPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int _TargetTop = mDistanceToTriggerSync;
            if (mFrom > mDistanceToTriggerSync) {
                _TargetTop = (mFrom + (int) ((mDistanceToTriggerSync - mFrom) * interpolatedTime));
            }
            int _Offset = _TargetTop - mTarget.getTop();
            final int currentTop = mTarget.getTop();
            if (_Offset + currentTop < 0) {
                _Offset = 0 - currentTop;
            }
            SetTargetOffsetTop(_Offset, true);
        }
    };

    public CustomSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setWillNotDraw(false);
        mTopProgressBar = new CustomSwipeProgressBar(this);
        SetProgressBarHeight(PROGRESS_BAR_HEIGHT);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

        final TypedArray _Ar = context.obtainStyledAttributes(attrs, R.styleable.CustomSwipeRefreshLayout);
        if (_Ar != null) {
            boolean _ProgressBarEnabled = _Ar.getBoolean(R.styleable.CustomSwipeRefreshLayout_enable_top_progress_bar, true);
            mReturnToOriginalTimeout = _Ar.getInteger(R.styleable.CustomSwipeRefreshLayout_time_out_return_to_top,
                    RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
            mRefreshCompleteTimeout = _Ar.getInteger(R.styleable.CustomSwipeRefreshLayout_time_out_refresh_complete,
                    REFRESH_COMPLETE_POSITION_TIMEOUT);
            mReturnToTopDuration = _Ar.getInteger(R.styleable.CustomSwipeRefreshLayout_return_to_top_duration,
                    RETURN_TO_TOP_DURATION);
            mReturnToHeaderDuration = _Ar.getInteger(R.styleable.CustomSwipeRefreshLayout_return_to_header_duration,
                    RETURN_TO_HEADER_DURATION);

            int _Color1 = _Ar.getColor(R.styleable.CustomSwipeRefreshLayout_pgb_color_1, 0);
            int _Color2 = _Ar.getColor(R.styleable.CustomSwipeRefreshLayout_pgb_color_2, 0);
            int _Color3 = _Ar.getColor(R.styleable.CustomSwipeRefreshLayout_pgb_color_3, 0);
            int _Color4 = _Ar.getColor(R.styleable.CustomSwipeRefreshLayout_pgb_color_4, 0);
            SetProgressBarColor(_Color1, _Color2, _Color3, _Color4);
            EnableTopProgressBar(_ProgressBarEnabled);
            _Ar.recycle();
        }
    }

    private void AnimateStayComplete(AnimationListener Listener) {
        mAnimateStayComplete.reset();
        mAnimateStayComplete.setDuration(mRefreshCompleteTimeout);
        mAnimateStayComplete.setAnimationListener(Listener);
        mTarget.startAnimation(mAnimateStayComplete);
    }

    private void AnimateOffsetToTrigerPosition(int From, AnimationListener Listener) {
        mFrom = From;
        mAnimateToTrigerPosition.reset();
        mAnimateToTrigerPosition.setDuration(mReturnToHeaderDuration);
        mAnimateToTrigerPosition.setAnimationListener(Listener);
        mAnimateToTrigerPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToTrigerPosition);
    }

    private void AnimateOffsetToStartPosition(int From, AnimationListener Listener) {
        mFrom = From;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mReturnToTopDuration);
        mAnimateToStartPosition.setAnimationListener(Listener);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mTarget.startAnimation(mAnimateToStartPosition);
    }

    private boolean CanViewScrollUp(View V, MotionEvent Event) {
        boolean _Ret;

        Event.offsetLocation(V.getScrollX() - V.getLeft(), V.getScrollY() - V.getTop());

        if (mScrollUpHandler != null) {
            boolean _CanViewScrollUp = mScrollUpHandler.CanScrollUp(V);

            if (_CanViewScrollUp) {
                return true;
            }
        }

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (V instanceof AbsListView) {
                final AbsListView _AbsListView = (AbsListView) V;

                _Ret = _AbsListView.getChildCount() > 0
                        && (_AbsListView.getFirstVisiblePosition() > 0 || _AbsListView.getChildAt(0)
                        .getTop() < _AbsListView.getPaddingTop());
            } else {
                _Ret = V.getScrollY() > 0 || CanChildrenScroolUp(V, Event);
            }
        } else {
            _Ret = ViewCompat.canScrollVertically(V, -1) || CanChildrenScroolUp(V, Event);
        }
        return _Ret;
    }

    private boolean CanChildrenScroolUp(View V, MotionEvent Event) {
        if (V instanceof ViewGroup) {
            final ViewGroup _Vg = (ViewGroup) V;
            int _Count = _Vg.getChildCount();

            for (int i = 0; i < _Count; ++i) {
                View _Child = _Vg.getChildAt(i);
                Rect _Bounds = new Rect();
                _Child.getHitRect(_Bounds);
                if (_Bounds.contains((int) Event.getX(), (int) Event.getY())) {
                    return CanViewScrollUp(_Child, Event);
                }
            }
        }
        return false;
    }

    private boolean CanViewScrollHorizontally(View V, MotionEvent Event, int Direction) {
        boolean _Ret;

        Event.offsetLocation(V.getScrollX() - V.getLeft(), V.getScrollY() - V.getTop());

        if (mScrollLeftOrRightHandler != null) {
            boolean _CanViewScrollLeftOrRight = mScrollLeftOrRightHandler.CanScrollLeftOrRight(V, Direction);

            if (_CanViewScrollLeftOrRight) {
                return true;
            }
        }

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (V instanceof ViewPager) {
                _Ret = ((ViewPager) V).canScrollHorizontally(Direction);
            } else {
                _Ret = V.getScrollX() * Direction > 0;
            }
        } else {
            _Ret = ViewCompat.canScrollHorizontally(V, Direction);
        }

        _Ret = _Ret || CanChildrenScroolHorizontally(V, Event, Direction);

        return _Ret;
    }

    private boolean CanChildrenScroolHorizontally(View V, MotionEvent Event, int Direction) {
        if (V instanceof ViewGroup) {
            final ViewGroup _Vg = (ViewGroup) V;
            int _Count = _Vg.getChildCount();

            for (int i = 0; i < _Count; ++i) {
                View _Child = _Vg.getChildAt(i);
                Rect _Bounds = new Rect();

                _Child.getHitRect(_Bounds);
                if (_Bounds.contains((int) Event.getX(), (int) Event.getY())) {
                    return CanViewScrollHorizontally(_Child, Event, Direction);
                }
            }
        }
        return false;
    }

    public void SetCustomHeadview(View CustomHeadView) {
        if (mHeadView != null) {
            if (mHeadView == CustomHeadView) {
                return;
            }
            removeView(mHeadView);
        }
        mHeadView = CustomHeadView;
        addView(mHeadView, new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(mCancel);
        removeCallbacks(mReturnToStartPosition);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mReturnToStartPosition);
        removeCallbacks(mCancel);
    }

    public void SetOnRefreshListener(OnRefreshListener Listener) {
        mListener = Listener;
    }

    private void SetTriggerPercentage(float Percent) {
        if (Percent == 0f) {
            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
            // means we're trying to reset state, so there's nothing to reset in this case.
            mCurrPercentage = 0;
            return;
        }
        mCurrPercentage = Percent;
        if (enableTopProgressBar) {
            mTopProgressBar.SetTriggerPercentage(Percent);
        }
    }

    private void SetRefreshState(int State) {
        mCurrentState.update(State, mCurrentTargetOffsetTop, mTriggerOffset);
        ((CustomSwipeRefreshHeadLayout) mHeadView).OnStateChange(mCurrentState, mLastState);
        mLastState.update(State, mCurrentTargetOffsetTop, mTriggerOffset);
    }

    private void UpdateHeadViewState(boolean ChangeHeightOnly) {
        if (ChangeHeightOnly) {
            SetRefreshState(mCurrentState.getRefreshState());
        } else {
            if (mTarget.getTop() > mDistanceToTriggerSync) {
                SetRefreshState(State.STATE_READY);
            } else {
                SetRefreshState(State.STATE_NORMAL);
            }
        }
    }

    public void RefreshComplete() {
        SetRefreshing(false);
    }

    public void SetProgressBarColor(int Color1, int Color2, int Color3, int Color4) {
        mTopProgressBar.SetColorScheme(Color1, Color2, Color3, Color4);
    }

    public boolean IsRefreshing() {
        return mRefreshing;
    }

    private void SetRefreshing(boolean Refreshing) {
        if (mRefreshing != Refreshing) {
            EnsureTarget();
            mCurrPercentage = 0;
            mRefreshing = Refreshing;
            if (mRefreshing) {
                if (enableTopProgressBar) {
                    mTopProgressBar.Start();
                }

                mReturnToTrigerPosition.run();
            } else {
                // keep refreshing state for refresh complete
                if (enableTopProgressBar) {
                    mTopProgressBar.Stop();
                }

                mRefreshing = true;
                removeCallbacks(mReturnToStartPosition);
                removeCallbacks(mCancel);
                mStayRefreshCompletePosition.run();

                SetRefreshState(State.STATE_COMPLETE);
            }
        }
    }

    private View GetHeadView() {
        return getChildAt(0) == mHeadView ? getChildAt(1) : getChildAt(0);
    }

    private void EnsureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            if (getChildCount() > 2 && !isInEditMode()) {
                throw new IllegalStateException("can host only one direct child");
            }
            mTarget = GetHeadView();
            mTargetOriginalTop = mTarget.getTop();
        }
        if (mDistanceToTriggerSync == -1) {
            if (getParent() != null && ((View) getParent()).getHeight() > 0) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                mTriggerOffset = (int) (mTriggerDistance * metrics.density);
                mDistanceToTriggerSync = (int) Math.min(((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                        mTriggerOffset + mTargetOriginalTop);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (enableTopProgressBar) {
            mTopProgressBar.Draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int _Width = getMeasuredWidth();

        if (enableTopProgressBar) {
            int l, t, r, b;

            l = getPaddingLeft();
            t = getPaddingTop(); //getResources().getDimensionPixelSize(R.dimen.swipe_refresh_layout_heigt) - mConvertedProgressBarHeight;
            r = getPaddingLeft() + _Width;
            b = getPaddingTop() + mConvertedProgressBarHeight; //getResources().getDimensionPixelSize(R.dimen.swipe_refresh_layout_heigt);

            Log.d(TAG, String.format("mTopProgressBar[l = %d, t = %d, r = %d, b = %d]", l, t, r, b));

            mTopProgressBar.SetBounds(l, t, r, b);
        } else {
            mTopProgressBar.SetBounds(0, 0, 0, 0);
        }

        if (getChildCount() == 0) {
            return;
        }

        MarginLayoutParams _Lp = (MarginLayoutParams) mHeadView.getLayoutParams();
        final int _HeadViewLeft = getPaddingLeft() + _Lp.leftMargin;
        final int _HeadViewTop = mCurrentTargetOffsetTop - mHeadView.getMeasuredHeight() +
                getPaddingTop() + _Lp.topMargin;
        final int _HeadViewRight = _HeadViewLeft + mHeadView.getMeasuredWidth();
        final int _HeadViewBottom = _HeadViewTop + mHeadView.getMeasuredHeight();
        mHeadView.layout(_HeadViewLeft, _HeadViewTop, _HeadViewRight, _HeadViewBottom);

        final View _HeadView = GetHeadView();
        _Lp = (MarginLayoutParams) _HeadView.getLayoutParams();
        final int _ChildLeft = getPaddingLeft() + _Lp.leftMargin;
        final int _ChildTop = mCurrentTargetOffsetTop + getPaddingTop() + _Lp.topMargin;
        final int _ChildRight = _ChildLeft + _HeadView.getMeasuredWidth();
        final int _ChildBottom = _ChildTop + _HeadView.getMeasuredHeight();
        _HeadView.layout(_ChildLeft, _ChildTop, _ChildRight, _ChildBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() > 2 && !isInEditMode()) {
            throw new IllegalStateException("can host one child content view.");
        }

        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, heightMeasureSpec, 0);

        final View _HeadView = GetHeadView();
        if (getChildCount() > 0) {
            MarginLayoutParams lp = (MarginLayoutParams) _HeadView.getLayoutParams();
            _HeadView.measure(
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - lp.leftMargin - lp.rightMargin,
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - lp.topMargin - lp.bottomMargin,
                            MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() > 1 && !isInEditMode()) {
            throw new IllegalStateException("can host only one child content view");
        }
        super.addView(child, index, params);
    }

    private boolean CheckCanDoRefresh() {
        if (mRefreshCheckHandler != null) {
            return mRefreshCheckHandler.CanRefresh();
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean ret = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        EnsureTarget();

        boolean _Handled = false;
        float _CurY = ev.getY();

        if (!isEnabled()) {
            return false;
        }

        // record the first event:
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrPercentage = 0;
            mDownEvent = MotionEvent.obtain(ev);
            mPrevY = mDownEvent.getY();
            mCheckValidMotionFlag = true;
            checkHorizontalMove = true;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float _Ydiff = Math.abs(_CurY - mDownEvent.getY());

            if (enableHorizontalScroll) {
                MotionEvent event = MotionEvent.obtain(ev);
                int _HorizontalScrollDirection = ev.getX() > mDownEvent.getX() ? -1 : 1;
                float _Xdiff = Math.abs(ev.getX() - mDownEvent.getX());
                if (isHorizontalScroll) {
                    mPrevY = _CurY;
                    checkHorizontalMove = false;
                    return false;
                } else if (_Xdiff <= mTouchSlop) {
                    checkHorizontalMove = true;
                    //return false;
                } else if (CanViewScrollHorizontally(mTarget, event, _HorizontalScrollDirection) &&
                        checkHorizontalMove && _Xdiff > 2 * _Ydiff) {
                    mPrevY = _CurY;
                    isHorizontalScroll = true;
                    checkHorizontalMove = false;
                    return false;
                } else {
                    checkHorizontalMove = false;
                }
            }

            if (_Ydiff < mTouchSlop) {
                mPrevY = _CurY;
                return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            float _Ydiff = Math.abs(_CurY - mDownEvent.getY());
            if (enableHorizontalScroll && isHorizontalScroll) {
                isHorizontalScroll = false;
                mPrevY = ev.getY();
                return false;
            } else if (_Ydiff < mTouchSlop) {
                mPrevY = _CurY;
                return false;
            }
        }

        MotionEvent _Event = MotionEvent.obtain(ev);
        if (!mInReturningAnimation && !CanViewScrollUp(mTarget, _Event)) {
            _Handled = onTouchEvent(ev);
            Log.d(TAG, "onInterceptTouchEvent(): handled = onTouchEvent(event);" + _Handled);
        } else {
            // keep updating last Y position when the event is not intercepted!
            mPrevY = ev.getY();
        }

        boolean _Ret = !_Handled ? super.onInterceptTouchEvent(ev) : _Handled;
        Log.d(TAG, "onInterceptTouchEvent() " + _Ret);
        return _Ret;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        final int _Action = event.getAction();
        boolean _Handled = false;
        int _CurTargetTop = mTarget.getTop();
        mCurrentTargetOffsetTop = _CurTargetTop - mTargetOriginalTop;

        switch (_Action) {
            case MotionEvent.ACTION_MOVE: {
                if (mDownEvent != null && !mInReturningAnimation) {
                    final float _EeventY = event.getY();
                    float _Ydiff = _EeventY - mDownEvent.getY();

                    boolean _IsScrollUp = _EeventY - mPrevY > 0;

                    // if yDiff is large enough to be counted as one move event
                    if (mCheckValidMotionFlag && (_Ydiff > mTouchSlop || _Ydiff < -mTouchSlop)) {
                        mCheckValidMotionFlag = false;
                    }

                    // keep refresh head above mTarget when refreshing
                    if (IsRefreshing()) {
                        mPrevY = event.getY();
                        _Handled = false;
                        break;
                    }

                    // curTargetTop is bigger than trigger
                    if (_CurTargetTop >= mDistanceToTriggerSync) {
                        if (_CurTargetTop > mDistanceToTriggerSync) {
                            mPrevY = event.getY();
                            _Handled = true;
                            FitTargetOffsetTop();
                            break;
                        }

                        // User movement passed distance; trigger a refresh
                        if (enableTopProgressBar) {
                            mTopProgressBar.SetTriggerPercentage(1f);
                        }
                        removeCallbacks(mCancel);
                    }
                    // curTargetTop is not bigger than trigger
                    else {
                        // Just track the user's movement
                        SetTriggerPercentage(mAccelerateInterpolator.getInterpolation(
                                (float) mCurrentTargetOffsetTop / mTriggerOffset));

                        if (!_IsScrollUp && (_CurTargetTop < mTargetOriginalTop + 1)) {
                            removeCallbacks(mCancel);
                            mPrevY = event.getY();
                            _Handled = false;
                            // clear the progressBar
                            mTopProgressBar.SetTriggerPercentage(0f);
                            break;
                        } else {
                            UpdatePositionTimeout(true);
                        }
                    }

                    _Handled = true;

                    if (_CurTargetTop >= mTargetOriginalTop && !IsRefreshing()) {
                        SetTargetOffsetTop((int) ((_EeventY - mPrevY) * mResistanceFactor), false);
                    } else {
                        SetTargetOffsetTop((int) ((_EeventY - mPrevY)), true);
                    }
                    mPrevY = event.getY();
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mRefreshing) {
                    break;
                }

                if (mCurrentTargetOffsetTop >= mTriggerOffset) {
                    StartRefresh();
                    _Handled = true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (mDownEvent != null) {
                    mDownEvent.recycle();
                    mDownEvent = null;
                }
                break;
            }
        }
        return _Handled;
    }

    private void StartRefresh() {
        if (!CheckCanDoRefresh()) {
            UpdatePositionTimeout(false);
            return;
        }
        removeCallbacks(mCancel);
        SetRefreshState(State.STATE_REFRESHING);
        SetRefreshing(true);
        if (mListener != null) {
            mListener.OnRefresh();
        }
    }

    private void FitTargetOffsetTop() {
        final int _Offset = mDistanceToTriggerSync - mTarget.getTop();

        mTarget.offsetTopAndBottom(_Offset);
        mHeadView.offsetTopAndBottom(_Offset);
        mCurrentTargetOffsetTop += _Offset;

        mCurrentState.update(State.STATE_READY, mCurrentTargetOffsetTop, mTriggerOffset);
        mLastState.update(State.STATE_READY, mCurrentTargetOffsetTop, mTriggerOffset);
    }

    private void UpdateContentOffsetTop(int TargetTop, boolean ChangeHeightOnly) {
        final int _CurrentTop = mTarget.getTop();
        if (TargetTop < mTargetOriginalTop) {
            TargetTop = mTargetOriginalTop;
        }
        SetTargetOffsetTop(TargetTop - _CurrentTop, ChangeHeightOnly);
    }


    private void SetTargetOffsetTop(int Offset, boolean ChangeHeightOnly) {
        if (Offset == 0) {
            return;
        }

        // check whether the mTarget total top offset is going to be smaller than 0
        if (mCurrentTargetOffsetTop + Offset >= 0) {
            mTarget.offsetTopAndBottom(Offset);
            mHeadView.offsetTopAndBottom(Offset);
            mCurrentTargetOffsetTop += Offset;
            invalidate();
        } else {
            UpdateContentOffsetTop(mTargetOriginalTop, ChangeHeightOnly);
        }
        UpdateHeadViewState(ChangeHeightOnly);
    }

    private void UpdatePositionTimeout(boolean Delayed) {
        removeCallbacks(mCancel);
        if (Delayed && mReturnToOriginalTimeout <= 0) {
            return;
        }
        postDelayed(mCancel, Delayed ? mReturnToOriginalTimeout : 0);
    }

    public void EnableTopProgressBar(boolean Enable) {
        enableTopProgressBar = Enable;
        requestLayout();
    }

    public void SetProgressBarHeight(int Height) {
        mProgressBarHeight = Height;
        mConvertedProgressBarHeight = (int) (getResources().getDisplayMetrics().density * mProgressBarHeight);
    }

    public interface OnRefreshListener {
        void OnRefresh();
    }

    public interface RefreshCheckHandler {
        boolean CanRefresh();
    }

    public interface ScrollUpHandler {
        boolean CanScrollUp(View view);
    }

    public interface ScrollLeftOrRightHandler {
        boolean CanScrollLeftOrRight(View view, int direction);
    }

    public interface CustomSwipeRefreshHeadLayout {
        void OnStateChange(State CurrentState, State LastState);
    }

    public static class State {
        public final static int STATE_NORMAL = 0;
        public final static int STATE_READY = 1;
        public final static int STATE_REFRESHING = 2;
        public final static int STATE_COMPLETE = 3;

        private int refreshState = STATE_NORMAL;
        private float percent;
        private int headerTop;
        private int trigger;

        public State(int refreshState) {
            this.refreshState = refreshState;
        }

        void update(int refreshState, int top, int trigger) {
            this.refreshState = refreshState;
            this.headerTop = top;
            this.trigger = trigger;
            this.percent = (float) top / trigger;
        }

        public int getRefreshState() {
            return refreshState;
        }

        public float getPercent() {
            return percent;
        }

        public String toString() {
            return "[refreshState = " + refreshState + ", percent = " +
                    percent + ", top = " + headerTop + ", trigger = " + trigger + "]";
        }
    }

    private class BaseAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
