package com.macmoim.pang.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.Layout.swipe.SimpleSwipeListener;
import com.macmoim.pang.Layout.swipe.SwipeLayout;
import com.macmoim.pang.Layout.swipe.adapters.RecyclerSwipeAdapter;
import com.macmoim.pang.LikeActivity;
import com.macmoim.pang.MyPostActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.ViewerActivity;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.util.Util;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class SwipeFoodRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeFoodRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<FoodItem> mValues;

    private Listener mListener;
    private boolean mEnableDelete = false;
    private boolean mEnableLike = false;
    private boolean mSwipeOpen = false;

    public interface Listener {
        public void onDeleteButtonClick(int dbId);

        public void onEditButtonClick(int dbId);

        public void onLikeButtonClick(int dbId);
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public SwipeLayout swipeLayout;
        public final View mView;
        public final NetworkImageView mImageView;
        public final TextView mNameTv;
        public final TextView mUserIdTv;
        public final TextView mTimeStampTv;
        public final ImageView mDeleteBtn;
        public final ImageView mEditBtn;
        public final ImageView mLikeBtn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mImageView = (NetworkImageView) view.findViewById(R.id.profilePic);
            mNameTv = (TextView) view.findViewById(R.id.name);
            mUserIdTv = (TextView) view.findViewById(R.id.user_id);
            mTimeStampTv = (TextView) view
                    .findViewById(R.id.timestamp);
            mDeleteBtn = (ImageView) view.findViewById(R.id.del_btn);
            mEditBtn = (ImageView) view.findViewById(R.id.edit_btn);
            mLikeBtn = (ImageView) view.findViewById(R.id.like_btn2);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameTv.getText();
        }
    }

    public FoodItem getValueAt(int position) {
        return mValues.get(position);
    }

    public SwipeFoodRecyclerViewAdapter(Activity activity, List<FoodItem> items) {
//        activity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.activity = activity;

        if (activity instanceof MyPostActivity) {
            mEnableDelete = true;
        }

        if (activity instanceof LikeActivity) {
            mEnableLike = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
//        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                super.onOpen(layout);
                mSwipeOpen = true;
            }

            @Override
            public void onClose(SwipeLayout layout) {
                //super.onClose(layout);
                mSwipeOpen = false;
                return;
            }
        });
        holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSwipeOpen) {
                    Context context = v.getContext();
                    Intent i = new Intent(context, ViewerActivity.class);
                    i.putExtra("id", mValues.get(position).getId());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(i);
                }
            }
        });

        FoodItem item = mValues.get(position);

//        URL url = null;
//        try {
//            url = new URL(item.getImge());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        if (url != null) {
//            Glide.with(holder.mImageView.getContext())
//                    .load(url)
//                    .fitCenter()
//                    .into(holder.mImageView);
//        } else {
        // user profile pic
        holder.mImageView.setImageUrl(getImageFolderURL() + item.getImge(), imageLoader);
//        }

        holder.mNameTv.setText(item.getName());
        holder.mUserIdTv.setText(item.getUserId());

        holder.mTimeStampTv.setText(item.getTimeStamp());

        final int dbId = item.getId();

        if (mEnableDelete) {
            holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeleteButtonClick(dbId);
                }
            });

            holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEditButtonClick(dbId);
                }
            });
            holder.mLikeBtn.setVisibility(View.GONE);
        } else if (mEnableLike) {
            holder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEditButtonClick(dbId);
                }
            });
            holder.mDeleteBtn.setVisibility(View.GONE);
            holder.mEditBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public String getImageFolderURL() {
        return Util.IMAGE_THUMBNAIL_FOLDER_URL;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
