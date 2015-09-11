package com.macmoim.pang.dialog;

/**
 * Created by P10452 on 2015-09-05.
 */
public class ExDialogSt {
    private static ExDialogSt Singleton;

    public static ExDialogSt Get(boolean Null) {
        if (Singleton == null && Null) {
            Singleton = new ExDialogSt();
        }
        return Singleton;
    }

    public static ExDialogSt Get() {
        return Get(true);
    }
}