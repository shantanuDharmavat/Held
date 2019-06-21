package com.held.activity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.adapters.SeenByAdapter;
import com.held.retrofit.response.Engager;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by swapnil on 6/2/16.
 */
public class FriendsListActivity extends ParentActivity{

    private ImageView mChat, mCamera, mNotification;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SeenByAdapter mSeenByAdapter;
    // private List<String []> mList = new ArrayList<>();
    private RecyclerView mSeenRecyclerView;
    private boolean isLastPage,isLoading;
    private LinearLayoutManager mLayoutManager;
    private String username="",img="";
    private PreferenceHelper mPreference;;
    private ArrayList<String[]> mList;
    private String mPostId;
    private int mlimit=10;
    private List<Engager> mFriendsList=new ArrayList<Engager>();
    private List<Engager> tempList=new ArrayList<Engager>();
    private String mUserId;
    private int mLimit = 10;
    private PreferenceHelper mPrefernce;
    long nextPage=0;
    View statusBar;
    private long mStart = System.currentTimeMillis();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);
        statusBar=(View)findViewById(R.id.statusBarView);
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
        // mLayoutManager = new LinearLayoutManager(this);
        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mCamera.setVisibility(View.GONE);
        mSearch_edt.setVisibility(View.GONE);

        mChat.setImageResource(R.drawable.back);
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mChat.setVisibility(View.VISIBLE);

        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mTitle.setText("Friends");
        //setToolbar();
        mPostId=getIntent().getExtras().getString("post_id");
        mPreference=PreferenceHelper.getInstance(this);
        mSeenRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSeenRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mPrefernce=PreferenceHelper.getInstance(this);
        Timber.d("setting seen by adapter");
        mSeenByAdapter = new SeenByAdapter(this,mFriendsList);
        mSeenByAdapter.setEngagersList(mFriendsList);
        mSeenRecyclerView.setAdapter(mSeenByAdapter);

        // removeCurrentUser();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getNetworkStatus()) {
                    isLastPage = false;
                    mFriendsList.clear();
                    mStart = System.currentTimeMillis();
//                    DialogUtils.showProgressBar();
                    mStart = System.currentTimeMillis();
                    callFriendsListApi(mStart);
                } else {
                    UiUtils.showSnackbarToast(mSwipeRefreshLayout, "You are not connected to internet.");
                }
            }
        });

        if (getNetworkStatus()) {
//            DialogUtils.showProgressBar();
            callFriendsListApi(mStart);
        } else {
            UiUtils.showSnackbarToast(mSwipeRefreshLayout, "Sorry! You don't seem to connected to internet");
        }

    }


    private void callFriendsListApi(long startVal) {
        isLoading = true;
        /*if (getNetworkStatus()) {

            HeldService.getService().getFriendsList(mPrefernce.readPreference(getString(R.string.API_session_token)),
                    mLimit, startVal , new Callback<FeedResponse>() {
                        @Override
                        public void success(FeedResponse feedResponse, Response response) {
                            DialogUtils.stopProgressDialog();

                            mSwipeRefreshLayout.setRefreshing(false);
                            mFeedResponse = feedResponse;
                            mFeedList.addAll(mFeedResponse.getObjects());
                            isLastPage = mFeedResponse.isLastPage();
                            nextPage=feedResponse.getNext();
                            mFeedAdapter.setFeedResponse(mFeedList, isLastPage);
                            mStart = mFeedResponse.getNextPageStart();
                            isLoading = false;
                            for(int i=0; i<mFeedList.size();i++){
                                Picasso.with(getActivity())
                                        .load(AppConstants.BASE_URL + mFeedList.get(i).getThumbnailUri())
                                        .fetch();
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogUtils.stopProgressDialog();
                            mSwipeRefreshLayout.setRefreshing(false);
                            isLoading = false;
                            if (error != null && error.getResponse() != null &&
                                    !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                                UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                                if (json.substring(json.indexOf(":") + 2, json.length() - 2).equals("")) {
                                }
                            } else
                                UiUtils.showSnackbarToast(mSwipeRefreshLayout, "Some Problem Occurred");
                        }
                    });
        }*/
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
