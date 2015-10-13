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
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.AlertDialogAttr;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by P14983 on 2015-10-02.
 */
public class AddPageActivity extends AppCompatActivity {

    static final String TAG = "AddPageActivity";
    protected ImageView mDialogAddPageIv;
    protected EditText contentEt;
    protected Button submitBtn;

    protected Uri mCropImagedUri;

    static final int REQ_CODE_PICK_PICTURE = 1;
    static final int REQ_CODE_TAKE_PHOTO = 2;
    static final int REQ_CODE_CROP = 3;

    protected static final int PROFILE_IMAGE_ASPECT_X = 4;
    protected static final int PROFILE_IMAGE_ASPECT_Y = 3;
    protected int PROFILE_IMAGE_OUTPUT_X;
    protected int PROFILE_IMAGE_OUTPUT_Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_add_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mDialogAddPageIv = (MyNetworkImageView) findViewById(R.id.page_dialog_imageview);
        mDialogAddPageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowChangeImageActionDialog();
            }
        });

        contentEt = (EditText) findViewById(R.id.page_dialog_content_et);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_add_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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
//        super.onBackPressed();
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

    private void ShowChangeImageActionDialog() {
        String[] dialogItems = {getResources().getString(R.string.capture_image), getResources().getString(R.string.select_image)};
        ExtDialog.Builder dialogBuilder = new ExtDialog.Builder(this);
        ExtDialog dialog = dialogBuilder.ListItems(dialogItems).ListItemsCallback(new ExtDialog.ListCallback() {
            @Override
            public void OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text) {
                if (which == 0) {
                    dispatchTakePictureIntent();
                } else if (which == 1) {
                    dispatchPickIntent();
                }
            }
        }).SetTitle(getResources().getString(R.string.change_image_action))//.BackgroundColor(getResources().getColor(R.color.mustard_op70))
                .ListItemColor(getResources().getColor(R.color.white_op100))
                .TitleColor(getResources().getColor(R.color.white_op100))
                .Build();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();

                File f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);

                dispatchCropIntent(selectedImageUri);
            }
        } else if (requestCode == REQ_CODE_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (mCropImagedUri == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                dispatchCropIntent(mCropImagedUri);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.capture_error), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (requestCode == REQ_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                mCropImagedUri = data.getData();

                mDialogAddPageIv.setImageURI(mCropImagedUri);



//                File mFile = new File(mCropImagedUri.getPath());
//                if (mProfileDbId != -1) {
//                    requestUpdateThumbImage(mFile);
//                } else {
//                    requestThumbImage(mFile);
//                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.crop_error), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void dispatchPickIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
        //retrieve data on return
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File f = createNewFile("CROP_");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Log.e("io", ex.getMessage());
            }

            if (f != null) {
                mCropImagedUri = Uri.fromFile(f);
                // Continue only if the File was successfully created
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mCropImagedUri);

                startActivityForResult(takePictureIntent, REQ_CODE_TAKE_PHOTO);
            }

        }
    }

    private void dispatchCropIntent(Uri imageCaptureUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        PROFILE_IMAGE_OUTPUT_X =  screenSize.x;// / 2;//mEditor.getWidth() / 2;
        PROFILE_IMAGE_OUTPUT_Y = PROFILE_IMAGE_OUTPUT_X * 3 / 4;
        intent.setDataAndType(imageCaptureUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", PROFILE_IMAGE_ASPECT_X);
        intent.putExtra("aspectY", PROFILE_IMAGE_ASPECT_Y);
        intent.putExtra("outputX", PROFILE_IMAGE_OUTPUT_X);
        intent.putExtra("outputY", PROFILE_IMAGE_OUTPUT_Y);
        intent.putExtra("scale", true);
        //retrieve data on return
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);

        startActivityForResult(intent, REQ_CODE_CROP);
    }

    private File createNewFile(String prefix) {
        if (prefix == null || "".equalsIgnoreCase(prefix)) {
            prefix = "IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory() + "/smtc/");
        if (!newDirectory.exists()) {
            if (newDirectory.mkdir()) {
                Log.d(getApplicationContext().getClass().getName(), newDirectory.getAbsolutePath() + " directory created");
            }
        }
        Calendar c = Calendar.getInstance();
        int mseconds = c.get(Calendar.MILLISECOND);
        File file = new File(newDirectory, (prefix + "crop_temp_"+String.valueOf(mseconds)+".jpg"));
        if (file.exists()) {
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }


}
