package com.macmoim.pang;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.app.CustomRequest;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by P11872 on 2015-08-31.
 */
public class EditProfileActivity extends ProfileActivity {
    private final String TAG = getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void setFloationAction() {
        //super.setFloationAction();
    }

    @Override
    protected void setAllFocus() {
        nViewHolder = new ViewHolder(this);
        nViewHolder.setviewAllFocus(true);
    }

    @Override
    protected void BackDropSetOnClickListener() {
        ivProfileCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
                intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
                intent.putExtra("outputX", 640);
                intent.putExtra("outputY", 480);
                intent.putExtra("scale", true);
                //retrieve data on return
                intent.putExtra("return-data", false);

                File f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);

                startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit: {
                if (mProfileDbId != -1) {
                    onRequestUpdateData();
                } else {
                    onRequestData();
                }
                return true;
            }
            case android.R.id.home: {
                ShowExitEditorDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ShowExitEditorDialog();
    }

    private void ShowExitEditorDialog() {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Title = getString(R.string.editor_exit_title);
        _Attr.TitleColor = R.color.white_op100;
        _Attr.TitleIcon = R.drawable.ic_pencil;
        _Attr.Message = getString(R.string.editor_exit);
        _Attr.MessageColor = R.color.white_op100;
        _Attr.NegativeButton = getString(R.string.no);
        _Attr.NegativeButtonColor = R.color.white_op100;
        _Attr.PositiveButton = getString(R.string.yes);
        _Attr.PositiveButtonColor = R.color.white_op100;
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                finish();
                super.OnPositive(dialog);
            }

            @Override
            public void OnNegative(ExtDialog dialog) {
                super.OnNegative(dialog);
            }
        };

        ExtDialogSt.Get().AlertExtDialog(this, _Attr);
    }


    protected void onRequestData() {
        Map<String, String> obj = new HashMap<String, String>();
        // temp

        obj.put("user_name", nViewHolder.getName());
        obj.put("user_email", nViewHolder.getEmail());
        obj.put("user_score", nViewHolder.getScore());
        obj.put("user_gender", nViewHolder.getGender());
        obj.put("user_intro", nViewHolder.getIntro());
        obj.put("profile_img_url", mImageURL);

        CustomRequest jsonReq = new CustomRequest(Request.Method.POST,
                _URL_PROFILE, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    showJSONResponseData(response);
                    Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    protected void onRequestUpdateData() {
        Map<String, String> obj = new HashMap<String, String>();
        // temp

        obj.put("user_name", nViewHolder.getName());
        obj.put("user_email", nViewHolder.getEmail());
        obj.put("user_score", nViewHolder.getScore());
        obj.put("user_gender", nViewHolder.getGender());
        obj.put("user_intro", nViewHolder.getIntro());
        obj.put("profile_img_url", mImageURL);

        String url = _URL_PROFILE + "/" + mProfileDbId;

        CustomRequest jsonReq = new CustomRequest(Request.Method.PUT,
                url, obj, new Response.Listener<JSONObject>() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Objects.requireNonNull(response, " response is null");
                    VolleyLog.d(TAG, "Response: " + response.toString());

                    String ret = "";
                    try {
                        ret = response.getString("ret_val");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(ret)) {
//                        showJSONResponseData(response);
                        Toast.makeText(getApplicationContext(), getText(R.string.save), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        //Toast.makeText(getApplicationContext(), getText(R.string.failsave), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.d(TAG, "FeedListView onErrorResponse statusCode = " + response.statusCode + ", data=" + new String(response.data));
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }
}
