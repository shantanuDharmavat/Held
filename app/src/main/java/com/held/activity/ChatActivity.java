package com.held.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.ParentFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AndroidBug5497Workaround;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by swapnil on 3/10/15.
 */
public class ChatActivity extends ParentActivity implements View.OnClickListener{

    ImageView mChat, mCamera, mNotification;
    TextView mInvite,mChatNotification;
    boolean mIsOneToOne;
    EditText mSearchEdt;
    Activity mActivity;
    Fragment mDisplayFragment;
    String mId,mChatBackImg, mUsername, mPostMsgId;
    boolean flag;
    View statusBar;
    private LocalBroadcastManager localBroadcastManager;
    PreferenceHelper mPreference;
    public Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            statusBar.setVisibility(View.VISIBLE);
            AndroidBug5497Workaround.assistActivity(this);
        }else {
            statusBar.setVisibility(View.GONE);
        }

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPreference = PreferenceHelper.getInstance(this);
        mActivity = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(HeldApplication.getAppContext());
        localBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter("CHAT"));
        mChatNotification = (TextView) findViewById(R.id.tvChatIndicator);
        mChatNotification.setVisibility(View.GONE);
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mNotification.setVisibility(View.GONE);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setVisibility(View.GONE);
        mSearchEdt.setVisibility(View.GONE);
        mChat.setImageResource(R.drawable.back);
        mCamera.setImageResource(R.drawable.menu);
        mCamera.setVisibility(View.VISIBLE);
        mCamera.setOnClickListener(this);
        //mNotification.setOnClickListener(this);
        mChat.setOnClickListener(this);

        //mChat.setPadding(50, 20, 100, 20);
        TextView title = (TextView)findViewById(R.id.toolbar_title_txt);

        if (!getIntent().getExtras().isEmpty())
            if (getIntent().getExtras().containsKey("badge"))
                super.showBadge();

        Bundle extras = getIntent().getExtras();
        final boolean isOneToOne = extras.getBoolean("isOneToOne");
        mIsOneToOne = isOneToOne;
        mId=extras.getString("id", null);
        Timber.d("Chat activity started for id " + mId);
        mUsername = extras.getString("username", "null");
        mPostMsgId = extras.getString("msgid", "null");
        Timber.d("Post msg id : " + mPostMsgId);
        Timber.e("ONE TO ONE USER ID : " + extras.getString("username"));
        if(mIsOneToOne && mUsername != null){
            if(mUsername.length() > 11){
                mUsername = mUsername.substring(0, 7) + "...";
            }
            if(mUsername != null && !mUsername.equals("")){
                Timber.d("Chat activity received username: " + mUsername);
                title.setText("Chat with " + mUsername);
            }
            else
                title.setText("");
        }
        else
            title.setText("");

        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        title.setTypeface(medium);
        final ChatActivity chat = this;

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ChatActivity.this, mCamera);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_post_chat, popup.getMenu());

                final ChatActivity current = chat;
                //registering popup with OnMenuItemClickListener

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        final MenuItem item1 = item;

                        AlertDialog.Builder alert = new AlertDialog.Builder(chat);
                        alert
                        .setMessage("Are you sure you want to " + item1.getTitle())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                 switch(item1.getItemId()){
                                    case R.id.btnDownloadReq:
                                        callDownloadRequestAPI();
                                        break;
                                    case R.id.btnReportAbuse:
                                        callReportAbuse();
                                        break;


                                }
                                check = true;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                check = false;
                            }
                        })
                        .show();

                        return check;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method

        hideChatCount();

        handleIntent(extras);


       // mChatBackImg=extras.getString("chatBackImg");
       // flag=extras.getBoolean("flag");
       // String chatId = extras.getString("chatId");
       // Timber.d("Chat activity received chat id " + chatId + " isontotone: " + isOneToOne);

    }

    private void callReportAbuse(){

        HeldService.getService().reportAbuse(mPreference.readPreference("SESSION_TOKEN"), mId, "",
                new Callback<SearchUserResponse>() {

                    @Override
                    public void success(SearchUserResponse data, Response response) {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Your message has been sent to admin.");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Error in sending message.");
                    }

                });


    }

    private void callDownloadRequestAPI(){

        HeldService.getService().sendDownloadRequest(mPreference.readPreference("SESSION_TOKEN"), mId,"",
                new Callback<DownloadRequestData>() {

                    @Override
                    public void success(DownloadRequestData data, Response response) {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Download request sent.");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "Error in sending download request.");
                    }

                });
    }

    private void handleIntent(Bundle extras){
        Log.e("Content of Extra",""+extras.get("id"));
        Log.e("Content of Extra",""+extras.get("isOneToOne"));

        if(mIsOneToOne){

           if((mId == "" || mId == null) &&(mUsername != "" && mUsername != null)){
               Timber.d("Calling search api to convert username to id");
               HeldService.getService().searchByName(mPreference.readPreference(getString(R.string.API_session_token)),
                       mUsername, new Callback<Engager>() {
                           @Override
                           public void success(Engager engager, Response response) {

                               String rid = engager.getUser().getRid();
                               mId = rid;

                               Timber.d("Found user with id: " + rid);
                               launchChatScreen(mId, mIsOneToOne);

                           }

                           @Override
                           public void failure(RetrofitError error) {
                               UiUtils.showToast("Error in retrieving chat data");

                           }
                       });
            }else{
               launchChatScreen(mId, mIsOneToOne);

           }
        }else{

            if(mId == null || mId == ""){

                if(mPostMsgId == null || mPostMsgId == ""){
                    UiUtils.showToast("Message id not received");
                    return;
                }

                HeldService.getService().getPostChatMsg(mPreference.readPreference(getString(R.string.API_session_token)),
                        "dummy", mPostMsgId, new Callback<PostChatData>() {
                            @Override
                            public void success(PostChatData data, Response response) {

                                String rid = data.getPost().getRid();
                                Timber.d("Message id converted to post id: " + rid);
                                mId = rid;
                                launchChatScreen(rid, mIsOneToOne);

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                UiUtils.showToast("Error in retrieving chat data");

                            }
                        });
            }else{
                launchChatScreen(mId, mIsOneToOne);
            }

        }

    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent

            Timber.d("Chat broadcast receiver received message" + intent.getExtras().keySet());
            final boolean isOneToOne = intent.getBooleanExtra("isOneToOne", false);
         //   notifIndicator.setVisibility(View.GONE);
            if(isOneToOne){

                final String username = intent.getStringExtra("username");
                final String message  = intent.getStringExtra("message");

                HeldService.getService().searchByName(mPreference.readPreference(getString(R.string.API_session_token)),
                        username, new Callback<Engager>() {
                            @Override
                            public void success(Engager engager, Response response) {

                                String rid = engager.getUser().getRid();
                                if (mId.equals(rid) && (isOneToOne == mIsOneToOne)) {
                                    Log.e("refreshing chat*","");
                                    Timber.d("Chat broadcast receiver received message" + message);
                                    // update current chat
                                    ((ChatFragment) mDisplayFragment).appendMessage(message);

                                } /*else {
                                Log.e("starting new activity*","");

                                // launch a new activity
                                Intent newIntent = new Intent(ChatActivity.this, ChatActivity.class);
                                newIntent.putExtra("id", rid);
                                newIntent.putExtra("isOneToOne", isOneToOne);
                                startActivity(newIntent);
                            }*/

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                UiUtils.showToast("Error in retrieving chat data");

                            }
                        });

            }else{

                final String msgid = intent.getStringExtra("msgid");

                HeldService.getService().getPostChatMsg(mPreference.readPreference(getString(R.string.API_session_token)),
                        "dummy", msgid, new Callback<PostChatData>() {

                            @Override
                            public void success(PostChatData postChatData, Response response) {

                                String rid = postChatData.getPost().getRid();
                                if(mId.equals(rid)){
                                    // need to append this message
                                    ((ChatFragment) mDisplayFragment).appendGroupChatMessage(postChatData);

                                }else{
                                    Timber.d("group message received is not for current post.");
                                    Timber.d("mId: " + mId + " rid: " + rid);
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                UiUtils.showToast("Error in retrieving chat data");

                            }

                        });

            }

        }
    };

    private void launchChatScreen(String id, boolean isOneToOne) {
        ParentFragment frag = ChatFragment.newInstance(id, isOneToOne);
        Bundle bundle=new Bundle();
        HideNotification();
        if(isOneToOne)
            bundle.putString("user_id",id);
        else
            bundle.putString("msgid",id);
        //bundle.putString("chatBackImg",mChatBackImg);
        bundle.putBoolean("isOneToOne",isOneToOne);
        frag.setArguments(bundle);
        addFragment(frag, ChatFragment.TAG, false);
        mDisplayFragment = frag;
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }

    @Override
    public void onBackPressed() {
        /*int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackEntryCount == 0){
            Intent intent = new Intent(ChatActivity.this,FeedActivity.class);
            startActivity(intent);
            this.finish();
        }*/

        super.onBackPressed();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;


            case R.id.toolbar_post_img:
                // todo:
                break;

            case R.id.toolbar_chat_img:
                onBackPressed();
                break;
        }
    }

    private void launchCreatePostScreen() {
        Intent intent = new Intent(ChatActivity.this, PostActivity.class);
        startActivity(intent);
    }

    private void launchNotificationScreen() {
        Intent intent = new Intent(ChatActivity.this, NotificationActivity.class);
        startActivity(intent);
    }





}
