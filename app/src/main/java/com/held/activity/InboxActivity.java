package com.held.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.FriendsListFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import static com.held.utils.HeldApplication.getAppContext;

public class InboxActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mCamera, mNotification,mSearch;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername,title,mInvite;
    private static InboxActivity activity;
    private final String TAG = "InboxActivity";
    private final boolean flag=true;
    private LocalBroadcastManager localBroadcastManager;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    View statusBar;


    public static InboxActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting Chat activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
        activity = this;
        title = (TextView)findViewById(R.id.toolbar_title_txt);
        title.setText("Inbox");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        title.setTypeface(medium);


        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
        mSearch=(ImageView) findViewById(R.id.toolbar_chat_img);
        mSearch.setImageResource(R.drawable.search);
     //   mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setTextColor(getResources().getColor(R.color.positve_btn));
        mCamera.setImageResource(R.drawable.home);
        mCamera.setVisibility(View.VISIBLE);
        //mCamera.setImageDrawable(homeIcon);


        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        updateSeenTime();
        hideChatCount();


//        mRetakeBtn.setOnClickListener(this);
  //      mPostBtn.setOnClickListener(this);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey("badge"))
                super.showBadge();

            String chatid = getIntent().getExtras().getString("id");
            Boolean isOneToOne = getIntent().getExtras().getBoolean("isOneToOne");
            launchChatScreen( chatid, isOneToOne);
        } else {
            Log.d(TAG, "Launching inbox");
            launchInboxPage();
        }
        mSearchEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearch.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearch.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    mSearchEdt.setVisibility(View.GONE);
                }
            }
        });

        localBroadcastManager = LocalBroadcastManager.getInstance(getAppContext());
        localBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter("notification"));
    }

    private void launchInboxPage() {

        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void updateSeenTime(){

        String selfID = PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_regId));

        HeldService.getService().updateSeenTime(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_session_token)),
                selfID, "last_chat_view_time", "" + System.currentTimeMillis(), "",

                new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    private void launchChatScreenFromInbox(String id, String username, boolean isOneToOne) {
        Timber.d("Launching chatscreen with user id " + id + " isonetoone " + isOneToOne);
        Intent intent = new Intent(InboxActivity.this, ChatActivity.class);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("id", id);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }



    @Override
    public void onBackPressed() {
        if (mDisplayFragment instanceof ChatFragment) {
            Log.d(TAG, "on back pressed. current fragment is chat fragment");
            super.onBackPressed();
            mSearchEdt.setVisibility(View.GONE);
//            mUsername.setVisibility(View.INVISIBLE);
            mCamera.setImageResource(R.drawable.icon_feed);

            mDisplayFragment = Utils.getCurrVisibleFragment(this);
        } else {
            Log.d(TAG, "unknown current fragment");
            super.onBackPressed();
        }
    }




    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                if (bundle != null)

                    launchChatScreenFromInbox(bundle.getString("user_id"),
                            bundle.getString("username"), true);
                break;
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showBadge();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;


            case R.id.toolbar_post_img:
                launchFeedScreen();
                break;


            case R.id.toolbar_chat_img:
                /*Log.d(TAG, "toolbar search image has been clicked");
                if(firstClick) {
                    visibleTextView();
                    firstClick=false;
                }
                else {
                    mUserNameForSearch= mSearchEdt.getText().toString();
                    Timber.i("User Name for search :" + mUserNameForSearch);
                    mSearchEdt.setText("");
                    hideTextView();
                    firstClick=true;
                    launchSearchScreen(mUserNameForSearch);

                }*/
                launchSearchScreen();

                break;
        }
    }



    private void launchFeedScreen() {
        Intent intent = new Intent(InboxActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(InboxActivity.this, NotificationActivity.class);
        startActivity(intent);
    }
    public void visibleTextView(){

        mSearchEdt.setVisibility(View.VISIBLE);
        mSearchEdt.setFocusable(true);
        mSearchEdt.setFocusableInTouchMode(true);
        mSearchEdt.requestFocus();
        title.setVisibility(View.GONE);


    }
    public void hideTextView(){

        mSearchEdt.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }
    private void launchSearchScreen() {
        Intent intent = new Intent(InboxActivity.this, SearchActivity.class);
/*
        intent.putExtra("userName", uname);
*/
        startActivity(intent);
    }

    @Override
    public void onLeftSwipe() {
        // Do something
        finish();
    }

    @Override
    public void onRightSwipe() {
        // Do something
        launchSearchScreen();
    }
}
