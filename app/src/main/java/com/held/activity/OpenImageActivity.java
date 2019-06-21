package com.held.activity;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.utils.AppConstants;
import com.held.utils.CustomContact;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by admin on 20-Feb-16.
 */
public class OpenImageActivity extends ParentActivity{
    private static final int PICK_CONTACT =1 ;
    int  max_pic_contact = 5;
    //    private Fragment mDisplayFragment;
    public static boolean isBlured = true;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite;
    private GestureDetector mGestureDetector, mPersonalChatDetector;
    private GestureDetector gestureDetector;
    protected Toolbar mHeld_toolbar;
    private final String TAG = "FeedActivity";
    private RelativeLayout mPosttoolbar,statusbar;
    private PreferenceHelper mPreference;
    View statusBar;
    private ImageOpenHolder mImageOpenHolder;
    private ImageView mFullImg;
    private boolean firstClick=true;
    RelativeLayout myLayout;
    private boolean isFullScreenMode = false;
    private String mUserNameForSearch,mImageUri,mImageThumbUri,mUserImgUri,mPostId,mPostText,mUserName;
    private Long mHoldTime;
    CustomContact personContact=new CustomContact();
    ArrayList<CustomContact> inviteContactList=new ArrayList<>(max_pic_contact);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        mImageOpenHolder = new ImageOpenHolder(this.findViewById(android.R.id.content).getRootView());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        mImageUri = bundle.getString("postimg");
        mImageThumbUri = bundle.getString("postimgthumb");
        mPostId = bundle.getString("postid");
        mUserImgUri = bundle.getString("userimg");
        mPostText = bundle.getString("posttext");
        mUserName = bundle.getString("username");
        mHoldTime = bundle.getLong("postheld");

        mGestureDetector = new GestureDetector(this,new GestureListener());
        mFullImg = (ImageView) findViewById(R.id.activity_full_image);
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

        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mChat.setImageResource(R.drawable.back);
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(getAssets(), "BentonSansBook.otf");
        mTitle.setTypeface(medium);
        mInvite.setTypeface(medium);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        mNotification.setVisibility(View.INVISIBLE);
        mTitle.setVisibility(View.INVISIBLE);
        mInvite.setVisibility(View.INVISIBLE);
        mSearch_edt.setVisibility(View.INVISIBLE);
        mCamera.setVisibility(View.INVISIBLE);


        BlurTransformation mBlurTransformation = new BlurTransformation(this,25f);

        Timber.d("imageURI : " + mImageUri);
        Timber.d("PostText : " + mPostText);
        Timber.d("PostId : " + mPostId);
        Timber.d("hold time : " + mHoldTime);
        Timber.d("thumb uri : " + mImageThumbUri);
        Timber.d("username : " + mUserName);

      /*  mChat.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mSearch_edt.setVisibility(View.GONE);
        mInvite.setOnClickListener(this);*/

        PicassoCache.getPicassoInstance(this)
                .load(AppConstants.BASE_URL + mUserImgUri)
                .placeholder(R.drawable.user_icon)
                .into(mImageOpenHolder.mUserImg);

        PicassoCache.getPicassoInstance(this)
                .load(AppConstants.BASE_URL + mImageThumbUri)
                .placeholder(R.drawable.user_icon)
                .into(mImageOpenHolder.mFeedImg);


        // Set typeface of numbers as Benton Sans medium
        mImageOpenHolder.mTimeMinTxt.setTypeface(medium);
        mImageOpenHolder.mTimeSecTxt.setTypeface(medium);
        mImageOpenHolder.mPersonCount.setTypeface(medium);


        // Set typeface of remaining text to Benton Sans Book
        mImageOpenHolder.mPersonCountTxt.setTypeface(book);
        mImageOpenHolder.mPersonCountTxt2.setTypeface(book);
        mImageOpenHolder.mTimeTxt.setTypeface(book);
        mImageOpenHolder.mTimeTxt2.setTypeface(book);

