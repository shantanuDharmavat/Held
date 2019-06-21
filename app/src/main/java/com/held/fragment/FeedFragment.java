package com.held.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.held.activity.FeedActivity;
import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.adapters.FeedAdapter;
import com.held.customview.BlurTransformation;
import com.held.customview.PicassoCache;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class FeedFragment extends ParentFragment {

    public static final String TAG = FeedFragment.class.getSimpleName();

    private RecyclerView mFeedRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeedAdapter mFeedAdapter;
    private FeedResponse mFeedResponse;
    private BlurTransformation blurTransformation;
    private GestureDetector gestureDetector;
    private EditText mSearchEdt;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isLastPage, isLoading;
    private List<FeedData> mFeedList = new ArrayList<>();
    private int mLimit = 10;
    private long mStart = System.currentTimeMillis();
    private ImageView mFullImg,mUserImg;
    private GestureDetector mGestureDetector;
    private PreferenceHelper mPrefernce;
    long nextPage=0;

   // private RelativeLayout toolbar;
    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);

    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {

        Log.d(TAG, " PIN " + PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)));
        Log.d(TAG, " Start " + System.currentTimeMillis());

        mFullImg = (ImageView) view.findViewById(R.id.FEED_full_img);
        mFeedRecyclerView = (RecyclerView) view.findViewById(R.id.FEED_recycler_view);
        mLayoutManager = new LinearLayoutManager(getCurrActivity());
        mFeedRecyclerView.setLayoutManager(mLayoutManager);
        mFeedResponse = new FeedResponse();
        blurTransformation = new BlurTransformation(getCurrActivity(), 25f);
        mFeedAdapter = new FeedAdapter((FeedActivity) getCurrActivity(), mFeedList, blurTransformation, isLastPage, this);
        mFeedRecyclerView.setAdapter(mFeedAdapter);
        mPrefernce=PreferenceHelper.getInstance(getCurrActivity());

