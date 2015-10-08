package com.macmoim.pang.pangeditor;

/**
 * Created by P14983 on 2015-10-05.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.macmoim.pang.app.AppController;
import com.macmoim.pang.util.Util;
import com.macmoim.pang.R;
import com.navercorp.volleyextensions.view.ZoomableNetworkImageView;

public class PageViewerFragment extends Fragment {

    private static final String TAG = "PageViewerFragment";
    private static String REQ_TAG = "FOOD-REQ";

    private ViewGroup mRoot;
    private ZoomableNetworkImageView mImageView;
    private TextView mTextView;
    private PageItem mPageItem;

    public static PageViewerFragment GetInstance(int position) {
        //Construct the fragment
        PageViewerFragment myFragment = new PageViewerFragment();

        //New bundle instance
        Bundle args = new Bundle();

        //Passing in the Integer position of the fragment into the argument
        args.putInt("position", position);

        //Setting the argument of the fragment to be the position
        myFragment.setArguments(args);

        REQ_TAG += position;

        //Return the fragment
        return myFragment;
    }

    public void setPageItem(PageItem item) {
        mPageItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_viewer_page, container, false);

        mImageView = (ZoomableNetworkImageView) mRoot.findViewById(R.id.page_network_iv);

        mTextView = (TextView) mRoot.findViewById(R.id.page_content_tv);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mImageView.setImageUrl(Util.IMAGE_FOLDER_URL + mPageItem.getImageUri().toString(), AppController.getInstance().getImageLoader());
        mTextView.setText(mPageItem.getContents());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
