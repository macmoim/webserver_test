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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.macmoim.pang.R;
import com.macmoim.pang.util.Util;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageViewerFrontPageFragment extends Fragment {
    private static final String TAG = "PageViewerFragment";
    private static String REQ_TAG = "FOOD-REQ";

    private ViewGroup mRoot;
    private FrontPageItem mPageItem;
    private CircleImageView profilePic;
    private ImageView TitleIv = null;

    private PageMoveListener mListener;

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

    public void setPageListener(PageMoveListener l) {
        mListener = l;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = (ViewGroup) inflater.inflate(R.layout.fragment_viewer_front_page, container, false);
        profilePic = (CircleImageView) mRoot.findViewById(R.id.profilePic);
        TitleIv = (ImageView) mRoot.findViewById(R.id.title_iv);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            Glide.with(TitleIv.getContext()).load(new URL(Util.IMAGE_FOLDER_URL + mPageItem.getImageUri().toString())).fitCenter().into(TitleIv);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ((TextView) mRoot.findViewById(R.id.title_tv)).setText(mPageItem.getTitle());
        String likeSum = mPageItem.getLike();
        ((TextView) mRoot.findViewById(R.id.like_text)).setText("  " + (likeSum.equals("null") ? "0" : likeSum));

        String score = mPageItem.getStar();
        ((TextView) mRoot.findViewById(R.id.score_text)).setText("  " + (score.equals("null") ? "0" : score));

        String postUserName = mPageItem.getUserName();
        ((TextView) mRoot.findViewById(R.id.user_name_text)).setText(postUserName);
        ((TextView) mRoot.findViewById(R.id.user_email)).setText(mPageItem.getUserEmail());
        String commentSum = mPageItem.getCommentSum();
        ((TextView) mRoot.findViewById(R.id.comments_numbers_tv)).setText((commentSum.equals("null") ? "0" : commentSum) + " comments");
        String pageSum = mPageItem.getPageSum();
        ((TextView) mRoot.findViewById(R.id.contents_numbers_tv)).setText((pageSum.equals("null") ? "0" : pageSum) + " contents");
        ((TextView) mRoot.findViewById(R.id.date_tv)).setText(mPageItem.getUploadDate());

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

        ((ViewGroup) mRoot.findViewById(R.id.start_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnChangePage(getArguments().getInt("position") + 1);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
