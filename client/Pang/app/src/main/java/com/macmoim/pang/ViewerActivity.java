package com.macmoim.pang;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-07-24.
 */
public class ViewerActivity extends ActionBarActivity {
    private static final String TAG = "ViewerActivity";
    private WebView mViewer;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer_main);

        onMaketoolbar();

        mViewer = (WebView) findViewById(R.id.viewer);
        mViewer.setVerticalScrollBarEnabled(true);
        mViewer.getSettings().setJavaScriptEnabled(true);
        mViewer.getSettings().setDefaultTextEncodingName("UTF-8");
        mViewer.setWebChromeClient(new WebChromeClient());

        int id = getIntent().getIntExtra("id", 0);
        showHTML(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showHTML(int id) {

        String url = "http://localhost:8080/web_test/getPost.php";
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("id", String.valueOf(id));

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                url, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    String htmlPath = "";
                    try {
                        htmlPath = response.getString("filepath");
                        htmlPath += response.getString("db_filename");

                        Log.d(TAG, "showHTML path " + htmlPath);
                        ((TextView)findViewById(R.id.post_category)).setText(response.getString("category"));
                        ((TextView)findViewById(R.id.post_title)).setText(response.getString("title"));
                        mViewer.loadUrl(htmlPath);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void onMaketoolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mViewer = null;
        super.onDestroy();

    }
}
