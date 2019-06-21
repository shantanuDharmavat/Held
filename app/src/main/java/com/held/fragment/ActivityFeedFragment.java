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
import com.held.adapters.ActivityFeedAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ActivityFeedData;
import com.held.retrofit.response.ActivityFeedDataResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ActivityFeedFragment extends ParentFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActivityFeedAdapter mActivityFeedAdapter;
    private List<ActivityFeedData> mActivityFeedDataList = new ArrayList<>();
    private boolean mIsLastPage, mIsLoading = false;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 10;
    private String mUid;
    private NotificationFragment mNotificationFragment;
    private PreferenceHelper mPreference;

    public static final String TAG = ActivityFeedFragment.class.getSimpleName();

    public static ActivityFeedFragment newInstance(String uid) {
        ActivityFeedFragment activityFeedFragment = new ActivityFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        activityFeedFragment.setArguments(bundle);
        return activityFeedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPreference=PreferenceHelper.getInstance(getCurrActivity());
        callActivityFeedApi();
        Log.e("activityfeed", "hello");
        return inflater.inflate(R.layout.fragment_activity_feed, container, false);

    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FEED_rc_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mActivityFeedAdapter = new ActivityFeedAdapter(getCurrActivity(), mActivityFeedDataList, mIsLastPage);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mActivityFeedAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FEED_swipe_refresh_layout);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callActivityFeedApi();
                    mActivityFeedAdapter.notifyDataSetChanged();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getCurrActivity().getNetworkStatus()) {
                    mIsLastPage = false;
                    mActivityFeedDataList.clear();
                    mStart = System.currentTimeMillis();
//                    DialogUtils.showProgressBar();
                    callActivityFeedApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        if (getCurrActivity().getNetworkStatus()) {
            //callSearcUserApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet");
        }
    }


    private void callActivityFeedApi() {

        mIsLoading = true;
        DialogUtils.resetDialog(getCurrActivity());
        DialogUtils.showDarkProgressBar();
        HeldService.getService().getActivitiesFeed(mPreference.readPreference(getString(R.string.API_session_token)), mLimit, mStart,
                new Callback<ActivityFeedDataResponse>() {
            @Override
            public void success(ActivityFeedDataResponse activityFeedDataResponse, Response response) {
                DialogUtils.stopProgressDialog();

                mActivityFeedDataList.addAll(activityFeedDataResponse.getObjects());
                mIsLastPage = activityFeedDataResponse.isLastPage();
                mActivityFeedAdapter.setActivityFeedList(mActivityFeedDataList, mIsLastPage);
                mPreference.writePreference(getString(R.string.API_HELD_COUNT), mActivityFeedDataList.size());
                mStart = activityFeedDataResponse.getNextPageStart();
                mActivityFeedAdapter.setActivityFeedList(mActivityFeedDataList, mIsLastPage);
                mActivityFeedAdapter.notifyDataSetChanged();
                mIsLoading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                mIsLoading = false;
                if (error != null && error.getResponse() != null &&
                        !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                                UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                    if (json.substring(json.indexOf(":") + 2, json.length() - 2).equals("")) {
                    }
                } else
                    UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
            }
        });
    }

    public void setmActivityFeedFragment(NotificationFragment fr){
     mNotificationFragment = fr;
    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }
}
