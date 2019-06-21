package com.held.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.ProfileActivity;
import com.held.activity.R;
import com.held.activity.SearchActivity;
import com.held.activity.SeenByActivity;
import com.held.activity.VerificationActivity;
import com.held.customview.PicassoCache;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.EngagersResponse;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.LatestHold;
import com.held.retrofit.response.SeenByData;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import static com.held.utils.Utils.getString;

/**
 * Created by MAHESH on 10/3/2015.
 */
public class SeenByAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ParentActivity mActivity;
    private boolean mIsLastPage;
    private List<Engager> mEngagersList=new ArrayList<Engager>();
    public PreferenceHelper mPreference;
    private String friendUserId;



    public SeenByAdapter(ParentActivity activity,List<Engager> engagerList){
        mActivity = activity;
      //  mIsLastPage = isLastPage;
        mEngagersList=engagerList;
        mPreference=PreferenceHelper.getInstance(mActivity);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_seenby, parent, false);
        //removeCurrentUser();
        Timber.d("created seenby view holder");

        return new SeenByViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        String userName = null,img = null;
        String requestStatus =null;


        Timber.d("on bind viewholder");
        // Timber.d("SeenBy Profile Url"+mEngagersList.get(position).getUser().getProfilePic());
        SeenByViewHolder viewHolder = (SeenByViewHolder) holder;
        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + mEngagersList.get(position).getUser().getProfilePic())
                .into(viewHolder.mProfilePic);

        viewHolder.mUserName.setText(mEngagersList.get(position).getUser().getDisplayName());
        // viewHolder.mButton.setText((CharSequence) mEngagersList.get(position).getFriendshipStatus());
        requestStatus = mEngagersList.get(position).getFriendshipStatus();
        friendUserId=mEngagersList.get(position).getUser().getRid();
        setBtnColor(viewHolder.mButton, requestStatus);

        viewHolder.mProfilePic.setOnClickListener(new  View.OnClickListener (){

            @Override
            public void onClick(View view) {
                launchProfileScreen( mEngagersList.get(position).getUser().getRid());

            }
        });

       /* viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        viewHolder.mButton.setTextColor(mActivity.getResources().getColor(R.color.friend_btn_color));
        /*switch(position){
            case 0:
                userName = "swapnil3";
                img = "/user_thumbnails/swapnil3_1443690679233.jpg";
                requestStatus = "Add as Friend";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.new_btn_color));
                break;
            case 1:
                userName = "vinay123";
                img = "/user_images/swapnil_1443349455353.jpg";
                requestStatus = "Friends";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                viewHolder.mButton.setTextColor(mActivity.getResources().getColor(R.color.friend_btn_color));
                break;
            case 2:
                userName = "swapnil4";
                img = "/user_thumbnails/vinay123_1443851725451.jpg";
                requestStatus = "Request Sent";
                viewHolder.mButton.setBackgroundColor(mActivity.getResources().getColor(R.color.friend_req_color));
                break;

        }*/
            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            Typeface sanBook = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");
            viewHolder.mUserName.setTypeface(medium);
            viewHolder.mButton.setTypeface(sanBook);

       /*/ viewHolder.mUserName.setText(userName);
        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + img)
                .placeholder(R.drawable.user_icon)
                .into(viewHolder.mProfilePic);

        //viewHolder.mButton.setText(requestStatus);*/


    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(mActivity, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
       //todo: fix later
        int count= mEngagersList.size();
        Timber.d("Engager item count in adapter: " + count);
        return count;
       // return 1;

    }



    public void setEngagersList(List<Engager> engagersList) {
        //mActivityDataList = activitySeenList;
        //mEngagersList.clear();
        mEngagersList=engagersList;
       // mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    public static class SeenByViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePic;
        private TextView mUserName;
        private Button mButton;

        public SeenByViewHolder(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.profile_img);
            mUserName = (TextView) itemView.findViewById(R.id.user_name_txt);
            mButton = (Button) itemView.findViewById(R.id.button);

        }
    }

    public void setBtnColor(final Button btn,String reqStatus){
        if(reqStatus.equalsIgnoreCase("none")){
            ///Have to check request status for this for add as friends
            Drawable dr=mActivity.getResources().getDrawable(R.drawable.friendrequest);
            dr.setBounds(50,70,50,70);
            btn.setCompoundDrawablesWithIntrinsicBounds(dr,null,null,null);
            btn.setCompoundDrawablePadding(-30);
            btn.setGravity(Gravity.CENTER);
            btn.setBackgroundColor(mActivity.getResources().getColor(R.color.positve_btn));
            btn.setTextColor(mActivity.getResources().getColor(R.color.white));
            btn.setPadding(20, 0, 0, 0);
            btn.setText("Add as Friend");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //call api here
                    callSendFriendRequestApi();
                    setRequestSendBtn(btn);

                }
            });


        }else if(reqStatus.equalsIgnoreCase("friends")){
            btn.setText("Friends");
            btn.setBackground(mActivity.getResources().getDrawable(R.drawable.button_background));
            btn.setTextColor(mActivity.getResources().getColor(R.color.friend_btn_color));
            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            btn.setTypeface(medium);
        } else if(reqStatus.equalsIgnoreCase("requested")){
            ///Have to check request status for this
            setRequestSendBtn(btn);
        }


    }


    public void callSendFriendRequestApi(){
        HeldService.getService().sendRequests(PreferenceHelper.getInstance(mActivity).readPreference(getString(R.string.API_session_token))
                , friendUserId,"" ,new Callback<FriendRequestResponse>() {
            @Override
            public void success(FriendRequestResponse friendRequestResponse, Response response) {
                Timber.i("Friend Request Sent to :"+friendUserId);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
    public void setRequestSendBtn(Button btn){
        btn.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        btn.setCompoundDrawablePadding(0);
        btn.setPadding(0, 0, 0, 0);
        btn.setBackgroundColor(mActivity.getResources().getColor(R.color.friend_btn_color));
        btn.setTextColor(mActivity.getResources().getColor(R.color.white));
        btn.setText("Request Sent");

    }

}
