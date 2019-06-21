package com.held.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.held.fragment.ActivityFeedFragment;
import com.held.fragment.DownloadRequestFragment;
import com.held.fragment.FriendRequestFragment;
import com.held.fragment.NotificationFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments;
    private Fragment mParentFragment;

    public ViewPagerAdapter(FragmentManager fm, NotificationFragment parent) {
        super(fm);
        mFragments = new ArrayList<>();

        FriendRequestFragment friendReqFr = new FriendRequestFragment();
        friendReqFr.setNotificationFragment(parent);

        ActivityFeedFragment activityFeedFr = new ActivityFeedFragment();
        activityFeedFr.setmActivityFeedFragment(parent);

        DownloadRequestFragment downloadReqFr = new DownloadRequestFragment();
        downloadReqFr.setDownloadFragment(parent);

        mFragments.add(activityFeedFr);
        mFragments.add(downloadReqFr);
        mFragments.add(friendReqFr);
        mParentFragment = parent;

        Log.e("Aadapter", "ViewPagerAdapter");
        Log.e("current Item:",""+getItem(0));
    }



    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
