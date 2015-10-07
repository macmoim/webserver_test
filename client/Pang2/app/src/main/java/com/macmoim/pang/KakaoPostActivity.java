package com.macmoim.pang;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kakao.auth.ErrorResult;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;
import com.macmoim.pang.dialog.typedef.ProgressCircleDialogAttr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by P16018 on 2015-09-24.
 */
public class KakaoPostActivity extends Activity implements View.OnClickListener{
    private final String TAG =getClass().getName();
    final String IMAGE_SCHEME = "image";
    final int MAX_SIZE_ATTACHMENT = 12 * 1024 * 1024;

    private static int RESULT_LOAD_IMAGE = 1;

    private ImageView imageView;
    private EditText editText;
    private Button button;
    String filePath;
    Uri contentUri;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);

        imageView = (ImageView) findViewById(R.id.imageview_thumb);
        button = (Button) findViewById(R.id.button_post);
        button.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.edittext_content);

        contentUri = getIntent().getData();
        setImage();
        AppController.setCurrentActivity(this);
    }

    private void setImage() {

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(contentUri, new String[]{MediaStore.MediaColumns.DATA},
                null, null, null);

        cursor.moveToFirst();
        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        cursor.close();
        Bitmap bitmapSelectedPhoto = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmapSelectedPhoto);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



        } else {
            super.onBackPressed();
        }
    }

    private static String writeStoryImage(final Context context, final Bitmap bitmap) throws IOException {
        final File diskCacheDir = new File(context.getCacheDir(), "story");

        if (!diskCacheDir.exists())
            diskCacheDir.mkdirs();

        final String file = diskCacheDir.getAbsolutePath() + File.separator + "temp_" + System.currentTimeMillis() + ".jpg";

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), 8 * 1024);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return file;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_post) {
            if (contentUri != null) {
                ContentResolver contentResolver = getContentResolver();
                String mimeType = contentResolver.getType(contentUri);
                final File file = new File(filePath);

                if (/*file.getUsableSpace()*/ file.length() > MAX_SIZE_ATTACHMENT) {
                    Toast.makeText(this,
                            R.string.attachment_much_long,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                final List<File> l = new ArrayList<File>();
                l.add(file);
                String storyPostText = editText.getText().toString();

                ProgressCircleDialogAttr _Attr = new ProgressCircleDialogAttr();
                _Attr.Message = getResources().getString(R.string.loading);
                _Attr.MessageColor = R.color.ExtDialogMessageColor;
                _Attr.Cancelable = false;
                final ExtDialog mDialog = ExtDialogSt.Get().GetProgressCircleExtDialog(this, _Attr);
                mDialog.show();

                KakaoStoryService.requestPostPhoto(new StoryResponseCallback<MyStoryInfo>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.d(TAG, errorResult.getErrorMessage());
                        mDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(MyStoryInfo result) {
                        Toast.makeText(KakaoPostActivity.this, "upload success", Toast.LENGTH_LONG).show();
                        file.delete();
                        mDialog.dismiss();
                        CreateDialog();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onNotSignedUp() {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onNotKakaoStoryUser() {
                        mDialog.dismiss();
                    }

                }, l, storyPostText);
            }

        }
    }

    private void CreateDialog() {
        AlertDialogAttr _Attr = new AlertDialogAttr();
        _Attr.Title = getString(R.string.move);
        _Attr.TitleColor = R.color.ExtDialogTitleColor;
        _Attr.TitleIcon = R.drawable.ic_pencil;
        _Attr.Message = getString(R.string.post_upload_comment);
        _Attr.MessageColor = R.color.ExtDialogMessageColor;
        _Attr.NegativeButton = getString(R.string.no);
        _Attr.NegativeButtonColor = R.color.ExtDialogNegativeButtonTextColor;
        _Attr.PositiveButton = getString(R.string.yes);
        _Attr.PositiveButtonColor = R.color.ExtDialogPositiveButtonTextColor;
        _Attr.ButtonCB = new ExtDialog.ButtonCallback() {
            @Override
            public void OnPositive(ExtDialog dialog) {
                Intent intent = new Intent( );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setType("*/*");
                intent.setPackage("com.kakao.story");
                startActivityForResult(intent, 0);
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
}