package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.macmoim.pang.OtherUserPostActivity;
import com.macmoim.pang.OtherUserProfileActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.data.FoodCommentItem;
import com.macmoim.pang.dialog.ExtDialog;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class PangEditorAdapter extends RecyclerView.Adapter<PangEditorAdapter.ViewHolder> {

    private Activity activity;

    private List<PageItem> mValues;

//    CircleFlatingMenuWithActionView mCf;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mPageNumberTv;
        public final TextView mContentTv;
        public final ImageView mPageImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPageNumberTv = (TextView) view.findViewById(R.id.page_number_tv);
            mPageImage = (ImageView) view
                    .findViewById(R.id.page_image);
            mContentTv = (TextView) view.findViewById(R.id.content_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentTv.getText();
        }
    }

    public PageItem getValueAt(int position) {
        return mValues.get(position);
    }

    public PangEditorAdapter(Activity activity, List<PageItem> items) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pang_editor_page_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        PageItem item = mValues.get(position);

        holder.mPageNumberTv.setText("page " + String.valueOf(position + 1) + ".");
        holder.mContentTv.setText(item.getContents());
        holder.mPageImage.setImageURI(item.getImageUri());


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
