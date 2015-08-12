package com.macmoim.pang.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macmoim.pang.R;
import com.macmoim.pang.data.FoodCommentItem;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodCommentRecyclerViewAdapter extends RecyclerView.Adapter<FoodCommentRecyclerViewAdapter.ViewHolder> {

    private Activity activity;

    private List<FoodCommentItem> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        //        public final NetworkImageView mImageView;
        public final TextView mCommentTv;
        public final TextView mUserIdTv;
        public final TextView mTimeStampTv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserIdTv = (TextView) view.findViewById(R.id.user_id);
            mTimeStampTv = (TextView) view
                    .findViewById(R.id.timestamp);
            mCommentTv = (TextView) view.findViewById(R.id.comment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserIdTv.getText();
        }
    }

    public FoodCommentItem getValueAt(int position) {
        return mValues.get(position);
    }

    public FoodCommentRecyclerViewAdapter(Activity activity, List<FoodCommentItem> items) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        FoodCommentItem item = mValues.get(position);

        holder.mUserIdTv.setText(item.getCommentUserId());
        holder.mTimeStampTv.setText(item.getTimeStamp());
        holder.mCommentTv.setText(item.getComment());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
