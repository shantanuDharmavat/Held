package com.held.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.InviteResponse;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.held.utils.Utils.getString;

/**
 * Created by swapnil on 30/4/16.
 */
public class InvitedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ParentActivity mActivity;
    private List<InviteResponse> mInvitedList=new ArrayList<InviteResponse>();
    public PreferenceHelper mPreference;
    private String friendUserId;


    public InvitedAdapter(ParentActivity activity,List<InviteResponse> invitedList){
        mActivity = activity;
        mInvitedList=invitedList;
        mPreference= PreferenceHelper.getInstance(mActivity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_seenby, parent, false);


        return new InviteViewHolder(v);

    }

    public void setInviteList(List<InviteResponse> invitedList){
         this.mInvitedList = invitedList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        InviteViewHolder viewHolder = (InviteViewHolder) holder;
        InviteResponse inviteObj = mInvitedList.get(position);
        viewHolder.mPhone.setText(inviteObj.getPhone());
        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                callInviteDeleteApi(mInvitedList.get(position).getRid());

            }
        });

    }

    @Override
    public int getItemCount() {
        int count= mInvitedList.size();
        return count;

    }

    private class InviteViewHolder extends RecyclerView.ViewHolder {

        private TextView mPhone;
        private Button mDelete;

        public InviteViewHolder(View itemView) {
            super(itemView);
            mPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            mDelete = (Button) itemView.findViewById(R.id.btn_delete);

        }
    }



    public void callInviteDeleteApi(String inviteId){

        HeldService.getService().deleteInvite(PreferenceHelper.getInstance(mActivity).readPreference(getString(R.string.API_session_token))
                , inviteId, new Callback<InviteResponse>() {

            @Override
            public void success(InviteResponse friendRequestResponse, Response response) {

                UiUtils.showSnackbarToast(mActivity.getWindow().getDecorView().getRootView(), "Invite cancelled");

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                UiUtils.showSnackbarToast(mActivity.getWindow().getDecorView().getRootView(), "Unable to cancel Invite");

            }
        });
    }

}
