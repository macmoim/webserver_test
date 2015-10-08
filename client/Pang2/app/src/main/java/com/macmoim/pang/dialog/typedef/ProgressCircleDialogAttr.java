package com.macmoim.pang.dialog.typedef;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class ProgressCircleDialogAttr {
    public ProgressCircleDialogAttr() {

    }

    public ProgressCircleDialogAttr(@StringRes String title, @ColorRes int titleColor,
                                    @StringRes String message, @ColorRes int messageColor,
                                    @ColorRes int widgetColor,
                                    boolean cancelable,
                                    int bgColor, @ColorRes int dividerColor, @ColorRes int titleFrameColor) {
        this.Title = title;
        this.TitleColor = titleColor;
        this.Message = message;
        this.MessageColor = messageColor;
        this.WidgetColor = widgetColor;

        this.Cancelable = cancelable;
        this.DialogBgColor = bgColor;
        this.DividerColor = dividerColor;
        this.TitleFrameColor = titleFrameColor;
    }

    @StringRes
    public String Title = null;
    @ColorRes
    public int TitleColor = -1;
    @DrawableRes
    public int TitleIcon = -1;
    @StringRes
    public String Message = null;
    @ColorRes
    public int MessageColor = -1;
    @ColorRes
    public int WidgetColor = -1;

    public boolean Cancelable = true;

    public int DialogBgColor = -1;
    @ColorRes
    public int DividerColor = -1;
    @ColorRes
    public int TitleFrameColor = -1;
}