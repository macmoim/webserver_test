package com.macmoim.pang;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.util.Util;

import java.util.ArrayList;

/**
 * Created by P14983 on 2015-08-20.
 */
public class SelectThumbImageDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ArrayList<String> mUrls;
    private ArrayList<ImageView> mImageViewArr;

    private LinearLayout mImageLayout;
    private Listener mListener;
    private int mSelectedIndex = 0;

    private static final String URL_IMAGE_FOLDER = Util.SERVER_ROOT + "/image_test/upload_image/";

    public interface Listener {
        public void onSeletedThumbnail(String url);
    }

    public SelectThumbImageDialog(Context context, ArrayList<String> urls) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        mUrls = urls;
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
//        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        lpWindow.gravity = Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(lpWindow);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(R.layout.sel_thumb_dialog);
        mImageLayout = (LinearLayout) findViewById(R.id.image_layout);
        ((ImageButton) findViewById(R.id.thumb_ok_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSeletedThumbnail(mUrls.get(mSelectedIndex));
                dismiss();
            }
        });

        mImageViewArr = new ArrayList<>();
        setupImageView();
    }

    public void setUrls(ArrayList<String> urls) {
        mUrls = urls;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    private void setupImageView() {
        for (String url : mUrls) {
            NetworkImageView iv = new NetworkImageView(mContext);
            iv.setLayoutParams(new ViewGroup.LayoutParams(400, 400));
            iv.setOnClickListener(this);
            iv.setImageUrl(URL_IMAGE_FOLDER + url, AppController.getInstance().getImageLoader());
            mImageLayout.addView(iv);
            mImageViewArr.add(iv);
        }

        mImageViewArr.get(0).setAlpha(0.2f);
    }

    @Override
    public void onClick(View v) {
        mSelectedIndex = mImageViewArr.indexOf(v);
        for (ImageView view : mImageViewArr) {
            if (view == v) {
                view.setAlpha(0.2f);
            } else {
                view.setAlpha(1f);
            }
        }

    }

    @Override
    public void dismiss() {
        if (mImageViewArr != null) {
            mImageViewArr.clear();
            mImageViewArr = null;
        }
//        if (mUrls != null) {
//            mUrls.clear();
//            mUrls = null;
//        }
        mImageLayout = null;
        mListener = null;
        mContext = null;
        super.dismiss();
    }
}
