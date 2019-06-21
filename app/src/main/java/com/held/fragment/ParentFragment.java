package com.held.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.held.activity.ParentActivity;

public abstract class ParentFragment extends Fragment implements View.OnClickListener {

    public static String TAG = ParentFragment.class.getSimpleName();
    public final int DISABLE_DURATION = 500;
    private boolean mIsOneItemClicked = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialiseView(view, savedInstanceState);
        bindListeners(view);
        callInitialApi();
    }

    protected abstract void initialiseView(View view, Bundle savedInstanceState);

    protected abstract void bindListeners(View view);

    protected void callInitialApi() {
    }


    protected ParentActivity getCurrActivity() {
        return (ParentActivity) getActivity();
    }

    public abstract void onClicked(View v);

    @Override
    public void onClick(View v) {
        if (!mIsOneItemClicked) {
            onClicked(v);
            mIsOneItemClicked = true;

            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsOneItemClicked = false;
                }
            }, DISABLE_DURATION);
        }
    }

    public void onNetworkStatusChanged(boolean mNetworkStatus) {

    }


}

