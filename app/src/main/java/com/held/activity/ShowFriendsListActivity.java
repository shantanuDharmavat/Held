package com.held.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.adapters.FriendListAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FriendData;
import com.held.retrofit.response.FriendsResponse;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by admin on 13-Feb-16.
 */
public class ShowFriendsListActivity extends ParentActivity{

    private View statusBar;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PreferenceHelper mPreferenceHelper;
    private RecyclerView mFriendListRecyclerView;
    private int mLimit = 100;
    private String mUserId = null;
    private boolean isLastPage;
    private FriendListAdapter mFriendListAdapter;
    private long mStart = System.currentTimeMillis();
    private List<FriendData> mFriendList = new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);
        statusBar = findViewById(R.id.statusBarView);
        Window w = getWindow();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            statusBar.setVisibility(View.VISIBLE);

        }else {
            statusBar.setVisibility(View.GONE);
        }

        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mTitle.setText("Friends");
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mFriendListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFriendListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPreferenceHelper = PreferenceHelper.getInstance(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mFriendListAdapter = new FriendListAdapter(this,mFriendList);
        mFriendListAdapter.setFreindList(mFriendList);
        mFriendListRecyclerView.setAdapter(mFriendListAdapter);
        mUserId = getIntent().getStringExtra("userid");
        Timber.e("USER ID : " + mUserId);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getNetworkStatus()) {
                    isLastPage = false;
                    mFriendList.clear();
                    mStart = System.currentTimeMillis();
                    callFriendListApi(mStart,mUserId);
                }else{
                    UiUtils.showSnackbarToast(mSwipeRefreshLayout,"You are not connected to the internet");
                }
            }
        });
        if (getNetworkStatus()){
            callFriendListApi(mStart,mUserId);
        }else {
            UiUtils.showSnackbarToast(mSwipeRefreshLayout,"You are not connected to the internet");
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(HeldApplication.getAppContext());
        localBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter("notification"));

    }
    public void callFriendListApi(long mStart,String mUserId){
        try{
            HeldService.getService().getFriendsList(mPreferenceHelper.readPreference(getString(R.string.API_session_token)),
                    mLimit,mUserId, mStart, new Callback<FriendsResponse>() {
                @Override
                public void success(FriendsResponse friendsResponse, Response response) {
                    List<FriendData> friendlist = friendsResponse.getObjects();
                    Timber.d(" received friendlist size: " + friendlist.size());
                    mFriendList.addAll(friendlist);
                    mFriendListAdapter.setFreindList(mFriendList);
                    mFriendListAdapter.notifyDataSetChanged();
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showBadge();
        }
    };
}
