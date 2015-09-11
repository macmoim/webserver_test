package com.macmoim.pang.dialog.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.typedef.GravityEnum;


/**
 * Created by P10452 on 2015-09-05.
 */
public class Utils {
    public static int AdjustAlpha(int color, @SuppressWarnings("SameParameterValue") float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.argb(alpha, red, green, blue);
    }

    public static int ResolveColor(Context context, @AttrRes int attr) {
        return ResolveColor(context, attr, 0);
    }

    public static int ResolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList ResolveActionTextColorStateList(Context context, @AttrRes int colorAttr, ColorStateList fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{colorAttr});
        try {
            final TypedValue value = a.peekValue(0);
            if (value == null) {
                return fallback;
            }
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return GetActionTextStateList(context, value.data);
            } else {
                final ColorStateList stateList = a.getColorStateList(0);
                if (stateList != null) {
                    return stateList;
                } else {
                    return fallback;
                }
            }
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList GetActionTextColorStateList(Context context, @ColorRes int colorId) {
        final TypedValue value = new TypedValue();
        context.getResources().getValue(colorId, value, true);
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return GetActionTextStateList(context, value.data);
        } else {
            return context.getResources().getColorStateList(colorId);
        }
    }

    public static String ResolveString(Context context, @AttrRes int attr) {
        TypedValue v = new TypedValue();
        context.getTheme().resolveAttribute(attr, v, true);
        return (String) v.string;
    }

    private static int GravityEnumToAttrInt(GravityEnum value) {
        switch (value) {
            case CENTER:
                return 1;
            case END:
                return 2;
            default:
                return 0;
        }
    }

    public static GravityEnum ResolveGravityEnum(Context context, @AttrRes int attr, GravityEnum defaultGravity) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            switch (a.getInt(0, GravityEnumToAttrInt(defaultGravity))) {
                case 1:
                    return GravityEnum.CENTER;
                case 2:
                    return GravityEnum.END;
                default:
                    return GravityEnum.START;
            }
        } finally {
            a.recycle();
        }
    }

    public static Drawable ResolveDrawable(Context context, @AttrRes int attr) {
        return ResolveDrawable(context, attr, null);
    }

    private static Drawable ResolveDrawable(Context context, @AttrRes int attr, @SuppressWarnings("SameParameterValue") Drawable fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            Drawable d = a.getDrawable(0);
            if (d == null && fallback != null)
                d = fallback;
            return d;
        } finally {
            a.recycle();
        }
    }

    public static int ResolveDimension(Context context, @AttrRes int attr) {
        return ResolveDimension(context, attr, -1);
    }

    private static int ResolveDimension(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getDimensionPixelSize(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean ResolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static boolean ResolveBoolean(Context context, @AttrRes int attr) {
        return ResolveBoolean(context, attr, false);
    }

    public static boolean IsColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    public static void SetBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }

    public static void ShowKeyboard(DialogInterface di, final ExtDialog.Builder builder) {
        final ExtDialog dialog = (ExtDialog) di;
        if (dialog.GetInputEditText() == null) return;
        dialog.GetInputEditText().post(new Runnable() {
            @Override
            public void run() {
                dialog.GetInputEditText().requestFocus();
                InputMethodManager imm = (InputMethodManager) builder.GetContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(dialog.GetInputEditText(), InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public static void HideKeyboard(DialogInterface di, final ExtDialog.Builder builder) {
        final ExtDialog dialog = (ExtDialog) di;
        if (dialog.GetInputEditText() == null) return;
        dialog.GetInputEditText().post(new Runnable() {
            @Override
            public void run() {
                dialog.GetInputEditText().requestFocus();
                InputMethodManager imm = (InputMethodManager) builder.GetContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(dialog.GetInputEditText().getWindowToken(), 0);
            }
        });
    }

    public static ColorStateList GetActionTextStateList(Context context, int newPrimaryColor) {
        final int fallBackButtonColor = Utils.ResolveColor(context, android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) newPrimaryColor = fallBackButtonColor;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{} // enabled
        };
        int[] colors = new int[]{
                Utils.AdjustAlpha(newPrimaryColor, 0.4f),
                newPrimaryColor
        };
        return new ColorStateList(states, colors);
    }
}
