package com.held.adapters;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.ProfileActivity;
import com.held.activity.R;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.User;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 1;
    private ParentActivity mActivity;
    private List<PostChatData> mPostChatData;
    private int lastPosition = -1;
    private GestureDetector mGestureDetector;
    private boolean delayEnterAnimation = true, animationsLocked,mIsLastPage;
    private PreferenceHelper mPreference;
    private User currentUser=null,friendUser=null;
    private String mchatBackUrl;
    private ImageView mChatBackground;

    public ChatAdapter(ParentActivity activity, List<PostChatData> postChatData) {
        mActivity = activity;
        mPostChatData = postChatData;
        mPreference = PreferenceHelper.getInstance(mActivity);
      // mChatBackground=(ImageView)mActivity.findViewById(R.id.background_imageView);
    }


    @Override
    public int getItemViewType(int position) {

        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        int type;
        if(mPostChatData.get(position).getUser()==null) {
            type = (mPostChatData.get(position).getFromUser().getDisplayName()).
                    equals(mPreference.readPreference(Utils.getString(R.string.API_user_name))) ? ITEM_RIGHT : ITEM_LEFT;
        }else {
            type = (mPostChatData.get(position).getUser().getDisplayName()).
                    equals(mPreference.readPreference(Utils.getString(R.string.API_user_name))) ? ITEM_RIGHT : ITEM_LEFT;
        }

        return type;

    }

    @Override
    public int getItemCount() {
        return mPostChatData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v;
//        switch (viewType) {
//            case 0:
        if (viewType == ITEM_RIGHT) {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_right, parent, false);
            return new ViewHolder0(v);
        } else if(viewType == ITEM_LEFT) {
            v = LayoutInflater.from(mActivity).inflate(R.layout.layout_chat_left, parent, false);
            return new ViewHolder2(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ///Get Current user
        String userName=mPreference.readPreference(Utils.getString(R.string.API_user_name));
        if(mPostChatData.get(position).getUser()==null) {
            if (userName.equals(mPostChatData.get(position).getFromUser().getDisplayName())) {
                currentUser = mPostChatData.get(position).getFromUser();
                friendUser = mPostChatData.get(position).getToUser();
            } else if (userName.equals(mPostChatData.get(position).getToUser().getDisplayName()) && mPostChatData.get(position).getUser() == null) {
                currentUser = mPostChatData.get(position).getToUser();
                friendUser = mPostChatData.get(position).getFromUser();
            }
        }
        else {
            if (userName.equals(mPostChatData.get(position).getUser().getDisplayName()))
            {
                currentUser = mPostChatData.get(position).getUser();
            } else if(!userName.equals(mPostChatData.get(position).getUser().getDisplayName())) {
                friendUser = mPostChatData.get(position).getUser();
            }
            /*mchatBackUrl=mPostChatData.get(position).getPost().getImageUri();
            PicassoCache.getPicassoInstance(mActivity)
                    .load(AppConstants.BASE_URL+mchatBackUrl)
                    .into(mChatBackground);*/
        }

        if (holder instanceof ViewHolder0) {
            ViewHolder0 viewHolder0 = (ViewHolder0) holder;
//            viewHolder0.mUserNameTxt.setText(currentUser.getDisplayName());
            String ts = Long.toString(mPostChatData.get(position).getDate());
            viewHolder0.mDateTxt.setText(Utils.convertDate(ts));
            viewHolder0.mDesTxt.setText(mPostChatData.get(position).getText());
            setTypeFace(viewHolder0.mDateTxt, "book");
            setTypeFace(viewHolder0.mDesTxt,"book");
//            Picasso.with(mActivity).load(AppConstants.BASE_URL + currentUser.getProfilePic()).into(viewHolder0.mProfilePic);
        } else if(holder instanceof ViewHolder2) {
            ViewHolder2 viewHolder = (ViewHolder2) holder;
           // viewHolder.mUserNameTxt.setText(friendUser.getDisplayName());
            String ts = Long.toString(mPostChatData.get(position).getDate());
            viewHolder.mDateTxt.setText(Utils.convertDate(ts));
            viewHolder.mDesTxt.setText(mPostChatData.get(position).getText());
            setTypeFace(viewHolder.mDateTxt, "book");
            setTypeFace(viewHolder.mDesTxt,"book");
            Picasso.with(mActivity).load(AppConstants.BASE_URL + friendUser.getProfilePic()).into(viewHolder.mProfilePic);
            viewHolder.mProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchProfileActivity(friendUser);
                }
            });
        }
        runEnterAnimation(holder.itemView, position);
    }

    public void launchProfileActivity(User friend){
        Intent intent = new Intent(mActivity, ProfileActivity.class);
        intent.putExtra("user_id", friend.getRid());
        mActivity.startActivity(intent);
    }

    public void setPostChats(List<PostChatData> postChatData) {

        //mPostChatData.clear();
        mPostChatData=postChatData;
        //this.notifyItemInserted(0);
        this.notifyDataSetChanged();
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView mUserNameTxt, mDesTxt, mDateTxt;
        private ImageView mProfilePic;

        public ViewHolder0(View itemView) {
            super(itemView);
//            mUserNameTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_user_name_txt);
            mDesTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_des_txt);
            mDateTxt = (TextView) itemView.findViewById(R.id.CHATRIGHT_date_txt);
//            mProfilePic = (ImageView) itemView.findViewById(R.id.CHATRIGHT_profile_img);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView mUserNameTxt, mDesTxt, mDateTxt;
        private ImageView mProfilePic;

        public ViewHolder2(View itemView) {
            super(itemView);
//            mUserNameTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_user_name_txt);
            mDesTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_des_txt);
            mDateTxt = (TextView) itemView.findViewById(R.id.CHATLEFT_date_txt);
            mProfilePic = (ImageView) itemView.findViewById(R.id.CHATLEFT_profile_img);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastPosition) {
            lastPosition = position;
            view.setTranslationY(100);
            view.setAlpha(1.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 50 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(1.f))
                    .setDuration(750)

                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    animationsLocked = false;
                                }
                            })
                    .start();
        }
    }
    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
}