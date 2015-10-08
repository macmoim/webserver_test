package com.macmoim.pang.pangeditor;

/**
 * Created by P14983 on 2015-10-05.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.util.Util;
import com.navercorp.volleyextensions.view.ZoomableNetworkImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageViewerFrontPageFragment extends Fragment {

    private static final String TAG = "PageViewerFragment";
    private static String REQ_TAG = "FOOD-REQ";

    private ViewGroup mRoot;
    private ZoomableNetworkImageView mImageView;
    private TextView mTextView;
    private FrontPageItem mPageItem;
    private CircleImageView profilePic;

    public static PageViewerFrontPageFragment GetInstance(int position) {
        //Construct the fragment
        PageViewerFrontPageFragment myFragment = new PageViewerFrontPageFragment();

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

    public void setPageItem(FrontPageItem item) {
        mPageItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_viewer_front_page, container, false);

        mImageView = (ZoomableNetworkImageView) mRoot.findViewById(R.id.page_network_iv);
        profilePic = (CircleImageView) mRoot.findViewById(R.id.profilePic);

        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) mRoot.findViewById(R.id.title_tv)).setText(mPageItem.getTitle());
        mImageView.setImageUrl(Util.IMAGE_FOLDER_URL + mPageItem.getImageUri().toString(), AppController.getInstance().getImageLoader());

        ((TextView) mRoot.findViewById(R.id.like_text)).setText("  " + mPageItem.getLike());

        String score = mPageItem.getStar();
        ((TextView) mRoot.findViewById(R.id.score_text)).setText("  " + (score.equals("null") ? "0" : score));


        String postUserName = mPageItem.getUserName();

        ((TextView) mRoot.findViewById(R.id.user_name_text)).setText(postUserName);


        String profile_img_url = mPageItem.getProfileImgUrl();
        if (profile_img_url != null) {
            Glide.with(profilePic.getContext())
                    .load(profile_img_url)
                    .fitCenter()
                    .into(profilePic);
        } else {
            Glide.with(profilePic.getContext())
                    .load(R.drawable.person)
                    .fitCenter()
                    .into(profilePic);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
