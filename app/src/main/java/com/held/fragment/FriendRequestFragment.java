package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.R;
import com.held.adapters.FriendRequestAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FriendRequestObject;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FriendRequestFragment extends ParentFragment {

    public static final String TAG = FriendRequestFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<FriendRequestObject> mFriendRequestList = new ArrayList<>();
    private FriendRequestAdapter mFriendRequestAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 7;
    private boolean mIsLastPage, mIsLoading;
    private PreferenceHelper mPreference;
    private NotificationFragment mNotificationFragment = null;


    public static FriendRequestFragment newInstance() {
        return new FriendRequestFragment();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("friendrequest", "hello");
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FR_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity(), LinearLayoutManager.VERTICAL, false);
        mFriendRequestAdapter = new FriendRequestAdapter(getCurrActivity(), mFriendRequestList, mIsLastPage, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mFriendRequestAdapter);
        mPreference=PreferenceHelper.getInstance(getCurrActivity());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FR_swipe_refresh_layout);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callFriendRequestListApi();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mStart = System.currentTimeMillis();
                mFriendRequestList.clear();
                mIsLastPage = false;
                if (getCurrActivity().getNetworkStatus()) {
                    callFriendRequestListApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });
        if (getCurrActivity().getNetworkStatus()) {
            DialogUtils.resetDialog(getCurrActivity());
            DialogUtils.showProgressBar();
            callFriendRequestListApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
        }
    }

    public void callFriendRequestListApi() {
        mIsLoading = true;
        HeldService.getService().getFriendRequests(mPreference.readPreference(getString(R.string.API_session_token)),
                mLimit, mStart, new Callback<FriendRequestResponse>() {
                    @Override
                    public void success(FriendRequestResponse friendRequestResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        mIsLastPage = friendRequestResponse.isLastPage();
                        mStart = friendRequestResponse.getNextPageStart();
                        mFriendRequestList.addAll(friendRequestResponse.getObjects());
                        mFriendRequestAdapter.setFriendRequestList(mFriendRequestList, mIsLastPage);
                        mIsLoading = false;
                        mFriendRequestAdapter.notifyDataSetChanged();
                        updateFriendCount();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        mIsLoading = false;
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    public void setNotificationFragment(NotificationFragment fr){
        mNotificationFragment = fr;
    }

    public void updateFriendCount(){

        if(mNotificationFragment != null){
            mNotificationFragment.updateFriendCount(mFriendRequestList.size());

        }

    }


    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }
}
