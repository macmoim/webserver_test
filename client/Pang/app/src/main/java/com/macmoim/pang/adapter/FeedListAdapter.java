package com.macmoim.pang.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.macmoim.pang.FeedImageView;
import com.macmoim.pang.R;
import com.macmoim.pang.app.AppController;
import com.macmoim.pang.data.FeedItem;

import java.util.List;


public class FeedListAdapter extends BaseAdapter {
	private static final String TAG = "FeedListAdapter";
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;
	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.feed_item, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView user_id = (TextView) convertView.findViewById(R.id.user_id);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);
		TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic);
		FeedImageView feedImageView = (FeedImageView) convertView
				.findViewById(R.id.feedImage1);

		FeedItem item = feedItems.get(position);

		name.setText(item.getName());
		user_id.setText(item.getUserId());

		// Converting timestamp into x ago format
//		CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
//				Long.parseLong(item.getTimeStamp()),
//				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		timestamp.setText(item.getTimeStamp());

		// Chcek for empty status message
//		if (!TextUtils.isEmpty(item.getStatus())) {
//			statusMsg.setText(item.getStatus());
//			statusMsg.setVisibility(View.VISIBLE);
//		} else {
//			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
//		}

		// Checking for null feed url
//		if (item.getUrl() != null) {
//			url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
//					+ item.getUrl() + "</a> "));
//
//			// Making url clickable
//			url.setMovementMethod(LinkMovementMethod.getInstance());
//			url.setVisibility(View.VISIBLE);
//		} else {
			// url is null, remove from the view
			url.setVisibility(View.GONE);
//		}

		// user profile pic
		profilePic.setImageUrl(/*item.getProfilePic()*/item.getImge(), imageLoader);

		// Feed image
//		if (item.getImge() != null) {
//			feedImageView.setImageUrl(/*"http://localhost:8080/web_test/image_test/upload_image/"+*/item.getImge(), imageLoader);
//			feedImageView.setVisibility(View.VISIBLE);
//			feedImageView
//					.setResponseObserver(new FeedImageView.ResponseObserver() {
//						@Override
//						public void onError() {
//						}
//
//						@Override
//						public void onSuccess() {
//						}
//					});
//		} else {
//			feedImageView.setVisibility(View.GONE);
//		}

		return convertView;
	}

}
