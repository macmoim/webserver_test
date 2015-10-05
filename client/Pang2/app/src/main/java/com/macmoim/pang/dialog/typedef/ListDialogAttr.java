package com.macmoim.pang.dialog.typedef;

import android.support.annotation.ColorRes;

import com.macmoim.pang.dialog.ExtDialog;

public class ListDialogAttr {
    public ListDialogAttr() {}

    public ListDialogAttr( @ColorRes int item_color,
                           CharSequence[] items,
                           ExtDialog.ListCallback listCB) {
        this.ListItemColor = item_color;
        this.Items = items;
        this.ListCB = listCB;
    }

    public CharSequence[] Items = null;

    public ExtDialog.ListCallback ListCB = null;

    public int ListItemColor = -1;
}