//        mGestureDetector = new GestureDetector(getCurrActivity(), new GestureListener());
//
//        mFeedRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });


        if (getCurrActivity().getNetworkStatus()) {
//            DialogUtils.showProgressBar();
            callFeedApi(mStart);
        } else {
            UiUtils.showSnackbarToast(getView(), "Sorry! You don't seem to connected to internet");
        }

  /*      mSearchEdt = (EditText) view.findViewById(R.id.toolbar_search_edt_txt);
        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (getCurrActivity().getNetworkStatus()) {
                        DialogUtils.showProgressBar();
                        callUserSearchApi();
                    } else {
                        UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                    }
                    return true;
                }
                return false;
            }
        });

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (getCurrActivity().getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callUserSearchApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });
        */

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FEED_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getCurrActivity().getNetworkStatus()) {
                    isLastPage = false;
                    mFeedList.clear();
                    mStart = System.currentTimeMillis();
//                    DialogUtils.showProgressBar();
                    mStart = System.currentTimeMillis();
                    callFeedApi(mStart);
                    mFeedAdapter.notifyDataSetChanged();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
            }
        });

        mFeedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                //Timber.i("item Count :"+totalItemCoount);
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                //Timber.i("Last Item :"+lastVisibleItemPosition);
                if (!isLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !isLoading) {
                    Timber.i("Inside If for call Feed Api");
                    callFeedApi(nextPage);
                    mFeedAdapter.notifyDataSetChanged();
                }
            }
        });

        Utils.hideSoftKeyboard(getCurrActivity());
    }


    public void showFullImg(String url) {

        getCurrActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getCurrActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        mFullImg.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);

        ParentActivity myactivity = getCurrActivity();
        if(!myactivity.isFinishing()) {
            DialogUtils.resetDialog(myactivity);
            DialogUtils.showProgressBar();
        }
        Picasso.with(getActivity())
                .load(url)
                .priority(Picasso.Priority.HIGH)
                .noFade()
                .fit()
                .into(mFullImg, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if(!getCurrActivity().isFinishing())
                    DialogUtils.stopProgressDialog();
            }

            @Override
            public void onError() {

            }
        });
        mFeedRecyclerView.setEnabled(false);
        mSwipeRefreshLayout.setEnabled(false);
        //UiUtils.hideSystemUI(this.getView());
        ((FeedActivity)getCurrActivity()).hideToolbar();

    }

    public void showRCView() {
        getCurrActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getCurrActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFullImg.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mFeedRecyclerView.setEnabled(true);
        mSwipeRefreshLayout.setEnabled(true);

    }

    public int getHeight(){
        Context context = getActivity();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }
    public int getWidth(){
        Context context = getActivity();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    private void callUserSearchApi() {
        HeldService.getService().searchUser(mPrefernce.readPreference(getString(R.string.API_session_token)),
                mSearchEdt.getText().toString().trim(), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        Utils.hideSoftKeyboard(getCurrActivity());
                        Bundle bundle = new Bundle();

                        bundle.putString("name", searchUserResponse.getDisplayName());
                        bundle.putString("image", searchUserResponse.getPic());
                        getCurrActivity().perform(AppConstants.LAUNCH_FRIEND_REQUEST_SCREEN, bundle);

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

    private void preFetchImages(){
        for(int i=0; i<mFeedList.size();i++){
            Picasso.with(getActivity())
                    .load(AppConstants.BASE_URL + mFeedList.get(i).getThumbnailUri())
                    .fetch();
            Picasso.with(getActivity())
                    .load(AppConstants.BASE_URL + mFeedList.get(i).getImageUri())
                    .fetch();
        }
    }

    @Override
    public void onResume() { super.onResume();}

    private void callFeedApi(long startVal) {
        isLoading = true;
        if (getCurrActivity().getNetworkStatus()) {//PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN")
            HeldService.getService().feedPostWithPage(mPrefernce.readPreference(getString(R.string.API_session_token)),
                    mLimit,startVal , new Callback<FeedResponse>() {
                        @Override
                        public void success(FeedResponse feedResponse, Response response) {
//                            DialogUtils.stopProgressDialog();

                            mSwipeRefreshLayout.setRefreshing(false);
                            mFeedResponse = feedResponse;
                            mFeedList.addAll(mFeedResponse.getObjects());
                            isLastPage = mFeedResponse.isLastPage();
                            nextPage=feedResponse.getNext();
                            mFeedAdapter.setFeedResponse(mFeedList, isLastPage);
                            mStart = mFeedResponse.getNextPageStart();
                            isLoading = false;
                            preFetchImages();

                        }

                        @Override
                        public void failure(RetrofitError error) {
//                            DialogUtils.stopProgressDialog();
                            mSwipeRefreshLayout.setRefreshing(false);
                            isLoading = false;
                            if (error != null && error.getResponse() != null &&
                                    !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
//                                UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                                if (json.substring(json.indexOf(":") + 2, json.length() - 2).equals("")) {
                                }
                            } else
                                UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                        }
                    });
        }
    }

    public void notifyChange(int location, String view, long held){
        FeedData obj1 = new FeedData();

        obj1 = mFeedList.get(location);
        obj1.setHeld(held);
        obj1.setViews(view);
        mFeedList.set(location,obj1);
        mFeedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void bindListeners(View view) {
    }

    @Override
    public void onClicked(View v) {
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
//            try {
//                float diffAbs = Math.abs(e1.getY() - e2.getY());
//                float diff = e1.getX() - e2.getX();
//                Timber.i(TAG,"@@@Inside try");
//
//                if (diffAbs > SWIPE_MAX_OFF_PATH)
//                    return false;

//                // Left swipe
//                if (diff > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    ((FeedActivity) getCurrActivity()).onLeftSwipe();
//                    // Right swipe
//                } else if (-diff > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    ((FeedActivity) getCurrActivity()).onRightSwipe();
//                }
                if (e1.getX()<e2.getX()) {
                    ((FeedActivity) getCurrActivity()).onLeftSwipe();
                    // Right swipe
                } else if (e1.getX()>e2.getX()) {
                    ((FeedActivity) getCurrActivity()).onRightSwipe();
                }



//            } catch (Exception e) {
//                Log.e("YourActivity", "Error on gestures");
//            }
            return false;
        }
    }
}
