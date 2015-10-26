package com.macmoim.pang.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by P10452 on 2015-07-29.
 */
public class Util {
    private static final String TAG = "Util";

    public static final String SERVER_ROOT = "http://54.65.198.72/web_test";//"http://localhost:8080/web_test";
    public static final String IMAGE_FOLDER_URL = SERVER_ROOT + "/image_test/upload_image/";
    public static final String IMAGE_THUMBNAIL_FOLDER_URL = SERVER_ROOT + "/image_test/thumbnails/";

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
            return filename_arr[last_index - 1];
        } else {
            return imageUrl;
        }
    }

    public static ArrayList<String> splitString(String value, String separator) {
        if (value != null) {
            String[] result = value.split(separator);
            ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(result));
            return stringList;
        } else {
            return null;
        }
    }

    public static void openKeyBoard(Context c) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    //For close keyboard
    public static void closeKeyBoard(Context c) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    public static Uri getUriFromPath(Context context, String path) {
        String fileName = path;//"file:///sdcard/DCIM/Camera/2013_07_07_12345.jpg";
        Uri fileUri = Uri.parse(fileName);
        String filePath = fileUri.getPath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null);
        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) purgeDirectory(file);
            file.delete();
        }
    }

}
