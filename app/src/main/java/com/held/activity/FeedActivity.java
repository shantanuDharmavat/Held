package com.held.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.fragment.FeedFragment;
import com.held.fragment.HomeFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;



public class FeedActivity extends ParentActivity implements View.OnClickListener {


    //    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    private GestureDetector gestureDetector;
    protected Toolbar mHeld_toolbar;
    private final String TAG = "FeedActivity";
    private RelativeLayout mPosttoolbar,statusbar;
    private int mPosition = 1;
    private PreferenceHelper mPreference;
    View statusBar;
    private LocalBroadcastManager localBroadcastManager;
    private boolean firstClick=true;
    private String mUserNameForSearch;



//    private View toolbar_divider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("starting feed activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mHeld_toolbar);
        mPreference = PreferenceHelper.getInstance(this);

        statusBar=(View)findViewById(R.id.statusBarView);
        Window w = getWindow();
        mPreference = PreferenceHelper.getInstance(this);

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

        localBroadcastManager = LocalBroadcastManager.getInstance(HeldApplication.getAppContext());
        localBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter("notification"));

        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mInvite.setTypeface(medium);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        launchFeedScreen();


        mNotification.setPadding(20, 0, 20, 0);

        mChat.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);
        mInvite.setOnClickListener(this);

        mSearch_edt = (EditText) findViewById(R.id.toolbar_search_edt_txt);



        mSearch_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //mSearch.setVisibility(View.GONE);
                    mTitle.setVisibility(View.GONE);
                } else {
                    mTitle.setVisibility(View.VISIBLE);
                    mSearch_edt.setVisibility(View.GONE);
                }
            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showBadge();
        }
    };

    @Override
    public void setContentView(int layout){

        super.setContentView(layout);
        showBadge();

    }

    private void launchFeedScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }

    private void launchHomeScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(HomeFragment.newInstance(), HomeFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }

    private void launchChatScreen(String id,boolean isOneToOne,String userName) {
        Intent intent = new Intent(FeedActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("username", userName);
        startActivity(intent);
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(FeedActivity.this, InboxActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(FeedActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private void launchRequestFriendScreen(String name, String image) {
        updateToolbar(false, false, false, false, false, false, false, "");
        addFragment(SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image), SendFriendRequestFragment.TAG, true);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
    }
    public void launchSeenBy(String post_id){
        Intent intent = new Intent(FeedActivity.this, SeenByActivity.class);
        intent.putExtra("post_id", post_id);
        startActivity(intent);
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        Log.d(TAG, "performing action " + id);
        switch (id) {
            case AppConstants.LAUNCH_POST_SCREEN:
                launchCamera();
                break;
            case AppConstants.LAUNCH_FEED_SCREEN:
                launchHomeScreen();
                break;
            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"),bundle.getString("username"));
                break;
            case AppConstants.LAUNCH_NOTIFICATION_SCREEN:
                launchNotificationScreen();
                break;
            case AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN:
                launchChatListScreen();
                break;
            case AppConstants.LAUNCH_PROFILE_SCREEN:
                if (bundle != null)
                    launchProfileScreen(bundle.getString("user_id"));
                break;
            case AppConstants.LAUNCH_INVITE_SCREEN:
                launchInviteScreen();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBadge();
    }

    private void launchCamera(){
        Intent intent = new Intent(FeedActivity.this, CameraSurfaceView.class);
        startActivity(intent);
    }

    private void launchInviteScreen(){
        Intent intent = new Intent(FeedActivity.this, InviteActivity.class);
        startActivity(intent);
    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(FeedActivity.this, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }
    private void launchSearchScreen() {
        Intent intent = new Intent(FeedActivity.this, SearchActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (mDisplayedFragment instanceof FeedFragment && mDisplayedFragment.isVisible()) {
            this.finishActivity(Activity.RESULT_OK);
            this.finish();

        }
        else {
            Timber.d("Calling super.onbackpressed");
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onclick detected. mPosition is " + mPosition);
        mDisplayedFragment = Utils.getCurrVisibleFragment(this);
        switch (view.getId()) {


            case R.id.toolbar_chat_img:
                Log.d(TAG, "toolbar chat image has been clicked. mPosition is " + mPosition);
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_PERSONAL_CHAT_SCREEN, null);
                }
                break;
            case R.id.toolbar_notification_img:
                Log.d(TAG, "toolbar notification image has been clicked");
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                } else if (mPosition == 3) {
                    perform(AppConstants.LAUNCH_NOTIFICATION_SCREEN, null);
                }
                break;
            case R.id.toolbar_post_img:
                Log.d(TAG, "toolbar post image has been clicked");
                if (mPosition == 0) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 1) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 2) {
                    perform(AppConstants.LAUNCH_POST_SCREEN, null);
                } else if (mPosition == 3) {
                   perform(AppConstants.LAUNCH_POST_SCREEN, null);
                }

                break;

            case R.id.toolbar_invite_txt:
                perform(AppConstants.LAUNCH_INVITE_SCREEN, null);
                break;
        }
    }


    public void updateViewPager(int position) {
        mPosition = position;
        updateToolbar();
    }


    @Override
    public void onLeftSwipe() {
        // Do something
        launchCamera();

    }

    @Override
    public void onRightSwipe() {
        // Do something
        launchChatListScreen();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }


    public void visibleTextView(){

        mSearch_edt.setVisibility(View.VISIBLE);
        mSearch_edt.setFocusable(true);
        mSearch_edt.setFocusableInTouchMode(true);
        mSearch_edt.requestFocus();
        mTitle.setVisibility(View.GONE);

    }
    public void hideTextView(){

        mSearch_edt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void updateToolbar() {
        if (mPosition == 0) {
            mChat.setImageResource(R.drawable.chat);
            mCamera.setImageResource(R.drawable.camera);
        } else if (mPosition == 1) {


            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearch_edt.setVisibility(View.VISIBLE);
            mChat.setImageResource(R.drawable.chat);
            mCamera.setImageResource(R.drawable.camera);
        } else if (mPosition == 2) {
            mCamera.setVisibility(View.GONE);
            mNotification.setVisibility(View.GONE);
            mChat.setVisibility(View.GONE);
            mSearch_edt.setVisibility(View.INVISIBLE);

        } else if (mPosition == 3) {
            mCamera.setVisibility(View.VISIBLE);
            mNotification.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mSearch_edt.setVisibility(View.VISIBLE);

            mChat.setImageResource(R.drawable.icon_camera);
            mCamera.setImageResource(R.drawable.icon_feed);


        }
    }

    public void hideToolbar(){
        mHeld_toolbar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
        statusBar.setVisibility(View.GONE);}
    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
        statusBar.setVisibility(View.VISIBLE);}
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                mSearch_edt.getText().toString().trim(), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        DialogUtils.stopProgressDialog();

                        Bundle bundle = new Bundle();

                        bundle.putString("name", searchUserResponse.getDisplayName());
                        perform(AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN, bundle);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "Some Problem Occurred");
                    }
                });
    }


}
