package com.macmoim.pang.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<FoodRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<FoodItem> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

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
            mTimeStampTv = (TextView) view
                    .findViewById(R.id.timestamp);
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
        activity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.activity = activity;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, ViewerActivity.class);
                i.putExtra("id", mValues.get(position).getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });

//        Glide.with(holder.mImageView.getContext())
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(holder.mImageView);


        FoodItem item = mValues.get(position);

        holder.mNameTv.setText(item.getName());
        holder.mUserIdTv.setText(item.getUserId());

        holder.mTimeStampTv.setText(item.getTimeStamp());


        // user profile pic
        holder.mImageView.setImageUrl(item.getImge(), imageLoader);


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
