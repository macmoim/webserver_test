package com.macmoim.pang.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.R;
import com.macmoim.pang.ViewerActivity;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.data.FoodItem;
import com.macmoim.pang.pangeditor.ViewerActivity2;
import com.macmoim.pang.util.Util;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<FoodRecyclerViewAdapter.ViewHolder> {
    private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
    protected List<FoodItem> mValues;

    // private Activity activity;
    // private final TypedValue mTypedValue = new TypedValue();
    // private int mBackground;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NetworkImageView mImageView;
        public final TextView mNameTv;
        public final TextView mUserIdTv;
        public final TextView mTimeStampTv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (NetworkImageView) view.findViewById(R.id.profilePic);
            mNameTv = (TextView) view.findViewById(R.id.name);
            mUserIdTv = (TextView) view.findViewById(R.id.user_id);
            mTimeStampTv = (TextView) view.findViewById(R.id.timestamp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameTv.getText();
        }
    }

    public FoodItem getValueAt(int position) {
        return mValues.get(position);
    }

    public FoodRecyclerViewAdapter(Activity activity, List<FoodItem> items) {
//        activity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View _V = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(_V);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, ViewerActivity2.class);
                i.putExtra("id", mValues.get(position).getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
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
        holder.mImageView.setImageUrl(getImageFolderURL() + item.getImge(), mImageLoader);
//        }

        holder.mNameTv.setText(item.getName());
        holder.mUserIdTv.setText(item.getUserName());
        holder.mTimeStampTv.setText(item.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public String getImageFolderURL() {
        return Util.IMAGE_THUMBNAIL_FOLDER_URL;
    }
}
