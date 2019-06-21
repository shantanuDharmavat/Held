package com.held.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ParentFragment;
import com.held.receiver.NetworkStateReceiver;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.NetworkUtil;
import com.held.utils.PreferenceHelper;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public abstract class ParentActivity extends AppCompatActivity implements NetworkStateReceiver.OnNetworkChangeListener {

    private static final String TAG = ParentActivity.class.getSimpleName();

    protected Toolbar mToolbar;

    protected boolean mNetworkStatus;
    // Fragment related: ContainerId, Currently Displayed Fragment, add/replace Animations.
    protected int mContainerID;
    protected ParentFragment mDisplayedFragment;
    protected int mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim;
    private boolean mShowChat, mShowRetakeBtn, mShowSearch, mShowUserName, mShowCamera, mShowNotification, mShowPostBtn;
    private String mUserName;
    protected static int mUnreadNotifCount = 0, mUnreadMsgCount = 0;
    protected static boolean mBadgeCountFetched = false;
    private GestureDetectorCompat mGestureDetector;
    private LocalBroadcastManager localBroadcastManager;
    TextView notifIndicator, chatIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Handle if Font style changed from Device setting(Specially Samsung S3, S4..), then current activity is going to restart again.
        if (savedInstanceState != null) {

        }
        DialogUtils.resetDialog(ParentActivity.this);
        //Get current Network status during Activity creation for first time
        mNetworkStatus = NetworkUtil.isInternetConnected(getApplicationContext());

        //Register here to get Network status
        NetworkStateReceiver.registerOnNetworkChangeListener(this);

        mGestureDetector = new GestureDetectorCompat(this, new GestureListener());
        mFragEnterAnim = R.anim.slide_in_right;
        mFragExitAnim = R.anim.slide_out_left;
        mFragPopEnterAnim = R.anim.slide_in_left;
        mFragPopExitAnim = R.anim.slide_out_right;


        HeldApplication.getInstance().setCurrentActivity(this);

        localBroadcastManager = LocalBroadcastManager.getInstance(HeldApplication.getAppContext());

    }

