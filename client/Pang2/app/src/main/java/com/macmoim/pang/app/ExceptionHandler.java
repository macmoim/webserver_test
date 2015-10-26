package com.macmoim.pang.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.macmoim.pang.util.Util;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-10-23.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";
    private final String URL_LOG = Util.SERVER_ROOT + "/send_log";


    Thread.UncaughtExceptionHandler defaultUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "catch uncaught exception");
        ex.printStackTrace();
        handleUncaughtException(thread, ex);
        defaultUncaughtHandler.uncaughtException(thread, ex);
    }

    public ExceptionHandler() {

    }

    private void handleUncaughtException(Thread thread, Throwable e) {




        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = extractLogToFile(); // code not shown
                sendToServer(file);
            }
        }).start();



//        killProcess();
    }

    private void sendToServer(final File file) {
        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("log", "log");


        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("logfile", file);


        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                URL_LOG, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                } else {
                    VolleyLog.d(TAG, "Error: response is null!!!!");
                }

                deleteFile(file);

//                killProcess();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "insertPost requestError : " + error.getMessage());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "PangEditor insert post onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }

            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            //this wont be executed
            Log.d(TAG, "deleteFile " + file.getAbsolutePath());
            file.delete();
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }



    private File extractLogToFile() {
        PackageManager manager = AppController.getInstance().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(AppController.getInstance().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        Log.d(TAG, "extractLogFile model " + model);
        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = Environment.getExternalStorageDirectory() + "/" + "smtc/";
        File smtcFolder = new File(path);
        if (!smtcFolder.exists()) {
            boolean result = smtcFolder.mkdirs();
            Log.d(TAG, "zzolcover folder .mkdirs() : " + result);
        } else {
            // at first, clean directory
            Util.purgeDirectory(smtcFolder);
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = "_log_" + dateformat.format(new Date());
        String fullName = path + filename;

        Log.d(TAG, "extractLogFile file name " + fullName);

        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output. In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ? "logcat -d -v threadtime MyApp:v dalvikvm:v System.err:v *:s"
                    : "logcat -d -v threadtime";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                }

            // You might want to write a failure message to the log here.
            return null;
        }

        return file;
    }

    private void killProcess() {
        Log.d(TAG, "killProcess");
//        defaultUncaughtHandler.uncaughtException(mThread, mE);
//        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
