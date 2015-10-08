package com.macmoim.pang.dialog;

import android.content.Context;

import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.dialog.typedef.ListDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;

/**
 * Created by P10452 on 2015-09-05.
 */
public class ExtDialogSt {
    private static ExtDialogSt Singleton;

    public static ExtDialogSt Get(boolean Is) {
        if (Singleton == null && Is) {
            Singleton = new ExtDialogSt();
        }
        return Singleton;
    }

    public static ExtDialogSt Get() {
        return Get(true);
    }

    public void AlertExtDialog(Context Con, AlertDialogAttr Attr) {
        ExtDialog.Builder _Builder = new ExtDialog.Builder(Con);

        _Builder.SetCancelable(Attr.Cancelable);

        if (Attr.DialogBgColor != -1) {
            _Builder.DividerColorRes(Attr.DialogBgColor);
        }

        if (Attr.DividerColor != -1) {
            _Builder.DividerColorRes(Attr.DividerColor);
        }

        if (Attr.Title != null) {
            _Builder.SetTitle(Attr.Title);

            if (Attr.TitleFrameColor != -1) {
                _Builder.TitleFrameColorRes(Attr.TitleFrameColor);
            }

            if (Attr.TitleColor != -1) {
                _Builder.TitleColorRes(Attr.TitleColor);
            }

            if (Attr.TitleIcon != -1) {
                _Builder.TitleIconRes(Attr.TitleIcon);
            }
        }

        if (Attr.Message != null) {
            _Builder.SetMessage(Attr.Message);

            if (Attr.MessageColor != -1) {
                _Builder.MessageColorRes(Attr.MessageColor);
            }
        }

        if (Attr.NegativeButton != null) {
            _Builder.SetNegativeButton(Attr.NegativeButton);

            if (Attr.NegativeButtonColor != -1) {
                _Builder.NegativeColorRes(Attr.NegativeButtonColor);
            }
        }

        if (Attr.PositiveButton != null) {
            _Builder.SetPositiveButton(Attr.PositiveButton);

            if (Attr.PositiveButtonColor != -1) {
                _Builder.PositiveColorRes(Attr.PositiveButtonColor);
            }
        }

        if (Attr.ButtonCB != null) {
            _Builder.CallBack(Attr.ButtonCB);
        }

        _Builder.Show();
    }

    public ExtDialog GetProgressCircleExtDialog(Context Con, ProgressCircleDialogAttr Attr) {
        ExtDialog.Builder _Builder = new ExtDialog.Builder(Con);

        _Builder.SetCancelable(Attr.Cancelable);

        if (Attr.DialogBgColor != -1) {
            _Builder.DividerColorRes(Attr.DialogBgColor);
        }

        if (Attr.DividerColor != -1) {
            _Builder.DividerColorRes(Attr.DividerColor);
        }

        if (Attr.Title != null) {
            _Builder.SetTitle(Attr.Title);

            if (Attr.TitleFrameColor != -1) {
                _Builder.TitleFrameColorRes(Attr.TitleFrameColor);
            }

            if (Attr.TitleColor != -1) {
                _Builder.TitleColorRes(Attr.TitleColor);
            }

            if (Attr.TitleIcon != -1) {
                _Builder.TitleIconRes(Attr.TitleIcon);
            }
        }

        if (Attr.Message != null) {
            _Builder.SetMessage(Attr.Message);

            if (Attr.MessageColor != -1) {
                _Builder.MessageColorRes(Attr.MessageColor);
            }
        }

        if (Attr.WidgetColor != -1) {
            _Builder.WidgetColorRes(Attr.WidgetColor);
        }

        return _Builder
                .Progress(true, 0)
                .Build();
    }

    public void AlertListDialog(Context Con, ListDialogAttr Attr) {
        ExtDialog.Builder _Builder = new ExtDialog.Builder(Con);

        _Builder.SetCancelable(Attr.Cancelable);

        _Builder.SetTitle(Attr.Title)
                .TitleColorRes(Attr.TitleColor)
                .ListItemColorRes(Attr.ListItemColor)
                .ListItems(Attr.ListItems)
                .ListItemsCallback(Attr.ListCB)
                .Show();
    }
}