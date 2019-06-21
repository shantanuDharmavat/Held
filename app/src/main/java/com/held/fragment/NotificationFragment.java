package com.held.fragment;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.R;
import com.held.adapters.ViewPagerAdapter;
import com.held.utils.PreferenceHelper;

import timber.log.Timber;

public class NotificationFragment extends ParentFragment {

    public static final String TAG = NotificationFragment.class.getSimpleName();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private int mId;
    private TextView mFriendRequest, mDownloadRequest, mActivityFeed, mFriendCount, mDownloadCount, mHeldCount;
    private RelativeLayout mFRLayout, mDRLayout, mAFLayout;


    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    public static NotificationFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*public static NotificationFragment newInstance(int id,String type) {
        Bundle bundle = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        bundle.putInt("id", id);
        bundle.putString("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {

//        mTabLayout = (TabLayout) view.findViewById(R.id.NOTIFY_tab_layout);
//        mTabLayout.addTab(mTabLayout.newTab().setText("Friend Requests"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("Download Requests"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("Activity Feed"));

        mFriendRequest = (TextView) view.findViewById(R.id.NOTIFY_friend_request);
        mDownloadRequest = (TextView) view.findViewById(R.id.NOTIFY_download_request);
        mActivityFeed = (TextView) view.findViewById(R.id.NOTIFY_feed_activity);

        mFriendCount = (TextView) view.findViewById(R.id.NOTIFY_friend_request_count);
        mDownloadCount = (TextView) view.findViewById(R.id.NOTIFY_download_request_count);
        mHeldCount = (TextView) view.findViewById(R.id.NOTIFY_feed_activity_count);

        Typeface medium = Typeface.createFromAsset(getActivity().getAssets(), "BentonSansMedium.otf");
        mFriendCount.setTypeface(medium);
        mDownloadCount.setTypeface(medium);
        mHeldCount.setTypeface(medium);

        mFRLayout = (RelativeLayout) view.findViewById(R.id.NOTIFY_fr_layout);
        mDRLayout = (RelativeLayout) view.findViewById(R.id.NOTIFY_dr_layout);
        mAFLayout = (RelativeLayout) view.findViewById(R.id.NOTIFY_af_layout);

        mFRLayout.setOnClickListener(this);
        mDRLayout.setOnClickListener(this);
        mAFLayout.setOnClickListener(this);

        mViewPager = (ViewPager) view.findViewById(R.id.NOTIFY_view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);

        int countActivity = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_HELD_COUNT), 0);
        int countDownload = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
        int countFriends = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);

        Timber.e("COUNT Activity: " + countActivity);
        Timber.e("COUNT Download: " + countDownload);
        Timber.e("INSTANCE : " + PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0));
        if (countActivity != 0) {
            mHeldCount.setVisibility(View.VISIBLE);
            mHeldCount.setText(countActivity + "");
        }
        if (countDownload != 0) {
            mDownloadCount.setVisibility(View.VISIBLE);
            mDownloadCount.setText(countDownload + "");
        }


//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

//        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        if (getArguments() != null) {
            mId = getArguments().getInt("id");
            Log.e("mId inside activityFeed",""+mId);
            if (mId == 0) {
                mViewPager.setCurrentItem(0);
                mFRLayout.setBackgroundColor(Color.TRANSPARENT);
                mDRLayout.setBackgroundColor(Color.TRANSPARENT);
                mAFLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mFriendRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mDownloadRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mActivityFeed.setTextColor(Color.WHITE);

                countActivity = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_HELD_COUNT), 0);
                if (countActivity != 0) {
                    mHeldCount.setVisibility(View.VISIBLE);
                    mHeldCount.setText(countActivity + "");
                }
                countDownload = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
                if (countDownload != 0) {
                    mDownloadCount.setVisibility(View.VISIBLE);
                    mDownloadCount.setText(countDownload + "");
                }
            } else if (mId == 1) {
                mViewPager.setCurrentItem(1);
                mFRLayout.setBackgroundColor(Color.TRANSPARENT);
                mDRLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mAFLayout.setBackgroundColor(Color.TRANSPARENT);
                mFriendRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mDownloadRequest.setTextColor(Color.WHITE);
                mActivityFeed.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));

                countDownload = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
                if (countDownload != 0) {
                    mDownloadCount.setVisibility(View.VISIBLE);
                    mDownloadCount.setText(countDownload + "");
                }
                countActivity = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_HELD_COUNT), 0);
                if (countActivity != 0) {
                    mHeldCount.setVisibility(View.VISIBLE);
                    mHeldCount.setText(countActivity + "");
                }
            } else if (mId == 2) {
                mViewPager.setCurrentItem(2);
                mFRLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mDRLayout.setBackgroundColor(Color.TRANSPARENT);
                mAFLayout.setBackgroundColor(Color.TRANSPARENT);
                mFriendRequest.setTextColor(Color.WHITE);
                mDownloadRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mActivityFeed.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));

                countActivity = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_HELD_COUNT), 0);
                if (countActivity != 0) {
                    mHeldCount.setVisibility(View.VISIBLE);
                    mHeldCount.setText(countActivity + "");
                }
                countDownload = PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
                if (countDownload != 0) {
                    mDownloadCount.setVisibility(View.VISIBLE);
                    mDownloadCount.setText(countDownload + "");
                }
            }
        }
    }

    public void updateFriendCount(int count){

        if (count != 0) {
            mFriendCount.setVisibility(View.VISIBLE);
            mFriendCount.setText(count + "");
        }else{
            mFriendCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.NOTIFY_fr_layout:
                mViewPager.setCurrentItem(2);
                mFRLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mDRLayout.setBackgroundColor(Color.TRANSPARENT);
                mAFLayout.setBackgroundColor(Color.TRANSPARENT);
                mFriendRequest.setTextColor(Color.WHITE);
                mDownloadRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mActivityFeed.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                //mFriendCount.setVisibility(View.GONE);
                break;
            case R.id.NOTIFY_dr_layout:
                mViewPager.setCurrentItem(1);
                mFRLayout.setBackgroundColor(Color.TRANSPARENT);
                mDRLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mAFLayout.setBackgroundColor(Color.TRANSPARENT);
                mFriendRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mDownloadRequest.setTextColor(Color.WHITE);
                mActivityFeed.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                PreferenceHelper.getInstance(getCurrActivity()).writePreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
                PreferenceHelper.getInstance(getCurrActivity()).writePreference(getString(R.string.API_HELD_COUNT), 0);
                //mDownloadCount.setVisibility(View.GONE);
                break;
            case R.id.NOTIFY_af_layout:
                mViewPager.setCurrentItem(0);
                mFRLayout.setBackgroundColor(Color.TRANSPARENT);
                mDRLayout.setBackgroundColor(Color.TRANSPARENT);
                mAFLayout.setBackgroundColor(getResources().getColor(R.color.selected_tab_color));
                mFriendRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mDownloadRequest.setTextColor(getResources().getColor(R.color.unselected_tab_txt_color));
                mActivityFeed.setTextColor(Color.WHITE);
                PreferenceHelper.getInstance(getCurrActivity()).writePreference(getString(R.string.API_HELD_COUNT), 0);
                PreferenceHelper.getInstance(getCurrActivity()).writePreference(getString(R.string.API_DOWNLOAD_REQUEST_COUNT), 0);
                //mHeldCount.setVisibility(View.GONE);
                break;
        }
    }
}
