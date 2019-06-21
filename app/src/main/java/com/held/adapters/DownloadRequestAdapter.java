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

import com.held.activity.NotificationActivity;
import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.fragment.DownloadRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ApproveDownloadResponse;
import com.held.retrofit.response.DeclineDownloadResponse;
import com.held.retrofit.response.DownloadRequestData;
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

public class DownloadRequestAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private ParentActivity mActivity;
    private List<DownloadRequestData> mDownloadRequestList;
    private boolean mIsLastPage = true;
    private DownloadRequestFragment mDownloadRequestFragment;
    private PreferenceHelper mPreference;

    public DownloadRequestAdapter(ParentActivity activity, List<DownloadRequestData> DownloadRequestList, boolean isLastPage, DownloadRequestFragment downloadRequestFragment) {
        mActivity = activity;
        mDownloadRequestList = DownloadRequestList;
        mIsLastPage = isLastPage;
        mDownloadRequestFragment = downloadRequestFragment;
        mPreference=PreferenceHelper.getInstance(mActivity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.row_download_request, parent, false);
            return new DownloadRequestViewHolder(v);
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
        if (holder instanceof DownloadRequestViewHolder) {

            DownloadRequestViewHolder viewHolder = (DownloadRequestViewHolder) holder;

            Picasso.with(mActivity).load(AppConstants.BASE_URL + mDownloadRequestList.get(position).getUser().getProfilePic()).into(viewHolder.mProfileImg);
            viewHolder.mUserNameTxt.setText(mDownloadRequestList.get(position).getUser().getDisplayName());
            Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
            viewHolder.mUserNameTxt.setTypeface(medium);
            Picasso.with(mActivity).load(AppConstants.BASE_URL + mDownloadRequestList.get(position).getPost().getThumbnailUri()).into(viewHolder.mPostimg);
            viewHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callApproveDownloadApi(mDownloadRequestList.get(position).getRid(),mDownloadRequestList.get(position).getPost().getRid());
                    } else {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                    }
                }
            });
            viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity.getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callDeclineDownloadApi(mDownloadRequestList.get(position).getRid(),mDownloadRequestList.get(position).getPost().getRid());
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

    private void callDeclineDownloadApi(final String requestId, final String post_id) {
        HeldService.getService().declineDownloadRequest(mPreference.readPreference(mActivity.getString(R.string.API_session_token)),
                post_id,requestId,"true","","", new Callback<DeclineDownloadResponse>() {
            @Override
            public void success(DeclineDownloadResponse declineDownloadResponse, Response response) {
                DialogUtils.stopProgressDialog();

                callDeleteDownloadReqApi(requestId,post_id);
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

    private void callApproveDownloadApi(String requestId,String post_id) {
        HeldService.getService().approveDownloadRequest(mPreference.readPreference(mActivity.getString(R.string.API_session_token)),
                post_id, requestId, "", "true", "", new Callback<ApproveDownloadResponse>() {
                    @Override
                    public void success(ApproveDownloadResponse approveDownloadResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        mDownloadRequestList.clear();
                        // long start = System.currentTimeMillis();
                        mDownloadRequestFragment.callDownloadRequestListApi();
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


    void callDeleteDownloadReqApi(String requestId,String post_id){
        HeldService.getService().deleteDownloadRequest(mPreference.readPreference(mActivity.getString(R.string.API_session_token)),
                post_id, requestId, new Callback<DeclineDownloadResponse>() {
                    @Override
                    public void success(DeclineDownloadResponse declineDownloadResponse, Response response) {
                        mDownloadRequestList.clear();
                        mDownloadRequestFragment.callDownloadRequestListApi();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }


    @Override
    public int getItemCount() {
        return mDownloadRequestList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mDownloadRequestList.size() == position ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void setDownloadRequestList(List<DownloadRequestData> downloadRequestList, boolean isLastPage) {
        mDownloadRequestList = downloadRequestList;
        mIsLastPage = isLastPage;
        notifyDataSetChanged();
    }

    public static class DownloadRequestViewHolder extends RecyclerView.ViewHolder {

        ImageView mProfileImg,mPostimg;
        TextView mUserNameTxt;
        Button mAcceptBtn, mDeleteBtn;
        public final RelativeLayout myRequestLayout = (RelativeLayout) itemView.findViewById(R.id.row_download_request);
        public DownloadRequestViewHolder(View itemView) {
            super(itemView);
            mProfileImg = (ImageView) itemView.findViewById(R.id.user_profile_pic);
            mUserNameTxt = (TextView) itemView.findViewById(R.id.user_name_txt);
            mAcceptBtn = (Button) itemView.findViewById(R.id.acceptBtn);
            mDeleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
            mPostimg=(ImageView) itemView.findViewById(R.id.post_pic);
            myRequestLayout.setPadding(0,7,0,0);

        }
    }
}