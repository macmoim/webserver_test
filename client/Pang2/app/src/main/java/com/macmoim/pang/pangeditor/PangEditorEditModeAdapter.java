package com.macmoim.pang.pangeditor;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.util.Util;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class PangEditorEditModeAdapter extends RecyclerView.Adapter<PangEditorEditModeAdapter.ViewHolder> {

    private EditClickListener mListener;

    private Activity activity;

    private List<PageItem> mValues;

    public interface EditClickListener {
        void OnEditViewClick(int index);
    }

    public PangEditorEditModeAdapter(Activity activity, List<PageItem> items) {
        mValues = items;
        this.activity = activity;
    }

    public void setListener(EditClickListener l) {
        mListener = l;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mPageNumberTv;
        public final TextView mContentTv;
        public final NetworkImageView mPageImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPageNumberTv = (TextView) view.findViewById(R.id.page_number_tv);
            mPageImage = (NetworkImageView) view
                    .findViewById(R.id.page_image);
            mContentTv = (TextView) view.findViewById(R.id.content_tv);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentTv.getText();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pang_editor_edit_page_item, parent, false);
        return new ViewHolder(view);
    }

    public PageItem getValueAt(int position) {
        return mValues.get(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnEditViewClick(position);
            }
        });


        PageItem item = mValues.get(position);

        holder.mPageNumberTv.setText("page " + String.valueOf(position + 1) + ".");
        holder.mContentTv.setText(item.getContents());
        holder.mPageImage.setImageUrl(Util.IMAGE_FOLDER_URL + item.getImageUri().toString(), AppController.getInstance().getImageLoader());



    }

}
