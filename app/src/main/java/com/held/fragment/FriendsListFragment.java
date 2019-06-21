package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.held.activity.InboxActivity;
import com.held.activity.R;
import com.held.adapters.FriendsAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.ChatHeadResponse;
import com.held.retrofit.response.FriendsResponse;
import com.held.retrofit.response.InboxData;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import com.held.retrofit.response.FriendData;



public class FriendsListFragment extends ParentFragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FriendsAdapter mFriendAdapter;
    private List<InboxData> mFriendList = new ArrayList<>();
    private boolean mIsLastPage, mIsLoading;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long mStart = System.currentTimeMillis();
    private int mLimit = 8;
    private GestureDetector mGestureDetector;

    public static final String TAG = FriendsListFragment.class.getSimpleName();

    public static FriendsListFragment newInstance() {
        return new FriendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.FRIENDLIST_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriendAdapter = new FriendsAdapter((InboxActivity) getCurrActivity(), mFriendList, mIsLastPage);
        mRecyclerView.setAdapter(mFriendAdapter);

        mGestureDetector = new GestureDetector(getCurrActivity(), new GestureListener());

//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FRIENDLIST_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStart = System.currentTimeMillis();
                mIsLastPage = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mFriendList.clear();
                if (getCurrActivity().getNetworkStatus()) {
                    callFriendsListApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet");
                }
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callFriendsListApi();
                }
            }
        });
        if (getCurrActivity().getNetworkStatus()) {
            callFriendsListApi();
        } else {
            UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
        }
    }

    private void callFriendsListApi() {
        mIsLoading = true;
        HeldService.getService().getChatHeadList(PreferenceHelper.getInstance(getCurrActivity())
                .readPreference(getString(R.string.API_session_token)), mLimit, mStart, new Callback<ChatHeadResponse>() {
            @Override
            public void success(ChatHeadResponse chatHeadResponse, Response response) {
                Log.d(TAG, "received friends list succesfully");
                mIsLastPage = chatHeadResponse.isLastPage();
                mFriendList.addAll(chatHeadResponse.getObjects());
                Log.d(TAG, "received no. of friends: " + mFriendList.size());
                mStart = chatHeadResponse.getNextPageStart();
                mFriendAdapter.setFriendList(mFriendList, mIsLastPage);
                mIsLoading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                mIsLoading = false;
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

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ((InboxActivity) getCurrActivity()).onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ((InboxActivity) getCurrActivity()).onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("YourActivity", "Error on gestures");
            }
            return false;
        }
    }
}
