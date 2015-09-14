package com.macmoim.pang.dialog.typedef;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class ProgressCircleDialogAttr {
    public ProgressCircleDialogAttr() {}

    public ProgressCircleDialogAttr(@StringRes String title, @ColorRes int titleColor,
                                    @StringRes String message, @ColorRes int messageColor) {
        this.Title = title;
        this.TitleColor = titleColor;
        this.Message = message;
        this.MessageColor = messageColor;
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
}