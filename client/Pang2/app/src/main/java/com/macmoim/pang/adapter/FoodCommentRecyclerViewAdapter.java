package com.macmoim.pang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.macmoim.pang.OtherUserPostActivity;
import com.macmoim.pang.OtherUserProfileActivity;
import com.macmoim.pang.R;
import com.macmoim.pang.data.FoodCommentItem;
import com.macmoim.pang.dialog.ExtDialog;
import com.macmoim.pang.dialog.ExtDialogSt;
import com.macmoim.pang.dialog.typedef.ListDialogAttr;

import java.util.List;

/**
 * Created by P14983 on 2015-07-27.
 */
public class FoodCommentRecyclerViewAdapter extends RecyclerView.Adapter<FoodCommentRecyclerViewAdapter.ViewHolder> {
    private Activity activity;

    private List<FoodCommentItem> mValues;

//    CircleFlatingMenuWithActionView mCf;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        //        public final NetworkImageView mImageView;
        public final TextView mCommentTv;
        public final TextView mUserIdTv;
        public final TextView mTimeStampTv;
        public final ImageView mProfilePic;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfilePic = (ImageView) view.findViewById(R.id.profilePic);
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

        if (item.getProfileImgUrl() != null) {
            Glide.with(holder.mProfilePic.getContext())
                    .load(item.getProfileImgUrl())
                    .fitCenter()
                    .into(holder.mProfilePic);
        } else {
            Glide.with(holder.mProfilePic.getContext())
                    .load(R.drawable.person)
                    .fitCenter()
                    .into(holder.mProfilePic);
        }

        holder.mUserIdTv.setText(item.getCommentUserName());
        holder.mTimeStampTv.setText(item.getTimeStamp());
        holder.mCommentTv.setText(item.getComment());

//        setFloationAction(holder.mProfilePic, item);
        SetProfileViewAction(holder.mProfilePic, item);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void SetProfileViewAction(View actionView, final FoodCommentItem item) {
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialogAttr _Attr = new ListDialogAttr();
                _Attr.Title = activity.getResources().getString(R.string.user_info);
                _Attr.TitleColor = R.color.ExtDialogTitleColor;
                _Attr.ListItems = new CharSequence[]{activity.getResources().getString(R.string.goto_user_profile),
                        activity.getResources().getString(R.string.goto_user_post)};
                _Attr.ListCB = new ExtDialog.ListCallback() {
                    @Override
                    public void OnSelection(ExtDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == 0) {
                            Intent intent = new Intent(activity, OtherUserProfileActivity.class);
                            intent.putExtra("other-user-id", item.getCommentUserId());
                            intent.putExtra("other-user-name", item.getCommentUserName());
                            activity.startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(activity, OtherUserPostActivity.class);
                            intent.putExtra("other-user-id", item.getCommentUserId());
                            intent.putExtra("other-user-name", item.getCommentUserName());
                            activity.startActivity(intent);
                        }
                    }
                };
                _Attr.ListItemColor = R.color.ExtDialogListItemTextColor;

                ExtDialogSt.Get().AlertListDialog(activity, _Attr);
            }
        });

    }

    /*private void setFloationAction(View actionView, final FoodCommentItem item) {
        final int[] id = {R.drawable.person, R.drawable.ic_dashboard};

        mCf = new CircleFlatingMenuWithActionView(activity, actionView);
        mCf.setListener(new CircleFlatingMenu.Listener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if ((int) v.getTag() == R.drawable.person) {
                        mCf.menuClose(false);
                        Intent intent = new Intent(activity, OtherUserProfileActivity.class);
                        intent.putExtra("other-user-id", item.getCommentUserId());
                        intent.putExtra("other-user-name", item.getCommentUserName());
                        activity.startActivity(intent);
                    } else if ((int) v.getTag() == R.drawable.ic_dashboard) {
                        mCf.menuClose(false);
                        Intent intent = new Intent(activity, OtherUserPostActivity.class);
                        intent.putExtra("other-user-id", item.getCommentUserId());
                        intent.putExtra("other-user-name", item.getCommentUserName());
                        activity.startActivity(intent);

                    }

                }
                return true;
            }
        });
        mCf.addResId(id);
        mCf.setItemAngle(0, -90);
        mCf.setItemRadius(activity.getResources().getDimensionPixelSize(R.dimen.radius_medium));
        mCf.setFloationAction();
    }*/
}