        //username
        mImageOpenHolder.mUserNameTxt.setTypeface(medium);
        mImageOpenHolder.mFeedTxt.setTypeface(book);
//        String selfID = mPreference.readPreference(getString(R.string.API_user_regId));

        /*String  pCount = ;
        int personCount = Integer.parseInt(pCount);

        if(personCount > 1){
            mImageOpenHolder.mPersonCountTxt2.setText(" people");
        }else{
            mImageOpenHolder.mPersonCountTxt2.setText(" person");
        }
        mImageOpenHolder.mPersonCount.setText("" + personCount);*/

        mImageOpenHolder.mFeedTxt.setText(mPostText);
        mImageOpenHolder.mUserNameTxt.setText(mUserName);
        setTimeText(mHoldTime, mImageOpenHolder.mTimeMinTxt, mImageOpenHolder.mTimeSecTxt);
        mImageOpenHolder.mFeedImg.setVisibility(View.VISIBLE);
        myLayout=mImageOpenHolder.myLayout;

        mImageOpenHolder.mFeedImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
//                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        showRCView();
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        /*mImageOpenHolder.mUserImg.setVisibility(View.GONE);
                        mImageOpenHolder.mUserNameTxt.setVisibility(View.GONE);*/
                        Picasso.with(getParent()).load(AppConstants.BASE_URL + mImageThumbUri).
                                into(mImageOpenHolder.mFeedImg);
                        mImageOpenHolder.myTimeLayout.setVisibility(View.VISIBLE);
                        mImageOpenHolder.mFullImage.setVisibility(View.GONE);
                        mImageOpenHolder.mUserNameTxt.setVisibility(View.VISIBLE);
                        mImageOpenHolder.mUserImg.setVisibility(View.VISIBLE);
                        mImageOpenHolder.mFeedTxt.setVisibility(View.VISIBLE);
                        myLayout.setVisibility(View.VISIBLE);
                        if (isFullScreenMode) {

                            isFullScreenMode = false;
                        }

                        isBlured = true;
                        showToolbar();

                        break;
                }
                //mPostId = mFeedList.get(position).getCreator().getRid();
                return mGestureDetector.onTouchEvent(motionEvent);
            }

        });

    }
    public void launchFeedScreen(){

    }

    public void showFullImg(String url) {

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mFullImg.setVisibility(View.VISIBLE);
        myLayout.setVisibility(View.GONE);
        PicassoCache.getPicassoInstance(this)
                .load(url)
                .priority(Picasso.Priority.HIGH)
                .noFade()
                .fit()
                .into(mImageOpenHolder.mFullImage);
        Timber.d("FULL IMAGE URL: " + url);

        hideToolbar();
    }

    public void hideToolbar(){
        mHeld_toolbar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.GONE);}
//        toolbar_divider.setVisibility(View.GONE);
    }
    public void showRCView() {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFullImg.setVisibility(View.GONE);
//        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//        mFeedRecyclerView.setEnabled(true);
//        mSwipeRefreshLayout.setEnabled(true);

    }

    public void showToolbar(){
        mHeld_toolbar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.VISIBLE);}
