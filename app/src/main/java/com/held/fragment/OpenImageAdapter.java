package com.held.fragment;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.OpenImageActivity;
import com.held.activity.R;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import static com.held.utils.Utils.getString;

/**
 * Created by admin on 22-Feb-16.
 */
public class OpenImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OpenImageActivity mActivity;
    private BlurTransformation mBlurTransformation;
    private GestureDetector mGestureDetector, mPersonalChatDetector;
    private boolean isFullScreenMode = false;
    RelativeLayout myLayout;
    private ImageOpenHolder mImageOpenHolder;
    private String mUserNameForSearch,mImageUri,mImageThumbUri,mUserImgUri,mPostId,mPostText,mUserName;
    private Long mHoldTime;
    private PreferenceHelper mPreference;

    public OpenImageAdapter(OpenImageActivity mActivity, BlurTransformation mBlurTransformation, String mUserName, String mPostId, String mUserImgUri, String mImageUri, String mImageThumbUri, String mPostText, Long mHoldTime){
        this.mActivity = mActivity;
        this.mBlurTransformation = mBlurTransformation;
        this.mUserName = mUserName;
        this.mPostId = mPostId;
        this.mUserImgUri = mUserImgUri;
        this.mImageUri = mImageUri;
        this.mImageThumbUri = mImageThumbUri;
        this.mPostText = mPostText;
        this.mHoldTime = mHoldTime;
        mGestureDetector = new GestureDetector(mActivity,new GestureListener());
        mPreference=PreferenceHelper.getInstance(mActivity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
            View v = LayoutInflater.from(mActivity).inflate(R.layout.activity_open_image,
                    parent, false);
            holder = new ImageOpenHolder(v);
        return holder;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Bundle bundle = new Bundle();

            bundle.putString("id", mPostId);
            bundle.putBoolean("oneToOne", false);

            // bundle.putString("chatBackImg",mPostImgUrl); we have post img url in api
            //bundle.putBoolean("flag",false);//not necessary
            mActivity.perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mActivity.getNetworkStatus()) {
//                Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedList.get(mPosition).getImage()).into(feedViewHolder.mFeedImg);

                mImageOpenHolder.myTimeLayout.setVisibility(View.INVISIBLE);
                mImageOpenHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
                mActivity.showFullImg(AppConstants.BASE_URL + mImageUri);
                if(e.getAction() == MotionEvent.ACTION_UP){
                }
                isFullScreenMode = true;
            } else {
                UiUtils.showSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
            }
            super.onLongPress(e);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ImageOpenHolder mImageOpenHolder = (ImageOpenHolder) holder;

        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + mUserImgUri)
                .placeholder(R.drawable.user_icon)
                .into(mImageOpenHolder.mUserImg);

        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + mImageThumbUri)
                .placeholder(R.drawable.user_icon)
                .into(mImageOpenHolder.mFeedImg);

        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");

        // Set typeface of numbers as Benton Sans medium
        mImageOpenHolder.mTimeMinTxt.setTypeface(medium);
        mImageOpenHolder.mTimeSecTxt.setTypeface(medium);
        mImageOpenHolder.mPersonCount.setTypeface(medium);


        // Set typeface of remaining text to Benton Sans Book
        mImageOpenHolder.mPersonCountTxt.setTypeface(book);
        mImageOpenHolder.mPersonCountTxt2.setTypeface(book);
        mImageOpenHolder.mTimeTxt.setTypeface(book);
        mImageOpenHolder.mTimeTxt2.setTypeface(book);

        //username
        mImageOpenHolder.mUserNameTxt.setTypeface(medium);
        mImageOpenHolder.mFeedTxt.setTypeface(book);
        String selfID = mPreference.readPreference(getString(R.string.API_user_regId));

        /*String  pCount = ;
        int personCount = Integer.parseInt(pCount);

        if(personCount > 1){
            mImageOpenHolder.mPersonCountTxt2.setText(" people");
        }else{
            mImageOpenHolder.mPersonCountTxt2.setText(" person");
        }
        mImageOpenHolder.mPersonCount.setText("" + personCount);*/

        mImageOpenHolder.mFeedTxt.setText(mPostText);
        mImageOpenHolder.mUserNameTxt.setText(mUserName);
        setTimeText(mHoldTime, mImageOpenHolder.mTimeMinTxt, mImageOpenHolder.mTimeSecTxt);
        mImageOpenHolder.mFeedImg.setVisibility(View.VISIBLE);
        myLayout=mImageOpenHolder.myLayout;

        mImageOpenHolder.mFeedImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                      /*  if (mActivity.getNetworkStatus()) {
                            Picasso.with(mActivity).load("http://139.162.1.137/api" + mFeedResponse.getObjects().get(position).getImage()).into(holder.mFeedImg);
                            callHoldApi(mFeedResponse.getObjects().get(position).getRid());
                            holder.mTimeTxt.setVisibility(View.INVISIBLE);
                        } else {
                            UiUtils.owSnackbarToast(mActivity.findViewById(R.id.frag_container), "You are not connected to internet");
                        }*/