//    protected void setStatusBarColor() {
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintColor(Utilities.getColor(R.color.toolbar_bg_blue));
//        }
//    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mContainerID = R.id.frag_container;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    protected void removeAllFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    protected void addFragment(ParentFragment fragment, String tag) {
        mDisplayedFragment=fragment;

        addFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag) {
        mDisplayedFragment=fragment;

        replaceFragment(fragment, tag, false,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void addFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        mDisplayedFragment=fragment;
        addFragment(fragment, tag, addToBackStack,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void replaceFragment(ParentFragment fragment, String tag, boolean addToBackStack) {
        mDisplayedFragment=fragment;
        replaceFragment(fragment, tag, addToBackStack,
                mFragEnterAnim, mFragExitAnim, mFragPopEnterAnim, mFragPopExitAnim);
    }

    protected void addFragment(ParentFragment fragment, String tag, boolean addToBackStack,
                               int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mDisplayedFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                .add(mContainerID, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    protected void replaceFragment(ParentFragment fragment, String tag, boolean addToBackStack,
                                   int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        mDisplayedFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                .replace(mContainerID, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HeldApplication.IS_APP_FOREGROUND = true;
        //Get current Network status during Activity resume
        mNetworkStatus = NetworkUtil.isInternetConnected(getApplicationContext());
        HeldApplication.getInstance().setCurrentActivity(this);

    }

    @Override
    protected void onPause() {
        HeldApplication.IS_APP_FOREGROUND = false;
        clearReferences();
        super.onPause();
    }

    //Called during Network status changed
    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetworkStatus = isEnabled;
    }

    public boolean getNetworkStatus() {
        return mNetworkStatus;
    }

    @Override
    protected void onDestroy() {

//        MusicUtils.stopMusic();
        //Unregister here (remove listener)
        NetworkStateReceiver.unregisterOnNetworkChangeListener(this);

        clearReferences();
        super.onDestroy();

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.e("BROAD CAST RECIEVE");
            showBadge();
        }
    };

    private void showBadgeCount() {
        TextView indicator = (TextView) findViewById(R.id.tvNotificationIndicator);
        if (mUnreadNotifCount > 0) {
            indicator.setText("" + mUnreadNotifCount);
            //showNotificationCount(mUnreadMsgCount);
            indicator.setVisibility(View.VISIBLE);
        }
    }

    private void showBadgeCount(boolean msgCount, boolean notifCount) {
        Timber.d("showing badge count");
        TextView indicator = (TextView) findViewById(R.id.tvNotificationIndicator);
        TextView cIndicator = (TextView) findViewById(R.id.tvChatIndicator);
        ImageView notificationIcon = (ImageView) findViewById(R.id.toolbar_notification_img);
        ImageView chatIcon = (ImageView) findViewById(R.id.toolbar_chat_img);

        if (mUnreadNotifCount > 0 && notifCount) {
            if (notificationIcon != null && notificationIcon.getVisibility() != View.GONE) {
                indicator.setText("" + mUnreadNotifCount);
                indicator.setVisibility(View.VISIBLE);
            }
/*
        } else {
            indicator.setVisibility(View.GONE);
        }*/
            if (mUnreadMsgCount > 0 && msgCount) {
                if (chatIcon != null && chatIcon.getVisibility() != View.GONE) {

                    cIndicator.setText("" + mUnreadMsgCount);
                    cIndicator.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void showBadge () {
        showBadge(true, true);
    }

    protected void showBadge ( final boolean msgCount, final boolean notifCount){

        Timber.d("in show badge activity: " + this.getLocalClassName());
        TextView notifIndicator = (TextView) findViewById(R.id.tvNotificationIndicator);
        TextView chatIndicator = (TextView) findViewById(R.id.tvChatIndicator);
        if (notifIndicator == null || chatIndicator == null)
            return;
        Typeface book = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        notifIndicator.setTypeface(book);
        chatIndicator.setTypeface(book);


        String selfID = PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_regId));
        HeldService.getService().searchUser(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_session_token)),
                selfID, new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {

                        mUnreadNotifCount = searchUserResponse.getUser().getUnseenNotificationsCount();
                        mUnreadMsgCount = searchUserResponse.getUser().getUnseenMessagesCount();

                        showBadgeCount(msgCount, notifCount);

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Timber.e("Error in fetch unread notification count");
                    }
                });
      /*  runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffAbs = Math.abs(e1.getY() - e2.getY());
        float diff = e1.getX() - e2.getX();
        Timber.i(TAG, "@@@Inside try");

//            if (diffAbs > SWIPE_MAX_OFF_PATH)
//                return false;

            if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onLeftSwipe();
            } else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onRightSwipe();
            }
            return true;
        }
    }

            protected void onLeftSwipe () {

            }

            protected void onRightSwipe () {

            }

            private void clearReferences () {
                HeldApplication myApp = HeldApplication.getInstance();
                Activity currActivity = myApp.getCurrentActivity();
                if (this.equals(currActivity))
                    myApp.setCurrentActivity(null);
            }


            @Override
            public void startActivity (Intent intent){
                super.startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void startActivityForResult (Intent intent,int requestCode){
                super.startActivityForResult(intent, requestCode);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void finish () {
                super.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            @Override
            public void onBackPressed () {
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }

            public void hideChatCount () {
                Timber.d("inside hide chat count");
                TextView tv = (TextView) findViewById(R.id.tvChatIndicator);
                if (tv != null)
                    tv.setVisibility(View.GONE);
            }

            public void hideNotifCount () {
                TextView tv = (TextView) findViewById(R.id.tvNotificationIndicator);
                if (tv != null)
                    tv.setVisibility(View.GONE);
            }

            public Toolbar getToolbar () {
                return mToolbar;
            }

            public void setToolbar () {
                if (mToolbar == null) {
                    mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
                }
                if (mToolbar == null) return;

                setSupportActionBar(mToolbar);
                ImageView chat_img = (ImageView) findViewById(R.id.toolbar_chat_img);

                ImageView notification_img = (ImageView) findViewById(R.id.toolbar_notification_img);
                ImageView camera_img = (ImageView) findViewById(R.id.toolbar_post_img);
                TextView toolbar_title_txt = (TextView) findViewById(R.id.toolbar_title_txt);
                EditText toolbar_search_edt_txt = (EditText) findViewById(R.id.toolbar_search_edt_txt);


            }


            public void updateToolbar ( boolean showChat, boolean showRetakeBtn,
            boolean showSearchBar, boolean showUserName,
            boolean showCamera, boolean showNotification, boolean showPostBtn, String userNameTxt){

                mShowChat = showChat;
                mShowRetakeBtn = showRetakeBtn;
                mShowUserName = showUserName;
                mShowNotification = showNotification;
                mShowCamera = showCamera;
                mShowSearch = showSearchBar;
                mShowPostBtn = showPostBtn;
                mUserName = userNameTxt;

                setToolbar();
            }

            public void perform ( int id, Bundle bundle){
            }

            private BroadcastReceiver mNotificationRecieve = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String count = intent.getStringExtra("notificationcount");
                    Timber.e("INTENT VALUES : " + intent.getExtras().keySet());
                    //if (count.equals())
                }
            };

            protected void HideNotification () {
                chatIndicator = (TextView) findViewById(R.id.tvChatIndicator);
                chatIndicator.setVisibility(View.GONE);
            }

            }

