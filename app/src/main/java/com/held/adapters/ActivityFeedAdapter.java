package com.held.adapters;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.customview.PicassoCache;
import com.held.retrofit.response.ActivityFeedData;
import com.held.retrofit.response.PostData;
import com.held.retrofit.response.User;
import com.held.utils.AppConstants;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okio.Okio;
import timber.log.Timber;

public class ActivityFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
    private List<ActivityFeedData> mActivityFeedDataList;

    private boolean mIsLastPage;

    private static final int TYPE_DOWNLOAD_REQUEST = 0;
    private static final int TYPE_POST_HOLD = 1;
    private static final int TYPE_FRIEND_MESSAGE = 2;
    private static final int TYPE_POST_MESSAGE = 3;
    private static final int TYPE_FOOTER = 4;
    private static final int TYPE_FRIEND_APPROVE = 5;
    private static final int TYPE_FRIEND_DECLINE = 6;



    public ActivityFeedAdapter(ParentActivity activity, List<ActivityFeedData> activityFeedDataList, boolean isLastPage) {
        mActivity = activity;
        mActivityFeedDataList = activityFeedDataList;
        mIsLastPage = isLastPage;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_activity_feed, parent, false);
            return new FeedViewHolder(v);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private void setProfilePic(FeedViewHolder viewHolder, User user){

        viewHolder.mUserImg.setVisibility(View.VISIBLE);
        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + user.getProfilePic())
                .placeholder(R.drawable.user_icon)
                .into(viewHolder.mUserImg);
        viewHolder.mActivityFeedUserName.setText(user.getDisplayName() + ": ");
        ProfileTouchListener listener = new ProfileTouchListener();
        listener.setUser(user);
        viewHolder.mUserImg.setOnTouchListener(listener);
    }

    private void setPostImg(FeedViewHolder viewHolder, PostData postData){

        viewHolder.mPostImg.setVisibility(View.VISIBLE);
        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + postData.getImageUri())
        .into(viewHolder.mPostImg);


    }

    public void launchSeenByScreen(PostData postData){

        Bundle bundle = new Bundle();
        bundle.putString("post_id", postData.getRid());
        mActivity.perform(AppConstants.LAUNCH_SEEN_BY_SCREEN, bundle);
        return;

    }

    public void launchSingleImageScreen(PostData postData){

        Bundle bundle = new Bundle();
        bundle.putString("post_id", postData.getRid());
        mActivity.perform(AppConstants.LAUNCH_SEEN_BY_SCREEN, bundle);
        return;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");


        if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder viewHolder = (ProgressViewHolder) holder;
            if (mIsLastPage) {
                viewHolder.mIndicationTxt.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.GONE);
            } else {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.mIndicationTxt.setVisibility(View.GONE);
                viewHolder.progressBar.setIndeterminate(true);
            }
        } else {
            final FeedViewHolder viewHolder = (FeedViewHolder) holder;
            viewHolder.mActivityFeedUserName.setTypeface(medium);
            viewHolder.mActivityTxt.setTypeface(book);

            final ActivityFeedData actObj = mActivityFeedDataList.get(position);
            String activityType = actObj.getActivityType();
            



            if(activityType.equals("friend:message")){

                viewHolder.mPostImg.setVisibility(View.GONE);
                setProfilePic(viewHolder, actObj.getEntity().getFromUser());
                viewHolder.mActivityTime.setText(getPostTime(Long.parseLong(actObj.getEntity().getDate())));
                viewHolder.mActivityTxt.setText(actObj.getEntity().getText());
                viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();

                        bundle.putString("id", actObj.getEntity().getFromUser().getRid());
                        bundle.putBoolean("oneToOne", true);
                        mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);

                    }
                });

            }
            if(activityType.equals("post:hold")){

                viewHolder.mUserImg.setVisibility(View.VISIBLE);
                viewHolder.mUserImg.setImageResource(R.drawable.logo);
                final PostData curPostData = actObj.getEntity().getPost();
                viewHolder.mUserImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        launchSeenByScreen(curPostData);

                    }
                });
                viewHolder.mActivityFeedUserName.setVisibility(View.GONE);
                setPostImg(viewHolder, actObj.getEntity().getPost());

                String heldTime = setTimeText(actObj.getEntity().getHeld());
                viewHolder.mActivityTxt.setText("Your friend held your post for " + heldTime);
                viewHolder.mActivityTime.setText(getPostTime(actObj.getEntity().getPost().getLatestHold().getDate()));
                MyTouchListener myTouchListener  = new MyTouchListener();
                myTouchListener.setPostData(actObj.getEntity().getPost());
                viewHolder.mPostImg.setOnTouchListener(myTouchListener);

            }
            if (activityType.equals("post:message")){

                setPostImg(viewHolder, actObj.getEntity().getPost());
                setProfilePic(viewHolder, actObj.getEntity().getUser());
                viewHolder.mActivityTxt.setText(actObj.getEntity().getText());
                viewHolder.mActivityTime.setText(getPostTime(actObj.getEntity().getPost().getLatestMessage().getDate()));
                final String rid = actObj.getEntity().getPost().getRid();
                viewHolder.mPostImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();

                        bundle.putString("id", rid );
                        bundle.putBoolean("oneToOne", false);
                        mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);

                    }
                });

            }
            if (activityType.equals("post:download_request")){
                setPostImg(viewHolder, actObj.getEntity().getPost());
                setProfilePic(viewHolder, actObj.getEntity().getFromUser());
                viewHolder.mActivityFeedUserName.setVisibility(View.GONE);
                viewHolder.mActivityTxt.setText("your friend approved your download request");
                viewHolder.mActivityTime.setText(getPostTime(actObj.getEntity().getPost().getLatestMessage().getDate()));
                final String filePath =  actObj.getEntity().getPost().getImageUri();
                viewHolder.mPostImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("Download Image")
                                .setMessage("Save image to gallery?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(filePath).build();
                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                                            @Override
                                            public void onFailure(Request request, IOException e) {
                                                // handle failure
                                            }

                                            @Override
                                            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                                                File outputFile = new File(mActivity.getCacheDir(), "tmp.jpg");
                                                response.body().source().readAll(Okio.sink(outputFile));
                                                ContentValues values = new ContentValues();
                                                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                                values.put(MediaStore.MediaColumns.DATA, outputFile.getAbsolutePath());
                                                mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                            }
                                        });
                                        Toast.makeText(mActivity, "Image saved", Toast.LENGTH_SHORT).show();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });
            }
            if (activityType.equals("friend:approve")){

                viewHolder.mPostImg.setVisibility(View.GONE);
                User u = new User();
                u.setDisplayName(actObj.getEntity().getDisplayName());
                u.setProfilePic(actObj.getEntity().getProfilePic());
                setProfilePic(viewHolder, u);
                viewHolder.mActivityTxt.setText("Your friend approved your friend request");
                viewHolder.mActivityTime.setText(getPostTime(Long.parseLong(actObj.getDate())));
                viewHolder.mActivityFeedUserName.setText(actObj.getEntity().getDisplayName());
            }
        }
    }


    private class ProfileTouchListener implements View.OnTouchListener {

        GestureDetector mPersonalChatDetector;

        public void setUser(User user){

        PersonalChatListener chatListener = new PersonalChatListener();
        chatListener.setUser(user);
        mPersonalChatDetector = new GestureDetector(mActivity, chatListener);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if(mPersonalChatDetector != null)
                return mPersonalChatDetector.onTouchEvent(motionEvent);
            else
                return false;
        }
    }

    private class MyTouchListener implements View.OnTouchListener {

           GestureDetector mGestureDetector;


        public void setPostData(PostData postData){
            GestureListener listener =  new GestureListener();
            listener.setPost(postData);
            mGestureDetector = new GestureDetector(mActivity, listener);

        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mGestureDetector!=null)
                return mGestureDetector.onTouchEvent(motionEvent);
            else
                return false;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        PostData mPost;

        public void setPost(PostData post){
            mPost = post;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Bundle bundle = new Bundle();

            bundle.putString("id", mPost.getRid());
            bundle.putBoolean("oneToOne", false);


            mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Bundle bundle = new Bundle();
            bundle.putString("postimg", mPost.getImageUri());
            bundle.putString("userimg", mPost.getCreator().getProfilePic());
            bundle.putString("postid", mPost.getRid());
            bundle.putLong("postheld", mPost.getHeld());
            bundle.putString("posttext", mPost.getText());
            bundle.putString("username", mPost.getCreator().getDisplayName());
            bundle.putString("postimgthumb",mPost.getThumbnailUri());
            bundle.putString("postholdcount",mPost.getViews());
            Timber.e("postholdcount : "+mPost.getViews());

            mActivity.perform(AppConstants.LAUNCH_INDIVIDUAL_IMAGE_SCREEN,bundle);
            return super.onSingleTapConfirmed(e);
        }
    }

    private class PersonalChatListener extends GestureDetector.SimpleOnGestureListener {

        User mUser;

        public void setUser(User user){
            mUser = user;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Bundle bundle = new Bundle();
            bundle.putString("id", mUser.getRid());
            bundle.putString("username", mUser.getDisplayName());
            bundle.putBoolean("oneToOne",true);
            mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Bundle bundle = new Bundle();
            Timber.i("User Id" + mUser.getRid());
            bundle.putString("user_id", mUser.getRid());
            // bundle.putString("userImg", AppConstants.BASE_URL + mFeedList.get(mPosition).getThumbnailUri());
            mActivity.perform(AppConstants.LAUNCH_PROFILE_SCREEN, bundle);

            return true;
        }

    }

    public void setActivityFeedList(List<ActivityFeedData> activityFeedList, boolean isLastPage) {
        mActivityFeedDataList = activityFeedList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mActivityFeedDataList.size()){
            return TYPE_FOOTER;
        }

        ActivityFeedData obj = mActivityFeedDataList.get(position);
        switch(obj.getActivityType()){
            case "post:download_request":
                return TYPE_DOWNLOAD_REQUEST;

            case "friend:message":
                return TYPE_FRIEND_MESSAGE;

            case "post:hold":
                return TYPE_POST_HOLD;

            case "post:message":
                return TYPE_POST_MESSAGE;

            case "friend:approve":
                return TYPE_FRIEND_APPROVE;

        }
        return TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {
        return mActivityFeedDataList.size() + 1;
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mLayout;
        private ImageView mPostImg, mUserImg;
        private TextView mActivityFeedUserName, mActivityTxt, mActivityTime;

        public FeedViewHolder(View itemView) {
            super(itemView);
            mPostImg = (ImageView) itemView.findViewById(R.id.post_pic);
            mActivityFeedUserName = (TextView) itemView.findViewById(R.id.user_name_txt);
            mUserImg = (ImageView) itemView.findViewById(R.id.user_profile_pic);
            mActivityTxt = (TextView) itemView.findViewById(R.id.activity_msg_txt);
            mActivityTime = (TextView) itemView.findViewById(R.id.activity_post_time);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.row_download_request);
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
    private String setTimeText(long time) {
        String mtime = "";
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        if(hours >0 ){
            mtime = "" + hours+" hours ";
        }
        if(minutes>0){
            mtime += "" + minutes + " minutes ";
        }
        if(seconds > 0){
            mtime += "" + seconds + " seconds";
        }
        mtime += ".";
        return mtime;
    }

    private String getPostTime(long time){
        long actualTime,currentTime = System.currentTimeMillis();
        actualTime = currentTime-time;
        Timber.e("DATE POST : " + time);

        Timber.e("DATE CURRENT: " + currentTime);

        Timber.e("DATE ACTUAL: " + actualTime);

        String mtime = "";
        int seconds = (int) (actualTime / 1000) % 60;
        int minutes = (int) ((actualTime / (1000 * 60)) % 60);
        int hours = (int) ((actualTime / (1000 * 60 * 60)) % 24);
        if(hours > 0 && hours < 24){
            mtime = "" + hours+" hours ago";
        }
        else if(minutes>0){
            mtime += "" + minutes + " minutes ago";
        }
        else if(seconds > 0){
            mtime += "" + seconds + " seconds ago";
        }
        else if (hours > 24){
            mtime += "" + " yesterday";
        }
        mtime += ".";
        Timber.e("DATE Final: " + mtime);

        return mtime;
    }
}
