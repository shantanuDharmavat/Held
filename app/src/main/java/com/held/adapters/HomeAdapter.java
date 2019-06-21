package com.held.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.held.fragment.FeedFragment;
import com.held.fragment.FriendsListFragment;
import com.held.fragment.NotificationFragment;
import com.held.fragment.PostFragment;
import com.held.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public HomeAdapter(FragmentManager fm) {
        super(fm);

        mFragments = new ArrayList<>();
        mFragments.add(new FriendsListFragment());
        mFragments.add(new FeedFragment());
        mFragments.add(new PostFragment());
        mFragments.add(new ProfileFragment());
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
