package com.macmoim.pang.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;

import com.macmoim.pang.dialog.base.RootLayout;

/**
 * Created by P10452 on 2015-09-05.
 */
public class DialogBase extends Dialog implements DialogInterface.OnShowListener {
    protected RootLayout vExtDialog;
    private OnShowListener mShowListener;

    protected DialogBase(Context context, int theme) {
        super(context, theme);
    }

    protected final void SetOnShowListenerExt() {
        super.setOnShowListener(this);
    }

    protected final void SetContentViewExt(View view) {
        super.setContentView(view);
    }

    @Override
    public View findViewById(int id) {
        return vExtDialog.findViewById(id);
    }

    @Override
    public final void setOnShowListener(OnShowListener listener) {
        mShowListener = listener;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mShowListener != null) {
            mShowListener.onShow(dialog);
        }
    }

    @Override
    @Deprecated
    public void setContentView(int layoutResID) throws IllegalAccessError {
        throw new IllegalAccessError("setContentView() is not supported in ExtDialog. Specify a custom view in the Builder instead.");
    }

    @Override
    @Deprecated
    public void setContentView(View view) throws IllegalAccessError {
        throw new IllegalAccessError("setContentView() is not supported in ExtDialog. Specify a custom view in the Builder instead.");
    }

    @Override
    @Deprecated
    public void setContentView(View view, ViewGroup.LayoutParams params) throws IllegalAccessError {
        throw new IllegalAccessError("setContentView() is not supported in ExtDialog. Specify a custom view in the Builder instead.");
    }
}