package com.held.adapters;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.fragment.FriendRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ApproveFriendResponse;
import com.held.retrofit.response.DeclineFriendResponse;
import com.held.retrofit.response.FriendRequestObject;
import com.held.retrofit.response.UnDeclineFriendResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class FriendRequestAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private ParentActivity mActivity;
    private List<FriendRequestObject> mFriendRequestList;
    private boolean mIsLastPage;
    private FriendRequestFragment mFriendRequestFragment;
    private PreferenceHelper mPreference;


    public FriendRequestAdapter(ParentActivity activity, List<FriendRequestObject> friendRequestList, boolean isLastPage, FriendRequestFragment friendRequestFragment) {
        mActivity = activity;
        mFriendRequestList = friendRequestList;
        mIsLastPage = isLastPage;
        mFriendRequestFragment = friendRequestFragment;
        mPreference=PreferenceHelper.getInstance(mActivity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.row_friend_request, parent, false);
            return new FriendRequestViewHolder(v);
        } else {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_progress_bar, parent, false);
            return new ProgressViewHolder(v);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String friendReqId;
        if (holder instanceof FriendRequestViewHolder) {

            FriendRequestViewHolder viewHolder = (FriendRequestViewHolder) holder;

            Picasso.with(mActivity)
                    .load(AppConstants
                            .BASE_URL + mFriendRequestList
                            .get(position)
                            .getFromUser()
                            .getProfilePic())
                    .placeholder(R.drawable.user_icon)
                    .into(viewHolder.mProfileImg);
            viewHolder.mUserNameTxt.setText(mFriendRequestList.get(position).getFromUser().getDisplayName());


            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            Typeface book = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansBook.otf");
            viewHolder.mUserNameTxt.setTypeface(medium);
            viewHolder.mAcceptBtn.setTypeface(book);
            viewHolder.mDeleteBtn.setTypeface(book);

            friendReqId=mFriendRequestList.get(position).getRid();
            viewHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showDarkProgressBar();
                        callAcceptFriendRequestApi(friendReqId);
                    } else
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                }
            });
            viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showDarkProgressBar();
                        callDeclinedFriendRequestApi(mFriendRequestList.get(position).getRid());
                    } else {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                    }
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

    private void callDeclinedFriendRequestApi(final String friendReqId) {//PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_session_token))
        HeldService.getService().declineFriend(mPreference.readPreference(mActivity.getString(R.string.API_session_token)), friendReqId,
                "true", "", "", new Callback<DeclineFriendResponse>() {
                    @Override
                    public void success(DeclineFriendResponse declineFriendResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Friend request deleted.");
                        for (int i = 0; i < mFriendRequestList.size(); i++) {
                            FriendRequestObject friendReq = mFriendRequestList.get(i);
                            if (friendReq.getRid().equals(friendReqId)) {
                                mFriendRequestList.remove(i);
                                break;
                            }
                        }
                        notifyDataSetChanged();
                        mFriendRequestFragment.updateFriendCount();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }

    private void callAcceptFriendRequestApi(final String friendReqId) {
        HeldService.getService().approveFriend(mPreference.readPreference(mActivity.getString(R.string.API_session_token)),
                friendReqId,"","true","" ,new Callback<ApproveFriendResponse>() {
                    @Override
                    public void success(ApproveFriendResponse approveFriendResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        String fromUser = "";
                        for(int i=0; i< mFriendRequestList.size(); i++){
                            FriendRequestObject friendReq = mFriendRequestList.get(i);
                            if(friendReq.getRid().equals(friendReqId)){

                                fromUser = friendReq.getFromUser().getDisplayName();
                                mFriendRequestList.remove(i);
                                break;
                            }
                        }
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are now friends with " + fromUser);
                        notifyDataSetChanged();
                        mFriendRequestFragment.updateFriendCount();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }

    /*private void callUndeclinedApi(final String friendReqId) {
        HeldService.getService().undeclineFriend(PreferenceHelper.getInstance(mActivity).readPreference(mActivity.getString(R.string.API_session_token)),
                friendReqId, new Callback<UnDeclineFriendResponse>() {
                    @Override
                    public void success(UnDeclineFriendResponse unDeclineFriendResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        if (mActivity.getNetworkStatus()) {
                            DialogUtils.showProgressBar();
                            callAcceptFriendRequestApi(friendReqId);
                        } else
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        callAcceptFriendRequestApi(friendReqId);
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }

*/
    @Override
    public int getItemCount() {
        return mFriendRequestList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mFriendRequestList.size() == position ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void setFriendRequestList(List<FriendRequestObject> friendRequestList, boolean isLastPage) {
        mFriendRequestList = friendRequestList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        ImageView mProfileImg;
        TextView mUserNameTxt;
        Button mAcceptBtn, mDeleteBtn;
        public final RelativeLayout myRequestLayout = (RelativeLayout) itemView.findViewById(R.id.row_friend_request);
        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            mProfileImg = (ImageView) itemView.findViewById(R.id.user_profile_pic);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.user_name_txt);
            mAcceptBtn = (Button) itemView.findViewById(R.id.acceptBtn);
            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
            myRequestLayout.setPadding(0,5,0,5);
        }
    }
}
