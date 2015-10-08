package com.macmoim.pang.dialog.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.macmoim.pang.R;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.typedef.GravityEnum;
import com.macmoim.pang.dialog.util.Utils;

/**
 * Created by P10452 on 2015-09-05.
 */
public class RootLayout extends ViewGroup {
    private final String TAG = getClass().getName();

    private View mTitleBar;
    private View mContent;

    private static final int INDEX_NEGATIVE = 0;
    private static final int INDEX_POSITIVE = 1;

    private DialogButton[] mButtons = new DialogButton[2];
    private boolean mForceStack = false;
    private boolean mIsStacked = false;
    private int mButtonBarHeight;

    private boolean mDrawTopDivider = false;
    private boolean mDrawBottomDivider = false;
    private boolean mDrawButtonDivider = false;
    private Paint mDividerPaint;
    private int mDividerTopDepth;
    private int mDividerBottomDepth;
    private int mDividerButtonDepth;

    private ViewTreeObserver.OnScrollChangedListener mTopOnScrollChangedListener;
    private ViewTreeObserver.OnScrollChangedListener mBottomOnScrollChangedListener;

    public RootLayout(Context context) {
        super(context);
        Init(context, null, 0);
    }

    public RootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init(context, attrs, defStyleAttr);
    }

    private void Init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources _Res = context.getResources();

        mButtonBarHeight = _Res.getDimensionPixelSize(R.dimen.ext_dialog_button_frame_height);

        mDividerPaint = new Paint();
        mDividerPaint.setColor(Utils.ResolveColor(context, R.attr.ext_dialog_divider_color));
        mDividerTopDepth = _Res.getDimensionPixelSize(R.dimen.ext_dialog_divider_top_depth);
        mDividerBottomDepth = _Res.getDimensionPixelSize(R.dimen.ext_dialog_divider_bottom_depth);
        mDividerButtonDepth = _Res.getDimensionPixelSize(R.dimen.ext_dialog_divider_botton_depth);

        /* must to acted onDraw() */
        setWillNotDraw(false);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0; i < getChildCount(); i++) {
            View _View = getChildAt(i);

            if (_View.getId() == R.id.titleFrame) {
                mTitleBar = _View;
            } else if (_View.getId() == R.id.negative) {
                mButtons[INDEX_NEGATIVE] = (DialogButton) _View;
            } else if (_View.getId() == R.id.positive) {
                mButtons[INDEX_POSITIVE] = (DialogButton) _View;
            } else {
                mContent = _View;
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int _Width = MeasureSpec.getSize(widthMeasureSpec);
        int _Height = MeasureSpec.getSize(heightMeasureSpec);

        boolean _HasButtons = false;

        final boolean _Stacked;

        if (!mForceStack) {
            int _ButtonsWidth = 0;

            for (DialogButton _Button : mButtons) {
                if (_Button != null && IsVisible(_Button)) {
                    _Button.SetStacked(false, false);
                    measureChild(_Button, widthMeasureSpec, heightMeasureSpec);
                    _ButtonsWidth += _Button.getMeasuredWidth();
                    _HasButtons = true;
                }
            }

            _Stacked = _ButtonsWidth > _Width;
        } else {
            _Stacked = true;
        }

        int _StackedHeight = 0;

        mIsStacked = _Stacked;

        if (_Stacked) {
            for (DialogButton button : mButtons) {
                if (button != null && IsVisible(button)) {
                    button.SetStacked(true, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    _StackedHeight += button.getMeasuredHeight();
                    _HasButtons = true;
                }
            }
        }

        int _AvailableHeight = _Height;

        if (_HasButtons) {
            if (mIsStacked) {
                _AvailableHeight -= _StackedHeight;
            } else {
                _AvailableHeight -= mButtonBarHeight;
            }
        }

        if (IsVisible(mTitleBar)) {
            mTitleBar.measure(MeasureSpec.makeMeasureSpec(_Width, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
            _AvailableHeight -= mTitleBar.getMeasuredHeight();
        }

        if (IsVisible(mContent)) {
            mContent.measure(MeasureSpec.makeMeasureSpec(_Width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(_AvailableHeight, MeasureSpec.AT_MOST));

            if (mContent.getMeasuredHeight() <= _AvailableHeight) {
                _AvailableHeight -= mContent.getMeasuredHeight();
            } else {
                _AvailableHeight = 0;
            }
        }

        setMeasuredDimension(_Width, _Height - _AvailableHeight);
    }

    private static boolean IsVisible(View v) {
        boolean _Visible = v != null && v.getVisibility() != View.GONE;

        if (_Visible && v instanceof DialogButton) {
            _Visible = ((DialogButton) v).getText().toString().trim().length() > 0;
        }

        return _Visible;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int _Padding = getResources().getDimensionPixelSize(R.dimen.ext_dialog_divider_stroke_width);

        if (mContent != null) {
            if (mDrawTopDivider) {
                canvas.drawRect(_Padding, mContent.getTop(), getMeasuredWidth() - _Padding, mContent.getTop() + mDividerTopDepth, mDividerPaint);
            }

            if (mDrawBottomDivider) {
                canvas.drawRect(_Padding, mContent.getBottom() - mDividerBottomDepth, getMeasuredWidth() - _Padding, mContent.getBottom(), mDividerPaint);
            }
        }

        if (mButtons != null) {
            if (mIsStacked) {

            } else {
                if (mDrawButtonDivider) {
                    canvas.drawRect(mButtons[INDEX_NEGATIVE].getRight() - (mDividerButtonDepth / 2), mButtons[INDEX_NEGATIVE].getTop(),
                            mButtons[INDEX_NEGATIVE].getRight() + (mDividerButtonDepth / 2), mButtons[INDEX_NEGATIVE].getBottom() - _Padding, mDividerPaint);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, final int l, int t, final int r, int b) {
        if (IsVisible(mTitleBar)) {
            int _Height = mTitleBar.getMeasuredHeight();

            mTitleBar.layout(l, t, r, t + _Height);

            t += _Height;
        }

        if (IsVisible(mContent)) {
            mContent.layout(l, t, r, t + mContent.getMeasuredHeight());
        }

        if (mIsStacked) {
            for (DialogButton mButton : mButtons) {
                if (IsVisible(mButton)) {
                    mButton.layout(l, b - mButton.getMeasuredHeight(), r, b);
                    b -= mButton.getMeasuredHeight();
                }
            }
        } else {
            int _AreaTop;
            int _AreaBottom = b;
            int _AreaCenterX = (r - l) / 2;

            _AreaTop = _AreaBottom - mButtonBarHeight;

            if (IsVisible(mButtons[INDEX_NEGATIVE])) {
                mButtons[INDEX_NEGATIVE].layout(l, _AreaTop, _AreaCenterX, _AreaBottom);
            }

            if (IsVisible(mButtons[INDEX_POSITIVE])) {
                mButtons[INDEX_POSITIVE].layout(_AreaCenterX, _AreaTop, r, _AreaBottom);
            }
        }

        SetDividersVisibility(mContent, true, true);
    }

    public void SetForceStack(boolean forceStack) {
        mForceStack = forceStack;
        invalidate();
    }

    public void SetDividerColor(int color) {
        mDividerPaint.setColor(color);
        invalidate();
    }

    public void SetButtonStackedGravity(GravityEnum gravity) {
        for (DialogButton mButton : mButtons) {
            if (mButton != null) {
                mButton.SetStackedGravity(gravity);
            }
        }
    }

    private void SetDividersVisibility(final View view, final boolean top, final boolean bottom) {
        if (view == null) {
            return;
        }

        if (view instanceof ScrollView) {
            Log.e(TAG, "content area used scroll view");
            final ScrollView sv = (ScrollView) view;

            if (CanScrollViewScroll(sv)) {
                AddScrollListener(sv, top, bottom);
            } else {
                mDrawTopDivider = top;
                mDrawBottomDivider = bottom;
            }
        } else if (view instanceof AdapterView) {
            Log.e(TAG, "content area used Adapter view");
            final AdapterView sv = (AdapterView) view;

            if (CanAdapterViewScroll(sv)) {
                AddScrollListener(sv, top, bottom);
            } else {
                mDrawTopDivider = top;
                mDrawBottomDivider = bottom;
            }
        } else if (view instanceof WebView) {
            Log.e(TAG, "content area used web view");
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (view.getMeasuredHeight() != 0) {
                        if (!CanWebViewScroll((WebView) view)) {
                            mDrawTopDivider = top;
                            mDrawBottomDivider = bottom;
                        } else {
                            AddScrollListener((ViewGroup) view, top, bottom);
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        } else if (view instanceof RecyclerView) {
            Log.e(TAG, "content area used recyler view");
            boolean canScroll = CanRecyclerViewScroll((RecyclerView) view);

            mDrawTopDivider = top;
            mDrawBottomDivider = bottom;
        } else if (view instanceof ViewGroup) {
            Log.e(TAG, "content area used view group");

            View _TopView = GetTopView((ViewGroup) view);
            SetDividersVisibility(_TopView, top, bottom);

            View _BottomView = GetBottomView((ViewGroup) view);
            if (_BottomView != _TopView) {
                SetDividersVisibility(_BottomView, false, true);
            }

            mDrawTopDivider = top;
            mDrawBottomDivider = bottom;
        } else {
            mDrawTopDivider = top;
            mDrawBottomDivider = bottom;
        }

        if (!IsVisible(mTitleBar)) {
            mDrawTopDivider = false;
        }

        if (mIsStacked) {
            boolean _Has = false;
            for (DialogButton mButton : mButtons) {
                if (IsVisible(mButton)) {
                    _Has = true;
                }
            }
            if (!_Has) {
                mDrawBottomDivider = false;
                mDrawButtonDivider = false;
            }
        } else {
            if (IsVisible(mButtons[INDEX_POSITIVE]) || IsVisible(mButtons[INDEX_NEGATIVE])) {
                if (IsVisible(mButtons[INDEX_NEGATIVE])) {
                    mDrawButtonDivider = true;
                }
            } else {
                mDrawBottomDivider = false;
                mDrawButtonDivider = false;
            }
        }
    }

    private void AddScrollListener(final ViewGroup vg, final boolean top, final boolean bottom) {
        if ((!bottom && mTopOnScrollChangedListener == null || (bottom && mBottomOnScrollChangedListener == null))) {
            ViewTreeObserver.OnScrollChangedListener _Listener = new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    boolean _HasButtons = false;
                    for (DialogButton button : mButtons) {
                        if (button != null && button.getVisibility() != View.GONE) {
                            _HasButtons = true;
                            break;
                        }
                    }
                    if (vg instanceof WebView) {
                        InvalidateDividersForWebView((WebView) vg, top, bottom, _HasButtons);
                    } else {
                        InvalidateDividersForScrollingView(vg, top, bottom, _HasButtons);
                    }
                    invalidate();
                }
            };
            if (!bottom) {
                mTopOnScrollChangedListener = _Listener;
                vg.getViewTreeObserver().addOnScrollChangedListener(mTopOnScrollChangedListener);
            } else {
                mBottomOnScrollChangedListener = _Listener;
                vg.getViewTreeObserver().addOnScrollChangedListener(mBottomOnScrollChangedListener);
            }
            _Listener.onScrollChanged();
        }
    }

    private void InvalidateDividersForScrollingView(ViewGroup view, final boolean top, boolean bottom, boolean hasButtons) {
        if (top && view.getChildCount() > 0) {
            mDrawTopDivider = mTitleBar != null && mTitleBar.getVisibility() != View.GONE &&
                    view.getScrollY() + view.getPaddingTop() > view.getChildAt(0).getTop();

        }
        if (bottom && view.getChildCount() > 0) {
            mDrawBottomDivider = hasButtons &&
                    view.getScrollY() + view.getHeight() - view.getPaddingBottom() < view.getChildAt(view.getChildCount() - 1).getBottom();
        }
    }

    private void InvalidateDividersForWebView(WebView view, final boolean top, boolean bottom, boolean hasButtons) {
        if (top) {
            mDrawTopDivider = mTitleBar != null && mTitleBar.getVisibility() != View.GONE &&
                    view.getScrollY() + view.getPaddingTop() > 0;
        }
        if (bottom) {
            //noinspection deprecation
            mDrawBottomDivider = hasButtons &&
                    view.getScrollY() + view.getMeasuredHeight() - view.getPaddingBottom() < view.getContentHeight() * view.getScale();
        }
    }

    public static boolean CanRecyclerViewScroll(RecyclerView view) {
        if (view == null || view.getAdapter() == null || view.getLayoutManager() == null) {
            return false;
        }

        final RecyclerView.LayoutManager _LM = view.getLayoutManager();
        final int _Count = view.getAdapter().getItemCount();
        int _LastVisible;

        if (_LM instanceof LinearLayoutManager) {
            LinearLayoutManager _LLM = (LinearLayoutManager) _LM;
            _LastVisible = _LLM.findLastVisibleItemPosition();
        } else {
            throw new ExtDialog.NotImplementedException("only supports linear layout manager");
        }

        if (_LastVisible == -1) {
            return false;
        }

        /* We scroll if the last item is not visible */
        final boolean _LastItemVisible = _LastVisible == _Count - 1;

        return !_LastItemVisible ||
                (view.getChildCount() > 0 && view.getChildAt(view.getChildCount() - 1).getBottom() > view.getHeight() - view.getPaddingBottom());
    }

    private static boolean CanScrollViewScroll(ScrollView sv) {
        if (sv.getChildCount() == 0) {
            return false;
        }

        final int _ChildHeight = sv.getChildAt(0).getMeasuredHeight();

        return sv.getMeasuredHeight() - sv.getPaddingTop() - sv.getPaddingBottom() < _ChildHeight;
    }

    private static boolean CanWebViewScroll(WebView view) {
        //noinspection deprecation
        return view.getMeasuredHeight() < view.getContentHeight() * view.getScale();
    }

    private static boolean CanAdapterViewScroll(AdapterView lv) {
        /* Force it to layout it's children */
        if (lv.getLastVisiblePosition() == -1) {
            return false;
        }

        /* We can scroll if the first or last item is not visible */
        boolean _FirstItemVisible = lv.getFirstVisiblePosition() == 0;
        boolean _LastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

        if (_FirstItemVisible && _LastItemVisible && lv.getChildCount() > 0) {
            /* Or the first item's top is above or own top */
            if (lv.getChildAt(0).getTop() < lv.getPaddingTop()) {
                return true;
            }

            /* or the last item's bottom is beyond our own bottom */
            return lv.getChildAt(lv.getChildCount() - 1).getBottom() >
                    lv.getHeight() - lv.getPaddingBottom();
        }

        return true;
    }

    @Nullable
    private static View GetBottomView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }

        View _View = null;

        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View _Child = viewGroup.getChildAt(i);

            if (_Child.getVisibility() == View.VISIBLE && _Child.getBottom() == viewGroup.getMeasuredHeight()) {
                _View = _Child;
                break;
            }
        }
        return _View;
    }

    @Nullable
    private static View GetTopView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }

        View _View = null;

        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View _Child = viewGroup.getChildAt(i);

            if (_Child.getVisibility() == View.VISIBLE && _Child.getTop() == 0) {
                _View = _Child;
                break;
            }
        }
        return _View;
    }
}