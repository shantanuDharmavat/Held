package com.held.adapters;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.InboxActivity;
import com.held.activity.R;
import com.held.retrofit.response.InboxData;
import com.held.utils.AppConstants;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

public class FriendsAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private InboxActivity mActivity;
    private List<InboxData> mFriendList;
    private boolean mIsLastPage;
    private GestureDetector mPersonalGestureDetector;

    public FriendsAdapter(InboxActivity activity, List<InboxData> friendList, boolean isLastPage) {
        mActivity = activity;
        mFriendList = friendList;
        mIsLastPage = isLastPage;
       // mPersonalGestureDetector = new GestureDetector(mActivity, new PersonalChatListener());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.row_friends_list, parent, false);
            return new FriendViewHolder(v);
        } else {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FriendViewHolder) {
            final InboxData friend  = mFriendList.get(position);
            FriendViewHolder viewHolder = (FriendViewHolder) holder;
            String picUrl = AppConstants.BASE_URL + friend.getProfilePic();
            Picasso.with(mActivity).load(picUrl).priority(Picasso.Priority.HIGH).noFade().into(viewHolder.mProfilePic);
            viewHolder.mUserName.setText(friend.getDisplayName());
            viewHolder.mUserDetail.setText(friend.getText());
            String ts = friend.getJoinDate();
            viewHolder.mTimeTxt.setText(Utils.convertDate(ts));

            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");
            viewHolder.mUserName.setTypeface(medium);
            viewHolder.mUserDetail.setTypeface(book);
            viewHolder.mTimeTxt.setTypeface(book);

            /*viewHolder.mContainer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                  //  mUserId = friend.getToUser().getRid();
                    return mPersonalGestureDetector.onTouchEvent(motionEvent);
                }
            });*/
            final String userId = friend.getFromUser().getRid();
            final String username = friend.getFromUser().getDisplayName();
            viewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Timber.d("Sending user id " + userId + " to perform");
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userId);
                    bundle.putString("username", username);
                    mActivity.perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, bundle);

                }
            });



        } else {
            ProgressViewHolder viewHolder = (ProgressViewHolder) holder;
            if (mIsLastPage) {
                viewHolder.mIndicationTxt.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
            } else {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.mIndicationTxt.setVisibility(View.GONE);
                viewHolder.progressBar.setIndeterminate(true);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mFriendList.size() == position ? TYPE_FOOTER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mFriendList.size() + 1;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePic;
        private TextView mUserName, mUserDetail, mTimeTxt;
        private RelativeLayout mContainer;

        public FriendViewHolder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.chat_row_container);
            mProfilePic = (ImageView) itemView.findViewById(R.id.FRIEND_profile_pic);
            mUserName = (TextView) itemView.findViewById(R.id.FRIEND_name);
            mUserDetail = (TextView) itemView.findViewById(R.id.FRIEND_description);
            mTimeTxt = (TextView) itemView.findViewById(R.id.FRIEND_time_txt);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView mIndicationTxt;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mIndicationTxt = (TextView) v.findViewById(R.id.indication_txt);
        }
    }

    public void setFriendList(List<InboxData> friendList, boolean isLastPage) {
        mFriendList = friendList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    /*private class PersonalChatListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Timber.d("Sending user id " + mUserId + " to perform");
            Bundle bundle = new Bundle();
            bundle.putString("user_id", mUserId);
            mActivity.perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, bundle);
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

    }*/
}
