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
import android.widget.TextView;

import com.held.activity.NotificationActivity;
import com.held.activity.R;
import com.held.adapters.DownloadRequestAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.DownloadRequestListResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class DownloadRequestFragment extends ParentFragment {

    public static final String TAG = DownloadRequestFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<DownloadRequestData> mDownloadRequestList = new ArrayList<>();
    private DownloadRequestAdapter mDownloadRequestAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 7,count;
    private NotificationFragment mNotificationFragment;
    private boolean mIsLastPage, mIsLoading;
    private PreferenceHelper mPreference;
    private TextView downReqCountTxt;

    public static DownloadRequestFragment newInstance() {
        return new DownloadRequestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("download", "hello");
        return inflater.inflate(R.layout.fragment_download_request, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.DR_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity(), LinearLayoutManager.VERTICAL, false);
        mDownloadRequestAdapter = new DownloadRequestAdapter(getCurrActivity(), mDownloadRequestList, mIsLastPage, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDownloadRequestAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.DR_swipe_refresh_layout);
        mPreference=PreferenceHelper.getInstance(getCurrActivity());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    // mStart=System.currentTimeMillis();
                    callDownloadRequestListApi();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mStart = System.currentTimeMillis();
                mDownloadRequestList.clear();
                mIsLastPage = false;
                if (getCurrActivity().getNetworkStatus()) {
                    callDownloadRequestListApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });
        if(getCurrActivity().getNetworkStatus()){
            callDownloadRequestListApi();
        }else{
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    public void callDownloadRequestListApi() {
        mIsLoading = true;
        mStart=System.currentTimeMillis();
        HeldService.getService().getDownLoadRequestList(mPreference.readPreference(getString(R.string.API_session_token)),
                mLimit, mStart, new Callback<DownloadRequestListResponse>() {
                    @Override
                    public void success(DownloadRequestListResponse downloadRequestListResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        mIsLastPage = downloadRequestListResponse.isLastPage();
                        mStart = downloadRequestListResponse.getNextPageStart();
                        mDownloadRequestList.addAll(downloadRequestListResponse.getObjects());
                        removeAcceptedRequest();
                        mDownloadRequestAdapter.setDownloadRequestList(mDownloadRequestList, mIsLastPage);
                        mIsLoading = false;
                        mPreference.writePreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), mDownloadRequestList.size());
                        mDownloadRequestAdapter.notifyDataSetChanged();


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

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }

    public void setDownloadFragment(NotificationFragment nf){
        mNotificationFragment = nf;
    }

    public void removeAcceptedRequest(){
        List<DownloadRequestData> mTempList = new ArrayList<>();
        mTempList=mDownloadRequestList;
        if(mDownloadRequestList.size()<=0)
            return;
        try{
            for(int i=0;i<=mDownloadRequestList.size();i++)
            {
                if(mTempList.get(i).getCanDownload())
                {
                    mTempList.remove(i);
                    Timber.i("User Removed");
                    break;
                }
            }
            mDownloadRequestList=mTempList;
        }catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
    }
}
