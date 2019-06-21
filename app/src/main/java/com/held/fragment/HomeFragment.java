package com.held.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.FeedActivity;
import com.held.activity.R;
import com.held.adapters.HomeAdapter;
import com.held.utils.UiUtils;

import java.util.List;

public class HomeFragment extends ParentFragment implements ViewPager.OnPageChangeListener {
    public ViewPager mViewPager;
    private HomeAdapter mHomeAdapter;

    public static final String TAG = HomeFragment.class.getSimpleName();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.HOME_view_pager);
        mHomeAdapter = new HomeAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mHomeAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(this);

    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((FeedActivity) getCurrActivity()).updateViewPager(position);
//        if (position == 2) {
//            ((FeedActivity) getCurrActivity()).updateToolbarForPost();
//        } else if (position == 0) {
//            ((FeedActivity) getCurrActivity()).updateToolbarForInbox();
//        } else {
//            ((FeedActivity) getCurrActivity()).updateToolbar();
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
                else
                    UiUtils.showSnackbarToast(getView(), "Oops, Something went wrong.");
            }
        }
    }

    public void updateViewPager(int position) {
        if (mViewPager != null)
            mViewPager.setCurrentItem(position);
    }


}
