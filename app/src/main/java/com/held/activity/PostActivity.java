package com.held.activity;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.FeedFragment;
import com.held.fragment.FriendsListFragment;
import com.held.fragment.NotificationFragment;
import com.held.fragment.PostFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;

import timber.log.Timber;

/**
 * Created by jay on 5/8/15.
 */
public class PostActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = "PostActivity";
    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;
    private String mImagePath;
    private RelativeLayout toolbar;
    Bundle bundle;
    private PreferenceHelper mPreference;
    String callfrom;
    View statusBar;
    int postCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "starting Post activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        statusBar=(View)findViewById(R.id.statusBarView);
        bundle = new Bundle();

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
            // comm
            statusBar.setVisibility(View.GONE);
        }
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
      //  mSearchEdt = (EditText) findViewById(R.id.TOOLBAR_search_edt);
      //  mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
      //  mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
      //  mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);
        toolbar=(RelativeLayout)findViewById(R.id.custom_toolbar);
        mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mPreference=PreferenceHelper.getInstance(getApplicationContext());
      //  mRetakeBtn.setOnClickListener(this);
      //  mPostBtn.setOnClickListener(this);
        toolbar.setVisibility(View.GONE);
        Log.i(TAG, "@@Inside post Activity");
        Intent data = getIntent();
        try {
            String type = data.getStringExtra("type");

            if (type.equals("camera")) {
                String path = data.getStringExtra("REQUEST");
                Timber.e("URI PATH = " + path);
                bundle.putString("path", path);
                bundle.putString("type","camera");
                // mImagePath = data.getStringExtra("REQUEST");
            } else {
                String uri = data.getStringExtra("REQUEST");
                Timber.e("URI PATH FROM GALLERY= " + uri);
                bundle.putString("path",uri);
                bundle.putString("type","gallery");
            }
        }catch (Exception e){

            e.printStackTrace();
        }

        launchCreatePostScreen(bundle);



    }

    public void StartCamera() {
        final int REQUEST = 1;
        Intent startCameraIntent = new Intent(PostActivity.this,CameraSurfaceView.class);
        startActivityForResult(startCameraIntent,REQUEST);
    }

    private void launchFeedScreen() {
       Intent intent = new Intent(PostActivity.this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

       /* updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
        mDisplayFragment = FeedFragment.newInstance();*/
    }

    private void launchCreatePostScreen(Bundle bundle) {
        PostFragment frag = PostFragment.newInstance(bundle);
        addFragment(frag, PostFragment.TAG, true);
        mDisplayFragment = frag;

    }

    private void launchNotificationScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(), NotificationFragment.TAG, true);
        mDisplayFragment = NotificationFragment.newInstance();
    }

    private void launchCreatePostFragmentFromFeed() {
        updateToolbar(false, true, false, false, false, false, true, "");
        addFragment(PostFragment.newInstance(bundle), PostFragment.TAG, true);
        mDisplayFragment.setArguments(bundle);
        mDisplayFragment = PostFragment.newInstance(bundle);
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        /////add chat back image for personal chat
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG, true);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bundle.isEmpty())
            this.finish();
    }

    @Override
    public void onBackPressed() {
        if(mPreference.readPreference(getString(R.string.is_first_post),false))
            this.finish();
        else
            super.onBackPressed();
    }


    @Override
    public void perform(int id, Bundle bundle) {
        Log.d(TAG, "In function perform");
        super.perform(id, bundle);
        switch (id) {
            case 0:
                launchCreatePostScreen(bundle);
                break;
            case 1:
                launchFeedScreen();
                break;
            case 2:
                if (bundle != null)
                    launchChatScreen(bundle.getString("postid"), false);
                break;
            case 3:
                launchCreatePostFragmentFromFeed();
                break;
            case 4:
                launchNotificationScreen();
                break;
            case 5:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case 6:
                if (bundle != null)
                    launchChatScreen(bundle.getString("owner_displayname"), true);
                break;
            case 7:
                launchInboxPage();
                break;

        }
    }

    private void launchInboxPage() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG, true);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchRequestFriendScreen(String name, String image) {
        updateToolbar(false, false, false, false, false, false, false, "");
        addFragment(SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image), SendFriendRequestFragment.TAG, true);
        mDisplayFragment = SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image);
    }

    @Override
    public void onClick(View view) {
        invalidateToolbar(view.getId());
    }

    private void invalidateToolbar(int id) {
        switch (id) {
            case R.id.toolbar_chat_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(7, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.chat);
                } else if (mDisplayFragment instanceof NotificationFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.chat);
                } else if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.chat);
                }
                break;
            case R.id.toolbar_notification_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(4, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    perform(4, null);
                } else if (mDisplayFragment instanceof NotificationFragment) {

                } else if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.chat);
                }
                break;
            case R.id.toolbar_post_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof NotificationFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof ChatFragment) {
                    perform(3, null);
                }
                break;
            /*case R.id.TOOLBAR_retake_btn:
                break;
            case R.id.toolbar_post_btn:
                break;*/
//            case R.id.back_home:
//                if(mPreference.readPreference(getString(R.string.is_first_post), false)==false){
//                    this.finish();
//                }
//                else
//                    onBackPressed();
//                break;
        }
    }

    public void onLeftSwipe() {
        // Do something
        launchFeedScreen();
    }

    public void onRightSwipe() {
        // Do something
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            String type = data.getStringExtra("type");

            if (type.equals("camera")) {
                String path = data.getStringExtra("REQUEST");
                Timber.e("URI PATH = " + path);
                bundle.putString("path", path);
                bundle.putString("type","camera");
               // mImagePath = data.getStringExtra("REQUEST");
            } else {
                String uri = data.getStringExtra("REQUEST");
                Timber.e("URI PATH FROM GALLERY= " + uri);
                bundle.putString("path",uri);
                bundle.putString("type","gallery");
            }
        }catch (Exception e){

            e.printStackTrace();
        }

        if (callfrom==null && mPreference.readPreference(getString(R.string.is_first_post), false)==true){
            launchCreatePostScreen(bundle);
        } else if(mPreference.readPreference(getString(R.string.is_first_post), false)==false) {
            launchCreatePostScreen(bundle);
        }

    }
}
