package com.macmoim.pang.dialog;

import android.content.Context;

import com.macmoim.pang.R;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
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

        _Builder.SetTitle(Attr.Title)
                .TitleColorRes(Attr.TitleColor)
                .TitleIconRes(Attr.TitleIcon)
                .SetMessage(Attr.Message)
                .MessageColorRes(Attr.MessageColor)
                .SetNegativeButton(Attr.NegativeButton)
                .NegativeColorRes(Attr.NegativeButtonColor)
                .SetPositiveButton(Attr.PositiveButton)
                .PositiveColorRes(Attr.PositiveButtonColor)
                .CallBack(Attr.ButtonCB)
                .BackgroudDrawble(R.drawable.ext_dialog_bg)
                .DividerColorRes(R.color.mustard_op100)
                .Show();
    }

    public void ProgressCircleExtDialog(Context Con, ProgressCircleDialogAttr Attr) {
        ExtDialog.Builder _Builder = new ExtDialog.Builder(Con);

        _Builder.SetTitle(Attr.Title)
                .TitleColorRes(Attr.TitleColor)
                .TitleIconRes(Attr.TitleIcon)
                .SetMessage(Attr.Message)
                .MessageColorRes(Attr.MessageColor)
                .Progress(true, 0)
                .BackgroudDrawble(R.drawable.ext_dialog_bg)
                .DividerColorRes(R.color.mustard_op100)
                .Show();
    }
}