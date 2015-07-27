package com.macmoim.pang;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P14983 on 2015-07-24.
 */
public class ViewerActivity extends AppCompatActivity {
    private static final String TAG = "ViewerActivity";
    private WebView mViewer;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer_main);

        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        loadBackdrop();

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
                    String thumbImgPath = "";
                    try {
                        htmlPath = response.getString("filepath");
                        htmlPath += response.getString("db_filename");
                        thumbImgPath = response.getString("thumb_img_path");

                        CollapsingToolbarLayout collapsingToolbar =
                                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                        collapsingToolbar.setTitle(response.getString("title"));

                        Log.d(TAG, "showHTML path " + htmlPath);
//                        ((TextView)findViewById(R.id.post_category)).setText(response.getString("category"));
//                        ((TextView)findViewById(R.id.post_title)).setText(response.getString("title"));
                        mViewer.loadUrl(htmlPath);

                        loadBackdrop(thumbImgPath);

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

    private void loadBackdrop(String url) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
//        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
        try {
            Glide.with(this).load(new URL(url)).centerCrop().into(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

//    class ReadHtmlTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            StringBuffer contents = new StringBuffer("");
//            try {
//                URL url = new URL(params[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setDoOutput(true);
//
//                //connect
//                urlConnection.connect();
//
////                InputStream inputStream = urlConnection.getInputStream();
//                InputStream input = new BufferedInputStream(url.openStream(),
//                        8192);
//
//                // Output stream
//                OutputStream output = new FileOutputStream(getApplicationContext().getFilesDir().toString()
//                        + "temp.jpg");
//
//                //create a buffer...
//                byte data[] = new byte[1024];
//
//                int count;
//                while ((count = input.read(data)) != -1) {
//                    // publishing the progress....
//                    // After this onProgressUpdate will be called
////                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
//
//                    // writing data to file
//                    output.write(data, 0, count);
//                }
//
//                // flushing output
//                output.flush();
//
//                // closing streams
//                output.close();
//                input.close();
//
//            } catch (final MalformedURLException e) {
//                e.printStackTrace();
//            } catch (final IOException e) {
//                e.printStackTrace();
//            } finally {
//
//            }
//            return getApplicationContext().getFilesDir().toString()
//                    + "temp.jpg";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }

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
