package info.androidhive.listviewfeed;

import info.androidhive.listviewfeed.app.AppController;
import info.androidhive.listviewfeed.multipart.MultiPartGsonRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

public class MultiVolleyTest extends Activity {

    private Button mUploadBtn;
    private Button mSelectBtn;
    private ImageView mResultImageView;
    
    private File mFilePath;
    
    static final int REQ_CODE_PICK_PICTURE = 1;
    static final String TAG = "MultiVolleyTest";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multivolley_activity);
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        if (mUploadBtn == null) {
            mUploadBtn = (Button) findViewById(R.id.upload_btn);
        }
        mUploadBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "http://localhost:8080/web_test/image_test/image_update_rest.php";
                
                Map<String, String> obj_body = new HashMap<String, String>();
                obj_body.put("title", "multi_volley.jpg");
                
                Map<String, File> obj_file = new HashMap<String, File>();
                obj_file.put("image", mFilePath);
                
                @SuppressWarnings("unchecked")
                MultiPartGsonRequest<JSONObject> jsonReq = new MultiPartGsonRequest(Method.POST,
                        url, JSONObject.class, obj_file, obj_body, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                VolleyLog.d(TAG, "Response: " + response.toString());
                                if (response != null) {
                                    parseJsonFeed(response);
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                            }
                        });

                
                // Adding request to volley request queue
                AppController.getInstance().addHttpStackToRequestQueue(jsonReq);
                
            }
        });

        if (mSelectBtn == null) {
            mSelectBtn = (Button) findViewById(R.id.select_btn);
        }
        mSelectBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.

                // 결과를 리턴하는 Activity 호출
                startActivityForResult(i, REQ_CODE_PICK_PICTURE);
            }
        });
        if (mResultImageView == null) {
            mResultImageView = (ImageView) findViewById(R.id.result_imageview);
        }
    }
    
    private void parseJsonFeed(JSONObject response) {
        String id="";
        try {
             id = response.getString("id");
            Log.d(TAG, "parseJsonFeed insert id " + id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
        
        
        // for test
        String url = "http://localhost:8080/web_test/image_test/image_rest.php";
        String[] arr = {url, id};
        
        new JsonImageParsingTask().execute(arr);
    }
    
    private class JsonImageParsingTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            String response = null;
            ArrayList<String> image_results = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("action", "get_image");
                parameters.put("id", params[1]);
                OutputStream os = conn.getOutputStream();
                os.write(getPostDataString(parameters).getBytes("utf-8"));
                os.flush();
                os.close();
                
                response = conn.getResponseMessage();
                Log.d("RESPONSE", "The response is: " + response);
                
                InputStream           is   = null;
                ByteArrayOutputStream baos = null;
                
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                 
                response = new String(byteData);
                
                Log.d("RESPONSE", "The response string is: " + response);
                
                JSONObject jsonObj = new JSONObject(response);
                image_results.add("http://localhost:8080/web_test/image_test/upload_image/"+jsonObj.getString("image"));
                image_results.add(jsonObj.getString("width"));
                image_results.add(jsonObj.getString("height"));
                 
                Log.i("RESPONSE", "DATA response = " + response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            String addr = image_results.get(0);
            URL image_url;
            HttpURLConnection conn_img = null;
            Bitmap bmImg = null;
            try {
                image_url = new URL(addr);
                conn_img = (HttpURLConnection)image_url.openConnection();
                conn_img.connect();
                InputStream is = conn_img.getInputStream();
                bmImg = BitmapFactory.decodeStream(is); // 스트림을 비트맵으로 변환
                if (Integer.parseInt(image_results.get(1)) > 4096 || Integer.parseInt(image_results.get(2)) > 4096) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmImg.compress(CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bmImg = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                
                conn_img.disconnect();
            }
            
            return bmImg;
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mResultImageView.setImageBitmap(result);
        }
        
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                mFilePath = new File(getRealPathFromURI(getApplicationContext(), data.getData()));
                Log.d(TAG, "onActivityResult filepath " + mFilePath);
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
        return cursor.getString(column_index);
}
    
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mUploadBtn != null) {
            mUploadBtn.setOnClickListener(null);
        }
        if (mSelectBtn != null) {
            mSelectBtn.setOnClickListener(null);
        }
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mUploadBtn = null;
        mSelectBtn = null;
        mResultImageView = null;
    }
}
