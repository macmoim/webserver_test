package com.macmoim.pang.dialog.typedef;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.macmoim.pang.dialog.ExtDialog;

public class AlertDialogAttr {
    public AlertDialogAttr() {}

    public AlertDialogAttr(@StringRes String title, @ColorRes int titleColor, @DrawableRes int titleIcon,
                           @StringRes String message, @ColorRes int messageColor,
                           @StringRes String negative, @ColorRes int negativeColor,
                           @StringRes String positive, @ColorRes int positiveColor,
                           ExtDialog.ButtonCallback buttonCB) {
        this.Title = title;
        this.TitleColor = titleColor;
        this.TitleIcon = titleIcon;
        this.Message = message;
        this.MessageColor = messageColor;
        this.NegativeButton = negative;
        this.NegativeButtonColor = negativeColor;
        this.PositiveButton = positive;
        this.PositiveButtonColor = positiveColor;
        this.ButtonCB = buttonCB;
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
    @StringRes
    public String NegativeButton = null;
    @ColorRes
    public int NegativeButtonColor = -1;
    @StringRes
    public String PositiveButton = null;
    @ColorRes
    public int PositiveButtonColor = -1;

    public ExtDialog.ButtonCallback ButtonCB = null;
}