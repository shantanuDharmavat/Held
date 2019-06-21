package com.held.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.NotificationFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private TextView tv;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private final String TAG = "NotificationActivity";
    private TextView mUsername,mTitle,mInvite;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    View statusBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting notification activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        statusBar=(View)findViewById(R.id.statusBarView);
        Window w = getWindow();


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            Log.e("spinner view","inside");
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            statusBar.setVisibility(View.VISIBLE);

        }else {
            statusBar.setVisibility(View.GONE);
        }
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mChat.setVisibility(View.GONE);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mNotification.setVisibility(View.GONE);

        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);

       // mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
     //   mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
       // mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        //mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
      //  mNotification.setOnClickListener(this);

        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setTextColor(getResources().getColor(R.color.positve_btn));
//        mRetakeBtn.setOnClickListener(this);
//        mPostBtn.setOnClickListener(this);
//        mChat.setVisibility(View.GONE);


        mCamera.setImageResource(R.drawable.home);
        mSearchEdt.setVisibility(View.GONE);
        mTitle.setText("Notifications");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);

        mSearchEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    mTitle.setVisibility(View.GONE);
                } else {

                    mTitle.setVisibility(View.VISIBLE);
                    mSearchEdt.setVisibility(View.GONE);
                }
            }
        });
        hideChatCount();
        updateSeenTime();

        if (getIntent().getExtras() != null) {
            launchNotificationScreen(getIntent().getExtras().getInt("id"));
            if (getIntent().getExtras().containsKey("badge"))
                super.showBadge();
        } else {
            launchNotificationScreen();
        }
    }

    private void updateSeenTime(){

        String selfID = PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_regId));

        HeldService.getService().updateSeenTime(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_session_token)),
                selfID, "last_notification_view_time", "" + System.currentTimeMillis(), "",
                new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }

    private void launchNotificationScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance();
    }

    private void launchNotificationScreen(int id) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(id), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance(id);
    }

    /*private void launchNotificationScreen(int id,String type) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(id), NotificationFragment.TAG);
        mDisplayFragment = NotificationFragment.newInstance(id,type);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_chat_img:
                launchChatListScreen();
                break;
            case R.id.toolbar_post_img:
                launchFeedScreen();
                break;

        }
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        Log.d(TAG, "performing action " + id);
        switch (id) {

            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"),
                            bundle.getString("username", ""));
                break;
            case AppConstants.LAUNCH_PROFILE_SCREEN:
                if (bundle != null)
                    launchProfileScreen(bundle.getString("user_id"));
                break;
            case AppConstants.LAUNCH_INDIVIDUAL_IMAGE_SCREEN:
                if (bundle != null)
                    launchOpenImage(bundle);
                break;
            case AppConstants.LAUNCH_SEEN_BY_SCREEN:
                if(bundle!=null)
                    launchSeenByScreen(bundle.getString("post_id"));
                break;
        }
    }

    private void launchSeenByScreen(String post_id){
        Intent intent = new Intent(NotificationActivity.this, SeenByActivity.class);
        intent.putExtra("post_id", post_id);
        startActivity(intent);
    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }

    private void launchChatScreen(String id,boolean isOneToOne, String username) {
        Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("username", username);
        startActivity(intent);

    }

    private void launchOpenImage(Bundle bundle){
        Intent intent = new Intent(NotificationActivity.this,OpenImageActivity.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }

    private void launchFeedScreen() {
        Intent intent = new Intent(NotificationActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    private void launchChatListScreen() {
        Intent intent = new Intent(NotificationActivity.this, InboxActivity.class);
        startActivity(intent);
    }
    public void visibleTextView(){

        mSearchEdt.setVisibility(View.VISIBLE);
        mSearchEdt.setFocusable(true);
        mSearchEdt.setFocusableInTouchMode(true);
        mSearchEdt.requestFocus();
        mTitle.setVisibility(View.GONE);


    }
    public void hideTextView(){

        mSearchEdt.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);

    }
    private void launchSearchScreen() {
        Intent intent = new Intent(NotificationActivity.this, SearchActivity.class);
        //intent.putExtra("userName", uname);
        startActivity(intent);
    }


}

