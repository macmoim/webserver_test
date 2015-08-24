package com.macmoim.pang.util;

/**
 * Created by P10452 on 2015-07-29.
 */
public class Util {
    private static final String TAG = "Util";

    public static final String IMAGE_FOLDER_URL = "http://localhost:8080/web_test/image_test/upload_image/";

    public static String MakeStringBuilder(final String... str) {
        String _Str = null;
        StringBuilder _StrBui = null;

        if (str == null) {
            return _Str;
        }

        if (str.length == 1) {
            _Str = str[0];
            return _Str;
        }

        _StrBui = new StringBuilder();

        for (String s : str) {
            _StrBui.append(s);
        }

        _Str = _StrBui.toString();

        _StrBui = null;

        return _Str;
    }

    public static String splitFilename(String imageUrl) {
        if (imageUrl.contains("http")) {
            String[] filename_arr = imageUrl.split("/");
            int last_index = filename_arr.length;
            return filename_arr[last_index-1];
        } else {
            return imageUrl;
        }
    }
}
