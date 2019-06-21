package com.held.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.held.activity.R;
import com.held.adapters.ChatAdapter;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.customview.SlideInUpAnimator;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.PostChatData;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.User;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.HeldApplication;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.squareup.picasso.MemoryPolicy;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class ChatFragment extends ParentFragment {

    public static final String TAG = ChatFragment.class.getSimpleName();
    private RecyclerView mChatList;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mChatAdapter;
    private List<PostChatData> mPostChatData = new ArrayList<>();
    private Button mSubmitBtn;
    private EditText mMessageEdt;
    private ImageView mDownLoad,mChatBackImage;
    private boolean mIsOneToOne,misLastPage =false,mIsFirstLoad=true;
    private String mId, mFriendId,mChatBackImg;
    private BroadcastReceiver broadcastReceiver;
    private PreferenceHelper mPreference;
    private int mLimit = 7;
    private long mStart = System.currentTimeMillis();
    private PostChatData objPostChat=new PostChatData();
    private List<PostChatData> tmpList=new ArrayList<PostChatData>();
    private BlurTransformation mBlurTransformation;
    private boolean mIsScrollEndReached = false;
    private int screenHeight,screenWidth;
    WindowManager wm;
    private Display display;
    User currentUser=new User();
    User otherUser=new User();
    private static String mUserId, mPostId;


    private ScrollView mScrollParent;
    public static ChatFragment newInstance(String id, boolean isOneToOne) {

        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", id);
        bundle.putString("postid", id);
        mUserId = id; mPostId = id;
        bundle.putBoolean("isOneToOne", isOneToOne);
       // bundle.putString("chatBackImg",backImg);
        Timber.d("ChatFragment new instance received arguments: user id: " + id + " isonetoone: " + isOneToOne);
        chatFragment.setArguments(bundle);
        return chatFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       // mflag=getArguments().getBoolean("flag");
        //Timber.d("ChatFragment new instance received arguments: user id: " + mId + " isonetoone: " + mIsOneToOne);
//        getCurrActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        HeldApplication.IS_CHAT_FOREGROUND = true;
        //LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),new IntentFilter("CHAT"));
    }

    @Override
    public void onPause() {
        super.onPause();
        HeldApplication.IS_CHAT_FOREGROUND = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("ChatFragment onStart called");
        HeldApplication.IS_CHAT_FOREGROUND = true;
        //LocalBroadcastManager.getInstance(getCurrActivity()).registerReceiver((broadcastReceiver),new IntentFilter("CHAT"));
    }

    @Override
    public void onStop() {
        //LocalBroadcastManager.getInstance(getCurrActivity()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        Animation animation = AnimationUtils.loadAnimation(getCurrActivity(), android.R.anim.slide_in_left);
        mIsOneToOne = getArguments().getBoolean("isOneToOne");
        mChatList = (RecyclerView) view.findViewById(R.id.CHAT_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mLayoutManager.setReverseLayout(true);
        //mLayoutManager.scrollToPositionWithOffset(mPostChatData.size(), 0);
        SlideInUpAnimator slideInUpAnimator = new SlideInUpAnimator();
        slideInUpAnimator.setAddDuration(1000);
        mChatList.setItemAnimator(slideInUpAnimator);
        mSubmitBtn = (Button) view.findViewById(R.id.CHAT_submit_btn);
        mSubmitBtn.setBackgroundColor(getResources().getColor(R.color.chat_submitBtn_color));
        mMessageEdt = (EditText) view.findViewById(R.id.CHAT_message);
        mSubmitBtn.setOnClickListener(this);
        wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        mBlurTransformation = new BlurTransformation(getCurrActivity(), 25f);
        mChatBackImage = (ImageView) view.findViewById(R.id.background_imageView);
        mPreference = PreferenceHelper.getInstance(getCurrActivity());
        mChatAdapter = new ChatAdapter(getCurrActivity(), mPostChatData);
        mChatList.setLayoutManager(mLayoutManager);
        mChatList.setAdapter(mChatAdapter);
        setTypeFace(mMessageEdt,"book");
        setTypeFace(mSubmitBtn,"book");
        mScrollParent = (ScrollView) view.findViewById(R.id.scrollParent);
        //mScrollParent.setEnabled(false);
        if (getCurrActivity().getNetworkStatus()) {
            getCurrentUser();
            if (mIsOneToOne == true) {
                Timber.d("Calling one to one chat api");
                callGetUserPostApi();
                callFriendsChatsApi();
                getOtherUser();
            } else {
                Timber.d("Calling post chat api");
                callSearchPostApi();
                callPostChatApi();
            }
        }



        mChatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                View view = (View) recyclerView.getChildAt(recyclerView.getChildCount() - 1);

                if(lastVisibleItemPosition == (totalItemCount -1)){
                    if(!misLastPage) {
                        if(!mIsScrollEndReached){
                            Timber.d("fetching more chats");
                            mIsScrollEndReached = true;
                            if(mIsOneToOne){
                                callFriendsChatsApi();
                            }else{
                                callPostChatApi();
                            }


                        }else{
                            Timber.d("fetching already in progress");
                        }

                    }else{
                        Timber.d("end of chat messages");
                    }
                }
            }



        });

    }

    public void appendMessage(String msg){
        // find current from user
        Timber.e("BROADCAST MESSAGE : " + msg);

        PostChatData p = new PostChatData();
        p.setText(msg);
        p.setFromUser(otherUser);
        p.setToUser(currentUser);
        p.setDate(System.currentTimeMillis());

        mPostChatData.add(0, p);
        mChatAdapter.setPostChats(mPostChatData);
        mChatAdapter.notifyDataSetChanged();
        MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.new_chat);
        mp.start();
    }

    public void appendGroupChatMessage(PostChatData postChatData){

        Timber.d("Appending group chat message");
        PostChatData p = new PostChatData();
        p.setText(postChatData.getText());
        p.setFromUser(postChatData.getUser());
        p.setToUser(currentUser);
        p.setDate(System.currentTimeMillis());

        mPostChatData.add(0, p);
        mChatAdapter.setPostChats(mPostChatData);
        mChatAdapter.notifyDataSetChanged();
        MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.new_chat);
        mp.start();
        Timber.d("new group message added");

    }


    private void callfriendChatSubmit() {

        Timber.d("Calling friend chat api");
        HeldService.getService().sendfriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
                //              mId, mMessageEdt.getText().toString().trim(), "", new Callback<PostMessageResponse>() {
                getArguments().getString("user_id"), mMessageEdt.getText().toString().trim(), "", new Callback<PostMessageResponse>() {
                    @Override
                    public void success(PostMessageResponse postMessageResponse, Response response) {
                        Timber.d("##$$@@post msg response" + postMessageResponse.getFromUser().getDisplayName());
//                        objPostChat.setDate(postMessageResponse.getDate());
//                        objPostChat.setRid(postMessageResponse.getRid());
//                        objPostChat.setText(postMessageResponse.getText());
//                        objPostChat.setToUser(postMessageResponse.getToUser());
//                        objPostChat.setFromUser(postMessageResponse.getFromUser());
//                        tmpList.add(objPostChat);
//                        mPostChatData.addAll(tmpList);
                        // mChatAdapter.setPostChats(mPostChatData);

                        // tmpList.clear();
                        //Timber.i("Inside chat submit",""+postMessageResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });

    }

    private void callFriendsChatsApi() {

        Timber.d("Calling friends chat api");

        HeldService.getService().getFriendChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), mStart, mLimit, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        Timber.d("friends chat call success");
                        misLastPage = postChatResponse.isLastPage();
                        if (!misLastPage) {
                            mStart = postChatResponse.getNext();
                        }
                        mPostChatData.addAll(postChatResponse.getObjects());
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();
                        Timber.d("resetting scroll check");
                        mIsScrollEndReached = false;
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    private void callPostChatApi() {

        Timber.d("@@@@@Post Id from Feed" + mPostId);
        HeldService.getService().getPostChat(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                mPostId, mStart, mLimit, new Callback<PostChatResponse>() {
                    @Override
                    public void success(PostChatResponse postChatResponse, Response response) {
                        misLastPage = postChatResponse.isLastPage();
                        if (!postChatResponse.isLastPage()) {
                            mStart = postChatResponse.getNext();
                        }
                        mPostChatData.addAll(postChatResponse.getObjects());
                        mChatAdapter.setPostChats(mPostChatData);
                        mChatAdapter.notifyDataSetChanged();
                        Timber.d("resetting scroll check");
                        mIsScrollEndReached = false;
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        DialogUtils.stopProgressDialog();
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
        switch (v.getId()) {
            case R.id.CHAT_submit_btn:
                if (getCurrActivity().getNetworkStatus()) {
                    submitChat();
                }

                break;

        }
    }

    /*private void callDownloadRequestApi() {
        HeldService.getService().requestDownLoadPost(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), new Callback<DownloadRequestData>() {
                    @Override
                    public void success(DownloadRequestData downloadRequestData, Response response) {
                        DialogUtils.stopProgressDialog();
                        UiUtils.showSnackbarToast(getView(), "Download Request Sent Successfully");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }
*/
    private void postGroupChat() {
        Timber.e("list of key values : "+getArguments().keySet());
        HeldService.getService().postChat(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("msgid"), mMessageEdt.getText().toString().trim(), "", new Callback<PostChatData>() {
                    @Override
                    public void success(PostChatData postMessageResponse, Response response) {


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    public void callSearchPostApi(){


        Timber.d("calling search post api for post id : " + mPostId);
        HeldService.getService().getSearchCurrentPost(mPreference.readPreference(getString(R.string.API_session_token)), mPostId,
                new Callback<PostResponse>() {
                    @Override
                    public void success(PostResponse postResponse, Response response) {
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                //.load(AppConstants.BASE_URL + postResponse.getImageUri())
                                .load(AppConstants.BASE_URL+postResponse.getThumbnailUri())
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .resize(screenWidth, screenHeight).centerCrop()
                                //.transform(mBlurTransformation)
                                        //.placeholder(R.drawable.milana_vayntrub)
                                .into(mChatBackImage);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public void callGetUserPostApi(){
        HeldService.getService().getUserPosts(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), mStart, mLimit, new Callback<FeedResponse>() {
                    @Override
                    public void success(FeedResponse feedResponse, Response response) {
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL + feedResponse.getObjects().get(0).getThumbnailUri())
                                .resize(screenWidth,screenHeight).centerCrop()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(mChatBackImage);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    public void setTypeFace(Button tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    void submitChat(){
        if(mIsOneToOne){

            String msg = mMessageEdt.getText().toString();
            if(msg.equals("")){
                UiUtils.showSnackbarToast(getView(), "Please enter a message");
                return;
            }
            PostChatData data = new PostChatData();
            data.setFromUser(currentUser);
            data.setToUser(otherUser);
            data.setDate(System.currentTimeMillis());
            data.setFromUser(currentUser);
            data.setText(msg);
            mPostChatData.add(0, data);
            mChatAdapter.notifyDataSetChanged();
            callfriendChatSubmit();
            mMessageEdt.setText("");
        }
        else {
            PostChatData data = new PostChatData();
            data.setDate(System.currentTimeMillis());
            data.setFromUser(currentUser);
            data.setText(mMessageEdt.getText().toString());
            mPostChatData.add(0, data);
            mChatAdapter.notifyDataSetChanged();
            postGroupChat();
            mMessageEdt.setText("");
        }

    }
    void getCurrentUser(){
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                mPreference.readPreference(getString(R.string.API_user_regId)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        currentUser=searchUserResponse.getUser();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }
    void getOtherUser(){
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                getArguments().getString("user_id"), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        otherUser=searchUserResponse.getUser();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
}
