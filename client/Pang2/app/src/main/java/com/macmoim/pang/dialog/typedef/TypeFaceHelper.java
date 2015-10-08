package com.macmoim.pang.dialog.typedef;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.SimpleArrayMap;

public class TypeFaceHelper {
    private static final SimpleArrayMap<String, Typeface> Cache = new SimpleArrayMap<>();

    public static Typeface Get(Context c, String name) {
        synchronized (Cache) {
            if (!Cache.containsKey(name)) {
                try {
                    Typeface _TypeFace = Typeface.createFromAsset(c.getAssets(), String.format("fonts/%s", name));
                    Cache.put(name, _TypeFace);
                    return _TypeFace;
                } catch (RuntimeException e) {
                    return null;
                }
            }
            return Cache.get(name);
        }
    }
}