//        toolbar_divider.setVisibility(View.VISIBLE);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Bundle bundle = new Bundle();

            bundle.putString("id", mPostId);
            bundle.putBoolean("oneToOne", false);
            // bundle.putString("chatBackImg",mPostImgUrl); we have post img url in api
            //bundle.putBoolean("flag",false);//not necessary
            perform(AppConstants.LAUNCH_CHAT_SCREEN, bundle);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (getNetworkStatus()) {

                mImageOpenHolder.myTimeLayout.setVisibility(View.INVISIBLE);
                //mImageOpenHolder.mFeedImg.getParent().requestDisallowInterceptTouchEvent(true);
                mImageOpenHolder.mUserImg.setVisibility(View.GONE);
                mImageOpenHolder.mUserNameTxt.setVisibility(View.GONE);
                mImageOpenHolder.mFullImage.setVisibility(View.VISIBLE);
                mImageOpenHolder.mFeedTxt.setVisibility(View.GONE);
                mImageOpenHolder.myLayout.setVisibility(View.GONE);
                /*Picasso.with(getParent())
                        .load(AppConstants.BASE_URL + mImageUri)
                        .into(mImageOpenHolder.mFullImage);*/
                showFullImg(AppConstants.BASE_URL + mImageUri);
                if(e.getAction() == MotionEvent.ACTION_UP){
                }
                isFullScreenMode = true;
            } else {
                UiUtils.showSnackbarToast(findViewById(R.id.frag_container), "You are not connected to internet");
            }
            super.onLongPress(e);
        }
    }

    public class ImageOpenHolder {
        // each data item is just a string in this case
        public final TextView mUserNameTxt, mFeedTxt, mTimeMinTxt, mTimeSecTxt;
        public final ImageView mFeedImg, mUserImg, mFullImage;
        public final RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.BOX_layout);
        public final RelativeLayout myTimeLayout = (RelativeLayout) findViewById(R.id.time_layout);
        public final LinearLayout myCountLayout = (LinearLayout) findViewById(R.id.layout_people_count);
        public final TextView mPersonCountTxt = (TextView) findViewById(R.id.tv_count_people);
        public final TextView mPersonCountTxt2 = (TextView) findViewById(R.id.tv_count_people2);
        public final TextView mPersonCount = (TextView) findViewById(R.id.count_hold_people);
        public final TextView mTimeTxt = (TextView) findViewById(R.id.time_txt);
        public final TextView mTimeTxt2 = (TextView) findViewById(R.id.time_txt2);
        de.hdodenhof.circleimageview.CircleImageView feedStatusIcon,feedring;

        private ImageOpenHolder(View v) {

            mFullImage = (ImageView) v.findViewById(R.id.activity_full_image);
            mUserNameTxt = (TextView) v.findViewById(R.id.user_name_txt);
            mFeedImg = (ImageView) v.findViewById(R.id.post_image);
            mUserImg = (ImageView) v.findViewById(R.id.profile_img);
            mFeedTxt = (TextView) v.findViewById(R.id.post_txt);
            mTimeMinTxt = (TextView) findViewById(R.id.box_min_txt);
            mTimeSecTxt=(TextView) findViewById(R.id.box_sec_txt);
            myTimeLayout.setVisibility(View.VISIBLE);

            feedStatusIcon=(de.hdodenhof.circleimageview.CircleImageView)v.findViewById(R.id.feed_status_icon);
            feedring=(de.hdodenhof.circleimageview.CircleImageView)v.findViewById(R.id.white_ring);

        }
    }
    private void setTimeText(long time, TextView textViewMinutes,TextView textViewSeconds) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        //textViewMinutes.setText(String.valueOf(minutes));
        //textViewSeconds.setText(String.valueOf(seconds));
        Integer startCount =  Integer.parseInt(textViewMinutes.getText().toString());
        updateCounter(startCount, minutes, textViewMinutes);
        startCount =  Integer.parseInt(textViewSeconds.getText().toString());
        updateCounter(startCount, seconds, textViewSeconds);
        textViewMinutes.setVisibility(View.VISIBLE);
        textViewSeconds.setVisibility(View.VISIBLE);
    }

    private void updateCounter(Integer startCount, Integer endCount, final TextView tv){
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startCount, endCount);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tv.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setDuration(700);
        animator.start();

    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id){
            case AppConstants.LAUNCH_CHAT_SCREEN:
                if (bundle != null)
                    launchChatScreen(bundle.getString("id"),bundle.getBoolean("oneToOne"),bundle.getString("username"));
                break;
        }
    }

    private void launchChatScreen(String id,boolean isOneToOne,String userName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isOneToOne", isOneToOne);
        intent.putExtra("username", userName);
        startActivity(intent);
    }
}