package com.macmoim.pang.dialog.typedef;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.macmoim.pang.dialog.ExtDialog;

public class ListDialogAttr {
    public ListDialogAttr() {

    }

    public ListDialogAttr(@StringRes String title, @ColorRes int titleColor, @DrawableRes int titleIcon,
                          @StringRes String message, @ColorRes int messageColor,
                          CharSequence[] listItems, @ColorRes int listItemColor, ExtDialog.ListCallback listCB,
                          boolean cancelable,
                          int bgColor, int dividerColor) {
        this.Title = title;
        this.TitleColor = titleColor;
        this.TitleIcon = titleIcon;
        this.Message = message;
        this.MessageColor = messageColor;
        this.ListItems = listItems;
        this.ListItemColor = listItemColor;
        this.ListCB = listCB;

        this.Cancelable = cancelable;
        this.DialogBgColor = bgColor;
        this.DividerColor = dividerColor;
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

    public CharSequence[] ListItems = null;
    @ColorRes
    public int ListItemColor = -1;

    public ExtDialog.ListCallback ListCB = null;

    public boolean Cancelable = true;

    public int DialogBgColor = -1;

    public int DividerColor = -1;
}