//                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
//                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mActivity.showRCView();
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        Picasso.with(mActivity).load(AppConstants.BASE_URL + mImageThumbUri).
                                into(mImageOpenHolder.mFeedImg);
                        mImageOpenHolder.myTimeLayout.setVisibility(View.VISIBLE);
                        if (isFullScreenMode) {

                            isFullScreenMode = false;
                        }

                        mActivity.isBlured = true;
                        mActivity.showToolbar();

                        break;
                }
                //mPostId = mFeedList.get(position).getCreator().getRid();
                return mGestureDetector.onTouchEvent(motionEvent);
            }

        });
    }

    private void setTimeText(long time, TextView textViewMinutes,TextView textViewSeconds) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        //textViewMinutes.setText(String.valueOf(minutes));
        //textViewSeconds.setText(String.valueOf(seconds));
        Integer startCount =  Integer.parseInt(textViewMinutes.getText().toString());
        updateCounter(startCount, minutes, textViewMinutes);
        startCount =  Integer.parseInt(textViewSeconds.getText().toString());
        updateCounter(startCount, seconds, textViewSeconds);
        textViewMinutes.setVisibility(View.VISIBLE);
        textViewSeconds.setVisibility(View.VISIBLE);
    }

    private void updateCounter(Integer startCount, Integer endCount, final TextView tv){
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startCount, endCount);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tv.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(700);
        animator.start();

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ImageOpenHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView mUserNameTxt, mFeedTxt, mTimeMinTxt, mTimeSecTxt;
        public final ImageView mFeedImg, mUserImg;
        public final RelativeLayout myLayout = (RelativeLayout) itemView.findViewById(R.id.BOX_layout);
        public final RelativeLayout myTimeLayout = (RelativeLayout) itemView.findViewById(R.id.time_layout);
        public final LinearLayout myCountLayout = (LinearLayout) itemView.findViewById(R.id.layout_people_count);
        public final TextView mPersonCountTxt = (TextView) itemView.findViewById(R.id.tv_count_people);
        public final TextView mPersonCountTxt2 = (TextView) itemView.findViewById(R.id.tv_count_people2);
        public final TextView mPersonCount = (TextView) itemView.findViewById(R.id.count_hold_people);
        public final TextView mTimeTxt = (TextView) itemView.findViewById(R.id.time_txt);
        public final TextView mTimeTxt2 = (TextView) itemView.findViewById(R.id.time_txt2);
        de.hdodenhof.circleimageview.CircleImageView feedStatusIcon,feedring;

        private ImageOpenHolder(View v) {
            super(v);

            mUserNameTxt = (TextView) v.findViewById(R.id.user_name_txt);
            mFeedImg = (ImageView) v.findViewById(R.id.post_image);
            mUserImg = (ImageView) v.findViewById(R.id.profile_img);
            mFeedTxt = (TextView) v.findViewById(R.id.post_txt);
            mTimeMinTxt = (TextView) itemView.findViewById(R.id.box_min_txt);
            mTimeSecTxt=(TextView) itemView.findViewById(R.id.box_sec_txt);
            myTimeLayout.setVisibility(View.VISIBLE);
            feedStatusIcon=(de.hdodenhof.circleimageview.CircleImageView)v.findViewById(R.id.feed_status_icon);
            feedring=(de.hdodenhof.circleimageview.CircleImageView)v.findViewById(R.id.white_ring);

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
}
