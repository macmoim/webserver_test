package com.macmoim.pang.dialog.typedef;

import android.os.Build;
import android.view.Gravity;
import android.view.View;

/**
 * Created by P10452 on 2015-09-05.
 */
public enum GravityEnum {
    START, CENTER, END;

    private static final boolean HAS_OVER_JB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    public int GetGravity() {
        switch (this) {
            case START:
                return HAS_OVER_JB ? Gravity.START : Gravity.LEFT;
            case CENTER:
                return Gravity.CENTER_HORIZONTAL;
            case END:
                return HAS_OVER_JB ? Gravity.END : Gravity.RIGHT;
            default:
                throw new IllegalStateException("Invalid gravity constant");
        }
    }

    public int GetTextAlignment() {
        switch (this) {
            case CENTER:
                return View.TEXT_ALIGNMENT_CENTER;
            case END:
                return View.TEXT_ALIGNMENT_VIEW_END;
            default:
                return View.TEXT_ALIGNMENT_VIEW_START;
        }
    }
}