package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by P14983 on 2015-10-02.
 */
public class EditPageActivity extends AddPageActivity {

    private int mIndex;

    private String mOldUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIndex = getIntent().getIntExtra("index", 0);
        String content = getIntent().getStringExtra("content");
        String imgUrl = getIntent().getStringExtra("img_url");
        mOldUrl = imgUrl;

        contentEt.setText(content);
        ((MyNetworkImageView)mDialogAddPageIv).setImageUrl(Util.IMAGE_FOLDER_URL + imgUrl, AppController.getInstance().getImageLoader());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit: {
                if (contentEt == null || (contentEt != null && "".equals(contentEt.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (mCropImagedUri == null) {
                    Toast.makeText(getApplicationContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("content", contentEt.getText().toString());
                returnIntent.putExtra("image-uri", mCropImagedUri.toString());
                returnIntent.putExtra("index", mIndex);
                if (!mOldUrl.equals(mCropImagedUri.toString())) {
                    returnIntent.putExtra("old-image-url", mOldUrl);
                }
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            }
            case android.R.id.home: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }



}
