package com.macmoim.pang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.multipart.MultiPartGsonRequest;
import com.macmoim.pang.richeditor.RichEditor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by P11872 on 2015-07-21.
 */
public class PangEditorActivity extends AppCompatActivity {
    private static final String TAG = "PangEditorActivity";
    private RichEditor mEditor = null;
    private Spinner mSpinner;
    private String mSelectedFood;
    private EditText mTitleEdit;

    private ArrayList<String> mImageUrlArr;
    private ProgressDialog mDialog;

    private Toolbar mToolbar;


    static final int REQ_CODE_PICK_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ediotr_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mEditor = (RichEditor) findViewById(R.id.editor);

        mEditor.setEditorHeight(200);
        mEditor.setPlaceholder("Insert text here...");

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });


        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mEditor.insertImage(
//                        "http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//                        "dachshund");

                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(MediaStore.Images.Media.CONTENT_TYPE);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.


                startActivityForResult(i, REQ_CODE_PICK_PICTURE);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUrlArr == null || (mImageUrlArr != null && mImageUrlArr.size() == 0)) {
                    Toast.makeText(getApplicationContext(), "이미지를 추가해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mTitleEdit.getText() == null || "".equals(mTitleEdit.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "titleedit " +mTitleEdit.getText().toString());
                showDialog();
                File file = saveHTML(mEditor.getHtml());

                String url = "http://localhost:8080/web_test/putHTML.php";

                Map<String, String> obj_body = new HashMap<String, String>();
                obj_body.put("title", mTitleEdit.getText().toString());
                obj_body.put("category", mSelectedFood);
                obj_body.put("thumb_img_url", mImageUrlArr != null ? mImageUrlArr.get(0) : "");

                Map<String, File> obj_file = new HashMap<String, File>();
                obj_file.put("html_file", file);

                @SuppressWarnings("unchecked")
                MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                        url, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            parseJsonHtml(response);
                        }else{
                            VolleyLog.d(TAG, "Error: response is null!!!!");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "requestError : " + error.getMessage());
                    }
                });


                // Adding request to volley request queue
                AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
            }
        });

        mTitleEdit = (EditText) findViewById(R.id.title);
        final String[] spinnerArr = getResources().getStringArray(R.array.food_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArr);
        adapter.setDropDownViewResource(R.layout.food_spinner_item);
        mSpinner = (Spinner) findViewById(R.id.food_spinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedFood = spinnerArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (getIntent().getBooleanExtra("edit", false)) {
            int id = getIntent().getIntExtra("id", 0);
            if (id > 0) {
                editHTML(id);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {

                File filePath = new File(getRealPathFromURI(getApplicationContext(), data.getData()));

                new ResizeBitmapTask().execute(filePath);
            }
        }


    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    private void parseJson(JSONObject response) {
        String url="";
        String fileName="";
        int width=0;
        int height=0;
        try {
            url = "http://localhost:8080/web_test/image_test/upload_image/"+response.getString("file_url");
            fileName = response.getString("file_url");
            width = response.getInt("width");
            height = response.getInt("height");
            Log.d(TAG, "parseJsonFeed upload url " + url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "upload success width " + width +" height " + height, Toast.LENGTH_SHORT).show();


//        mEditor.insertImage(url,"food");
        float ratio = 0f;
        int fixedWidth = 0;
        int fixedHeight = 0;
        if (width > height) {
            ratio = (float)width/(float)height;
            fixedHeight = 100;
            fixedWidth =(int) (fixedHeight * ratio);

        } else {
            ratio = (float)height/(float)width;
            fixedWidth = 100;
            fixedHeight =(int) (fixedWidth * ratio);
        }


        mEditor.insertImage(url,"food", fixedWidth, fixedHeight);

        if (mImageUrlArr == null) {
            mImageUrlArr = new ArrayList<>();
        }
        mImageUrlArr.add(fileName);

    }

    private void parseJsonHtml(JSONObject response) {
        String id="";
        try {
            id = response.getString("id");
            Log.d(TAG, "parseJsonFeed html upload id " + id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        removeDialog();
        Toast.makeText(getApplicationContext(), "html upload success " + id, Toast.LENGTH_SHORT).show();
    }

    private File saveHTML(String htmlText) {
        String dirPath = getFilesDir().getAbsolutePath();
        File file = new File(dirPath);
        if( !file.exists() ) {
            file.mkdirs();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File savefile = new File(dirPath+"/test.html");
        try{
            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(htmlText.getBytes());
            fos.close();
            Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
        } catch(IOException e){
            e.printStackTrace();
        }

        return savefile;
    }

    private void editHTML(int id) {

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

                        new ReadHtmlTask().execute(htmlPath);

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

    class ReadHtmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer contents = new StringBuffer("");
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //connect
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    contents.append(new String(buffer, 0, bufferLength));
                }

            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return contents.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mEditor.setHtml(s);
            removeDialog();
        }
    }

    class ResizeBitmapTask extends AsyncTask<File, Void, File> {

        @Override
        protected File doInBackground(File... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            long fileSize = params[0].length();
            if (fileSize > 2*1024*1024) {
                options.inSampleSize = 4;
            } else if (fileSize < 700*1024) {

            } else {
                options.inSampleSize = 2;
            }

            Bitmap bitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath(), options);

            String dirPath = getFilesDir().getAbsolutePath();
            File file = new File(dirPath);
            if( !file.exists() ) {
                file.mkdirs();
            }

            File savefile = new File(dirPath+"/temp");
            OutputStream out = null;
            try
            {
                savefile.createNewFile();
                out = new FileOutputStream(savefile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return savefile;
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            Log.d(TAG, "requestHTML path " + s.getAbsolutePath() + " size " + s.length());
            requestHTML(s);
        }
    }

    private void requestHTML(File filePath) {
        String url = "http://localhost:8080/web_test/putImage.php";

        Map<String, String> obj_body = new HashMap<String, String>();
        obj_body.put("title", "editor_image.jpg");

        Map<String, File> obj_file = new HashMap<String, File>();
        obj_file.put("image", filePath);

        @SuppressWarnings("unchecked")
        MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Request.Method.POST,
                url, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJson(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "requestError : " + error.getMessage());
            }
        });


        // Adding request to volley request queue
        AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
    }

    private void showDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        } else {
            mDialog = new ProgressDialog(this);
        }

        mDialog.show();

    }

    private void removeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mEditor.destroy();
        mEditor = null;
        mImageUrlArr = null;
        mSpinner.setOnItemSelectedListener(null);
        mSpinner = null;
        mTitleEdit = null;
        super.onDestroy();

    }
}
