package com.held.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.ProfileActivity;
import com.held.activity.R;
import com.held.customview.PicassoCache;
import com.held.retrofit.response.FriendData;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by admin on 13-Feb-16.
 */
public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FriendData> mFreindList = new ArrayList<>();
    private ParentActivity mActivity;
    private PreferenceHelper mPreferenceHelper;

    public FriendListAdapter(ParentActivity mActivity,List<FriendData> mFreindList){
        this.mFreindList = mFreindList;
        this.mActivity = mActivity;
        mPreferenceHelper = PreferenceHelper.getInstance(mActivity);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_seenby,parent,false);
        Timber.d("created friend list view holder");
        return new FriendListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String RequestStatus = null;
        Timber.d("on bind View Holder");
        FriendListViewHolder viewHolder = (FriendListViewHolder) holder;

        PicassoCache.getPicassoInstance(mActivity)
                .load(AppConstants.BASE_URL + mFreindList.get(position).getUser().getProfilePic())//might have a bug at "getFromUser().getProfilePic()"
                .into(viewHolder.mProfilePic);

        viewHolder.mUserName.setText(mFreindList.get(position).getUser().getDisplayName());
        viewHolder.mAcceptButton.setVisibility(View.GONE);

        viewHolder.mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileScreen(mFreindList.get(position).getUser().getRid());
            }
        });

        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        viewHolder.mUserName.setTypeface(medium);
    }

    @Override
    public int getItemCount() {
        return mFreindList.size();
    }

    public void setFreindList(List<FriendData> friendDataList){
        Timber.d("inside friendlist adapter");
        Timber.d("friend list count inside adapter : "+getItemCount());
        mFreindList = friendDataList;
        notifyDataSetChanged();
    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(mActivity, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        mActivity.startActivity(intent);
    }

    public static class FriendListViewHolder extends RecyclerView.ViewHolder{

        private ImageView mProfilePic;
        private TextView mUserName;
        private Button mAcceptButton;

        public FriendListViewHolder(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.profile_img);
            mAcceptButton = (Button) itemView.findViewById(R.id.button);
            mUserName = (TextView) itemView.findViewById(R.id.user_name_txt);
        }
    }
}
