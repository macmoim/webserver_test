package com.macmoim.pang.dialog.typedef;

import android.support.annotation.LayoutRes;

import com.macmoim.pang.R;


/**
 * Created by P10452 on 2015-10-12.
 */
public enum DialogType {
    COMMON, LIST_VIEW, PROGRESS_BAR, PROGRESS_CIRCLE, INPUT, CUSTOM;

    @LayoutRes
    public int GetLayoutId() {
        switch (this) {
            case COMMON:
            default:
                return R.layout.ext_dialog_type_common;
            case CUSTOM:
                return R.layout.ext_dialog_type_custom;
            case LIST_VIEW:
                return R.layout.ext_dialog_type_list;
            case PROGRESS_BAR:
                return R.layout.ext_dialog_type_progress;
            case PROGRESS_CIRCLE:
                return R.layout.ext_dialog_type_progress_circle;
            case INPUT:
                return R.layout.ext_dialog_type_input;
        }
    }
}
