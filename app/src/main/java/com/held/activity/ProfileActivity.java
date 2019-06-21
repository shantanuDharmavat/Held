package com.held.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.customview.PicassoCache;
import com.held.fragment.ParentFragment;
import com.held.fragment.ProfileFragment;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.ImageUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.Utils;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import timber.log.Timber;

public class ProfileActivity extends ParentActivity implements View.OnClickListener {

    ImageView mChat, mCamera, mNotification;
    EditText mSearchEdt;
    private TextView mTitle; //,mInvite;
    Activity mActivity;
    Fragment mDisplayFragment;
    String mUserId;
    ProfileFragment frag;
    private Toolbar mHeld_toolbar;
    private View toolbar_divider;
    private boolean firstClick=true;
    private String mUserNameForSearch;
    private final String TAG = "ProfileActivity";
    View statusBar;
    private LocalBroadcastManager localBroadcastManager;
    private File mFile;
    private Uri mFileUri;
    String sourceFileName;
    PreferenceHelper mPreference;
    final SearchUserResponse currentProfileUser=new SearchUserResponse();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mHeld_toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mHeld_toolbar);

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
        mActivity = this;

        //setToolbar();
        mPreference=PreferenceHelper.getInstance(this);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mTitle.setText("Profile");
       /* mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setText("See Invites");*/
        Bundle extras = getIntent().getExtras();
        mUserId=extras.getString("user_id");
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        //mInvite.setTypeface(medium);
        mChat = (ImageView) findViewById(R.id.toolbar_chat_img);
        mCamera = (ImageView) findViewById(R.id.toolbar_post_img);
        mNotification = (ImageView) findViewById(R.id.toolbar_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setVisibility(View.GONE);
        mChat.setImageResource(R.drawable.camera);
        mCamera.setImageResource(R.drawable.menu);
        mCamera.setVisibility(View.VISIBLE);
        mSearchEdt.setVisibility(View.GONE);
        toolbar_divider=(View)findViewById(R.id.toolbar_divider);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mChat.setOnClickListener(this);
        //mInvite.setOnClickListener(this);

        Timber.d("hiding chat count in profile activity");
        hideChatCount();

        launchProfileScreen(mUserId);
        localBroadcastManager = LocalBroadcastManager.getInstance(HeldApplication.getAppContext());
        localBroadcastManager.registerReceiver(
                mMessageReceiver, new IntentFilter("notification"));

    }

    private void launchProfileScreen(String uid) {
        ParentFragment frag = ProfileFragment.newInstance(uid);
        Bundle bundle=new Bundle();
        bundle.putString("user_id", uid);
        frag.setArguments(bundle);

        String selfID = mPreference.readPreference(getString(R.string.API_user_regId));

        boolean selfProfile = selfID.equals(uid);
        /*if(selfProfile){
            mInvite.setVisibility(View.VISIBLE);
        }else {
            mInvite.setVisibility(View.GONE);
        }*/

        addFragment(ProfileFragment.newInstance(uid), ProfileFragment.TAG, true);
        mDisplayFragment = ProfileFragment.newInstance(uid);
    }

    public Fragment getCurrentFragment() {
        return mDisplayFragment;
    }
    @Override
    public void onBackPressed() {
        getCurrentFragment();
        if (mDisplayedFragment instanceof ProfileFragment && mDisplayedFragment.isVisible()){
            super.onBackPressed();
            this.finishActivity(Activity.RESULT_OK);
        }else {
            super.onBackPressed();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showBadge(false, true);

        }
    };

    @Override
    protected void onResume() {
        showBadge(false, true);
        super.onResume();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_chat_img:
//                if (mDisplayFragment instanceof ProfileFragment) {
                    //onBackPressed();
                launchCreatePostScreen();

                break;
            case R.id.toolbar_notification_img:
                launchNotificationScreen();
                break;
            /*case R.id.toolbar_invite_txt:
                launchSeeInviteScreen();
                break;
            case R.id.invite_count:
                break; */

            case R.id.friend_count_layout:
                //launchFriendsListScreen();
                break;

        }

    }

    private void launchFriendsListScreen(){
        Intent intent = new Intent(ProfileActivity.this, FriendsListActivity.class);
        startActivity(intent);
    }


    public void hideToolbar(){
        mHeld_toolbar.setVisibility(View.GONE);
        toolbar_divider.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.GONE);}
    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
        toolbar_divider.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.VISIBLE);}
    }
    private void launchNotificationScreen() {
        Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
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
    private void launchSearchScreen(String uname) {
        Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
        intent.putExtra("userName", uname);
        startActivity(intent);
    }
    private void launchCreatePostScreen() {
        Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
        startActivity(intent);
    }
    /*private void launchSeeInviteScreen() {
        Intent intent = new Intent(ProfileActivity.this, SeeInviteActivity.class);
        startActivity(intent);
    }*/

    public void launchPersonalChatScreen(String id,boolean isOneToOne,String userName) {
        Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("username",userName);
        startActivity(intent);
    }

    public void openImageIntent() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Source");
        CharSequence charSequence[] = {"Camera ", "Gallery"};
        builder.setItems(charSequence,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                // GET IMAGE FROM THE CAMERA
                                Intent getCameraImage = new Intent(
                                        "android.media.action.IMAGE_CAPTURE");
                                getCameraImage.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                File cameraFolder;
                                cameraFolder = new File(Environment
                                        .getExternalStorageDirectory(), "/HELD");
                                if (!cameraFolder.exists())
                                    cameraFolder.mkdirs();
                                sourceFileName = "/IMG_"
                                        + System.currentTimeMillis() + ".jpg";
                                File photo = new File(cameraFolder, sourceFileName);
                                getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                startActivityForResult(getCameraImage, AppConstants.REQUEST_CAMEAR);
                                break;

                            case 1:
                                Intent intent;
                                intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent,
                                        "Choose a Picture");
                                startActivityForResult(chooser, AppConstants.REQUEST_GALLERY);
                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.i("Inside onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CAMEAR:
                    File photo = new File(Environment.getExternalStorageDirectory(),
                            "/HELD" + sourceFileName);
                    Uri photoUri = Uri.fromFile(photo);
                    doCrop(photoUri);
                    mFileUri=photoUri;
                    mFile = new File(photoUri.getPath());
                    //PicassoCache.getPicassoInstance(this).load(mFile).noFade().into(circularImage);
                    updateImageview();
                    break;

                case AppConstants.REQUEST_GALLERY:
                    Uri PhotoURI = data.getData();
                    doCrop(PhotoURI);
                    mFileUri=PhotoURI;
                    mFile = new File(getRealPathFromURI(PhotoURI));
                    // PicassoCache.getPicassoInstance(this).load(mFile).noFade().into(circularImage);
                    updateImageview();
                    break;
            }
        }

        if (requestCode == AppConstants.REQUEST_CROP) {
            File photo = new File(Environment.getExternalStorageDirectory(),
                    "/HELD" + sourceFileName);

            if (resultCode != Activity.RESULT_OK) {
                photo.delete();
                return;
            }

        }
    }
    public void updateImageview(){

        DialogUtils.showDarkProgressBar();
        callUploadFileApi();
    }
    private void doCrop(Uri mCurrentPhotoPath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(mCurrentPhotoPath, "image/*");

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 320);
        cropIntent.putExtra("outputY", 320);


        File cameraFolder;

        cameraFolder = new File(Environment.getExternalStorageDirectory(),
                "/HELD");

        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }

        sourceFileName = "/IMG_" + System.currentTimeMillis() + ".jpg";

        File photo = new File(cameraFolder, sourceFileName);
        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFile =new File(photo.getPath());

        Uri mCropImageUri = Uri.fromFile(photo);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);
        mActivity.startActivityForResult(cropIntent, AppConstants.REQUEST_CROP);

    }
    void callUploadFileApi() {


        final PreferenceHelper helper = PreferenceHelper.getInstance(this);
        String sessionToken = helper.readPreference(getString(R.string.API_session_token));
        String newFpath = ImageUtils.resizeImage(mFile.getPath(), 200);
        mFile = new File(newFpath);
        HeldService.getService().uploadFile(sessionToken, "", new TypedFile("multipart/form-data", mFile), "", new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                String imgUrl = postResponse.getImageUri();
                Timber.i("New Profile Pic Url:" + imgUrl);
                callUpdateNewProfilePicApi(imgUrl);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    void callUpdateNewProfilePicApi(String imgUrl){

        final PreferenceHelper helper = PreferenceHelper.getInstance(this);
        final de.hdodenhof.circleimageview.CircleImageView mCircularImage=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.circular_Profile_pic);
        String sessionToken = helper.readPreference(getString(R.string.API_session_token));

        HeldService.getService().uploadNewProfilePic(sessionToken, helper.readPreference(Utils.getString(R.string.API_user_regId)), "pic", imgUrl, "", new Callback<ProfilPicUpdateResponse>() {
            @Override
            public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {

                Timber.i("Profile pic Url" + AppConstants.BASE_URL + profilPicUpdateResponse.getProfilePic());
                Timber.i("Profile pic ImageView" + mCircularImage.toString());
                PicassoCache.getPicassoInstance(getApplicationContext()).load(AppConstants.BASE_URL + profilPicUpdateResponse.getProfilePic()).noFade()
                        .into(mCircularImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                DialogUtils.stopProgressDialog();
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public Activity getActivity(){
        return this;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(filePathColumn[0]);//MediaStore.Images.ImageColumns.DATA
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    void checkCurrentUser(String uid){

    }

    @Override
    public void onRightSwipe() {
        // Do something
        finish();

    }
    
    
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id){
            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null && bundle.containsKey("username"))
                    launchPersonalChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"),bundle.getString("username"));
                else if (bundle != null && bundle.containsKey("msgid"))
                    launchGroupChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"),bundle.getString("msgid"));
                break;
        }
    }

    /*private void launchChatScreen(String id,boolean isOneToOne,String userName) {
        Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("username",userName);
        //intent.putExtra("chatBackImg",backImg);
        //intent.putExtra("flag",flag);
        startActivity(intent);

        // mDisplayedFragment = Utils.getCurrVisibleFragment(this);
       *//*
       this is commented bcoz it opens inbox we need personalchat design
       Intent intent = new Intent(FeedActivity.this, InboxActivity.class);
        //intent.putExtra("id", id);
        //intent.putExtra("isOneToOne", isOneToOne);
        startActivity(intent);*//*
    }
*/
    public void launchGroupChatScreen(String id,boolean isOneToOne,String userName) {
        Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("msgid",userName);
        startActivity(intent);
    }
}
