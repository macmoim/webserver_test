package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.util.Util;

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

        if (getIntent().getBooleanExtra("is-thumb-img", false)) {
            mThumbImgCb.setChecked(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit: {
                if (contentEt == null || (contentEt != null && "".equals(contentEt.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                }
//                if (mCropImagedUri == null) {
//                    Toast.makeText(getApplicationContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
//                    return true;
//                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("content", contentEt.getText().toString());
                if (mCropImagedUri != null) {
                    returnIntent.putExtra("image-uri", mCropImagedUri.toString());
                    if (!mOldUrl.equals(mCropImagedUri.toString())) {
                        returnIntent.putExtra("old-image-url", mOldUrl);
                    }
                } else {

                }
                returnIntent.putExtra("index", mIndex);
                returnIntent.putExtra("thumb-img-checked", mThumbImgCb.isChecked());
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
